package chapter3basicoperators

import io.reactivex.Observable
import java.lang.Thread.sleep
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


fun main() {

    // INFO map
//    testMapOperator()

    // INFO startWith
    testStartWithOperator()

    // INFO defaultIfEmpty
//    testDefaultIfEmptyOperator()
    // INFO switchIfEmpty
//    testSwitchIfEmptyOperator()

    //INFO sorted
//    testSortedOperator()
//    testSortedOperatorWithComparator()

    // INFO delay
//    testDelayOperator()
//    sleep(5000)

    // INFO repeat
//    testRepeatOperator()

    // INFO scan
//    testScanOperator()
//    testScanOperatorWithString()
//    testScanOperatorFibonacci()

}

/**
 * 🔥 INFO map()
 *
 * For a given **Observable<T>**, the map() operator will transform
 * a **T emission** into an **R emission** using the provided **Function<T,R>** lambda
 *
 * The **map()** operator does a one-to-one conversion for each emission.
 * If you need to do a one-to-many conversion (turn one emission into several emissions),
 * you will likely want to use **flatMap()** or **concatMap()**
 */
private fun testMapOperator() {

    val dtf = DateTimeFormatter.ofPattern("M/d/yyyy")

    Observable.just("1/3/2016", "5/9/2016", "10/12/2016")
        .map { s -> LocalDate.parse(s, dtf) }
        .subscribe { println("RECEIVED: $it") }

    /*
    Transforms String into LocalDate
        Prints:
        RECEIVED: 2016-01-03
        RECEIVED: 2016-05-09
        RECEIVED: 2016-10-12
     */

}

private fun testCastOperator() {
    val items: Observable<Any> = Observable.just("Alpha", "Beta", "Gamma")
        .map { s -> s as Any }

    val itemsWithCast: Observable<Any> =
        Observable.just("Alpha", "Beta", "Gamma").cast(Any::class.java)
}

/**
 * 🔥 INFO startWith()
 *
 * For a given **Observable<T>**, the **startWith()** operator allows to
 * insert a T emission that precedes all the other emissions.
 * For instance, if we have an **Observable<String>** that emits items on a menu we want to print,
 * we can use **startWith()** to **append a title header first**:
 */
private fun testStartWithOperator() {

    val menu = Observable.just("Coffee", "Tea", "Espresso", "Latte")

    //print menu
    menu.startWith("COFFEE SHOP MENU").subscribe(System.out::println)

    /*
        Prints:
        COFFEE SHOP MENU
        Coffee
        Tea
        Espresso
        Latte
     */

    /*
    INFO
        If you want to start with more than one emission, use startWithArray() to accept varargs parameters.
        If we want to add a divider between our header and menu items,
        we can start with both the header and divider as emissions:
     */

    //print menu
    menu.startWithArray("COFFEE SHOP MENU", "----------------")
        .subscribe(System.out::println)
}

/**
 * 🔥 INFO defaultIfEmpty()
 *
 *  For a given **Observable<T>**, we can specify a default **T** emission if no emissions occur when **onComplete()** is called.
 */
private fun testDefaultIfEmptyOperator() {
    val items = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

    items.filter { s -> s.startsWith("Z") }
        .defaultIfEmpty("None")
        .subscribe { println(it) }

    /*
        Prints:
        None
     */
}


/**
 * 🔥 INFO switchIfEmpty()
 *
 * Specifies a different Observable to emit values from if the source Observable is empty.
 * This allows you specify a different sequence of emissions in the event that
 * the source is empty rather than emitting just one value.
 */
private fun testSwitchIfEmptyOperator() {

    val items = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

    items.filter { s -> s.startsWith("Z") }
        .switchIfEmpty(Observable.just("Zeta", "Eta", "Theta"))
        .subscribe(
            { i -> println("RECEIVED: $i") },
            { e -> println("RECEIVED ERROR: $e") }
        )

    /*
        Prints:
        RECEIVED: Zeta
        RECEIVED: Eta
        RECEIVED: Theta
     */

}

/**
 * 🔥 INFO sorted()
 *
 * If you have a finite **Observable<T>** emitting items that implement Comparable<T>, you can use **sorted()** to sort the emissions.
 * Internally, it will **collect all the emissions** and then **re-emit** them in their **sorted order**.
 */
private fun testSortedOperator() {
    Observable.just(6, 2, 5, 7, 1, 4, 9, 8, 3)
        .sorted()
        .subscribe(System.out::print)
    /*
        Prints:
        123456789
     */

    println("")
    // With Comparator

    Observable.just(6, 2, 5, 7, 1, 4, 9, 8, 3)
        .sorted(Comparator.reverseOrder())
        .subscribe(System.out::print)


}

/**
 * Since Comparator is a single-abstract-method interface,
 * you can implement it quickly with a lambda.
 * Specify the two parameters representing two emissions,
 * and then map them to their comparison operation.
 * We can use this to sort string emissions by their lengths, for instance:
 */
private fun testSortedOperatorWithComparator() {
    Observable.just(
        "Alpha", "Beta", "Gamma", "Delta",
        "Epsilon"
    )
        .sorted { x, y -> Integer.compare(x.length, y.length) }
        .subscribe { println(it) }

    /*
        Prints:
        Beta
        Alpha
        Gamma
        Delta
        Epsilon
     */
}

/**
 * 🔥 INFO delay()
 *
 * We can postpone emissions using the delay() operator.
 * It will hold any received emissions and delay each one for the specified time period.
 */
private fun testDelayOperator() {
    println("testDelayOperator()")
    Observable.just(
        "Alpha", "Beta", "Gamma", "Delta", "Epsilon"
    )
        .delay(3, TimeUnit.SECONDS)
        .subscribe { s -> println("Received: $s") }

    // 🔥🔥🔥 Delays 3 seconds and emits all values just after delay

    /*
    INFO
        Because delay() operates on a different scheduler (such as Observable.interval()),
        we need to leverage a sleep() method to keep the application alive long enough to see this happen.
        Each emission will be delayed by three seconds. You can pass an optional third Boolean argument indicating
        whether you want to delay error notifications as well.

        For more advanced cases, you can pass another Observable as your delay() argument,
        and this will delay emissions until that other Observable emits something.
     */
}

/**
 * 🔥 INFO repeat()
 *
 * The **repeat()** operator will repeat subscription upstream after **onComplete()** a specified number of times.
 *
 */
private fun testRepeatOperator() {

    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .repeat(2)
        .subscribe(
            { s ->
                println("Received: $s")
            },
            {
                println("onError(): $it")
            },
            {
                println("onComplete()")
            })

    /*
        Prints: when times argument is 2 for repeat() operator
        Received: Alpha
        Received: Beta
        Received: Gamma
        Received: Delta
        Received: Epsilon

        Received: Alpha
        Received: Beta
        Received: Gamma
        Received: Delta
        Received: Epsilon

        onComplete()
     */

    /*
    INFO
      If you do not specify a number, it will repeat infinitely, forever and onComplete() is not called.
      There is also a repeatUntil() operator that accepts a Boolean Supplier lambda argument
      and will continue repeating until it yields true.
     */
}

/**
 * 🔥 INFO scan()
 *
 * The **scan()** method is a rolling aggregator. For every emission, you add it to an accumulation.
 * Then, it will emit each incremental accumulation.
 * For instance, you can emit the rolling sum for each emission by passing a lambda to the **scan()** method
 * that adds each next emission to the accumulator:
 */
private fun testScanOperator() {

    Observable.just(5, 3, 7, 10, 2, 14)
        .scan { accumulator, next -> accumulator + next }
        .subscribe { s -> println("Received: $s") }

    /*
        Prints:
        Received: 5
        Received: 8
        Received: 15
        Received: 25
        Received: 27
        Received: 41
     */

    /*
     INFO
       The scan() method emits the rolling accumulation for each emission,
       whereas reduce() yields a single emission reflecting the final accumulation once onComplete() is called.
       scan() can be used on infinite Observables safely since it does not require an onComplete() call.
     */
}

/**
 * If we wanted to emit **the rolling count of emissions**,
 * we can **provide an initial value of 0 and just add 1 to it for every emission**.
 * Keep in mind that the initial value will be emitted first, so use **skip(1)** after **scan()**
 * if you do not want that initial emission
 */
private fun testScanOperatorWithString() {
//    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
//        .scan(0, object : BiFunction<Int, String, Int> {
//
//            @Throws(Exception::class)
//            override fun apply(integer: Int, s: String): Int {
//                return integer + 1
//            }
//        })
//        .subscribe { s -> println("RECEIVED: $s") }

    val observable = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .scan(0, { integer, next -> integer + 1 })
        .subscribe { s -> println("RECEIVED: $s") }

    /*
        Prints
        RECEIVED: 0
        RECEIVED: 1
        RECEIVED: 2
        RECEIVED: 3
        RECEIVED: 4
        RECEIVED: 5
     */

}

private fun testScanOperatorFibonacci() {

    // Fibonacci
    // Index    0,  1,  2,  3,  4,  5,  6, 7
    // Result   0,  1,  1,  2,  3,  5,  8, 13

    Observable.interval(1, TimeUnit.SECONDS)
        .scan(0.0, { accumulator, next ->
            println("accumulator: $accumulator, next: $next")
            accumulator + next
        })
        .subscribe {
            println("Total: $it")
        }

    sleep(10000)

}