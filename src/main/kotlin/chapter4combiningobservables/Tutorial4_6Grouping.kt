package chapter4combiningobservables

import io.reactivex.Observable


fun main() {

    // INFO groupBy
    testGroupByOperator()

}

/**
 * 🔥 INFO groupBy
 *
 * The GroupBy operator divides an Observable that emits items into an
 * Observable that emits Observables, each one of which emits some subset
 * of the items from the original source Observable.
 * Which items end up on which Observable is typically decided by a
 * discriminating function that evaluates each item and assigns it a key.
 * All items with the same key are emitted by the same Observable.
 *
 */
private fun testGroupByOperator() {

    val source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

    val byLengths = source.groupBy { s -> s.length }

    /*
       INFO
        We will likely need to use flatMap() on each GroupedObservable,
        but within that flatMap() operation, we may want to reduce or collect those
        common-key emissions (since this will return a Single, we will need to use
        flatMapSingle()). Let's call toList() so that we can emit the emissions
        as lists grouped by their lengths:
     */

    byLengths
        .flatMapSingle {
            it.toList()
        }
        .subscribe {
            println(it)
        }
    /*
        Prints:
        [Beta]
        [Alpha, Gamma, Delta]
        [Epsilon]
     */


}