package chapter4combiningobservables

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import model.Person
import java.lang.Thread.sleep
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit


fun main() {

    // INFO zip
//    testZipOperator()
//    testZipOperatorInterval()
//    testZipOperatorInterval2()
//    testZipOperatorInterval3()

    // INFO zipWith
//    testZipWithOperator()

    testZipOperatorAndFlatMap()
}


/**
 * 🔥 INFO zip
 *
 * Zipping allows you to take an emission from each **Observable** source and combine it into a single emission.
 * Each **Observable** can emit a **different type**, but you can combine these different emitted types **into a single emission**.
 *
 * If we have an **Observable<String>** and an **Observable<Integer>**,
 * we can zip each **String** and **Integer** together in a one-to-one pairing and concatenate it with a lambda.
 */
private fun testZipOperator() {

    val source1 = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .doOnNext {
            println("🚗source1 doOnNext() $it")
        }
        .doOnComplete {
            println("🚗source1 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🚗source1 doOnDispose()")
        }
    val source2 = Observable.range(1, 6)
        .doOnNext {
            println("🤑source2 doOnNext() $it")
        }
        .doOnComplete {
            println("🤑source2 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🤑source2 doOnDispose()")
        }

    Observable.zip(source1, source2, BiFunction { str: String, integer: Int ->
        "$str-$integer"
    })
        .doOnComplete {
            println("🚙zip() doOnComplete()")
        }
        .subscribe {
            println("🚙zip() onNext() $it")
        }


    /*
        Prints:
        Alpha-1
        Beta-2
        Gamma-3
        Delta-4
        Epsilon-5
     */

    /*
      INFO
        You can pass up to nine Observable instances to the Observable.zip() factory.
        If you need more than that, you can pass an Iterable<Observable<T>> or use zipArray()
        to provide an Observable[] array. Note that if one or more sources are producing emissions faster than another,
        zip() will queue up those rapid emissions as they wait on the slower source to provide emissions.
        This could cause undesirable performance issues as each source queues in memory.
     */

    /*
    Prints:
    🚗source1 doOnNext() Alpha
    🚗source1 doOnNext() Beta
    🚗source1 doOnNext() Gamma
    🚗source1 doOnNext() Delta
    🚗source1 doOnNext() Epsilon
    🚗source1 doOnComplete()
    🤑source2 doOnNext() 1
    🚙zip() onNext() Alpha-1
    🤑source2 doOnNext() 2
    🚙zip() onNext() Beta-2
    🤑source2 doOnNext() 3
    🚙zip() onNext() Gamma-3
    🤑source2 doOnNext() 4
    🚙zip() onNext() Delta-4
    🤑source2 doOnNext() 5
    🚙zip() onNext() Epsilon-5
    🔜🚗source1 doOnDispose()
    🔜🤑source2 doOnDispose()
    🚙zip() doOnComplete()
 */
}

/**
 * Zipping can also be helpful in slowing down emissions using **Observable.interval()**.
 * Here, we zip each string with a **1-second interval**.
 * This will slow each string emission by one second, but keep in mind the five string emissions
 * will likely be queued as they wait for an interval emission to pair with:
 */
private fun testZipOperatorInterval() {


    val source1 = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .doOnNext {
            println("🚗source1 doOnNext() $it")
        }
        .doOnComplete {
            println("🚗source1 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🚗source1 doOnDispose()")
        }
    val source2 = Observable.interval(1, TimeUnit.SECONDS)
        .doOnNext {
            println("🤑source2 doOnNext() $it")
        }
        .doOnComplete {
            println("🤑source2 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🤑source2 doOnDispose()")
        }

    Observable.zip(source1, source2, BiFunction { str: String, long: Long ->
        str
    })
        .doOnComplete {
            println("🚙zip() doOnComplete()")
        }
        .subscribe {
            println("🚙zip() onNext() Received $it at ${LocalTime.now()}")
        }

    sleep(10_000)


    /*
        Prints:
        🚗source1 doOnNext() Alpha
        🚗source1 doOnNext() Beta
        🚗source1 doOnNext() Gamma
        🚗source1 doOnNext() Delta
        🚗source1 doOnNext() Epsilon
        🚗source1 doOnComplete()
        🤑source2 doOnNext() 0
        🚙zip() onNext() Received Alpha at 20:45:21.976670
        🤑source2 doOnNext() 1
        🚙zip() onNext() Received Beta at 20:45:22.946160
        🤑source2 doOnNext() 2
        🚙zip() onNext() Received Gamma at 20:45:23.949292
        🤑source2 doOnNext() 3
        🚙zip() onNext() Received Delta at 20:45:24.950623
        🤑source2 doOnNext() 4
        🚙zip() onNext() Received Epsilon at 20:45:25.945386
        🔜🚗source1 doOnDispose()
        🔜🤑source2 doOnDispose()
        🚙zip() doOnComplete()
     */
}


private fun testZipOperatorInterval2() {


    val source1 = Observable.interval(300, TimeUnit.MILLISECONDS)
        .doOnNext {
            println("🚗source1 doOnNext() $it")
        }
        .doOnComplete {
            println("🚗source1 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🚗source1 doOnDispose()")
        }

    val source2 = Observable.interval(1, TimeUnit.SECONDS)
        .doOnNext {
            println("🤑source2 doOnNext() $it")
        }
        .doOnComplete {
            println("🤑source2 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🤑source2 doOnDispose()")
        }

    Observable.zip(source1, source2, BiFunction { l1: Long, l2: Long ->
        "SOURCE 1: $l1 SOURCE 2: $l2"
    })
        .doOnComplete {
            println("🚙zip() doOnComplete()")
        }
        .subscribe {
            println("🚙zip() onNext() $it")
        }
    sleep(3000)

    /*
        Prints:
        🚗source1 doOnNext() 0
        🚗source1 doOnNext() 1
        🚗source1 doOnNext() 2
        🤑source2 doOnNext() 0
        🚙zip() onNext() SOURCE 1: 0 SOURCE 2: 0
        🚗source1 doOnNext() 3
        🚗source1 doOnNext() 4
        🚗source1 doOnNext() 5
        🤑source2 doOnNext() 1
        🚙zip() onNext() SOURCE 1: 1 SOURCE 2: 1
        🚗source1 doOnNext() 6
        🚗source1 doOnNext() 7
        🚗source1 doOnNext() 8
        🚗source1 doOnNext() 9
        🤑source2 doOnNext() 2
        🚙zip() onNext() SOURCE 1: 2 SOURCE 2: 2
     */


}


private fun testZipOperatorInterval3() {


    val source1 = Observable.interval(300, TimeUnit.MILLISECONDS)
        .take(4)
        .doOnNext {
            println("🚗source1 doOnNext() $it")
        }
        .doOnComplete {
            println("🚗source1 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🚗source1 doOnDispose()")
        }

    val source2 = Observable.interval(1, TimeUnit.SECONDS)
        .take(4)
        .doOnNext {
            println("🤑source2 doOnNext() $it")
        }
        .doOnComplete {
            println("🤑source2 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🤑source2 doOnDispose()")
        }


    val source3 = Observable.interval(200, TimeUnit.MILLISECONDS)
        .take(4)
        .doOnNext {
            println("🗿source3 doOnNext() $it")
        }
        .doOnComplete {
            println("🗿source3 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🗿source3 doOnDispose()")
        }


    Observable.zip(source1, source2, source3, Function3() { l1: Long, l2: Long, l3: Long ->
        "SOURCE 1: $l1 SOURCE 2: $l2 SOURCE 3: $l3"
    })
        .doOnComplete {
            println("🚙zip() doOnComplete()")
        }
        .subscribe {
            println("🚙zip() onNext() $it")
        }


    sleep(50_000)

    /*
        🗿source3 doOnNext() 0
        🚗source1 doOnNext() 0
        🗿source3 doOnNext() 1
        🗿source3 doOnNext() 2
        🚗source1 doOnNext() 1
        🗿source3 doOnNext() 3
        🗿source3 doOnComplete()
        🚗source1 doOnNext() 2
        🤑source2 doOnNext() 0
        🚙zip() onNext() SOURCE 1: 0 SOURCE 2: 0 SOURCE 3: 0
        🚗source1 doOnNext() 3
        🚗source1 doOnComplete()
        🤑source2 doOnNext() 1
        🚙zip() onNext() SOURCE 1: 1 SOURCE 2: 1 SOURCE 3: 1
        🤑source2 doOnNext() 2
        🚙zip() onNext() SOURCE 1: 2 SOURCE 2: 2 SOURCE 3: 2
        🤑source2 doOnNext() 3
        🚙zip() onNext() SOURCE 1: 3 SOURCE 2: 3 SOURCE 3: 3
        🔜🚗source1 doOnDispose()
        🔜🤑source2 doOnDispose()
        🔜🗿source3 doOnDispose()
        🚙zip() doOnComplete()
        🤑source2 doOnComplete()
     */


}


private fun testZipWithOperator() {

    val letters = listOf("A", "B", "C", "D", "E")


    Observable.fromIterable(letters)
        .zipWith(
            Observable.range(1, Integer.MAX_VALUE),
            BiFunction { string: String, index: Int ->
                "$index-$string"
            })
        .doOnComplete {
            println("🚗zip() doOnComplete()")
        }
        .subscribe {
            println("🚙zip() onNext() $it")
        }

    /*
        Prints:
        🚙zip() onNext() 1-A
        🚙zip() onNext() 2-B
        🚙zip() onNext() 3-C
        🚙zip() onNext() 4-D
        🚙zip() onNext() 5-E
        🚗zip() doOnComplete()
     */

}


private fun testZipOperatorAndFlatMap() {

    val source1 = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .doOnNext {
            println("🚗source1 doOnNext() $it")
        }
        .doOnComplete {
            println("🚗source1 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🚗source1 doOnDispose()")
        }

    val source2 = Observable.range(1, 6)
        .doOnNext {
            println("🤑source2 doOnNext() $it")
        }
        .doOnComplete {
            println("🤑source2 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🤑source2 doOnDispose()")
        }

    Observable.zip(source1, source2,
        BiFunction { str: String, integer: Int ->
            "$str-$integer"
        }
    )
        .flatMap {
            getUser(it, it)
        }
        .doOnComplete {
            println("🚙zip() doOnComplete()")
        }
        .subscribe {
            println("🚙zip() onNext() $it")
        }

    /*
        Prints:
        🚗source1 doOnNext() Alpha
        🚗source1 doOnNext() Beta
        🚗source1 doOnNext() Gamma
        🚗source1 doOnNext() Delta
        🚗source1 doOnNext() Epsilon
        🚗source1 doOnComplete()
        🤑source2 doOnNext() 1
        🚙zip() onNext() Person(firstName=Alpha-1, surName=Alpha-1)
        🤑source2 doOnNext() 2
        🚙zip() onNext() Person(firstName=Beta-2, surName=Beta-2)
        🤑source2 doOnNext() 3
        🚙zip() onNext() Person(firstName=Gamma-3, surName=Gamma-3)
        🤑source2 doOnNext() 4
        🚙zip() onNext() Person(firstName=Delta-4, surName=Delta-4)
        🤑source2 doOnNext() 5
        🚙zip() onNext() Person(firstName=Epsilon-5, surName=Epsilon-5)
        🔜🚗source1 doOnDispose()
        🔜🤑source2 doOnDispose()
        🚙zip() doOnComplete()
     */


    sleep(15000)
}

private fun getUser(name: String, surName: String): Observable<Person> {
    val random = Random().nextInt(1000) + 1000
    sleep(2000)
    return Observable.just(Person(name, surName))
}
