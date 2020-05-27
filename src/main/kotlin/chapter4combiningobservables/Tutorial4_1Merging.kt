package chapter4combiningobservables

import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import model.Person
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.TimeUnit


fun main() {

    // INFO merge
//    testMergeOperator()
//    testMergeArrayOperator()
//    testMergeOperatorWithList()
//    testMergeOperatorInterval()
//    testMergeOperatorIntervalWithFiniteSource()
    // INFO mergeWith
//    testMergeWithOperator()

    // INFO flatMap
//    testFlatMapOperator()
//    testFlatMapOperator2()

//    testFlatMapPerson()

//    testFlatMapVsConcatMap()
//    testFlatMapVsSwitchMap()

//    testFlatMapOperatorWithInterval()
}

/**
 * 🔥 INFO merge
 *
 * The **Observable.merge()** operator will take two or more **Observable<T>** sources emitting
 * the **same type T** and then consolidate them into a **single Observable<T>**.
 *
 * If we have only two to four Observable<T> sources to merge,
 * you can pass each one as an argument to the **Observable.merge()** factory.
 *
 */
private fun testMergeOperator() {

    val source1 = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
    val source2 = Observable.just("Zeta", "Eta", "Theta")

    Observable.merge(source1, source2)
        .subscribe { i -> println("RECEIVED: $i") }

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

    // 🔥🔥🔥 WARNING merge operator waits until first stream ends and merges next one after that
}


private fun testMergeArrayOperator() {

    val source1 = Observable.just("Alpha", "Beta");
    val source2 = Observable.just("Gamma", "Delta");
    val source3 = Observable.just("Epsilon", "Zeta");
    val source4 = Observable.just("Eta", "Theta");
    val source5 = Observable.just("Iota", "Kappa");

    Observable.mergeArray(source1, source2, source3, source4, source5)
        .subscribe(
            {
                println("🚗 onNext() it $it")
            },
            {

            })

    /*
        Prints:
        🚗 onNext() it Alpha
        🚗 onNext() it Beta
        🚗 onNext() it Gamma
        🚗 onNext() it Delta
        🚗 onNext() it Epsilon
        🚗 onNext() it Zeta
        🚗 onNext() it Eta
        🚗 onNext() it Theta
        🚗 onNext() it Iota
        🚗 onNext() it Kappa
     */

    // Waits each stream to end before moving to next one
}

private fun testMergeOperatorWithList() {

    val source1 = Observable.just("Alpha", "Beta")
    val source2 = Observable.just("Gamma", "Delta")
    val source3 = Observable.just("Epsilon", "Zeta")
    val source4 = Observable.just("Eta", "Theta")
    val source5 = Observable.just("Iota", "Kappa")

    val sources = listOf(source1, source2, source3, source4, source5)

    Observable.merge(sources).subscribe { i -> println("🚗 onNext(): $i") }

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

/**
 *
 * The **Observable.merge()** works with infinite Observables.
 * Since it will subscribe to all Observables and fire their emissions as soon as they are available,
 * you can merge multiple infinite sources into a single stream.
 * Here, we merge two **Observable.interval()** sources that emit at one second and 300 millisecond intervals, respectively.
 */
private fun testMergeOperatorInterval() {

    //emit every second
    val source1 = Observable.interval(1, TimeUnit.SECONDS)
        .map { l -> l + 1 } // emit elapsed seconds
        .map { l -> "Source1: $l seconds" }

    //emit every 300 milliseconds
    val source2 = Observable.interval(300, TimeUnit.MILLISECONDS)
        .map { l -> (l + 1) * 300 } // emit elapsed milliseconds
        .map { l -> "Source2: $l milliseconds" }

    //merge and subscribe
    Observable.merge(source1, source2)
        .subscribe {
            println(it)
        }

    //keep alive for 3 seconds
    sleep(3000)

    /*
        Prints:
        Source2: 300 milliseconds
        Source2: 600 milliseconds
        Source2: 900 milliseconds
        Source1: 1 seconds
        Source2: 1200 milliseconds
        Source2: 1500 milliseconds
        Source2: 1800 milliseconds
        Source1: 2 seconds
        Source2: 2100 milliseconds
        Source2: 2400 milliseconds
        Source2: 2700 milliseconds
        Source1: 3 seconds
     */

}

/**
 * Even if one source is infinite like interval it continues emission until canceled
 */
private fun testMergeOperatorIntervalWithFiniteSource() {

    //emit every second
    val source1 = Observable.interval(1, TimeUnit.SECONDS)
        .map { l -> l + 1 } // emit elapsed seconds
        .map { l -> "Source1: $l seconds" }

    val source2 = Observable.just(1, 2, 3, 4)

    //merge and subscribe
    Observable.merge(source1, source2)
        .doFinally {
            println("doFinally()")
        }
        .subscribe {
            println(it)
        }

    sleep(10_000)

    /*
        Prints:

        1
        2
        3
        4
        Source1: 1 seconds
        Source1: 2 seconds
        Source1: 3 seconds
        Source1: 4 seconds
        ...
     */

}

private fun testMergeWithOperator() {

    val source1 = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
    val source2 = Observable.just("Zeta", "Eta", "Theta")

    source1
        .mergeWith(source2)
        .doFinally {
            println("doOnFinally()")
        }
        .subscribe { i -> println("RECEIVED: $i") }

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
        doOnFinally()
     */

}


/**
 * 🔥 INFO flatMap
 *
 * The flatMap operator converts each item returned from a source observable into an independent observable
 * using the supplied function and then merges all the observables into a single observable.
 *
 * Say, we want to emit the characters from each string coming from **Observable<String>**.
 * We can use **flatMap()** to specify a **Function<T,Observable<R>>** lambda that maps each string to an **Observable<String>**,
 * which will emit the letters. Note that the mapped **Observable<R>** can emit any type **R**,
 * different from the source T emissions.
 *
 */
private fun testFlatMapOperator() {

//    val source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
//    source.flatMap { s ->
//        Observable.fromArray(*s.split("").dropLastWhile { it.isEmpty() }.toTypedArray())
//    }
//        .subscribe { it: String ->
//            println(it)
//        }

    val source = Observable.just("Alpha")

    source.flatMap { s ->
        Observable.fromArray(*s.split("".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
    }
        .subscribe {
            println(it)
        }

//    val source = Observable.just("521934/2342/FOXTROT", "21962/12112/78886 /TANGO", "283242/4542/WHISKEY/2348562")
//
//    source.flatMap { s -> Observable.fromArray(*s.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) }
//        .filter { s -> s.matches("[0-9]+".toRegex()) } //use regex to filter integers
//        .map { Integer.valueOf(it) }
//        .subscribe { println(it) }
}


private fun testFlatMapPerson() {

    val person = Person("James", "Bond")

    val observable = Observable.just(person)

//    observable.map {
//        "Name: ${person.firstName}, lastName: ${person.surName} "
//    }.subscribe {
//        println("Final String: $it")
//    }

    val query = "Unknown"
    println("Query started...")

    val observableQuery = Observable.just(query)

    observableQuery.flatMap { q ->
        sleep(5000)
        Observable.just(Person(q, "${q}son"))
    }.subscribe {
        println("Person FlatMap -> is: ${person.firstName}, ${person.surName}")
    }


    observableQuery.map { q ->
        sleep(5000)
        Person(q, "${q}son")
    }.subscribe {
        println("Person Map -> is: ${person.firstName}, ${person.surName}")
    }


}


private fun testFlatMapOperator2() {

    val listOfPeople = listOf("Alan", "Bob", "Cobb", "Dan", "Evan", "Finch")

    // 🔥 WARNING This one delays each emission
    Observable.fromIterable(listOfPeople)
        .flatMap {
            val delay = Random().nextInt(5)
            Observable.just(it)
                .map(String::toUpperCase)
                .delay(delay.toLong(), TimeUnit.SECONDS)
        }
        .subscribe {
            println("flatMap() $it")
        }


    // 🔥 WARNING This one delays the emission then emits all values at once

    Observable.fromIterable(listOfPeople)
        .map {
            it.toUpperCase()
        }
        .delay(Random().nextInt(5).toLong(), TimeUnit.SECONDS)
        .subscribe {
            println("with map() $it")
        }


    sleep(5000)


}

fun testSpreadOperatorWithVarArg(vararg items: String) {

    items.forEach {
        println("$it")
    }
}


private fun testFlatMapVsConcatMap() {

    println("testFlatMapVsConcatMap()")


    val items = arrayOf("a", "b", "c", "d", "e", "f")
    testSpreadOperatorWithVarArg(*items)

    val scheduler = TestScheduler()

    // 🔥 The * operator is known as the Spread Operator in Kotlin.
    // It can be applied to an Array before passing it into a function that accepts varargs.
    // It passes each element one by one
    Observable.fromArray(*items)
        .concatMap { s ->
            val delay = Random().nextInt(10)
            Observable.just(s + "x")
                .delay(delay.toLong(), TimeUnit.SECONDS, scheduler)
        }
        .toList()
        .subscribe { strings -> println("concatMap onNext() : $strings") }

    scheduler.advanceTimeBy(1, TimeUnit.MINUTES)

    /*
        Prints :
        onNext() [ax, bx, cx, dx, ex, fx]

     */


    Observable.fromArray(*items)
        .flatMap { s ->
            val delay = Random().nextInt(10)
            Observable.just(s + "x")
                .delay(delay.toLong(), TimeUnit.SECONDS, scheduler)
        }
        .toList()
        .subscribe { strings -> println("flatMap onNext() $strings") }

    scheduler.advanceTimeBy(1, TimeUnit.MINUTES)

    /*
        Prints randomly:
        onNext() [bx, ex, fx, ax, dx, cx]
     */

}

private fun testFlatMapVsSwitchMap() {

    println("testFlatMapVsSwitchMap()")


    val items = arrayOf("a", "b", "c", "d", "e", "f")


    val scheduler = TestScheduler()

    Observable.fromArray(*items)
        .flatMap { s ->
            val delay = Random().nextInt(10)
            Observable.just(s + "x")
                .delay(delay.toLong(), TimeUnit.SECONDS, scheduler)
        }
        .toList()
        .subscribe { strings -> println("flatMap onNext() : $strings") }

    scheduler.advanceTimeBy(1, TimeUnit.MINUTES)

    /*
         Prints randomly:
         flatMap onNext() [bx, ex, fx, ax, dx, cx]
      */


    Observable.fromArray(*items)
        .switchMap { s ->
            val delay = Random().nextInt(10)
            Observable.just(s + "x")
                .delay(delay.toLong(), TimeUnit.SECONDS, scheduler)
        }
        .toList()
        .subscribe { strings -> println("switchMap onNext() $strings") }

    scheduler.advanceTimeBy(1, TimeUnit.MINUTES)

    /*
         Prints:
         switchMap onNext() [fx]
     */

}

private fun testFlatMapOperatorWithInterval() {


    val intervalArguments = Observable.just(2, 3, 10, 7)

    intervalArguments.flatMap { i ->

        Observable.interval(i.toLong(), TimeUnit.SECONDS)
            .map { timer -> "${i}s interval: ${(timer + 1) * (i)} seconds  elapsed" }
            .doOnSubscribe {
                println("🔥doOnSubscribe(): ${it.javaClass.simpleName}")
            }

    }.doOnSubscribe {
        println("🥶doOnSubscribe(): ${it.javaClass.simpleName}")
    }
        .subscribe { println(it) }

    sleep(12000)

    /*
        Prints:
        🥶doOnSubscribe(): MergeObserver
        🔥doOnSubscribe(): MapObserver
        🔥doOnSubscribe(): MapObserver
        🔥doOnSubscribe(): MapObserver
        🔥doOnSubscribe(): MapObserver

        2s interval: 2 seconds  elapsed
        3s interval: 3 seconds  elapsed
        2s interval: 4 seconds  elapsed
        3s interval: 6 seconds  elapsed
        2s interval: 6 seconds  elapsed
        7s interval: 7 seconds  elapsed
        2s interval: 8 seconds  elapsed
        3s interval: 9 seconds  elapsed
        2s interval: 10 seconds  elapsed
        10s interval: 10 seconds  elapsed
        2s interval: 12 seconds  elapsed
        3s interval: 12 seconds  elapsed
     */
}