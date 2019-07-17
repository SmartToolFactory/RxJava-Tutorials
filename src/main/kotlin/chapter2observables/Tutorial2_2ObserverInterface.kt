package chapter2observables

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

fun main() {

}

private fun testObserver() {

    val source = Observable.just(
        "Alpha", "Beta", "Gamma", "Delta", "Epsilon"
    )

    val myObserver = object : Observer<Int> {


        override fun onSubscribe(d: Disposable) {
            //do nothing with Disposable, disregard for now
        }

        override fun onNext(value: Int) {
            println("RECEIVED: $value")
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
        }

        override fun onComplete() {
            println("Done!")
        }
    }
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