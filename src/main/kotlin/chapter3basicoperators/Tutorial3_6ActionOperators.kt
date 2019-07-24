package chapter3basicoperators

import io.reactivex.Observable

fun main() {

    // INFO doOnNext
//    testDoOnNextOperator()

    // INFO doOnComplete
//    testDoOnCompleteOperator()

    // INFO doOnError
//    testDoOnErrorOperator()

    // INFO doOnSubscribe - doOnDispose
    testDoOnSubscribeAndDoOnDisposeOperator()


    // doOnSuccess
    testDoOnSuccessOperator()
}

/**
 * 🔥 INFO doOnNext()
 *
 * The **doOnNext()** operator allows you to peek at each emission coming out of an operator and going into the next.
 * This operator does not affect the operation or transform the emissions in any way.
 *
 * We just create a side-effect for each event that occurs at that point in the chain.
 * For instance, we can perform an action with each string before it is mapped to its length.
 * In this case, we will just print them by providing a **Consumer<T>** lambda:
 *
 */
private fun testDoOnNextOperator() {

    Observable.just(
        "Alpha", "Beta", "Gamma", "Delta", "Epsilon"
    )
        .doOnNext { s -> println("doOnNext(): Processing: $s") }
        .doAfterNext { s -> println("doAfterNext(): $s") }
        .map(String::length)
        .subscribe { i -> println("onNext(): Received: $i") }

    /*
        Prints:
    doOnNext(): Processing: Alpha
    onNext(): Received: 5
    doAfterNext(): Alpha
    doOnNext(): Processing: Beta
    onNext(): Received: 4
    doAfterNext(): Beta
    doOnNext(): Processing: Gamma
    onNext(): Received: 5
    doAfterNext(): Gamma
    doOnNext(): Processing: Delta
    onNext(): Received: 5
    doAfterNext(): Delta
    doOnNext(): Processing: Epsilon
    onNext(): Received: 7
    doAfterNext(): Epsilon
     */
}

/**
 * 🔥 INFO doOnComplete()
 *
 *
 *
 */
private fun testDoOnCompleteOperator() {
    Observable.just(
        "Alpha", "Beta", "Gamma", "Delta", "Epsilon"
    )
        .doOnComplete { println("Source is done emitting!") }
        .map(String::length)
        .subscribe { i -> println("Received: $i") }

    /*
        Prints:
        Received: 5
        Received: 4
        Received: 5
        Received: 5
        Received: 7
        Source is done emitting!
     */
}

/**
 * 🔥 INFO doOnError()
 *
 * **onError()** will peek at the error being emitted up the chain, and you can perform an action with it.
 * This can be helpful to put between operators to see which one is to blame for an error:
 *
 */
private fun testDoOnErrorOperator() {

    Observable.just(5, 2, 4, 0, 3, 2, 8)
        .doOnError { e -> println("Source failed!") }
        .map { i -> 10 / i }
        .doOnError { e -> println("Division failed!") }
        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") }
        )

    /*
        Prints:
        RECEIVED: 2
        RECEIVED: 5
        RECEIVED: 2
        Division failed!
        RECEIVED ERROR: java.lang.ArithmeticException: / by zero
     */
}

/**
 * 🔥 INFO doOnSubscribe()
 *
 *
 *
 */
private fun testDoOnSubscribeAndDoOnDisposeOperator() {

    val disposable = Observable.just(
        "Alpha", "Beta", "Gamma", "Delta", "Epsilon"
    )
        .doOnSubscribe { disposable -> println("Subscribing! ${disposable.javaClass.simpleName}") }
        .doOnDispose { println("Disposing!") }
        .subscribe { i -> println("RECEIVED: $i") }

    disposable.dispose()

    // 🔥 ?? dispose is not called, WHY?
}


/**
 * 🔥 INFO doOnSuccess()
 *
 * **Maybe** and **Single** types do not have an **onNext()** event but rather an **onSuccess()** operator to pass a single emission.
 * Therefore, there is no **doOnNext()** operator on either of these types,
 * as observed in the following code snippet, but rather a **doOnSuccess()** operator
 *
 */
private fun testDoOnSuccessOperator() {

    Observable.just(5, 3, 7, 10, 2, 14)
        .reduce { total, next -> total + next }
        .doOnSuccess { i -> println("Emitting: $i") }
        .subscribe { i -> println("Received: $i") }

    /*
        Prints:
        Emitting: 41
        Received: 41
     */

}