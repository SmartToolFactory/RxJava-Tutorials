package chapterXtests

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import java.util.*
import java.util.concurrent.TimeUnit


fun main() {

//    flatMap()
//    switchMap()
//    concatMap()

    //    andThen()

//    flattenAsObservableOperator()
//    flatMapIterableOperator()

//    delayEachItemWithConcatMap()

    asyncMerge()
//    asyncConcat()
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

    /*
        Prints in random order, below is one outcome
        Prints:
        flatMap onNext() : [bx, dx, ax, cx, fx, ex]
     */
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

    /*
        Prints
        switchMapX onNext() : [fx]
     */
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

    /*
        Outcome is always in same order
        Prints:
        concatMap onNext() : [ax, bx, cx, dx, ex, fx]

     */
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

    /*
        Prints:
        Item: A
        Item: B
        Item: C
        Item: D
        Item: E
     */

}

/*
    Test how  merge vs async behaves when sources are asynchronous
 */

fun <T> Observable<T>.getAsyncItems(): Observable<T> {

    return concatMap {

        val delay = Random().nextInt(10)

        Observable.just(it)
                .delay(delay.toLong(), TimeUnit.MILLISECONDS)
    }
}

private fun asyncMerge() {

    val source1 =
            Observable.just("A", "B", "C", "D", "E").getAsyncItems()
    val source2 =
            Observable.just(1, 2, 3, 4, 5).getAsyncItems()

    Observable.merge(source1, source2).blockingSubscribe {
        print("$it-")
    }
    /*
        Prints:
        A-1-B-C-2-3-4-D-5-E-
        A-1-2-3-B-C-D-4-E-5-

     */
}

private fun asyncConcat() {
    val source1 =
            Observable.just("A", "B", "C", "D", "E").getAsyncItems()
    val source2 =
            Observable.just(1, 2, 3, 4, 5).getAsyncItems()

    Observable.concat(source1, source2).blockingSubscribe {
        print("$it-")
    }

    /*
        Prints:
        A-B-C-D-E-1-2-3-4-5-
     */
}