package chapter2observables

import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.lang.Thread.sleep

fun main() {

//        testSingle()
//        testSingleFromObservable()

//    testMaybe()
//        testMaybeFromObservable()


//    testCompletable()
    testCompletableWithAndThen()
}

/**
 *   Single<T> is essentially an Observable<T> that will only emit one item.
 *
 *   It works just like an Observable, but it is limited only to operators that make sense
 *   for a single emission.
 *
 *   It has its own SingleObserver interface as well:
 *
 *   interface SingleObserver<T> {
 *      void onSubscribe(Disposable d);
 *      void onSuccess(T value);
 *      void onError(Throwable error);
}
 */
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


/**
 * Maybe is just like a Single except that it allows no emission to occur at all (hence Maybe).
 *
 * MaybeObserver is much like a standard Observer, but onNext() is called onSuccess() instead:
 *
 * public interface MaybeObserver<T> {
 *     void onSubscribe(Disposable d);
 *     void onSuccess(T value);
 *     void onError(Throwable e);
 *     void onComplete();
 * }
 */
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
        { s -> println("onSuccess(): Process 2 received: " + s!!) },
        { "onError() ${it.message}"},
        { println("onComplete()") }
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

/**
 * Completable is simply concerned with an action being executed,
 * but it does not receive any emissions.
 *
 * Logically, it does not have onNext() or onSuccess() to receive emissions,
 * but it does have onError() and onComplete():
 *
 * interface CompletableObserver<T> {
 *     void onSubscribe(Disposable d);
 *     void onComplete();
 *     void onError(Throwable error);
 * }
 */
private fun testCompletable() {

    val completable = Completable.fromRunnable {
        println("Hello World")
    }

    completable.subscribe(object : Action {
        override fun run() {
            println("onComplete()")
        }

    })

}


/**
 * andThen operator returns an Observable which will subscribe to this Completable
 * and once that is completed then will subscribe to the next ObservableSource.
 *
 * An error event from this Completable will be propagated to the downstream subscriber
 * and will result in skipping the subscription of the Observable.
 */
private fun testCompletableWithAndThen() {

    val list = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

    val action = Action {
        list.forEach {
            println("Item: $it")
            sleep(400)
        }
    }


    // 🔥 After action of Completable is finished new Observable starts
    Completable.fromAction(action)
        .andThen(Observable.just(1,2,3))
        .doOnNext {
            println("DoOnNext() $it")
        }
        .doOnComplete {
            println("doOnComplete()")
        }
        .doOnTerminate {
            println("doOnTerminate()")
        }
        .doFinally {
            println("doFinally()")
        }
        .subscribe {
            println("subscribe() onNext(): $it")
        }

    /*
        Prints:
        Item: Alpha
        Item: Beta
        Item: Gamma
        Item: Delta
        Item: Epsilon
        DoOnNext() 1
        subscribe() onNext(): 1
        DoOnNext() 2
        subscribe() onNext(): 2
        DoOnNext() 3
        subscribe() onNext(): 3
        doOnComplete()
        doOnTerminate()
        doFinally()
     */
}