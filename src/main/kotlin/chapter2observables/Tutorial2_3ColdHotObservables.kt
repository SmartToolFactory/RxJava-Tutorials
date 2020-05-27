package chapter2observables

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.observables.ConnectableObservable
import java.util.concurrent.TimeUnit

fun main() {
    // Cold Observables
//        testColdObservable()
//        testColdObservableWithOperators()
//        testColdObservableInterval()
//        testColdObservableCreate()


    // Hot Observables
        testConnectObservable()
//        testConnectObservableCreate()
//    testConnectObservableInterval()
//    testConnectObservableInterval2()
}


/*
     *** COLD OBSERVABLES ***
     */

/**
 * Cold observables start from 0 even they are subscribed to [Observable] later on
 */
private fun testColdObservable() {

    println("testColdObservable()")


    val source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
    //first observer
    source.subscribe { s -> println("Observer 1 Received: $s") }
    //second observer
    source.subscribe { s -> println("Observer 2 Received: $s") }

    /*
                   Prints:
                   Observer 1 Received: Alpha
                   Observer 1 Received: Beta
                   Observer 1 Received: Gamma
                   Observer 1 Received: Delta
                   Observer 1 Received: Epsilon

                   Observer 2 Received: Alpha
                   Observer 2 Received: Beta
                   Observer 2 Received: Gamma
                   Observer 2 Received: Delta
                   Observer 2 Received: Epsilon
                */

}

private fun testColdObservableWithOperators() {

    println("testColdObservableWithOperators()")

    val source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

    //first observer
    source.subscribe { s -> println("Observer 1 Received: $s") }

    //second observer
    source.map { it.length }.filter { i -> i >= 5 }
        .subscribe { s -> println("Observer 2 Received: " + s!!) }

    /*
      Prints:

      Observer 1 Received: Alpha
      Observer 1 Received: Beta
      Observer 1 Received: Gamma
      Observer 1 Received: Delta
      Observer 1 Received: Epsilon

      Observer 2 Received: 5
      Observer 2 Received: 5
      Observer 2 Received: 5
      Observer 2 Received: 7
    */
}


/**
 * Method to test Cold [Observable] within an interval
 *
 * @throws InterruptedException
 */
@Throws(InterruptedException::class)
private fun testColdObservableInterval() {
    println("testColdObservableInterval()")

    val source = Observable.interval(1, TimeUnit.SECONDS)

    source.subscribe { i -> println("Observer 1 Received: " + i!!) }

    Thread.sleep(3000)
    source.map { i -> "$i sec" }.subscribe { s ->
        println("Observer 2 Received: $s")

    }

    Thread.sleep(5000)


    // 🔥 WARNING Observer2 receives emission starting from 0 instead of continuing where emission is left of
    /*
            Prints:
        Observer 1 Received: 0
        Observer 1 Received: 1
        Observer 1 Received: 2
        Observer 1 Received: 3
        Observer 2 Received: 0 sec 🔥 Observer2 subscribes here
        Observer 1 Received: 4
        Observer 2 Received: 1 sec
        Observer 1 Received: 5
        Observer 2 Received: 2 sec
        Observer 1 Received: 6
        Observer 2 Received: 3 sec
        Observer 1 Received: 7
        Observer 2 Received: 4 sec
            ...
    */


}

private fun testColdObservableCreate() {

    println("testColdObservableCreate()")

    val observable = Observable.create<String> { emitter ->

        println("observable starting to emit...")
        emitter.onNext("Alpha")
        emitter.onNext("Beta")
        emitter.onNext("Gamma")
        emitter.onNext("Delta")
        emitter.onNext("Epsilon")

        /*
            Prints:
            Observer1 onNext(): Alpha
            Observer1 onNext(): Beta
            Observer1 onNext(): Gamma
            Observer1 onNext(): Delta
            Observer1 onNext(): Epsilon

            Observer2 onNext(): 5
            Observer2 onNext(): 4
            Observer2 onNext(): 5
            Observer2 onNext(): 5
            Observer2 onNext(): 7
         */
    }


    val observer1 = object : Observer<String> {
        override fun onSubscribe(d: Disposable) {

        }

        override fun onNext(string: String) {
            println("Observer1 onNext(): $string")
        }

        override fun onError(e: Throwable) {

        }

        override fun onComplete() {

        }
    }


    val observer2 = object : Observer<Int> {

        override fun onNext(integer: Int) {
            println("Observer2 onNext(): " + integer!!)

        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onError(e: Throwable) {

        }

        override fun onComplete() {

        }
    }

    observable.subscribe(observer1)

    observable
        .map { it.length }
        .subscribe(observer2)
}


/*
     *** HOT OBSERVABLES ***
*/

private fun testConnectObservable() {

    println("testConnectObservable()")
    val source = Observable
        .just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .publish()

    //Set up observer 1
    source.subscribe { s -> println("Observer 1: $s") }

    //Set up observer 2
    source.map { it.length }
        .subscribe { i -> println("Observer 2: " + i!!) }

    //Fire!
    source.connect()
}

/**
 * Method to test [ConnectableObservable]. ConnectableObservable is used to convert a **COLD** into **hot** observable
 */
private fun testConnectObservableCreate() {


    val cold = Observable.create<Int> { subscriber ->
        for (i in 0..1) {
            println("Source Emit $i")
            subscriber.onNext(i)
        }
    }


    val subscriber1 = Consumer<Int> { integer -> println("Subscriber 1 :" + integer!!) }
    val subscriber2: (Int) -> Unit = { integer -> println("Subscriber 2 :" + integer!!) }

    // 🔥 WARNING: invoking cold.publish() and contactable.connect() causes the observable will emit all the items even if there is no subscriber yet.
    val contactable = cold.publish()

    contactable.subscribe(subscriber1)
    contactable.subscribe(subscriber2)

    // This should be after subscribing
    contactable.connect()

    /*
        Prints:

        Source Emit 0
        Subscriber 1 :0
        Subscriber 2 :0

        Source Emit 1
        Subscriber 1 :1
        Subscriber 2 :1

    */

}

@Throws(InterruptedException::class)
private fun testConnectObservableInterval() {

    val myObservable = Observable.interval(1, TimeUnit.SECONDS)

    val connectibleObservable = myObservable.publish()

    connectibleObservable.subscribe { item -> println("Observer 1 Received: " + item!!) }

    connectibleObservable.connect()

    Thread.sleep(3000)
    connectibleObservable.subscribe { item -> println("Observer 2 Received: $item sec") }
    Thread.sleep(5000)

    /*
        Prints:
        Observer 1: 0
        Observer 1: 1
        Observer 1: 2
        Observer 1: 3
        Observer 2: 3 🔥🔥 Observer 2 received the same emission as observer 1 upon subscription
        Observer 1: 4
        Observer 2: 4
        Observer 1: 5
        Observer 2: 5

    */
}


private fun testConnectObservableInterval2() {

    val myObservable = Observable.interval(1, TimeUnit.SECONDS)

    val connectibleObservable = myObservable.publish()

    connectibleObservable.connect()

    connectibleObservable.subscribe { item -> println("Observer 1 Received: $item") }
    connectibleObservable.subscribe { item -> println("Observer 2 Received: $item") }


    Thread.sleep(3000)
    connectibleObservable.subscribe { item -> println("Observer 3 Received: $item sec") }
    Thread.sleep(5000)

    /*
        Prints:
        Observer 1: 0
        Observer 1: 1
        Observer 1: 2
        Observer 1: 3
        Observer 2: 3 🔥🔥 Observer 2 received the same emission as observer 1 upon subscription
        Observer 1: 4
        Observer 2: 4
        Observer 1: 5
        Observer 2: 5

    */
}