package chapter1basics

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

private var timerRunning = false
private var disposable: Disposable? = null

fun main() {

//    observableJust()

//    observableCreate2()

//    observableFromArray()

//    observableLifeCycleMethods()
//    observableLifeCycleMethodsWithMap()
    observableLifeCycleMethodsWithMap2()
}

/**
 * Observable created with just operator
 */
private fun observableJust() {
    val observable: Observable<String> = Observable.just("Hello", "World")


    // TODO Methods to subscribe

//    // 1-) Object class that implements Consumer
//    val disposable = observable.subscribe(object : Consumer<String> {
//        override fun accept(t: String?) {
//            println(t)
//        }
//    })
//
//    // 2-) Object class that implements Consumer with lambda method
//    val disposable = observable.subscribe(
//        Consumer<String> { s -> println("Res: " + s + ", Thread: " + Thread.currentThread().name) }
//    );

    // 3-) Lambda
    val disposable = observable.subscribe {
        println(it)
    }


    disposable.dispose()
}


private fun testCreate() {

    val observable = Observable.create(ObservableOnSubscribe<String> { emitter ->
        emitter.onNext("One")
        emitter.onNext("Two")
        emitter.onNext("Three")

        emitter.onComplete()

        emitter.setCancellable { println("ObservableEmitter cancel()") }
    })

    observable.subscribe(object : Consumer<String> {

        override fun accept(s: String?) {

        }

    })


    observable.subscribe { s ->

    }


}


/**
 * Observable created with {@link Observable#create} method
 */
private fun observableCreate2() {

    // TODO Observable from create with object
    val observable0 = Observable.create(object : ObservableOnSubscribe<String> {

        override fun subscribe(emitter: ObservableEmitter<String>) {

        }
    })

    // TODO Observable from create with lambda
    val observable = Observable.create<String> { emitter ->
        emitter.onNext("Hello")
        emitter.onNext("Brave")
        emitter.onNext("A new World")

        emitter.onError(Exception("Some exception"))
    }


    // Observer with Lambda
    val subscribe = observable
//        .filter {
//            it.length > 5
//        }
        .subscribe(
            { s ->
                // onNext
                println("observableCreate2() First observer onNext(): $s")
            },
            { error ->
                // onError
                println("observableCreate2() First observer error:" + error.message)
            }
        )
    subscribe.dispose()

    // Second Observer
    val subscriber2 = object : Observer<String> {

        override fun onSubscribe(d: Disposable) {
            println("observableCreate2() Second observer onSubscribe() $d")

        }

        override fun onNext(string: String) {
            println("observableCreate2() Second observer onNext() $string")
        }

        override fun onError(e: Throwable) {
            println("observableCreate2() Second observer onError() ${e.message}")
        }

        override fun onComplete() {
            println("observableCreate2() Second observer onComplete()")
        }
    }


    // Third Observer
    val subscriber3 = object : Observer<String> {
        override fun onSubscribe(d: Disposable) {
            println("observableCreate2() Third observer onSubscribe() $d")

        }

        override fun onNext(string: String) {
            println("observableCreate2() Third observer onNext() $string")
        }

        override fun onError(e: Throwable) {
            println("observableCreate2() Third observer onError() ${e.message}")
        }

        override fun onComplete() {
            println("observableCreate2() Third observer onComplete()")
        }
    }

    // Subscribe observers
    observable.subscribe(subscriber2)

    observable.subscribe(subscriber3)

}

/**
 * Observable created with {@link Observable#fromArray} method
 */
private fun observableFromArray() {

    val observableInteger = Observable.fromArray(1, 2, 3, 4, 5)

    observableInteger

        .map { integer -> integer * 2 }
        .filter { integer -> integer > 4 }

        .subscribe(object : Observer<Int> {

            override fun onNext(integer: Int) {
                println("observableFromArray() -> onNext(): " + integer + ", thread: " + Thread.currentThread().name)
            }

            override fun onSubscribe(d: Disposable) {
                println("observableFromArray() -> onSubscribe d: " + d + ", thread: " + Thread.currentThread().name)
            }


            override fun onError(e: Throwable) {
                println("observableFromArray() -> onError() e:" + e.message)
            }

            override fun onComplete() {
                println("observableFromArray() -> onComplete(): " + ", thread: " + Thread.currentThread().name)
            }
        })
}


private fun testFromIterable() {

    val superHeroes = listOf("Superman", "Batman", "Aquaman", "Asterix", "Captain America")


    val observable = Observable.fromIterable(superHeroes)

    val disposable = observable.map { name ->

        //                    if (name.endsWith("x")) {
        //                        throw new RuntimeException("What a terrible failure!");
        //                    }
        name.toUpperCase()
    }
        // Use doOnNext, doOnComplete and doOnError to print messages
        // on each item, when the stream complete, and when an error occurs
        .doOnNext { s -> println(">> $s") }
        .doOnComplete { println("Completion... not called") }
        .doOnError { err -> println("Oh no! " + err.message) }
        .subscribe(
            {
                println("onNext() $it")
            },
            { error ->
                println("onError() $error")
            })

    disposable.dispose()
}

/**
 * Cold Observable with multiple subscriptions
 */
private fun testMultipleSubscription() {

    // INFO 🔥
    val source = Observable.just("Value1", "Value2", "Value3")
    source.subscribe { s -> println("Observer1: $s") }
    source.subscribe { s -> println("Observer2: $s") }

    /*
       Prints:
       Observer1: Value1
       Observer1: Value2
       Observer1: Value3
       Observer2: Value1
       Observer2: Value2
       Observer2: Value3
   */
}

private fun setTimer(seconds: Observable<Long>) {

    if (timerRunning) {
        val longObservable = seconds.unsubscribeOn(Schedulers.computation())
        disposable?.dispose()
    } else {
        disposable = createTimer(seconds)
    }

    timerRunning = !timerRunning
}

private fun createTimer(seconds: Observable<Long>): Disposable {

    return seconds
        .subscribe { l ->
            println(
                "Observer 1: " + l
                        + ", thread: " + Thread.currentThread().name
            )
        }
}

/**
 * 🔥🔥🔥 Life cycle of Observable from subscription to completion
 */
private fun observableLifeCycleMethods() {

    val source = Observable.just("Alpha", "Beta", "Gamma")


    source

        .doOnSubscribe {
            println("doOnSubscribe() thread: ${Thread.currentThread().name}")
        }
        .doOnEach {
            println("🎃 doOnEach() thread: ${Thread.currentThread().name}, event: ${it.value}")
        }
        .doOnNext {
            println("🥶doOnNext() thread: ${Thread.currentThread().name}")
        }
        .doAfterNext {
            println("😍 doAfterNext() thread: ${Thread.currentThread().name}")
        }

        .doOnComplete {
            println("doOnComplete() thread: ${Thread.currentThread().name}")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("doOnError() ${it.message}")
        }

        .subscribe(
            {
                println("😎 subscribe() -> onNext(): thread: ${Thread.currentThread().name}")
            },
            {
                println("😎 subscribe() -> onError(): thread: ${Thread.currentThread().name}, error: ${it.message}")

            }
        )


    /*
        Prints:
        doOnSubscribe() thread: main
        🎃 doOnEach() thread: main, event: Alpha
        🥶doOnNext() thread: main
        😎 subscribe() -> onNext(): thread: main
        😍 doAfterNext() thread: main
        🎃 doOnEach() thread: main, event: Beta
        🥶doOnNext() thread: main
        😎 subscribe() -> onNext(): thread: main
        😍 doAfterNext() thread: main
        🎃 doOnEach() thread: main, event: Gamma
        🥶doOnNext() thread: main
        😎 subscribe() -> onNext(): thread: main
        😍 doAfterNext() thread: main
        🎃 doOnEach() thread: main, event: null
        doOnComplete() thread: main
        doOnTerminate() thread: main
        doFinally() thread: main
        doAfterTerminate() thread: main
     */

}


private fun observableLifeCycleMethodsWithMap() {

    val source = Observable.just("Alpha", "Beta", "Gamma")


    source

        .doOnSubscribe {
            println("doOnSubscribe() thread: ${Thread.currentThread().name}")
        }
        .doOnEach {
            println("🎃 doOnEach() thread: ${Thread.currentThread().name}, event: ${it.value}")
        }
        .doOnNext {
            println("🥶doOnNext() thread: ${Thread.currentThread().name}")
        }
        .doAfterNext {
            println("😍 doAfterNext() thread: ${Thread.currentThread().name}")
        }

        .doOnComplete {
            println("doOnComplete() thread: ${Thread.currentThread().name}")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("doOnError() ${it.message}")
        }
        // 🔥🔥🔥 Order of map method changes doOnNext -> map -> onSubscribe()
        .map {
            println("map() 1 thread: ${Thread.currentThread().name}")
            it
        }

        .map {
            println("map() 2 thread: ${Thread.currentThread().name}")
            it
        }

        .subscribe(
            {
                println("😎 subscribe() -> onNext(): thread: ${Thread.currentThread().name}")
            },
            {
                println("😎 subscribe() -> onError(): thread: ${Thread.currentThread().name}, error: ${it.message}")

            }
        )


    /*
        Prints:
        🎃 doOnEach() thread: main, event: Alpha
        🥶doOnNext() thread: main
        map() 1 thread: main
        map() 2 thread: main
        😎 subscribe() -> onNext(): thread: main
        😍 doAfterNext() thread: main
        🎃 doOnEach() thread: main, event: Beta
        🥶doOnNext() thread: main
        map() 1 thread: main
        map() 2 thread: main
        😎 subscribe() -> onNext(): thread: main
        😍 doAfterNext() thread: main
        🎃 doOnEach() thread: main, event: Gamma
        🥶doOnNext() thread: main
        map() 1 thread: main
        map() 2 thread: main
        😎 subscribe() -> onNext(): thread: main
        😍 doAfterNext() thread: main
        🎃 doOnEach() thread: main, event: null
        doOnComplete() thread: main
        doOnTerminate() thread: main
        doFinally() thread: main
        doAfterTerminate() thread: main
     */

}

private fun observableLifeCycleMethodsWithMap2() {

    val source = Observable.just("Alpha", "Beta", "Gamma")


    source

        // 🔥🔥🔥 Order of map method changes map -> doOnNext -> onSubscribe()
        .map {
            println("map() 1 thread: ${Thread.currentThread().name}")
            it
        }

        .map {
            println("map() 2 thread: ${Thread.currentThread().name}")
            it
        }

        .doOnSubscribe {
            println("doOnSubscribe() thread: ${Thread.currentThread().name}")
        }
        .doOnEach {
            println("🎃 doOnEach() thread: ${Thread.currentThread().name}, event: ${it.value}")
        }
        .doOnNext {
            println("🥶doOnNext() thread: ${Thread.currentThread().name}")
        }
        .doAfterNext {
            println("😍 doAfterNext() thread: ${Thread.currentThread().name}")
        }

        .doOnComplete {
            println("doOnComplete() thread: ${Thread.currentThread().name}")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("doOnError() ${it.message}")
        }

        .subscribe(
            {
                println("😎 subscribe() -> onNext(): thread: ${Thread.currentThread().name}")
            },
            {
                println("😎 subscribe() -> onError(): thread: ${Thread.currentThread().name}, error: ${it.message}")

            }
        )


    /*
        Prints:
         doOnSubscribe() thread: main
        map() 1 thread: main
        map() 2 thread: main
        🎃 doOnEach() thread: main, event: Alpha
        🥶doOnNext() thread: main
        😎 subscribe() -> onNext(): thread: main
        😍 doAfterNext() thread: main
        map() 1 thread: main
        map() 2 thread: main
        🎃 doOnEach() thread: main, event: Beta
        🥶doOnNext() thread: main
        😎 subscribe() -> onNext(): thread: main
        😍 doAfterNext() thread: main
        map() 1 thread: main
        map() 2 thread: main
        🎃 doOnEach() thread: main, event: Gamma
        🥶doOnNext() thread: main
        😎 subscribe() -> onNext(): thread: main
        😍 doAfterNext() thread: main
        🎃 doOnEach() thread: main, event: null
        doOnComplete() thread: main
        doOnTerminate() thread: main
        doFinally() thread: main
        doAfterTerminate() thread: main
     */

}