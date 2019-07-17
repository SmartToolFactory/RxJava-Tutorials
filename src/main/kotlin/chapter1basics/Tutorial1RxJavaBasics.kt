package chapter1basics

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


fun main() {

//    observableJust()

    observableCreate()

//    observableFromArray()
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


/**
 * Observable created with {@link Observable#create} method
 */
private fun observableCreate() {

    val observable0 = Observable.create(object : ObservableOnSubscribe<String> {

        override fun subscribe(emitter: ObservableEmitter<String>) {

        }
    })

    val observable = Observable.create<String> { emitter ->
        emitter.onNext("Hello")
        emitter.onNext("Brave")
        emitter.onNext("A new World")

        emitter.onError(Exception("Some exception"))
    }

    val subscribe = observable
//        .filter {
//            it.length > 5
//        }
        .subscribe(
            { s ->
                // onNext
                println("observableCreate() First observer onNext(): $s")
            },
            { error ->
                // onError
                println("observableCreate() First observer error:" + error.message)
            }
        )
    subscribe.dispose()

    // Second Observer
    val subscriber2 = object : Observer<String> {

        override fun onSubscribe(d: Disposable) {
            println("observableCreate() Second observer onSubscribe() $d")

        }

        override fun onNext(string: String) {
            println("observableCreate() Second observer onNext() $string")
        }

        override fun onError(e: Throwable) {
            println("observableCreate() Second observer onError() ${e.message}")
        }

        override fun onComplete() {
            println("observableCreate() Second observer onComplete()")
        }
    }


    // Third Observer
    val subscriber3 = object : Observer<String> {
        override fun onSubscribe(d: Disposable) {
            println("observableCreate() Third observer onSubscribe() $d")

        }

        override fun onNext(string: String) {
            println("observableCreate() Third observer onNext() $string")
        }

        override fun onError(e: Throwable) {
            println("observableCreate() Third observer onError() ${e.message}")
        }

        override fun onComplete() {
            println("observableCreate() Third observer onComplete()")
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