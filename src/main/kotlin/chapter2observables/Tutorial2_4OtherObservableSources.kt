package chapter2observables

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.lang.Thread.sleep
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

// Variables for Defer example
private val start = 1
private var count = 3

fun main() {

    //     testObservableRange()

//        testObservableInterval()
//        testObservableIntervalMultiple()

//        testObservableFuture()

//        testObservableEmpty()
//        testObservableNever()

//        testObservableDefer()

    observeCallable()
}


private fun testObservableRange() {

    println("testObservableRange()")

    //        Observable.range(1, 10)
    //                .subscribe(s -> System.out.println("RECEIVED: " + s));

    Observable.range(5, 3)
        .subscribe { s -> println("RECEIVED: " + s!!) }
}

private fun testObservableInterval() {

    println("testObservableInterval()")

    Observable.interval(1, TimeUnit.SECONDS)
        .subscribe { s -> println(s!!.toString() + " Mississippi") }

    // This is for program not exiting because interval is executed in Scheduler thread
    try {
        sleep(5000)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }

}

private fun testObservableIntervalMultiple() {

    println("testObservableIntervalMultiple()")


    val seconds = Observable.interval(
        1,
        TimeUnit.SECONDS
    )
    //Observer 1
    seconds.subscribe { l -> println("Observer 1: " + l!!) }
    //sleep 5 seconds
    sleep(5000)

    // WARNING Observable2 starts from 0
    //Observer 2
    seconds.subscribe { l -> println("Observer 2: $l sec") }
    //sleep 5 seconds
    sleep(5000)
}

private fun testObservableFuture() {

    println("testObservableFuture()")

    // Future awaits for asynchronous task to end returns result with future.get()
    val future = object : Future<String> {
        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            return false
        }

        override fun isCancelled(): Boolean {
            return false
        }

        override fun isDone(): Boolean {
            return false
        }

        @Throws(InterruptedException::class, ExecutionException::class)
        override fun get(): String {
            Thread.sleep(5000)
            return "I am from the future"
        }

        @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
        override fun get(timeout: Long, unit: TimeUnit): String {
            Thread.sleep(5000)
            return "I am from the future with timeout: $timeout"
        }

    }
    Observable.fromFuture(future)
        .subscribe(Consumer<String> { println(it) })

}


private fun testObservableEmpty() {

    println("testObservableEmpty()")

    val empty = Observable.empty<String>()

//    empty.subscribe(
//        Consumer<String> { println(it) },
//        Consumer<Throwable> { it.printStackTrace() },
//        Action { println("Done!") }
//    )

    empty.subscribe(
        { next ->
            // onNext
            println(next)
        },
        { error ->
            // onError
            error.printStackTrace()
        },
        {
            // onComplete
            println("Done!")
        }
    )
}

private fun testObservableNever() {

    println("testObservableNever()")

    val empty = Observable.never<String>()
    empty.subscribe(
        Consumer<String> { println(it) },
        Consumer<Throwable> { it.printStackTrace() },
        Action { println("Done!") }
    )

    sleep(5000)

}


/**
 * If you subscribe to this Observable, modify the count, and then subscribe again,
 * you will find that the second Observer does not see this change:
 *
 *
 *
 * To remedy this problem of Observable sources not capturing state changes,
 * you can create a fresh Observable for each subscription.
 * This can be achieved using [Observable.defer],
 * which accepts a lambda instructing how to create an Observable for every subscription.
 * Because this creates a new Observable each time,
 * it will reflect any changes driving its parameters:
 */


private fun testObservableDefer() {


    println("testObservableDefer()")

    val source = Observable.range(start, count)
    source.subscribe { i -> println("Observer 1: " + i!!) }
    //modify count
    count = 5
    source.subscribe { i -> println("Observer 2: " + i!!) }

    /*
       Prints:
       Observer 1: 1
       Observer 1: 2
       Observer 1: 3

       Observer 2: 1
       Observer 2: 2
       Observer 2: 3
    */


    val sourceDefer = Observable.defer { Observable.range(start, count) }
    sourceDefer.subscribe { i -> println("Defer Observer 1: " + i!!) }
    //modify count
    count = 5
    sourceDefer.subscribe { i -> println("Defer Observer 2: " + i!!) }

    /*
       Prints
       Observer 1: 1
       Observer 1: 2
       Observer 1: 3

       Observer 2: 1
       Observer 2: 2
       Observer 2: 3
       Observer 2: 4
       Observer 2: 5
    */
}


private fun observeCallable() {

    // 🔥 Throws ArithmeticException
    //        Observable.just(1 / 0)
    //                .subscribe(
    //                        i -> System.out.println("RECEIVED: " + i),
    //                        e -> System.out.println("Error Captured: " + e)
    //                );


    Observable.fromCallable { 1 / 0 }
        .subscribe(
            { i -> println("Received: " + i!!) },
            { e -> println("Error Captured: $e") }
        )
}