package chapter5multicastingreplayingcaching

import io.reactivex.Observable
import java.util.concurrent.ThreadLocalRandom


/**
 * Multi-casting is for setting a job one before splitting it to Observers
 *
 * map operation before publish is only done once and then onNext() methods are called for
 * each Hot Observers
 */
fun main() {
    // INFO Cold/Hot Observables
//    testColdObservables()
//    testConnectableObservable()

    // INFO Multicasting with operatos
//    testWithoutMulticasting()
//    testMulticasting()
    testWithoutMulticasting2()
//    testWithMulticasting2()
}


/**
 *  Observer One received all three emissions and called onComplete().
 *  After that, Observer Two received the three emissions (which were regenerated again) and called onComplete().
 *  These were two separate streams of data generated for two separate subscriptions.
 */
private fun testColdObservables() {

    val threeIntegers = Observable.range(1, 3);

    threeIntegers.subscribe { i -> println("Observer One: $i") };
    threeIntegers.subscribe { i -> println("Observer Two: $i") };

    /*
        Prints:
        Observer One: 1
        Observer One: 2
        Observer One: 3

        Observer Two: 1
        Observer Two: 2
        Observer Two: 3
     */
}

/**
 *
 */
private fun testConnectableObservable() {

    val threeIntegers = Observable.range(1, 3).publish();

    threeIntegers.subscribe { i -> println("Observer One: $i") };

    threeIntegers.subscribe { i -> println("Observer Two: $i") };

    // WARNING When connect is called changes the outcome
    threeIntegers.connect();

    /*
        Prints
        Observer One: 1
        Observer Two: 1
        Observer One: 2
        Observer Two: 2
        Observer One: 3
        Observer Two: 3
     */

}

/*
     *** Mulitcasting with Operators ***
*/

/**
 * What happens here is that the Observable.range() source will yield two separate emission generators,
 * and each will coldly emit a separate stream for each Observer.
 * Each stream also has its own separate map() instance, hence each Observer gets different random integers.
 */
private fun testWithoutMulticasting() {

    println("testWithoutMulticasting()")


    val threeRandoms = Observable.range(1, 3)
        .map { i ->
            println("map() $i")
            randomInt()
        }

    threeRandoms.subscribe { i -> println("Observer 1: $i") }
    threeRandoms.subscribe { i -> println("Observer 2: $i") }

    /*
        Prints:
         map() 1
        Observer 1:63515
        map() 2
        Observer 1:30572
        map() 3
        Observer 1:14494
        map() 1
        Observer 2:87617
        map() 2
        Observer 2:1974
        map() 3
        Observer 2:56997
     */

    /*
       INFO
        What happens here is that the Observable.range() source will yield two separate emission generators,
        and each will coldly emit a separate stream for each Observer.
        Each stream also has its own separate map() instance,
        hence each Observer gets different random integers.
     */
}

private fun testMulticasting() {

    println("testMulticasting()")

    val threeRandoms =
        Observable.range(1, 3).map { i ->
            println("map() $i")
            randomInt()
        }.publish()

    threeRandoms.subscribe { i -> println("Observer 1: $i") }
    threeRandoms.subscribe { i -> println("Observer 2:$i") }
    threeRandoms.connect()

    /*
        Prints:
        map() 1
        Observer 1: 72881
        Observer 2:72881
        map() 2
        Observer 1: 42317
        Observer 2:42317
        map() 3
        Observer 1: 38867
        Observer 2:38867
     */

    /*
        Each Observer got the same three random integers,
        and we have effectively multicast the entire operation right before the two Observers
     */
}

/**
 * This method
 */
private fun testWithoutMulticasting2() {
    val observable = Observable.just(0)
        // 🔥 WARNING This operation is done for both observers
        .map {
            println("🔧 Memory heavy work is being done val: $it")
            it + 1
        }

    observable.subscribe { println("Observer1 onSubscribe() result: $it") }

    observable.subscribe { println("Observer2 onSubscribe() result: $it") }

    /*
        Prints
        🔧 Memory heavy work is being done val: 0
        Observer1 onSubscribe() result: 1
        🔧 Memory heavy work is being done val: 0
        Observer2 onSubscribe() result: 1
     */
}

/**
 * This functions invoke map only once. After that each subscriber's onNext method is called.
 */
private fun testWithMulticasting2() {

    val observable = Observable.just(0)
        // 🔥 WARNING This operation is done once
        .map {
            println("🔧 Memory heavy work is being done val: $it")
            it + 1
        }
        .publish()


    observable.subscribe { println("Observer1 onSubscribe() result: $it") }
    observable.subscribe { println("Observer2 onSubscribe() result: $it") }

    observable.connect()

    /*
        Prints :
        🔧 Memory heavy work is being done val: 0
        Observer1 onSubscribe() result: 1
        Observer2 onSubscribe() result: 1
     */

}

fun randomInt(): Int {
    return ThreadLocalRandom.current().nextInt(100000)
}