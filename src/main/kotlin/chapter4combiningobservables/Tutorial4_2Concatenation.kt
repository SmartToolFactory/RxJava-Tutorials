package chapter4combiningobservables

import io.reactivex.Observable

fun main() {

    // INFO concat
    testConcatOperator()

    // INFO concatMap()
    testConcatMap()

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
       Observable.concat() should be used to guarantee emission ordering, as merging does not guarantee it.
     */

    // INFO concatWith
//    source1.concatWith(source2)
//        .subscribe { i -> println("RECEIVED: $i") }
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
private fun testConcatMap() {

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