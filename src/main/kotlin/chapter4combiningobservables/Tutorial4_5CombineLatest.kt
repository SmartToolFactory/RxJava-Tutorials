package chapter4combiningobservables

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit


fun main() {
    // INFO combineLatest
//    testCombineLatestOperator()

    testCombineLatestOperatorInterval()

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