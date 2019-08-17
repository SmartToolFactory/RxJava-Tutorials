package chapterXtests

import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import java.util.*
import java.util.concurrent.TimeUnit


fun main() {

    flatMap()

    switchMap()

    concatMap()
}

@Throws(Exception::class)
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

@Throws(Exception::class)
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

@Throws(Exception::class)
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