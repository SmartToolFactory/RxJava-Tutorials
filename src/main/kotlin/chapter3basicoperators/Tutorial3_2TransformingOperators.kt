package chapter3basicoperators

import io.reactivex.Observable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun main() {

    // INFO map
//    testMapOperator()

    // INFO startWith
    testStartWithOperator()
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
    val items: Observable<Any> = Observable.just("Alpha", "Beta", "Gamma").map { s -> s as Any }

    val itemsWithCast: Observable<Any> = Observable.just("Alpha", "Beta", "Gamma").cast(Any::class.java)
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
    menu.startWith("COFFEE SHOP MENU").subscribe(System.out::println);

    /*
        Prints:
        COFFEE SHOP MENU
        Coffee
        Tea
        Espresso
        Latte
     */

    /*
        If you want to start with more than one emission, use startWithArray() to accept varargs parameters.
        If we want to add a divider between our header and menu items,
        we can start with both the header and divider as emissions:
     */

    //print menu
    menu.startWithArray("COFFEE SHOP MENU","----------------")
        .subscribe(System.out::println);
}