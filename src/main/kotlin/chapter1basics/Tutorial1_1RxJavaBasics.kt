package chapter1basics

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer


fun main() {

    // 🔥 Subscription types
//    testSubscriptionMethods()
//    testSubscriptionsMethodsWithObjectDeclaration()
//    testSubscriptionMethodsWithLambda()


//    observableJust()

//    observableCreate2()

//    observableFromArray()

//    testFromIterable()

//    testMultipleSubscription()

    testScanOperator()

}

private fun testSubscriptionMethods() {

    // INFO 🔥
    val source = Observable.just("Value1", "Value2", "Value3")

    source.subscribe(
        Consumer<String> {
            println("onNext() $it")
        },
        Consumer<Throwable> {
            println("onError() ${it.message}")
        },
        Action { println("onComplete()") }
    )

}

private fun testSubscriptionsMethodsWithObjectDeclaration() {


    // INFO 🔥
    val source = Observable.just("Value1", "Value2", "Value3")

    source.subscribe(

        object : Consumer<String> {
            override fun accept(it: String) {
                println("onNext() $it")
            }
        },
        object : Consumer<Throwable> {
            override fun accept(it: Throwable) {
                println("onError() ${it.message}")
            }
        },
        object : Action {
            override fun run() {
                println("onComplete()")
            }
        }
    )

}

private fun testSubscriptionMethodsWithLambda() {

    // INFO 🔥
    val source = Observable.just("Value1", "Value2", "Value3")

    source.subscribe(
        // onNext
        {
            println("onNext() $it")
        },
        // onError
        {
            println("onError() ${it.message}")
        },
        // onComplete
        { println("onComplete()") }
    )

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


private fun testScanOperator() {

    val disposable = Observable.just(
        "Alpha", "Beta", "Gamma", "Delta", "Epsilon"
    )
        .scan(0, object : BiFunction<Int, String, Int> {

            override fun apply(integer: Int, s: String): Int {
                return integer + 1
            }


        })
        .doOnComplete { println("doOnComplete") }
        .doOnTerminate { println("doOnTerminate") }
        .doFinally { println("doFinally") }
        .doAfterTerminate { println("doAfterTerminate") }
        .subscribe { s: Int -> println("RECEIVED: $s") }

    disposable.dispose()
}



