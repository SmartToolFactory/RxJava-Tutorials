package chapter2observables

import io.reactivex.Observable

fun main() {

}


private fun testColdObservable() {

    val source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
    //first observer
    source.subscribe { s -> println("Observer 1 Received: $s") }
    //second observer
    source.subscribe { s -> println("Observer 2 Received: $s") }
}