package chapter5multicastingreplayingcaching

import io.reactivex.Observable
import java.lang.Thread.sleep
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

    // INFO Multicasting with operators
//    testWithoutMulticasting()
    testMulticasting()
//    testWithoutMulticasting2()
//    testWithMulticasting2()
}


/**
 *  Observer One received all three emissions and called onComplete().
 *  After that, Observer Two received the three emissions (which were regenerated again)
 *  and called onComplete().
 *
 *  These were two separate streams of data generated for two separate subscriptions.
 */
private fun testColdObservables() {

    val threeIntegers = Observable.range(1, 3)

    threeIntegers.subscribe { i -> println("Observer One: $i") }
    threeIntegers.subscribe { i -> println("Observer Two: $i") }

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
 * Using ConnectableObservable will force emissions from the source to become hot,
 * pushing a single stream of emissions to all Observers at the same time
 * rather than giving a separate stream to each Observer. This idea of stream consolidation
 * is known as multicasting, but there are nuances to it, especially when operators become involved.
 * Even when you call `publish()` and use a `ConnectableObservable`
 */
private fun testConnectableObservable() {

    val threeIntegers = Observable.range(1, 3).publish()

    threeIntegers.subscribe { i -> println("Observer One: $i") }

    threeIntegers.subscribe { i -> println("Observer Two: $i") }
    // WARNING When connect is called changes the outcome
    threeIntegers.connect()

    /*
        Prints
        Observer One: 1
        Observer Two: 1
        Observer One: 2
        Observer Two: 2
        Observer One: 3
        Observer Two: 3
     */

    sleep(10_000)

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

    threeRandoms.subscribe { i -> println("😛 Observer 1: $i") }
    threeRandoms.subscribe { i -> println("🎃 Observer 2: $i") }

    /*
        Prints:
        map() 1
        😛 Observer 1: 67921
        map() 2
        😛 Observer 1: 90654
        map() 3
        😛 Observer 1: 83350
        map() 1
        🎃 Observer 2: 985
        map() 2
        🎃 Observer 2: 3352
        map() 3
        🎃 Observer 2: 6663
     */

    /*
       INFO
        What happens here is that the Observable.range() source will yield
        two separate emission generators,
        and each will coldly emit a separate stream for each Observer.

        Each stream also has its own separate map() instance,
        hence each Observer gets different random integers.
     */
}


/**
 * * Everything before `publish()` was consolidated into a single
 * stream (or more technically, a single proxy Observer).
 *
 * * But after `publish()`, it will fork into separate streams for each Observer again
 */
private fun testMulticasting() {

    println("testMulticasting()")

    /*
        Each Observer got the same three random integers,
        and we have effectively multicast the entire operation right before the two Observers
     */


    val threeRandomsWithPublishBeforeMap =
        Observable
            .range(1, 3)
            .publish()


    val result = threeRandomsWithPublishBeforeMap
        .map { i ->
            println("map() $i")
            randomInt()
        }


    result.subscribe { i -> println("😛Observer 1: $i") }
    result.subscribe { i -> println("🎃 Observer 2:$i") }
    threeRandomsWithPublishBeforeMap.connect()

    /*
        Prints:
        map() 1
        😛Observer 1: 99528
        map() 1
        🎃 Observer 2:47752
        map() 2
        😛Observer 1: 3828
        map() 2
        🎃 Observer 2:87651
        map() 3
        😛Observer 1: 86789
        map() 3
        🎃 Observer 2:8941
     */

    /*
         Observable.range(1, 3)
                    |
                    |
            Emission generator
                    |
                    |
                  publish
                  /     \
                 /       \
                map1    map2
                 |        |
             Observer1  Observer2

 */

    println("testMulticasting() with publish same Stream")

    val threeRandoms =
        Observable.range(1, 3)
            .map { i ->
                println("map() $i")
                randomInt()
            }
            .publish()

    threeRandoms.subscribe { i -> println("😛 Observer 1: $i") }
    threeRandoms.subscribe { i -> println("🎃 Observer 2:$i") }
    threeRandoms.connect()

/*
    Prints:

     map() 1
    😛 Observer 1: 75861
    🎃 Observer 2:75861
    map() 2
    😛 Observer 1: 84842
    🎃 Observer 2:84842
    map() 3
    😛 Observer 1: 2762
    🎃 Observer 2:2762
 */

/*
 Observable.range(1, 3)
            |
            |
    Emission generator
            |
            |
           map
            |
            |
          publish
          /     \
         /       \
   Observer1    Observer2

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