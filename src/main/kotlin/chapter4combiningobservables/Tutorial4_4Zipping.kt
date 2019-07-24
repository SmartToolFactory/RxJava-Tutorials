package chapter4combiningobservables

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.lang.Thread.sleep
import java.time.LocalTime
import java.util.concurrent.TimeUnit


fun main() {

    testZipOperator()

    testZipOperatorInterval()
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

    sleep(7000)


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