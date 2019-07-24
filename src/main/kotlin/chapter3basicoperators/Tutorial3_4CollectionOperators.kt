package chapter3basicoperators

import io.reactivex.Observable
import io.reactivex.functions.BiConsumer
import io.reactivex.functions.Function
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap


fun main() {

    // INFO toList()
//    testToListOperator()
    testToListCustomOperator()

    // INFO toSortedList()
//    testToSortedListOperator()

    // INFO toMap()
//    testToMapOperator()
//    testToMapCustomOperator()

    // INFO toMultiMap()
//    testToMultiMapOperator()

    // INFO collect()
    testCollect()

}

/**
 * 🔥 INFO toList()
 *
 * For a given **Observable<T>**, it will collect incoming emissions into a **List<T>** and
 * then push that entire **List<T>** as a single emission (through **Single<List<T>>**).
 *
 */
private fun testToListOperator() {

    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .toList()   // INFO returns Single<List<String>>
        .subscribe { stringList ->
            println("onSuccess() Received: $stringList")

            stringList.forEach {
                println("List element: $it")
            }

        }

    /*
        Prints:
        onSuccess() Received: [Alpha, Beta, Gamma, Delta, Epsilon]
        List element: Alpha
        List element: Beta
        List element: Gamma
        List element: Delta
        List element: Epsilon
     */

}

private fun testToListCustomOperator() {

    // WARNING toList() operator with List other than ArrayList

//    // INFO Alternative 1
//    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
//        .toList(object: Callable<Collection<String>> {
//            override fun call(): Collection<String> {
//                return CopyOnWriteArrayList()
//            }
//        })
//
//
//    // INFO Alternative 2
//
//    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
//        .toList(::CopyOnWriteArrayList) // 🔥 Constructor Reference
//        .subscribe { s -> println("Received: $s") }

}

/**
 * 🔥 INFO toSortedList()
 *
 * **toSortedList()** will collect the emissions into a list that sorts the items naturally based on their Comparator implementation.
 * Then, it will emit that **sorted List<T>** forward to the Observer
 *
 */
private fun testToSortedListOperator() {

    Observable.just(6, 2, 5, 7, 1, 4, 9, 8, 3)
        .toSortedList()
        .subscribe { s -> println("Received: $s") }

    /*
        Prints:
        Received: [1, 2, 3, 4, 5, 6, 7, 8, 9]
     */

    /*
     INFO
      Like sorted(), you can provide a Comparator as an argument to apply a different sorting logic.
      You can also specify an initial capacity for the backing ArrayList just like toList().
     */
}

/**
 * 🔥 INFO toMap()
 *
 * For a given **Observable<T>**, the **toMap()** operator will collect emissions into **Map<K,T>**,
 * where K is the key type derived off a lambda **Function<T,K>** argument producing the key for each emission.
 *
 */
private fun testToMapOperator() {

    // TODO Alternative 1
//    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
//
//        .toMap(object : Function< String, Char> {
//            override fun apply(t: String): Char {
//                return t[0]
//            }
//
//        }).subscribe { s -> println("Received: $s") }

    // TODO Alternative 2
    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .toMap { s -> s[0] }
        .subscribe { s -> println("Received: $s") };

    /*
        Prints:
        Received: {A=Alpha, B=Beta, D=Delta, E=Epsilon, G=Gamma}
     */

    /*
     INFO
        The s -> s[0] lambda argument takes each string and derives the key to pair it
        with. In this case, we are making the first character of that string the key.
        If we wanted to yield a different value other than the emission to associate with the key,
        we can provide a second lambda argument that maps each emission to a different value.
        We can, for instance, map each first letter key with the length of that string:
     */

    // INFO Alternative 1

    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .toMap(

            // INFO keySelector
            object : Function<String, Char> {
                override fun apply(s: String): Char {
                    return s[0]
                }

            },

            // INFO valueSelector
            object : Function<String, Int> {
                override fun apply(t: String): Int {
                    return t.length
                }

            })
        .subscribe { s -> println("Received: $s") }

    // INFO Alternative 2
    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .toMap({ s -> s[0] }, { it.length })
        .subscribe { s -> println("Received: $s") }

    /*
        Prints:
        Received: {A=5, B=4, D=5, E=7, G=5}
     */

}

private fun testToMapCustomOperator() {

    // INFO Alternative 1

    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .toMap(

            // INFO keySelector
            object : Function<String, Char> {
                override fun apply(s: String): Char {
                    return s[0]
                }

            },

            // INFO valueSelector
            object : Function<String, Int> {
                override fun apply(t: String): Int {
                    return t.length
                }

            },

            // key = Char, Value = Int
            object : Callable<ConcurrentHashMap<Char, Int>> {

                override fun call(): ConcurrentHashMap<Char, Int> {
                    return ConcurrentHashMap()
                }

            })
        .subscribe { s -> println("Received: $s") }

    // INFO Alternative 2
    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .toMap(
            { s -> s[0] },
            { s -> s.length },
            ::ConcurrentHashMap
        )
        .subscribe { s -> println("Received: $s") };

}

/**
 * 🔥 INFO toMultiMap()
 *
 *If you want a given key to map to multiple emissions,
 * you can use **toMultiMap()** instead, which will maintain a list of corresponding values for each key.
 * Alpha, Gamma, and Delta will then all be put in a list that is keyed off the length five:
 *
 */
private fun testToMultiMapOperator() {

    /*
    INFO
        Note that if I have a key that maps to multiple emissions,
        the last emission for that key is going to replace subsequent ones.
        If I make the string length the key for each emission,
        Alpha is going to be replaced by Gamma, which is going to be replaced by Delta:
     */

    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .toMap(String::length)
        .subscribe { s -> println("Received: $s") }

    /*
        Prints:
        Received: {4=Beta, 5=Delta, 7=Epsilon}
     */

    /*
    INFO
        If you want a given key to map to multiple emissions,
        you can use toMultiMap() instead, which will maintain a list of corresponding values for each key.
        Alpha, Gamma, and Delta will then all be put in a list that is keyed off the length five:
     */

    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        .toMultimap(String::length)
        .subscribe(
            {
                println("onSuccess() Received: $it")
            },
            {
                print("onError ${it.message}")
            })

    // WARNING argument in subscribe is Map<K, Collection<T>>
    /*
        Prints:
        Received: {4=[Beta], 5=[Alpha, Gamma, Delta], 7=[Epsilon]}
     */
}

/**
 * 🔥 INFO collect()
 *
 *  When none of the collection operators have what you need,
 *  you can always use the **collect()** operator to specify a different type to collect items into.
 *
 *  For instance, there is no **toSet()** operator to collect emissions into a **Set<T>**,
 *  but you can quickly use collect() to effectively do this.
 *  You will need to specify **two arguments** that are built with lambda expressions: **initialValueSupplier**,
 *  which will provide a new HashSet for a new Observer, and **collector**, which specifies how each emission is added to that HashSet:
 *
 */
private fun testCollect() {

    // INFO Alternative1
    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Alpha", "Gamma")
        .collect(Callable<HashSet<Any>> { HashSet() }, BiConsumer<HashSet<Any>, String> { obj, e -> obj.add(e) })
        .subscribe { s -> println("Received: $s") }

    // INFO Alternative2
    Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Alpha", "Gamma")

        // initialSupplier, collector
        .collect(::HashSet) { obj: HashSet<Any>, e: String -> obj.add(e) }
        .subscribe { s -> println("Received: $s") }


    /*
        Prints:
        Received: [Gamma, Delta, Alpha, Epsilon, Beta]
     */
}