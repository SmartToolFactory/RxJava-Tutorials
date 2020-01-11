package chapter3basicoperators

import io.reactivex.Observable
import java.time.LocalDate


fun main() {

    // INFO count
//    testCountOperator()

    // INFO reduce
//    testReduceOperator()

    // INFO all
//    testAllOperator()

    // INFO any
//    testAnyOperator()

    // INFO contains
    testContains()

}

/**
 * 🔥 INFO count()
 *
 * The simplest operator to consolidate emissions into a single one is count().
 * It will count the number of emissions and emit through a **Single** once **onComplete()** is called
 */
private fun testCountOperator() {

    Observable.just(
        "Alpha", "Beta", "Gamma", "Delta", "Epsilon"
    )
        .count()    // returns single
        .subscribe(
            {
                println("onSuccess() Received: $it")
            },
            {
                print("onError ${it.message}")
            })

    /*
        Prints:
        onSuccess() Received: 5
     */
}

/**
 * 🔥 INFO reduce()
 *
 * The **reduce()** operator is syntactically identical to **scan()**,
 * but it only **emits the final accumulation** when the source calls **onComplete()**.
 * Depending on which overload you use, it can yield **Single** or **Maybe**.
 * If you want to emit the sum of all integer emissions, you can take each one and add it to the rolling total.
 * But it will only emit once it is finalized
 */
private fun testReduceOperator() {

    val observable = Observable.just(5, 3, 7, 10, 2, 14)

    observable
        .reduce { total, next -> total + next }
        .subscribe(
            { s -> println("onSuccess() Received: $s") },
            { println("onError ${it.message}") },
            { println("onComplete()") }
        )

    /*
        Prints:
        onSuccess() Received: 41
     */
}

/**
 * 🔥 INFO all()
 *
 * The **all()** operator verifies that each emission qualifies with a specified condition and return a **Single<Boolean>**.
 * If they all pass, it will **emit True**. If it encounters one that fails,
 * it will immediately **emit False**. In the following code snippet,
 * we emit a test against six integers, verifying that they all are less than 10
 *
 */
private fun testAllOperator() {
    Observable.just(5, 3, 7, 11, 2, 14)
        .all { i -> i < 10 } // Single
        .subscribe { s -> println("Received: $s") }
}

/**
 * 🔥 INFO any()
 *
 * The **any()** method will check whether at least one emission meets a specific criterion and return a **Single<Boolean>**.
 * The moment it finds an emission that qualifies, it will emit true and then call onComplete().
 * If it processes all emissions and finds that they all are false, it will emit false and call onSuccess().
 */
private fun testAnyOperator() {

    Observable.just(
        "2016-01-01", "2016-05-02", "2016-09-12",
        "2016-04-03"
    )
        .map { LocalDate.parse(it) }
        .any { dt -> dt.monthValue >= 6 } // Single
        .subscribe { s -> println("onSuccess() Received: $s") }

    /*
        Prints:
        onSuccess() Received: true
     */
}

/**
 * 🔥 INFO contains()
 *
 * The **contains()** operator will check whether a specific element (based on the hashCode()/equals() implementation)
 * ever emits from an Observable. It will return a **Single<Boolean>** that will emit **true** if it is found and **false** if it is not.
 *
 */
private fun testContains() {

    Observable.range(1, 10000)
        .contains(9563) // Single
        .subscribe {
                s -> println("onSuccess() Received: $s") }
    /*
        Prints:
        onSuccess() Received: true
     */

}
