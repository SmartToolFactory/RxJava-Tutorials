package chapter5multicastingreplayingcaching

import io.reactivex.Observable
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit


/**
 *  AUTOMATIC CONNECTION
 *
 *  There are definitely times you will want to manually call connect() on ConnectableObservable
 *  to precisely control when the emissions start firing.
 *  There are convenient operators that automatically call connect() for you,
 *  but with this convenience, it is important to have awareness of their subscribe timing behaviors.
 *  Allowing an Observable to dynamically connect can backfire if you are not careful,
 *  as emissions can be missed by Observers.
 */
fun main() {

    // INFO autoConnect
//    testAutoConnectOperator()
//    testAutoConnectOperatorWithNumberOfSubscribers()
//    testAutoConnectOperatorWithNumberOfSubscribers2()
    testAutoConnectWithZeroSubscribers()

    // INFO refCount
//    testRefCountOperator()
}

/**
 * INFO 🔥 autoConnect
 * The autoConnect() operator on ConnectableObservable can be quite handy.
 * For a given ConnectableObservable<T>, calling autoConnect() will return an Observable<T>
 * that will automatically call connect() after a specified number of Observers are subscribed
 */
private fun testAutoConnectOperator() {
    val threeRandoms =
        Observable.range(1, 3)
            .map { i ->
                println("map() $i")
                randomInt()
            }
            .publish()
            .autoConnect(2)

    // WARNING If numberOfSubscribers of autoConnect() is higher than observers it does not emit anything

    threeRandoms.subscribe { i -> println("😛 Observer 1: $i") }
    threeRandoms.subscribe { i -> println("🎃 Observer 2:$i") }

    /*
        Prints:
        map() 1
        😛 Observer 1: 27487
        🎃 Observer 2:27487
        map() 2
        😛 Observer 1: 8750
        🎃 Observer 2:8750
        map() 3
        😛 Observer 1: 46367
        🎃 Observer 2:46367
     */
}

/**
 * Even when all downstream Observers finish or dispose,
 * **autoConnect()** will persist its subscription to the source.
 * If the source is finite and disposes, it will not subscribe to it again when
 * a new **Observer** subscribes downstream. If we add a third **Observer** to our
 * example but keep **autoConnect()** specified at 2 instead of 3,
 * it is likely that the third Observer is going to miss the emissions
 */
private fun testAutoConnectOperatorWithNumberOfSubscribers() {
    val threeRandoms =
        Observable.range(1, 3)
            .map { i ->
                println("map() $i")
                randomInt()
            }
            .publish()
            .autoConnect(2)

    //Observer 1 - print each random integer
    threeRandoms.subscribe { i -> println("😛 Observer 1: $i") }

    //Observer 2 - print each random integer
    threeRandoms.subscribe { i -> println("🎃Observer 2: $i") }

    //Observer 3 - receives nothing
    threeRandoms.subscribe { i -> println("😱 Observer 3: $i") }

    /*
        Prints:
        map() 1
        😛 Observer 1: 31419
        🎃Observer 2: 31419
        map() 2
        😛 Observer 1: 61369
        🎃Observer 2: 61369
        map() 3
        😛 Observer 1: 7211
        🎃Observer 2: 7211
     */
}


private fun testAutoConnectOperatorWithNumberOfSubscribers2() {
    val threeRandoms = Observable.range(1, 3)
        .map { _ -> randomInt() }
        .publish()
        .autoConnect(2)

    //Observer 1 - print each random integer
    threeRandoms.subscribe { i -> println("Observer 1: $i") }

    //Observer 2 - sum the random integers, then print
    threeRandoms.reduce { total, next -> total + next }.subscribe { i -> println("Observer 2: $i") }

    //Observer 3 - receives nothing
    threeRandoms.subscribe { i -> println("Observer 3: $i") }

    /*
        Prints:
        Observer 1: 23732
        Observer 1: 48268
        Observer 1: 84900

        Observer 2: 156900
     */

}


/**
 * * Note that if you pass no argument for **numberOfSubscribers**, it will default to **1**.
 * This can be helpful if you want it to start firing on the first subscription and
 * do not care about any subsequent Observers missing previous emissions.
 * Here, we **publish** and **autoConnect** the **Observable.interval()**.
 *
 * * The first **Observer** starts the firing of emissions, and 3 seconds later,
 * another **Observer** comes in but misses the first few emissions.
 * But it does receive the live emissions from that point on
 */
private fun testAutoConnectWithZeroSubscribers() {

    val seconds = Observable.interval(1, TimeUnit.SECONDS)
        .publish()
        .autoConnect()

    //Observer 1
    seconds.subscribe { i -> println("🚗 Observer 1: $i") }

    sleep(3000)

    //Observer 2
    seconds.subscribe { i -> println("🤑 Observer 2: $i") }

    sleep(3000)

    //Observer 2
    seconds.subscribe { i -> println("🥶 Observer 3: $i") }

    sleep(5000)

    /*
        Prints:
        🚗 Observer 1: 0
        🚗 Observer 1: 1
        🚗 Observer 1: 2
        🚗 Observer 1: 3
        🤑 Observer 2: 3
        🚗 Observer 1: 4
        🤑 Observer 2: 4
        🚗 Observer 1: 5
        🤑 Observer 2: 5
     */

}

/**
 * INFO 🔥 refCount
 *
 * The **refCount()** operator on ConnectableObservable is similar to
 * **autoConnect(1)**, which fires after getting one subscription.
 *
 * But there is one important difference; when it has no **Observers** anymore,
 * it will **dispose of itself** and **start over** when a **new one** comes in.
 * It does not persist the subscription to the source when it has no more **Observers**,
 * and when another **Observer** follows, it will essentially "start over".
 */
private fun testRefCountOperator() {

    val seconds = Observable.interval(1, TimeUnit.SECONDS)
        .publish()
        .refCount()

    // 🔥 publish().refCount = share()

    //Observer 1
    seconds
        .take(5)
        .subscribe { i -> println("🚗 Observer 1: $i") }

    sleep(3000)

    //Observer 2
    seconds
        .take(4)
        .subscribe { i -> println("🤑 Observer 2: $i") }

    sleep(3000)

    // here should be no more Observers at this point

    // 🔥 Emission starts over here because observable disposes itself when there are no observers
    //Observer
    seconds.subscribe { l -> println("Observer 3: $l") }


    sleep(3000)

    /*
        Prints:
        🚗 Observer 1: 0
        🚗 Observer 1: 1
        🚗 Observer 1: 2
        🚗 Observer 1: 3
        🤑 Observer 2: 3
        🚗 Observer 1: 4
        🤑 Observer 2: 4
        Observer 3: 0
        Observer 3: 1
        Observer 3: 2
     */

    // WARNING Observer only starts over when there is no observers, otherwise it resumes from 6
    /*
      INFO
       Observable.interval() emitting every second, and it is multicast with refCount().
       Observer 1 takes five emissions, and Observer 2 takes two emissions.
       We stagger their subscriptions with our sleep() function to put three- second gaps between them.
       Because these two subscriptions are finite due to the take() operators,
       they should be terminated by the time Observer 3 comes in,
       and there should no longer be any previous Observers.
       Note how Observer 3 has started over with a fresh set of intervals starting at 0!
     */

    /*
       Using refCount() can be helpful to multicast between multiple Observers
       but dispose of the upstream connection when no downstream Observers are present anymore.
       You can also use an alias for publish().refCount() using the share() operator.
       This will accomplish the same result:

       Observable<Long> seconds =  Observable.interval(1, TimeUnit.SECONDS).share();
     */

}


