package chapter4combiningobservables

import io.reactivex.Observable
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit


fun main() {
//    testAmbOperator()
    testAmbOperatorOperatorInterval()


}

/**
 * 🔥 INFO amb
 *
 * The **Observable.amb()** factory (amb stands for ambiguous) will accept an **Iterable<Observable<T>>**
 * and emit the emissions of the first Observable that emits, while the others are disposed of.
 *
 * The first Observable with an emission is the one whose emissions go through.
 * This is helpful when you have **multiple sources for the same data or events and you want the fastest one** to win.
 */
private fun testAmbOperator() {
    println("testAmbOperator()")

    // 🔥 INFO amb emits the one who comes first, only the one with shortest delay will be emitted
    val delay1 = 3L
    val delay2 = 2L

    val source1 = Observable.just("1", "2", "3")
        .delay(delay1, TimeUnit.SECONDS)

    val source2 = Observable.just("Apple", "Orange", "Banana")
        .delay(delay2, TimeUnit.SECONDS)

    val arr = arrayOf<Observable<String>>(source1, source2)

    Observable.amb(arr.toList())
        .subscribe {
            println(it)
        }

    /*
        Prints:
        Apple
        Orange
        Banana
     */

    sleep(6000)

}

/**
 *
 */
private fun testAmbOperatorOperatorInterval() {

    //emit every second
    val source1 = Observable.interval(1, TimeUnit.SECONDS)

        .take(2)
        .map { l -> l + 1 } // emit elapsed seconds
        .map { l -> "Source1: $l seconds" }


    //emit every 300 milliseconds

    val source2 = Observable.interval(300, TimeUnit.MILLISECONDS)
        .map { l -> (l + 1) * 300 } // emit elapsed
        .map { l -> "Source2: $l milliseconds" }


    val arr = arrayOf<Observable<String>>(source1, source2)

    //emit Observable that emits first
    Observable.amb(arr.toList())
        .subscribe { i -> println("RECEIVED: $i") }

    /*
        Prints:
        RECEIVED: Source2: 300 milliseconds
        RECEIVED: Source2: 600 milliseconds
        RECEIVED: Source2: 900 milliseconds
        RECEIVED: Source2: 1200 milliseconds
        RECEIVED: Source2: 1500 milliseconds
        RECEIVED: Source2: 1800 milliseconds
        RECEIVED: Source2: 2100 milliseconds
        RECEIVED: Source2: 2400 milliseconds
        RECEIVED: Source2: 2700 milliseconds
        RECEIVED: Source2: 3000 milliseconds
     */

    sleep(3000)

}