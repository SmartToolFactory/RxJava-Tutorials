package chapter2observables

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

fun main() {


    testObserverWithMultipleObservables()
}

private fun testObserver() {

    val source = Observable.just(
        "Alpha", "Beta", "Gamma", "Delta", "Epsilon"
    )

    val myObserver = object : Observer<String> {


        override fun onSubscribe(d: Disposable) {
            //do nothing with Disposable, disregard for now
        }

        override fun onNext(value: String) {
            println("RECEIVED: $value")
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
        }

        override fun onComplete() {
            println("Done!")
        }
    }

    source.subscribe(myObserver)


}


/**
 * This method accepts 3 lambdas the **onNext** lambda, the **onError** lambda
 * , and the **onComplete** lambda.
 */
private fun testObserverLambda() {
    val source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

    source
        .map {
            it.length
        }
        .filter { it >= 5 }
        .subscribe(
            {
                println("RECEIVED $it")
            },
            {
                it.printStackTrace()
            },
            {
                println("onComplete()")
            }
        )

}

private fun testObserverWithMultipleObservables() {
    val source1 = Observable.just(
        "Alpha", "Beta", "Gamma", "Delta", "Epsilon"
    )

    val source2 = Observable.just("One", "Two", "Three", "Four")

    val myObserver = object : Observer<String> {


        override fun onSubscribe(d: Disposable) {
            //do nothing with Disposable, disregard for now
        }

        override fun onNext(value: String) {
            println("RECEIVED: $value")
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
        }

        override fun onComplete() {
            println("Done!")
        }
    }

    source1.subscribe(myObserver)
    source2.subscribe(myObserver)

    /*
        Prints:
        RECEIVED: Alpha
        RECEIVED: Beta
        RECEIVED: Gamma
        RECEIVED: Delta
        RECEIVED: Epsilon
        Done!
        RECEIVED: One
        RECEIVED: Two
        RECEIVED: Three
        RECEIVED: Four
        Done!


     */
}