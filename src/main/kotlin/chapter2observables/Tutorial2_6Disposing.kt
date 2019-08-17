package chapter2observables

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.ResourceObserver
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit

// This is from testDisposableFromObserver method
private var disposable: Disposable? = null


private val disposables = CompositeDisposable()

fun main() {
//        testDisposableInterval()

//        testResourceObserver()

    testCompositeDisposable()
}

@Throws(InterruptedException::class)
private fun testDisposableInterval() {
    val seconds = Observable.interval(1, TimeUnit.SECONDS)
    val disposable = seconds.subscribe { l -> println("Received: " + l!!) }
    //sleep 5 seconds
    sleep(5000)

    //dispose and stop emissions
    disposable.dispose()

    if (disposable.isDisposed) {
        println("Disposable is disposed!")
    }

    //sleep 5 seconds to prove
    //there are no more emissions
    sleep(5000)

}


private fun testDisposableFromObserverOnSubscribe() {

    val observer = object : Observer<Int> {
        override fun onSubscribe(d: Disposable) {
            disposable = d
        }

        override fun onNext(value: Int) {
            //has access to Disposable
        }

        override fun onError(e: Throwable) {
            //has access to Disposable
        }

        override fun onComplete() {
            //has access to Disposable
        }
    }
}

/**
 * [ResourceObserver] returns when subscribed with [Observable.subscribeWith]
 */
private fun testResourceObserver() {

    println("testResourceObserver")

    val source = Observable.interval(1, TimeUnit.SECONDS)


    val myObserver = object : ResourceObserver<Long>() {

        override fun onNext(value: Long) {
            println(" onNext() $value")
        }

        override fun onError(e: Throwable) {
            println("onError e: $e.message")
        }

        override fun onComplete() {
            println("onComplete()")
        }
    }

    // INFO 🔥🔥 ResourceObserver implements both Observab
    //capture Disposable
    val disposable = source.subscribeWith<ResourceObserver<Long>>(myObserver)

}

@Throws(InterruptedException::class)
private fun testCompositeDisposable() {

    val seconds = Observable.interval(1, TimeUnit.SECONDS)
    //subscribe and capture disposables
    val disposable1 = seconds.subscribe { l -> println("Observer 1: " + l!!) }
    val disposable2 = seconds.subscribe { l -> println("Observer 2: " + l!!) }
    //put both disposables into CompositeDisposable
    disposables.addAll(disposable1, disposable2)

    //sleep 5 seconds
    sleep(5000)

    //dispose all disposables
    disposables.dispose()

    if (disposables.isDisposed()) {
        println("CompositeDisposable is disposed!")
    }

    //sleep 5 seconds to prove
    //there are no more emissions
    sleep(5000)

}


private fun testDisposableFromCreate() {

    val source = Observable.create(ObservableOnSubscribe<Int> { observableEmitter ->
        try {

            for (i in 0..999) {
                while (!observableEmitter.isDisposed) {
                    observableEmitter.onNext(i)
                }
            }

            observableEmitter.onComplete()

        } catch (e: Throwable) {
            observableEmitter.onError(e)
        }
    })


}