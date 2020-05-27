package chapterXtests

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import java.util.*
import java.util.concurrent.TimeUnit


fun main() {

    flatMap()
    switchMap()
    concatMap()

    //    andThen()

    flattenAsObservableOperator()
//    flatMapIterableOperator()
}

fun flatMap() {

    println("flatMap()")

    val items = arrayOf("a", "b", "c", "d", "e", "f")

    val scheduler = TestScheduler()

    Observable.fromArray(*items)
        .flatMap { s ->
            val delay = Random().nextInt(10)
            Observable.just(s + "x")
                .delay(delay.toLong(), TimeUnit.SECONDS, scheduler)
        }
        .toList()
        .subscribe { strings -> println("flatMap onNext() : $strings") }

    scheduler.advanceTimeBy(1, TimeUnit.MINUTES)
}

fun switchMap() {

    println("switchMap()")

    val items = arrayOf("a", "b", "c", "d", "e", "f")

    val scheduler = TestScheduler()

    Observable.fromArray(*items)
        .switchMap { s ->
            val delay = Random().nextInt(10)
            Observable.just(s + "x")
                .delay(delay.toLong(), TimeUnit.SECONDS, scheduler)
        }
        .toList()
        .subscribe { strings -> println("switchMapX onNext() : $strings") }

    scheduler.advanceTimeBy(1, TimeUnit.MINUTES)
}

fun concatMap() {

    println("concatMap()")

    val items = arrayOf("a", "b", "c", "d", "e", "f")

    val scheduler = TestScheduler()

    Observable.fromArray(*items)
        .concatMap { s ->
            val delay = Random().nextInt(10)
            Observable.just(s + "x")
                .delay(delay.toLong(), TimeUnit.SECONDS, scheduler)
        }
        .toList()
        .subscribe { strings -> println("concatMap onNext() : $strings") }

    scheduler.advanceTimeBy(1, TimeUnit.MINUTES)
}

private fun andThen() {

    Single.just("Hello")
        .flatMapCompletable {
            Completable.complete()
        }
        .andThen(Maybe.just("Maybe"))
        .subscribe {
            println("Result $it")
        }

}

private fun flattenAsObservableOperator() {

    val numbers = listOf(1, 2, 3, 4, 5)


    Single.just(numbers).flattenAsObservable {
        it
    }.subscribe {
        println("flattenAsObservableO() onNext(): Result $it")
    }
}

private fun flatMapIterableOperator() {

    val numbers = listOf(1, 2, 3, 4, 5)


    Observable.just(numbers).flatMapIterable {
        it
    }.subscribe {
        println("flatMapIterable() onNext(): Result $it")
    }

}

private fun delayEachItemWithConcatMap() {

    val source1 = Observable.just("A", "B", "C", "D", "E")

    source1.concatMap {
        Observable.just(it)
//            .map {
//                println("🤝Before delay $it")
//                it
//            }
            .delay(1, TimeUnit.SECONDS)
//            .map {
//                println("🤪 After delay $it")
//                it
//            }
    }
        .blockingSubscribe {
            println("Item: $it")
        }

}