package chapter4combiningobservables

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun main() {

    // INFO concat
//    testConcatOperator()
//    testConcatOperatorWithList()
    testConcatOperatorInterval()
//    testConcatOperatorIntervalWithFiniteSource()

    // INFO concatMap()
//    testConcatMapOperator()

    // INFO delay each with concatMap()
//    delayEachItemWithConcatMap()

}


/**
 * 🔥 INFO concat
 *
 * The Observable.concat() factory is the concatenation equivalent to Observable.merge() but it always
 * emits in the same order and with intervals it's not possible to emit values from second observable.
 *
 */
private fun testConcatOperator() {

    val source1 = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
    val source2 = Observable.just("Zeta", "Eta", "Theta")

    Observable.concat(source1, source2).subscribe { i -> println("RECEIVED: $i") }

    /*
        Prints:
        RECEIVED: Alpha
        RECEIVED: Beta
        RECEIVED: Gamma
        RECEIVED: Delta
        RECEIVED: Epsilon

        RECEIVED: Zeta
        RECEIVED: Eta
        RECEIVED: Theta
     */

    /*
      INFO
       Observable.concat() should be used to guarantee emission ordering,
       as merging does not guarantee it.
     */

    // INFO concatWith
//    source1.concatWith(source2)
//        .subscribe { i -> println("RECEIVED: $i") }
}


private fun testConcatOperatorWithList() {

    val source1 = Observable.just("Alpha", "Beta")
    val source2 = Observable.just("Gamma", "Delta")
    val source3 = Observable.just("Epsilon", "Zeta")
    val source4 = Observable.just("Eta", "Theta")
    val source5 = Observable.just("Iota", "Kappa")

    val sources = listOf(source1, source2, source3, source4, source5)

    Observable.concat(sources).subscribe { i -> println("🚗 onNext(): $i") }

    /*
        Prints:
        🚗 onNext(): Alpha
        🚗 onNext(): Beta
        🚗 onNext(): Gamma
        🚗 onNext(): Delta
        🚗 onNext(): Epsilon
        🚗 onNext(): Zeta
        🚗 onNext(): Eta
        🚗 onNext(): Theta
        🚗 onNext(): Iota
        🚗 onNext(): Kappa
     */
}


private fun testConcatOperatorInterval() {

    //emit every second
    val source1 = Observable.interval(1, TimeUnit.SECONDS)
            .map { l -> l + 1 } // emit elapsed seconds
            .map { l -> "Source1: $l seconds" }

    //emit every 300 milliseconds
    val source2 = Observable.interval(300, TimeUnit.MILLISECONDS)
            .map { l -> (l + 1) * 300 } // emit elapsed milliseconds
            .map { l -> "Source2: $l milliseconds" }

    //merge and subscribe
    Observable.concat(source1, source2)
            .subscribe {
                println(it)
            }

    //keep alive for 3 seconds
    Thread.sleep(3000)

    /*
    🔥🔥🔥 WARNING concat operator waits until first stream ends, so the FIST INFINITE stream
    is emitted, but not the other ones
 */

    /*
        Observable.concat(source1, source2)
        Prints:
        Source1: 1 seconds
        Source1: 2 seconds
        Source1: 3 seconds

        Observable.concat(source2, source1)
        Prints:
        Source2: 300 milliseconds
        Source2: 600 milliseconds
        Source2: 900 milliseconds
        Source2: 1200 milliseconds
        Source2: 1500 milliseconds
        Source2: 1800 milliseconds
        Source2: 2100 milliseconds
        Source2: 2400 milliseconds
        Source2: 2700 milliseconds
        Source2: 3000 milliseconds

     */

}

private fun testConcatOperatorIntervalWithFiniteSource() {

    //emit every second
    val source1 = Observable.interval(1, TimeUnit.SECONDS)
            .map { l -> l + 1 } // emit elapsed seconds
            .map { l -> "Source1: $l seconds" }

    val source2 = Observable.just(1, 2, 3, 4)

    //merge and subscribe
    Observable.concat(source1, source2)
            .doFinally {
                println("doFinally()")
            }
            .subscribe {
                println(it)
            }

    Thread.sleep(10_000)

    // 🔥🔥🔥 WARNING Does not start emitting source2 while INFINITE stream is emitting

    /*
        Prints:
        Source1: 1 seconds
        Source1: 2 seconds
        Source1: 3 seconds
        Source1: 4 seconds
        Source1: 5 seconds
        Source1: 6 seconds
        Source1: 7 seconds
        Source1: 8 seconds
        Source1: 9 seconds
        Source1: 10 seconds
     */

}


/**
 * 🔥 INFO concatMap
 *
 * Just as there is **flatMap()**, which dynamically merges Observables derived off each emission,
 * there is a concatenation counterpart called concatMap().
 * You should prefer this operator **if you care about ordering** and want each **Observable mapped**
 * **from each emission to finish before starting the next one**.
 *
 * **concatMap()** will merge each mapped Observable sequentially and fire it one at a time.
 * It will **only move to the next** Observable when the current one calls **onComplete()**. If source emissions produce Observables faster than concatMap() can emit from them, those Observables will be queued.
 */
private fun testConcatMapOperator() {

    val source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

    source
        .concatMap { s -> Observable.fromArray(*s.split("".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) }
        .subscribe (
            {
                println(it)
            },
            {},
            {println("onComplete()")}
        )

}


private fun delayEachItemWithConcatMap() {

    val source1 = Observable.just("A", "B", "C", "D", "E")

    source1.concatMap {
        Observable.just(it)
//            .map {
//                println("🤝Before delay $it")
//                it
//            }
            .delay(1, TimeUnit.SECONDS)
//            .map {
//                println("🤪 After delay $it")
//                it
//            }
    }
        .blockingSubscribe {
            println("Item: $it")
        }

}

