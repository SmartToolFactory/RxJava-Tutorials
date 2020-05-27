package chapter5multicastingreplayingcaching

import io.reactivex.Observable
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit

/*
    1 share() and cache() are also options,
     but they are basically just shortcuts around ConnectableObservable.

     share() is just publish().refCount()

     and cache() can be recreated by using replay().autoConnect().


 */
fun main() {

    // INFO replay
    testReplayOperatorWithNoArguments()
//    testReplayWithArguments()
//    testReplayWithArguments2()

    // cache

}


/**
 * INFO 🔥 replay
 *
 * The replay() operator is a powerful way to hold onto previous emissions
 * within a certain scope and re-emit them when a new Observer comes in.
 * It will return a ConnectableObservable that will both multicast emissions as well as emit
 * previous emissions defined in a scope
 *
 */
private fun testReplayOperatorWithNoArguments() {

    val seconds = Observable.interval(1, TimeUnit.SECONDS)
        .replay()
        .autoConnect()

    //Observer 1
    seconds.subscribe { i -> println("🚗 Observer 1: $i") }

    sleep(4000)

    //Observer 2
    seconds.subscribe { i -> println("🤑 Observer 2: $i") }

    sleep(3000)

    /*
        Prints:
        🚗 Observer 1: 0
        🚗 Observer 1: 1
        🚗 Observer 1: 2
        🤑 Observer 2: 0
        🤑 Observer 2: 1
        🤑 Observer 2: 2
        🚗 Observer 1: 3
        🤑 Observer 2: 3
        🚗 Observer 1: 4
        🤑 Observer 2: 4
        🚗 Observer 1: 5
        🤑 Observer 2: 5
     */

    // 🔥 INFO Observer 2 gets all emissions tha previously fired at once
}

private fun testReplayWithArguments() {

    val seconds = Observable.interval(1, TimeUnit.SECONDS)
        .replay(2)
        .autoConnect()

    //Observer 1
    val disposable = seconds
        .subscribe { i -> println("🚗 Observer 1: $i") }

    sleep(4000)

    //Observer 2
    seconds.subscribe { i -> println("🤑 Observer 2: $i") }

    sleep(3000)

    // WARNING Observer 2 only gets the last 2 results which are 2 and 3,
    //  since replay(2) is used, and gets them instantly
    /*
        Prints:
        🚗 Observer 1: 0
        🚗 Observer 1: 1
        🚗 Observer 1: 2
        🚗 Observer 1: 3
        🤑 Observer 2: 2
        🤑 Observer 2: 3
        🚗 Observer 1: 4
        🤑 Observer 2: 4
        🚗 Observer 1: 5
        🤑 Observer 2: 5
        🚗 Observer 1: 6
        🤑 Observer 2: 6
     */
}

/**
 * Note that if you always want to persist the cached values in your replay()
 * even if there are no subscriptions, use it in conjunction with autoConnect(), not refCount().
 * If we emit our Alpha through Epsilon strings and use replay(2).autoConnect()
 * to hold on to the last 2 values, our second Observer will only receive the last 2 values
 */
private fun testReplayWithArguments2() {

    val source =
        Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
            .replay(2)
            .autoConnect()


    //Observer 1
    source.subscribe { i -> println("🚗 Observer 1: $i") }

    //Observer 2
    source.subscribe { i -> println("🤑 Observer 2: $i") }

    /*
        Prints:
        🚗 Observer 1: Alpha
        🚗 Observer 1: Beta
        🚗 Observer 1: Gamma
        🚗 Observer 1: Delta
        🚗 Observer 1: Epsilon
        🤑 Observer 2: Delta
        🤑 Observer 2: Epsilon
     */

}