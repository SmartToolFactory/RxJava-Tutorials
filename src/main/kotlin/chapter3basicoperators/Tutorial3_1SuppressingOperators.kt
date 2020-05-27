package chapter3basicoperators

import io.reactivex.Maybe
import io.reactivex.Observable
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import java.util.function.Predicate


fun main() {

    /*
        Suppressing Operators
     */
    // INFO filter
//    testFilterOperator()

    // INFO take
//    testTakeOperator()
//    testTakeOperatorWithInterval()
//    testSkipOperator()

    // INFO takeWhile
//    testTakeWhileOperator()
    // INFO skipWhile
//    testSkipWhileOperator()

    // INFO distinct
//    testDistinctOperator()
//    testDistinctOperator2()
//    testDistinctOperator3()

    // INFO distinctUntilChanged
//    testDistinctUntilChanged()

    // INFO elementAt
    testElementAtOperator()
}

/**
🔥 INFO filter()

The filter() operator accepts Predicate<T> for a given Observable<T>.
This means that you provide it a lambda that qualifies each emission
by mapping it to a Boolean value, and emissions with false will not go forward.
 */

private fun testFilterOperator() {

    val predicate1 = object : Predicate<String> {
        override fun test(s: String): Boolean {
            return s.length != 5
        }
    }

    val predicate2 = Predicate<String> { s -> s.length != 5 }


    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .filter { s -> s.length != 5 }
        .subscribe(
            { s -> println("onNext() RECEIVED: $s") },
            { error -> println("onError() $error") },
            { print("onComplete()") }
        )

    /*
        Prints:
        RECEIVED: Beta
        RECEIVED: Epsilon
     */

}

/**
🔥 INFO take()

The **take()** operator has two overloads.
One will take a specified number of emissions and then call **onComplete()**
after it captures all of them.
It will also dispose of the entire subscription so that no more emissions will occur.
For instance, take(3) will emit the first three emissions and then call the onComplete() event.
 */

private fun testTakeOperator() {

    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .take(3)
        .subscribe { s -> println("RECEIVED: $s") }

    /*
        Prints:
        RECEIVED: Alpha
        RECEIVED: Beta
        RECEIVED: Gamma
     */

}

/**
 * The other overload will take emissions within a specific time duration and then call **onComplete()**.
 * Of course, our **cold**🥶 Observable here will emit so quickly that it would serve
 * as a bad example for this case.
 * Maybe a better example would be to use an **Observable.interval()** function.
 * Let's emit every 300 milliseconds, but take() emissions for only 2 seconds in
 * the following code snippet:
 */
private fun testTakeOperatorWithInterval() {
    Observable.interval(300, TimeUnit.MILLISECONDS)
        .take(2, TimeUnit.SECONDS)
        .subscribe { i -> println("RECEIVED: $i") }

    sleep(5000)

    /*
        Prints:
        RECEIVED: 0
        RECEIVED: 1
        RECEIVED: 2
        RECEIVED: 3
        RECEIVED: 4
        RECEIVED: 5
     */
}

/**
 * 🔥 INFO skip()
 *
 * The **skip()** operator does the opposite of the **take()** operator.
 * It will ignore the specified number of emissions and then emit the ones that follow.
 * If I wanted to skip the first **90** emissions of an **Observable**, I could use this operator,
 */

private fun testSkipOperator() {

    Observable.range(1, 95)
        .skip(90)
        .subscribe { i -> println("RECEIVED: $i") }

    /*
        Prints:
        RECEIVED: 91
        RECEIVED: 92
        RECEIVED: 93
        RECEIVED: 94
        RECEIVED: 95
     */
}

/**
 *  🔥 INFO takeWhile()
 *
 *  Another variant of the **take()** operator is the **takeWhile()** operator,
 *  which takes emissions while a condition derived from each emission is true.
 *  The following example will keep taking emissions while emissions are less than 5.
 *  **The moment it encounters one that is not**, it will call the **onComplete()** function and dispose of this:
 */
private fun testTakeWhileOperator() {

    Observable.range(1, 100)
        .takeWhile { i -> i < 5 }
        .subscribe { i -> println("RECEIVED: $i") }

    /*
        Prints:
        RECEIVED: 1
        RECEIVED: 2
        RECEIVED: 3
        RECEIVED: 4
     */

    Observable.range(2, 100)
        .takeWhile { i -> i % 2 == 0 }
        .subscribe { i -> println("RECEIVED Mod2: $i") }

    /*
        Prints:
        RECEIVED Mod2: 2
     */
}

/**
 * 🔥 INFO skipWhile()
 *
 * **skipWhile()** function will keep skipping emissions while they qualify with a condition.
 * The moment that condition no longer qualifies, the emissions will start going through.
 * In the following code, we skip emissions as long as they are less than or equal to 95.
 */
private fun testSkipWhileOperator() {

    Observable.range(1, 100)
        .skipWhile { i -> i <= 95 }
        .subscribe { i -> println("RECEIVED: $i") }

    /*
        Prints:
        RECEIVED: 96
        RECEIVED: 97
        RECEIVED: 98
        RECEIVED: 99
        RECEIVED: 100
     */

}

/**
 * 🔥 INFO distinct()
 *
 * The **distinct()** operator will emit each unique emission,
 * but it will suppress any duplicates that follow.
 * Equality is based on **hashCode()/equals()** implementation of the emitted objects.
 * If we wanted to emit the distinct lengths of a string sequence.
 */
private fun testDistinctOperator() {
    Observable.just(
        "Alpha", "Beta", "Gamma", "Delta", "Epsilon"
    )
        .distinct {
            it.length
        }
        .subscribe { i -> println("RECEIVED: $i") }

    /*
        Distinction is based on length of the strings.
        Prints:
        RECEIVED: Alpha
        RECEIVED: Beta
        RECEIVED: Epsilon
     */
}

/**
 * You can also add a lambda argument that maps each emission to a key used for equality logic.
 * This allows the emissions, but not the key, to go forward while using the key for distinct logic.
 * For instance, we can key off each string's length and use it for uniqueness,
 * but emit the strings rather than their lengths:
 */
private fun testDistinctOperator2() {
    Observable.just(
        "Alpha", "Beta", "Gamma", "Delta", "Epsilon"
    )

        .distinct {
            it.length
        }
        .map { it -> it.length }
        .subscribe { i -> println("RECEIVED: $i") }
    /*
        Prints:
        RECEIVED: 5
        RECEIVED: 4
        RECEIVED: 7
     */
}

private fun testDistinctOperator3() {
    Observable.just("Acid", "Black", "Blue", "Berry", "Cyan", "Dark")
        .distinct() {
            it.first()
        }
        .subscribe { i -> println("RECEIVED: $i") }
}

/**
 * 🔥 INFO distinctUntilChanged()
 *
 * The **distinctUntilChanged()** function will ignore duplicate consecutive emissions.
 * It is a helpful way to ignore repetitions until they change.
 * If the same value is being emitted repeatedly, all the duplicates will
 * be ignored until a new value is emitted.
 * Duplicates of the next value will be ignored until it changes again, and so on.
 */
private fun testDistinctUntilChanged() {

    Observable
        .just(1, 1, 1, 2, 2, 3, 3, 2, 1, 1)
        .distinctUntilChanged()
        .subscribe { i -> println("RECEIVED: $i") }

    /*
        Prints:
        RECEIVED: 1
        RECEIVED: 2
        RECEIVED: 3
        RECEIVED: 2
        RECEIVED: 1
     */

}

/**
 *  🔥 INFO elementAt()
 *
 *  You can get a specific emission by its index specified by a Long, starting at 0.
 *  After that item is found and emitted, **onComplete()** will be called and the subscription will be disposed of.
 */
private fun testElementAtOperator() {

    val maybe: Maybe<String> = Observable.just(
        "Alpha", "Beta", "Zeta", "Eta", "Gamma",
        "Delta"
    )
        .elementAt(3)

    /*
        Prints:
       onSuccess() RECEIVED: Eta

     */

    maybe.subscribe(
        { i -> println("onSuccess() RECEIVED: $i") },
        { error -> println("onError $error") },
        { println("onComplete") })

    /*
        If element index is greater than length it only invokes onComplete()
     */

}
