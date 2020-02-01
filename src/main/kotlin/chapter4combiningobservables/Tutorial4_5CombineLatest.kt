package chapter4combiningobservables

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit


fun main() {
    // INFO combineLatest
//    testCombineLatestOperator()

//    testCombineLatestOperatorInterval()

    // INFO withLatestFrom
//    testWithLatestFromOperator()
//    testWithLatestFromOperatorInterval()


    testWithLatestFromOperatorInterval2()


}


/**
 * // 🔥 INFO combineLatest
 *
 * The **Observable.combineLatest()** factory is somewhat similar to **zip()**,
 * but for every emission that fires from one of the sources,
 * it will immediately couple up with the **latest emission** from every other source.
 * It will not queue up unpaired emissions for each source, but rather cache and pair the latest one.
 *
 */
private fun testCombineLatestOperator() {

    println("testCombineLatestOperator()")

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

    Observable.combineLatest(source1, source2, BiFunction { str: String, integer: Int ->
        "$str-$integer"
    })
        .doOnComplete {
            println("🚙combineLatest() doOnComplete()")
        }
        .subscribe {
            println("🚙combineLatest() onNext() $it")
        }

    // WARNING combineLatest couple only the latest emissions
    /*
        Prints:
        🚗source1 doOnNext() Alpha
        🚗source1 doOnNext() Beta
        🚗source1 doOnNext() Gamma
        🚗source1 doOnNext() Delta
        🚗source1 doOnNext() Epsilon
        🚗source1 doOnComplete()
        🤑source2 doOnNext() 1
        🚙combineLatest() onNext() Epsilon-1
        🤑source2 doOnNext() 2
        🚙combineLatest() onNext() Epsilon-2
        🤑source2 doOnNext() 3
        🚙combineLatest() onNext() Epsilon-3
        🤑source2 doOnNext() 4
        🚙combineLatest() onNext() Epsilon-4
        🤑source2 doOnNext() 5
        🚙combineLatest() onNext() Epsilon-5
        🤑source2 doOnNext() 6
        🚙combineLatest() onNext() Epsilon-6
        🤑source2 doOnComplete()
        🚙combineLatest() doOnComplete()
     */
}

/**
 *
 */
private fun testCombineLatestOperatorInterval() {


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

    Observable.combineLatest(source1, source2, BiFunction { l1: Long, l2: Long ->
        "SOURCE 1: $l1 SOURCE 2: $l2"
    })
        .doOnComplete {
            println("🚙combineLatest() doOnComplete()")
        }
        .subscribe {
            println("🚙combineLatest() onNext() $it")
        }
    sleep(3000)

    /*
        Prints:
        🚗source1 doOnNext() 0
        🚗source1 doOnNext() 1
        🚗source1 doOnNext() 2
        🤑source2 doOnNext() 0
        🚙combineLatest() onNext() SOURCE 1: 2 SOURCE 2: 0
        🚗source1 doOnNext() 3
        🚙combineLatest() onNext() SOURCE 1: 3 SOURCE 2: 0
        🚗source1 doOnNext() 4
        🚙combineLatest() onNext() SOURCE 1: 4 SOURCE 2: 0
        🚗source1 doOnNext() 5
        🚙combineLatest() onNext() SOURCE 1: 5 SOURCE 2: 0
        🤑source2 doOnNext() 1
        🚙combineLatest() onNext() SOURCE 1: 5 SOURCE 2: 1
        🚗source1 doOnNext() 6
        🚙combineLatest() onNext() SOURCE 1: 6 SOURCE 2: 1
        🚗source1 doOnNext() 7
        🚙combineLatest() onNext() SOURCE 1: 7 SOURCE 2: 1
        🚗source1 doOnNext() 8
        🚙combineLatest() onNext() SOURCE 1: 8 SOURCE 2: 1
        🚗source1 doOnNext() 9
        🤑source2 doOnNext() 2
        🚙combineLatest() onNext() SOURCE 1: 9 SOURCE 2: 1
        🚙combineLatest() onNext() SOURCE 1: 9 SOURCE 2: 2
     */

    /*
     INFO
      In simpler terms, when one source fires, it couples with the latest emissions from the others.
      Observable.combineLatest() is especially helpful in combining UI inputs,
      as previous user inputs are frequently irrelevant and only the latest is of concern.
     */
}


private fun testWithLatestFromOperator() {

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

    source1.withLatestFrom(source2, BiFunction { str: String, integer: Int ->
        "$str-$integer"
    })
        .doOnComplete {
            println("🚙withLatestFrom() doOnComplete()")
        }
        .subscribe {
            println("🚙withLatestFrom() onNext() $it")
        }

    /*
        Prints:
        🤑source2 doOnNext() 1
        🤑source2 doOnNext() 2
        🤑source2 doOnNext() 3
        🤑source2 doOnNext() 4
        🤑source2 doOnNext() 5
        🤑source2 doOnNext() 6
        🤑source2 doOnComplete()
        🚗source1 doOnNext() Alpha
        🚙withLatestFrom() onNext() Alpha-6
        🚗source1 doOnNext() Beta
        🚙withLatestFrom() onNext() Beta-6
        🚗source1 doOnNext() Gamma
        🚙withLatestFrom() onNext() Gamma-6
        🚗source1 doOnNext() Delta
        🚙withLatestFrom() onNext() Delta-6
        🚗source1 doOnNext() Epsilon
        🚙withLatestFrom() onNext() Epsilon-6
        🚗source1 doOnComplete()
        🔜🤑source2 doOnDispose()
        🚙withLatestFrom() doOnComplete()

     */

}

/**
 * 🔥 INFO withLatestFrom
 *
 * Similar to **Observable.combineLatest()**, but not exactly the same, is the **withLatestFrom()** operator.
 * It will map each T emission with the **latest values from other Observables and combine them**,
 * but it will **only take one emission from each of the other Observables**:
 */
private fun testWithLatestFromOperatorInterval() {

    val source1 = Observable.interval(1000, TimeUnit.MILLISECONDS)
        .doOnNext {
            println("🚗source1 doOnNext() $it")
        }
        .doOnComplete {
            println("🚗source1 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🚗source1 doOnDispose()")
        }

    val source2 = Observable.interval(300, TimeUnit.MILLISECONDS)
        .doOnNext {
            println("🤑source2 doOnNext() $it")
        }
        .doOnComplete {
            println("🤑source2 doOnComplete()")
        }
        .doOnDispose {
            println("🔜🤑source2 doOnDispose()")
        }


    source1.withLatestFrom(source2, BiFunction { l1: Long, l2: Long ->
        "SOURCE 1: $l1 SOURCE 2: $l2"
    })
        .doOnComplete {
            println("🚙withLatestFrom() doOnComplete()")
        }
        .subscribe {
            println("🚙withLatestFrom() onNext() $it")
        }
    sleep(3000)

    // This operator uses latest value of source1 and emits whenever a source 2 emits if source1 calls withLatestFrom

    /*
        Prints:

        // RESULT depends on Source1 🚗 after first emission 🤑 comes from Source2

        source1.withLatestFrom(source2,...)
        🚗source1 doOnNext() 0
        🚗source1 doOnNext() 1
        🚗source1 doOnNext() 2
        🤑source2 doOnNext() 0
        🚗source1 doOnNext() 3
        🚙withLatestFrom() onNext() SOURCE 1: 3 SOURCE 2: 0
        🚗source1 doOnNext() 4
        🚙withLatestFrom() onNext() SOURCE 1: 4 SOURCE 2: 0
        🚗source1 doOnNext() 5
        🚙withLatestFrom() onNext() SOURCE 1: 5 SOURCE 2: 0
        🤑source2 doOnNext() 1
        🚗source1 doOnNext() 6
        🚙withLatestFrom() onNext() SOURCE 1: 6 SOURCE 2: 1
        🚗source1 doOnNext() 7
        🚙withLatestFrom() onNext() SOURCE 1: 7 SOURCE 2: 1
        🚗source1 doOnNext() 8
        🚙withLatestFrom() onNext() SOURCE 1: 8 SOURCE 2: 1
        🤑source2 doOnNext() 2
        🚗source1 doOnNext() 9
        🚙withLatestFrom() onNext() SOURCE 1: 9 SOURCE 2: 2


        source2.withLatestFrom(source1,...)

        // RESULT depends on Source2 🤑 after first emission 🚗 comes from Source1

        🚗source1 doOnNext() 0
        🚗source1 doOnNext() 1
        🚗source1 doOnNext() 2
        🤑source2 doOnNext() 0
        🚙withLatestFrom() onNext() SOURCE 1: 0 SOURCE 2: 2
        🚗source1 doOnNext() 3
        🚗source1 doOnNext() 4
        🚗source1 doOnNext() 5
        🤑source2 doOnNext() 1
        🚙withLatestFrom() onNext() SOURCE 1: 1 SOURCE 2: 5
        🚗source1 doOnNext() 6
        🚗source1 doOnNext() 7
        🚗source1 doOnNext() 8
        🤑source2 doOnNext() 2
        🚗source1 doOnNext() 9
        🚙withLatestFrom() onNext() SOURCE 1: 2 SOURCE 2: 8
     */

    /*
     INFO
      As you can see here, source2 emits every one second while source1 emits every 300 milliseconds.
      When you call withLatestFrom() on source2 and pass it source1,
      it will combine with the latest emission from source1
      but it does not care about any previous or subsequent emissions.

      You can pass up to four Observable instances of any varying types to withLatestFrom().
      If you need more than that, you can pass it an Iterable<Observable<T>>.
     */

    // WARNING It emits after taking the latest emission but emit value just after caller Observable has changed
}


fun testWithLatestFromOperatorInterval2() {

    val qrResult = BehaviorSubject.createDefault("")


    getPeriodicUpdate()
        .withLatestFrom(qrResult,
            BiFunction { time: Long, result: String ->

                if (time % 5.0 == 0.0) {
                    ""
                } else {
                    result
                }

            }
        )
        .distinctUntilChanged()
        .filter {
            !it.isNullOrBlank()
        }
        .subscribe {
            println("Result: $it")
        }


    val strList = listOf(
        "Hello5", "Hello5",
        "Hello5", "Hello5", "Hello3",
        "Hello3", "Hello3",
        "Hello2", "Hello2", "Hello3",
        "Hello5", "Hello3",
        "Hello4", "Hello4", "Hello1"

    )

    getPeriodicUpdate(400)
        .take((strList.size - 1).toLong())
        .subscribe {
            val str = strList[it.toInt()]
            qrResult.onNext(str)
        }


    sleep(10_000)

}

private fun getPeriodicUpdate(period: Long = 1000): Observable<Long> =
    Observable.interval(period, TimeUnit.MILLISECONDS).startWith(0)

