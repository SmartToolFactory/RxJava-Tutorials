package chapter3basicoperators

import io.reactivex.Observable
import io.reactivex.ObservableSource
import java.lang.RuntimeException


fun main() {

    // INFO onError
//    testOnErrorReturnOperator()

//     INFO onErrorReturnItem
//    testOnErrorReturnItemOperator()

//     INFO error is caught with try-catch block
//    testErrorWithTryCatch()

//     🔥 INFO onErrorResumeNext
//    testOnErrorResumeNextOperator()
//    testOnErrorResumeNextOperatorWithRepeatedEmission()
//    testOnErrorResumeNextWithNewSequence()
//    Important 🔥🔥
//    testOnErrorResumeNextWithCondition()
//    Important 🔥🔥
    testOnErrorResumeNextWithConditionAndObservableSource()


//     INFO retry
//    testRetryOperator()
}


/**
 * 🔥 INFO onError()
 *
 *When you want to resort to a default value when an exception occurs,
 * you can use onErrorReturnItem(). If we want to emit -1 when an exception occurs, we can do it like this
 *
 */
private fun testOnErrorReturnOperator() {

    println("🔧 testOnErrorOperator()")

    // 🔥🔥 onErrorReturn should be after map method(down stream) for error to be caught
    // 🔥🔥🔥 doOnError() should be called after the method that throws error and before onErrorReturn

    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .map { i -> 10 / i }
        .onErrorReturnItem(-1)
        .subscribe(
            { i ->
                println("RECEIVED: $i")
            },
            { e ->
                println("RECEIVED ERROR: $e")
            }
        )
    /*
        Prints:
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        RECEIVED: -1
     */

    /*
      INFO
        You can also supply Function<Throwable,T> to dynamically produce the value using a lambda
     */

    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .map { i -> 10 / i }
        .doOnError {
            println("doOnError() throwable: ${it.message}")
        }
        .onErrorReturn { e -> -1 }

        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") }
        )

    /*
        Prints:
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        doOnError() throwable: / by zero
        RECEIVED: -1
     */

}

/**
 * 🔥 INFO
 *
 *  Returns T instead of Throwable.
 */
private fun testOnErrorReturnItemOperator() {

    println("🔧 testOnErrorReturnItemOperator()")


    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .map { i -> 10 / i }
        .onErrorReturnItem(-1)
        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") }
        )
    /*
        Prints:
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        RECEIVED: -1
     */

}

/**
 *
 * Note that even though we emitted -1 to handle the error, the sequence still terminated after that.
 * We did not get the 3, 2, or 8 that was supposed to follow. If you want to resume emissions,
 * you will just want to handle the error within the map() operator where the error can occur.
 * You would do this in lieu of onErrorReturn() or **onErrorReturnItem()**:
 *
 */

private fun testErrorWithTryCatch() {

    println("🔧 testErrorWithTryAndCatch()")

    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .map { i ->
            try {
                10 / i
            } catch (e: ArithmeticException) {
                -1
            }
        }

        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") }
        )

    /*
        Prints:
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        RECEIVED: -1
        RECEIVED: 3
        RECEIVED: 5
        RECEIVED: 1
     */
}


/**
 * 🔥 INFO onErrorResumeNext()
 *
 * Similar to **onErrorReturn()** and **onErrorReturnItem()**, **onErrorResumeNext()** is very similar.
 * The only difference is that it **accepts another Observable as a parameter** to emit potentially multiple values,
 * not a single value, in the event of an exception.
 *
 * This is somewhat contrived and likely has no business use case, but we can emit three -1 emissions in the event of an error:
 *
 */
private fun testOnErrorResumeNextOperator() {

    println("🔧 testOnErrorResumeNextOperator()")
    println("With Observable.just()")

    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .map { i -> 10 / i }
        .onErrorResumeNext(Observable.just(-1).repeat(3))
        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") },
            { println("💀 subscribe()-> onComplete()")}
        )

    /*
        Prints:
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        RECEIVED: -1
        RECEIVED: -1
        RECEIVED: -1
        💀 subscribe()-> onComplete()
     */

    // 🔥 INFO Observable.empty does not emit any value but lets onComplete in subscribe to be called
    println("🔧 With Observable.empty()")

    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .map { i -> 10 / i }
        .onErrorResumeNext(Observable.empty())
        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") },
            { println("🤬 subscribe()-> onComplete()")}
        )


    /*
        Prints:
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        🤬 subscribe()-> onComplete()
     */

}

/**
 * Similar to **onErrorReturn()**, you can provide a F**unction<Throwable,Observable<T>>** lambda
 * to produce an Observable dynamically from the emitted Throwable, as shown in the code snippet:
 */
private fun testOnErrorResumeNextOperatorWithRepeatedEmission() {

    println("🔧 testOnErrorResumeNextOperatorAndContinue()")

    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .map { i -> 10 / i }
        .onErrorResumeNext { e: Throwable -> Observable.just(-1).repeat(3) }
        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") }
        )

    /*
        Prints:
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        RECEIVED: -1
        RECEIVED: -1
        RECEIVED: -1
     */
}

/**
 * Returns a new sequence when an exception occurs.
 * doOnError method can be used to do something in the mean time
 */
private fun testOnErrorResumeNextWithNewSequence() {

    // INFO With a new sequence
    println("🔧 testOnErrorResumeNextWithNewSequence()")

    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .map { i -> 10 / i }
        .doOnError {
            println("doOnError() throwable: ${it.message}")
        }
        .onErrorResumeNext(Observable.just(100, 200, 300))
        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") }
        )

    /*
        Prints:
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        doOnError() throwable: / by zero
        RECEIVED: 100
        RECEIVED: 200
        RECEIVED: 300
     */

}

private fun testOnErrorResumeNextWithCondition() {

    println("🔧 testOnErrorResumeNextWithCondition()")

    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .map { i -> 10 / i }
        .doOnError {
            println("doOnError() throwable: ${it.message}")
        }
        .onErrorResumeNext { e: Throwable ->
            when (e) {
                is ArithmeticException -> Observable.just(10, 20, 30)
                else -> Observable.just(100, 200, 300)
            }
        }
        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") }
        )

    /*
        Prints:

        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        doOnError() throwable: / by zero
        RECEIVED: 10
        RECEIVED: 20
        RECEIVED: 30

     */

}

private fun testOnErrorResumeNextWithConditionAndObservableSource() {

    println("🔧 testOnErrorReturnWithConditionAndObservableSource()")

    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .map {
            if (it == 0) throw ArithmeticException("Can not divide by Zero")
            it
        }
        .doOnError {
            println("doOnError() throwable: ${it.message}")
        }
        .onErrorResumeNext { throwable: Throwable ->

            if (throwable is NullPointerException) {
                return@onErrorResumeNext ObservableSource<Int> {
                    it.onNext(400)
                }
            } else {
                return@onErrorResumeNext ObservableSource<Int> {
                    it.onNext(500)
                }
            }

        }
        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") }
        )

    /*
        Prints:
        RECEIVED: 5
        RECEIVED: 2
        RECEIVED: 4
        doOnError() throwable: Can not divide by Zero
        RECEIVED: 500
     */
}



/**
 * 🔥 INFO retry()
 *
 * Another way to attempt recovery is to use the retry() operator, which has several parameter overloads.
 * It will re-subscribe to the preceding Observable and, hopefully, not have the error again.
 * If you call retry() with no arguments, it will resubscribe an infinite number of times for each error.
 * You need to be careful with retry() as it can have chaotic effects.
 *
 *
 */
private fun testRetryOperator() {

    println("🔧 testRetryOperator()")


    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .map { i -> 10 / i }
        .retry(2)
        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") }
        )

    /*
        Prints:
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        RECEIVED ERROR: java.lang.ArithmeticException: / by zero
     */

    /*
     INFO
        You can also provide Predicate<Throwable> or BiPredicate<Integer,Throwable>
        to conditionally control when retry() is attempted.
        The retryUntil() operator will allow retries while a given BooleanSupplier lambda is false.
        There is also an advanced retryWhen() operator that supports advanced composition
        for tasks such as delaying retries.
     */
}