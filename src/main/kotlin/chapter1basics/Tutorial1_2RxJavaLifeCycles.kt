package chapter1basics

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


/**
 * doOnDispose() has a very narrow use case,
 * where the observable is explicitly disposed.
 *
 * In your example, the observable terminates "naturally" by onComplete().
 *
 * By the time that you call dispose(),
 * the observable is done, and nothing will happen -- disposing a completed observable has no effect.
 */
fun main() {


//    observableLifeCycle()

    // INFO map
//    observableLifeCycleWithMap()
//    observableLifeCycleWithMap2()

    // INFO flatMap
    observableLifeCycleWithFlatMap()
}


/**
 * 🔥🔥🔥 Life cycle of Observable from subscription to completion
 *
 * *subscribeOn affects upstream operators (operators above the subscribeOn)
 * *observeOn affects downstream operators (operators below the observeOn)
 * *If only subscribeOn is specified, all operators will be be executed on that thread
 * *If only observeOn is specified, all operators will be executed on the current thread and only operators below
 * the observeOn will be switched to thread specified by the observeOn
 */

private fun observableLifeCycle() {

    val source = Observable.just("Alpha", "Beta", "Gamma")


    source
        .doOnSubscribe {
            println("doOnSubscribe() thread: ${Thread.currentThread().name}")
        }
        .doOnEach {
            println("🎃 doOnEach() thread: ${Thread.currentThread().name}, event: ${it}, val: ${it.value}")
        }
        .doOnNext {
            println("🥶 doOnNext() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doAfterNext {
            println("😍 doAfterNext() thread: ${Thread.currentThread().name}, val: $it")
        }

        .doOnComplete {
            println("doOnComplete() thread: ${Thread.currentThread().name}")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("doOnError() ${it.message}")
        }

        .subscribe(
            {
                println("😎 subscribe() -> onNext(): thread: ${Thread.currentThread().name}, val: $it")
            },
            {
                println("😎 subscribe() -> onError(): thread: ${Thread.currentThread().name}, error: ${it.message}")

            }
        )

    Thread.sleep(1000)

    /*
        Prints:
        doOnSubscribe() thread: main
        🎃 doOnEach() thread: main, event: OnNextNotification[Alpha], val: Alpha
        🥶 doOnNext() thread: main, val: Alpha
        😎 subscribe() -> onNext(): thread: main, val: Alpha
        😍 doAfterNext() thread: main, val: Alpha
        🎃 doOnEach() thread: main, event: OnNextNotification[Beta], val: Beta
        🥶 doOnNext() thread: main, val: Beta
        😎 subscribe() -> onNext(): thread: main, val: Beta
        😍 doAfterNext() thread: main, val: Beta
        🎃 doOnEach() thread: main, event: OnNextNotification[Gamma], val: Gamma
        🥶 doOnNext() thread: main, val: Gamma
        😎 subscribe() -> onNext(): thread: main, val: Gamma
        😍 doAfterNext() thread: main, val: Gamma
        🎃 doOnEach() thread: main, event: OnCompleteNotification, val: null
        doOnComplete() thread: main
        doOnTerminate() thread: main
        doFinally() thread: main
        doAfterTerminate() thread: main

     */

}


/**
 * *subscribeOn affects upstream operators (operators above the subscribeOn)
 * *observeOn affects downstream operators (operators below the observeOn)
 * *If only subscribeOn is specified, all operators will be be executed on that thread
 * *If only observeOn is specified, all operators will be executed on the current thread and only operators below
 * the observeOn will be switched to thread specified by the observeOn
 */
private fun observableLifeCycleWithMap() {

    val source = Observable.just("Alpha", "Beta", "Gamma")


    source
        .doOnSubscribe {
            println("doOnSubscribe() thread: ${Thread.currentThread().name}")
        }
        .doOnEach {
            println("🎃 doOnEach() thread: ${Thread.currentThread().name}, event: ${it}, val: ${it.value}")
        }
        .doOnNext {
            println("🥶 doOnNext() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doAfterNext {
            println("😍 doAfterNext() thread: ${Thread.currentThread().name}, val: $it")
        }

        .doOnComplete {
            println("doOnComplete() thread: ${Thread.currentThread().name}")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("doOnError() ${it.message}")
        }
        // 🔥🔥🔥 Order of map method changes doOnNext -> map -> onSubscribe()
        .map {
            val value = "$it-Map1-"
            println("map() 1 thread: ${Thread.currentThread().name}, val: $value")
            value
        }
        .map {
            val value = it + "Map2"
            println("map() 2 thread: ${Thread.currentThread().name}, val: $value")
            value
        }
        .subscribe(
            {
                println("😎 subscribe() -> onNext(): thread: ${Thread.currentThread().name}, val: $it")
            },
            {
                println("😎 subscribe() -> onError(): thread: ${Thread.currentThread().name}, error: ${it.message}")

            }
        )

    Thread.sleep(1000)


    /*
        Prints:
        doOnSubscribe() thread: main
        🎃 doOnEach() thread: main, event: OnNextNotification[Alpha], val: Alpha
        🥶 doOnNext() thread: main, val: Alpha
        map() 1 thread: main, val: Alpha-Map1-
        map() 2 thread: main, val: Alpha-Map1-Map2
        😎 subscribe() -> onNext(): thread: main, val: Alpha-Map1-Map2
        😍 doAfterNext() thread: main, val: Alpha
        🎃 doOnEach() thread: main, event: OnNextNotification[Beta], val: Beta
        🥶 doOnNext() thread: main, val: Beta
        map() 1 thread: main, val: Beta-Map1-
        map() 2 thread: main, val: Beta-Map1-Map2
        😎 subscribe() -> onNext(): thread: main, val: Beta-Map1-Map2
        😍 doAfterNext() thread: main, val: Beta
        🎃 doOnEach() thread: main, event: OnNextNotification[Gamma], val: Gamma
        🥶 doOnNext() thread: main, val: Gamma
        map() 1 thread: main, val: Gamma-Map1-
        map() 2 thread: main, val: Gamma-Map1-Map2
        😎 subscribe() -> onNext(): thread: main, val: Gamma-Map1-Map2
        😍 doAfterNext() thread: main, val: Gamma
        🎃 doOnEach() thread: main, event: OnCompleteNotification, val: null
        doOnComplete() thread: main
        doOnTerminate() thread: main
        doFinally() thread: main
        doAfterTerminate() thread: main

     */

}


/**
 * Order of map operator determines what side-effects will receive and in which order
 */
private fun observableLifeCycleWithMap2() {

    val source = Observable.just("Alpha", "Beta", "Gamma")

    val disposable = source

        // 🔥🔥🔥 Order of map method changes map -> doOnNext -> onSubscribe()
        .map {
            val value = "$it-Map1-"
            println("map() 1 thread: ${Thread.currentThread().name}, val: $value")
            value
        }

        .doOnSubscribe {
            println("doOnSubscribe() thread: ${Thread.currentThread().name}")
        }
        .doOnEach {
            println("🎃 doOnEach() thread: ${Thread.currentThread().name}, event: ${it.value}")
        }
        .doOnNext {
            println("🥶 doOnNext() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doAfterNext {
            println("😍 doAfterNext() thread: ${Thread.currentThread().name}, val: $it")
        }

        .doOnComplete {
            println("doOnComplete() thread: ${Thread.currentThread().name}")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("doOnError() ${it.message}")
        }

        .map {
            val value = it + "Map2"
            println("map() 2 thread: ${Thread.currentThread().name}, val: $value")
            value
        }


        .subscribe(
            {
                println("😎 subscribe() -> onNext(): thread: ${Thread.currentThread().name}, val: $it")
            },
            {
                println("😎 subscribe() -> onError(): thread: ${Thread.currentThread().name}, error: ${it.message}")
            }
        )

    Thread.sleep(1000)


    /*
        Prints:
        doOnSubscribe() thread: main
        map() 1 thread: RxNewThreadScheduler-1, val: Alpha-Map1-
        🎃 doOnEach() thread: RxNewThreadScheduler-1, event: Alpha-Map1-
        🥶 doOnNext() thread: RxNewThreadScheduler-1, val: Alpha-Map1-
        map() 2 thread: RxNewThreadScheduler-1, val: Alpha-Map1-Map2
        😎 subscribe() -> onNext(): thread: RxNewThreadScheduler-1, val: Alpha-Map1-Map2
        😍 doAfterNext() thread: RxNewThreadScheduler-1, val: Alpha-Map1-
        map() 1 thread: RxNewThreadScheduler-1, val: Beta-Map1-
        🎃 doOnEach() thread: RxNewThreadScheduler-1, event: Beta-Map1-
        🥶 doOnNext() thread: RxNewThreadScheduler-1, val: Beta-Map1-
        map() 2 thread: RxNewThreadScheduler-1, val: Beta-Map1-Map2
        😎 subscribe() -> onNext(): thread: RxNewThreadScheduler-1, val: Beta-Map1-Map2
        😍 doAfterNext() thread: RxNewThreadScheduler-1, val: Beta-Map1-
        map() 1 thread: RxNewThreadScheduler-1, val: Gamma-Map1-
        🎃 doOnEach() thread: RxNewThreadScheduler-1, event: Gamma-Map1-
        🥶 doOnNext() thread: RxNewThreadScheduler-1, val: Gamma-Map1-
        map() 2 thread: RxNewThreadScheduler-1, val: Gamma-Map1-Map2
        😎 subscribe() -> onNext(): thread: RxNewThreadScheduler-1, val: Gamma-Map1-Map2
        😍 doAfterNext() thread: RxNewThreadScheduler-1, val: Gamma-Map1-
        🎃 doOnEach() thread: RxNewThreadScheduler-1, event: null
        doOnComplete() thread: RxNewThreadScheduler-1
        doOnTerminate() thread: RxNewThreadScheduler-1
        doFinally() thread: RxNewThreadScheduler-1
        doAfterTerminate() thread: RxNewThreadScheduler-1
     */

}


private fun observableLifeCycleWithFlatMap() {

    val source = Observable.just("Alpha", "Beta", "Gamma")


    source
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.newThread())
        .doOnSubscribe {
            println("doOnSubscribe() thread: ${Thread.currentThread().name}")
        }
        .doOnEach {
            println("🎃 doOnEach() thread: ${Thread.currentThread().name}, event: ${it}, val: ${it.value}")
        }
        .doOnNext {
            println("🥶 doOnNext() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doAfterNext {
            println("😍 doAfterNext() thread: ${Thread.currentThread().name}, val: $it")
        }

        .doOnComplete {
            println("doOnComplete() thread: ${Thread.currentThread().name}")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("doOnError() ${it.message}")
        }
        // 🔥🔥🔥 Order of map method changes doOnNext -> map -> onSubscribe()
        .flatMap {
            val value = "$it-🏪"
            println("flatMap() 1 thread: ${Thread.currentThread().name}, val: $value")
            Observable.just(value)
        }
        .flatMap {
            val value = "$it-🎂"
            println("flatMap() 2 thread: ${Thread.currentThread().name}, val: $value")
            Observable.just(value)
        }
        .subscribe(
            {
                println("😎 subscribe() -> onNext(): thread: ${Thread.currentThread().name}, val: $it")
            },
            {
                println("😎 subscribe() -> onError(): thread: ${Thread.currentThread().name}, error: ${it.message}")

            }
        )

    Thread.sleep(1000)


    /*
        Prints:
        doOnSubscribe() thread: main
        🎃 doOnEach() thread: main, event: OnNextNotification[Alpha], val: Alpha
        🥶 doOnNext() thread: main, val: Alpha
        flatMap() 1 thread: main, val: Alpha-🏪
        flatMap() 2 thread: main, val: Alpha-🏪-🎂
        😎 subscribe() -> onNext(): thread: main, val: Alpha-🏪-🎂
        😍 doAfterNext() thread: main, val: Alpha
        🎃 doOnEach() thread: main, event: OnNextNotification[Beta], val: Beta
        🥶 doOnNext() thread: main, val: Beta
        flatMap() 1 thread: main, val: Beta-🏪
        flatMap() 2 thread: main, val: Beta-🏪-🎂
        😎 subscribe() -> onNext(): thread: main, val: Beta-🏪-🎂
        😍 doAfterNext() thread: main, val: Beta
        🎃 doOnEach() thread: main, event: OnNextNotification[Gamma], val: Gamma
        🥶 doOnNext() thread: main, val: Gamma
        flatMap() 1 thread: main, val: Gamma-🏪
        flatMap() 2 thread: main, val: Gamma-🏪-🎂
        😎 subscribe() -> onNext(): thread: main, val: Gamma-🏪-🎂
        😍 doAfterNext() thread: main, val: Gamma
        🎃 doOnEach() thread: main, event: OnCompleteNotification, val: null
        doOnComplete() thread: main
        doOnTerminate() thread: main
        doFinally() thread: main
        doAfterTerminate() thread: main

     */

}
