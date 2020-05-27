package chapter7buffer_window_throttle_debounce

import io.reactivex.Observable
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit


fun main() {

    // INFO fixed-size buffering
//    testFixedSizeBuffering()
//    testScanWithBuffer()
//    testBufferWithHashSet()

    // INFO Skip
//    testBufferSkip()
//    testBufferSkipLessThanCount()
//    testBufferSkipWithPrevAndCurrent()

    // INFO Time-based-buffering
//    testTimeBasedBuffering()
//    testTimeBaseBufferingWithCount()
//    testTimeBaseBufferingWithCount2()
//    testTimeBaseBufferingWithTimeSkip()

    // INFO Boundary-based-buffering
    testBoundaryBasedBuffering()
}


/**
 * 🔥 INFO buffer
 *
 * The simplest overload for buffer() accepts a count argument
 * that batches emissions in that fixed size.
 *
 */
fun testFixedSizeBuffering() {

    Observable.range(1, 50)
        .buffer(8)
        .subscribe {
            println("$it")
        }

    /*
        Prints:
        [1, 2, 3, 4, 5, 6, 7, 8]
        [9, 10, 11, 12, 13, 14, 15, 16]
        [17, 18, 19, 20, 21, 22, 23, 24]
        [25, 26, 27, 28, 29, 30, 31, 32]
        [33, 34, 35, 36, 37, 38, 39, 40]
        [41, 42, 43, 44, 45, 46, 47, 48]
        [49, 50]

     */
}

fun testScanWithBuffer() {

    Observable.just(5, 3, 7, 10, 2, 14)
        .scan { accumulator, next -> accumulator + next }
        .buffer(3)
        .subscribe { s -> println("Received: $s") }

    /*
        Prints:
        Received: [5, 8, 15]
        Received: [25, 27, 41]

     */

}

/**
 * You can also supply a second bufferSupplier lambda argument to put items
 * in another collection besides a list, such as HashSe
 */
fun testBufferWithHashSet() {
    Observable.range(1, 50)
        .buffer(8, Callable<HashSet<Int>> { HashSet() })
        .subscribe {
            println(it)
        }

}


/**
 * You can also provide a skip argument that specifies how many
 * items should be skipped before starting a new buffer. If skip is equal to count,
 * the skip has no effect
 */
fun testBufferSkip() {
    Observable.range(1, 10)
        .buffer(2, 3)
        .subscribe {
            println("Received: $it")
        }

    /*
        Prints:
        Received: [1, 2]
        Received: [4, 5]
        Received: [7, 8]
        Received: [10]
     */
}

fun testBufferSkipLessThanCount() {
    Observable.range(1, 10)
        .buffer(3, 2)
        .subscribe {
            println("Received: $it")
        }

    /*
        Count = 3, skip = 1 Prints:

        Received: [1, 2, 3]
        Received: [2, 3, 4]
        Received: [3, 4, 5]
        Received: [4, 5, 6]
        Received: [5, 6, 7]
        Received: [6, 7, 8]
        Received: [7, 8, 9]
        Received: [8, 9, 10]
        Received: [9, 10]
        Received: [10]

     */
    /*
        Count = 3, skip = 2 Prints:

        Received: [1, 2, 3]
        Received: [3, 4, 5]
        Received: [5, 6, 7]
        Received: [7, 8, 9]
        Received: [9, 10]
     */
}

fun testBufferSkipWithPrevAndCurrent() {

    Observable.range(1, 10)
        .buffer(2, 1)
        .subscribe {
            println("Received: $it")
        }

    /*
        Prints:

        Received: [1, 2]
        Received: [2, 3]
        Received: [3, 4]
        Received: [4, 5]
        Received: [5, 6]
        Received: [6, 7]
        Received: [7, 8]
        Received: [8, 9]
        Received: [9, 10]
        Received: [10]
     */
}

/**
 * You can use buffer() at fixed time intervals by providing a long and TimeUnit.
 *
 * To buffer emissions into a list at 1-second intervals,
 * you can run the following code.
 *
 * * Note that we are making the source emit every 300 milliseconds,
 * and each resulting buffered list will likely contain three or
 * four emissions due to the one-second interval cut-offs:
 */
fun testTimeBasedBuffering() {

    Observable.interval(300, TimeUnit.MILLISECONDS)
        .map { i -> (i + 1) * 300 } // map to elapsed time
        .buffer(1, TimeUnit.SECONDS)
        .subscribe {
            println("Received: $it")
        }

    sleep(4000)
}


/**
 * You can also leverage a third count argument to provide a maximum buffer size.
 *
 * This will result in a buffer emission at each time interval or when count is reached,
 * whichever happens first.
 *
 * * If the count is reached right before the time window closes,
 * it will result in an empty buffer being emitted.
 */
fun testTimeBaseBufferingWithCount() {

    Observable.interval(300, TimeUnit.MILLISECONDS)
        .map { i -> (i + 1) * 300 } // map to elapsed time
        .buffer(1, TimeUnit.SECONDS, 2)
        .subscribe {
            println("Received: $it")
        }

    sleep(4000)

    /*
        Prints:

        Received: [300, 600]
        Received: [900]
        Received: [1200, 1500]
        Received: [1800]
        Received: [2100, 2400]
        Received: [2700]
        Received: [3000, 3300]
        Received: [3600, 3900]
        Received: []
        Received: [4200, 4500]
        Received: [4800]

     */
}

fun testTimeBaseBufferingWithCount2() {

    Observable.interval(330, TimeUnit.MILLISECONDS)
        .map { i -> (i + 1) * 330 } // map to elapsed time
        .buffer(1, TimeUnit.SECONDS, 3)
        .subscribe {
            println("Received: $it")
        }

    sleep(4000)

    /*
        Prints:
        Received: [330, 660, 990]
        Received: []
        Received: [1320, 1650, 1980]
        Received: []
        Received: [2310, 2640, 2970]
        Received: []
        Received: [3300, 3630, 3960]
        Received: []
        ...

     */
}

/**
 * TimeSkip is the period of time after which a new buffer will be created
 */
fun testTimeBaseBufferingWithTimeSkip() {

    Observable.interval(300, TimeUnit.MILLISECONDS)
        .map { i -> (i + 1) * 300 } // map to elapsed time
        .buffer(1, 3, TimeUnit.SECONDS)
        .subscribe {
            println("Received: $it")
        }

    sleep(5000)

    /*

        Prints without time skip:

        Received: [300, 600, 900]
        Received: [1200, 1500, 1800]
        Received: [2100, 2400, 2700]
        Received: [3000, 3300, 3600, 3900]
        Received: [4200, 4500, 4800]

        Prints with TimeSkip 2:

        Received: [300, 600, 900]
        Received: [2100, 2400, 2700, 3000]
        Received: [4200, 4500, 4800]

     */

}

fun testBoundaryBasedBuffering() {

    val cutOff = Observable.interval(1,TimeUnit.SECONDS)

    Observable.interval(300, TimeUnit.MILLISECONDS)
        .map { i -> (i + 1) * 300 } // map to elapsed time
        .buffer(cutOff)
        .subscribe {
            println("Received: $it")
        }

    sleep(5000)

    /*
        Prints:

        Received: [300, 600, 900]
        Received: [1200, 1500, 1800]
        Received: [2100, 2400, 2700]
        Received: [3000, 3300, 3600, 3900]
        Received: [4200, 4500, 4800]
     */

}