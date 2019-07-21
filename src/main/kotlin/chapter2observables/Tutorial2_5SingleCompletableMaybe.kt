package chapter2observables

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

fun main() {

//        testSingle()
//        testSingleFromObservable()

    testMaybe()
//        testMaybeFromObservable()

}

private fun testSingle() {
    val hello = Single.just("Hello")
        .map<Int> { it.length }
        .subscribe(
            // onSuccess
            { s -> println("onSuccess " + s!!) },
            // onError
            { error -> println("onError() " + error.message) }
        )


    Single.just("").subscribe(object : SingleObserver<String> {
        override fun onSubscribe(d: Disposable) {

        }

        override fun onSuccess(s: String) {

        }

        override fun onError(e: Throwable) {

        }
    })
}

private fun testSingleFromObservable() {
    val source = Observable.just("Alpha", "Beta", "Gamma")

    source.first("Nil") //returns a Single
        .subscribe(Consumer<String> { println(it) })
}


private fun testMaybe() {

    // has emission
    val presentSource = Maybe.just(100)
    presentSource.subscribe(
        { s -> println("Process 1 received: " + s!!) },
        { it.printStackTrace() },
        { println("Process 1 done!") }
    )

    // has emission
    //        Maybe<Integer> emptySource = Maybe.just(12);

    //no emission
    val emptySource = Maybe.empty<Int>()

    emptySource.subscribe(
        { s -> println("Process 2 received: " + s!!) },
        { it.printStackTrace() },
        { println("Process 2 done!") }
    )


}


private fun testMaybeFromObservable() {

    val source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

    source
        .firstElement() // Transforms Observable into Maybe
        .subscribe(
            { s -> println("RECEIVED $s") },
            { it.printStackTrace() },
            { println("Done!") }
        )

}