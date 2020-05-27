package chapter5multicastingreplayingcaching

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.*
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit


/**
 * There are a couple implementations of Subject,
 * which is an abstract type that implements both Observable and Observer.
 * This means that you can manually call onNext(), onComplete(), and onError() on a Subject,
 * and it will, in turn, pass those events downstream toward its Observers.
 */
fun main() {
    // INFO PublishSubject
    testPublishSubject()

//    testSubjectAsObserver()

    // INFO Serialization
//    testSubjectSerialization()

    // INFO BehaviorSubject
//    testBehaviorSubject()

    // INFO ReplaySubject
//    testReplaySubject()

    // INFO AsyncSubject
//    testAsyncSubject()

    // INFO UnicastSubject
//    testUnicastSubject()
    testUnicastSubjectWithMultipleObservers()
}


/**
 * INFO 🔥 PublishSubject
 *
 * The simplest Subject type is the PublishSubject, which, like all Subjects,
 * hotly broadcasts to its downstream Observers.
 * Other Subject types add more behaviors, but PublishSubject is the "vanilla" type
 */
fun testPublishSubject() {

    val subject = PublishSubject.create<String>()
    subject
        .map {
            it.length
        }
        .subscribe {
            println(it)
        }

    subject.onNext("Alpha")
    subject.onNext("Beta")
    subject.onNext("Gamma")
    subject.onComplete()

    /*
        Prints:
        5
        4
        5
     */

}

/*
    INFO When to use Subjects


 */
fun testSubjectAsObserver() {

    val source1 = Observable.interval(1, TimeUnit.SECONDS)
        .map { l -> "${l + 1} seconds" }

    val source2 = Observable.interval(300, TimeUnit.MILLISECONDS)
        .map { l -> "${(l + 1) * 300}  milliseconds " }

    val subject = PublishSubject.create<String>()
    subject.subscribe {
        println(it)
    }

    // INFO 🔥 Subject is observer here, it observes both source1 and source2
//    source1.subscribe(subject)
//    source2.subscribe(subject)

    /*
        Prints:
        300  milliseconds
        600  milliseconds
        900  milliseconds
        1 seconds
        1200  milliseconds
        1500  milliseconds
        1800  milliseconds
        2 seconds
        2100  milliseconds
        2400  milliseconds
        2700  milliseconds
        3 seconds
        3000  milliseconds
     */

    // 🔥 With Merge
//    Observable
//        .merge(source1, source2)
//        .subscribe {
//            println("Merge result: $it")
//        }

    /*
        Prints:
        Merge result: 300  milliseconds
        Merge result: 600  milliseconds
        Merge result: 900  milliseconds
        Merge result: 1 seconds
        Merge result: 1200  milliseconds
        Merge result: 1500  milliseconds
        Merge result: 1800  milliseconds
        Merge result: 2 seconds
        Merge result: 2100  milliseconds
        Merge result: 2400  milliseconds
        Merge result: 2700  milliseconds
        Merge result: 3000  milliseconds
     */

    // This example has NOTHING to do with this, i just checked out zip with interval
    // 🔥 With Zip
    Observable.zip(source1, source2, BiFunction { l1: String, l2: String ->
        "SOURCE 1: $l1 SOURCE 2: $l2"
    })
        .subscribe {
            println("$it")
        }

    /*
        Prints:
        SOURCE 1: 1 seconds SOURCE 2: 300  milliseconds
        SOURCE 1: 2 seconds SOURCE 2: 600  milliseconds
        SOURCE 1: 3 seconds SOURCE 2: 900  milliseconds
     */

    Thread.sleep(3000)
}


/**
 * A critical gotcha to note with Subjects is this: the onSubscribe(), onNext(), onError(),
 * and onComplete() calls are not threadsafe! If you have multiple threads calling these four methods,
 * emissions could start to overlap and break the Observable contract, which demands that emissions happen sequentially.
 *
 * If this happens, a good practice to adopt is to call toSerialized() on Subject to yield a safely
 * serialized Subject implementation (backed by the private SerializedSubject). This will safely sequentialize concurrent
 * event calls so no train wrecks occur downstream:

 */
private fun testSubjectSerialization() {

    // INFO serialize subject means thread-safe
    val subject = PublishSubject.create<String>().toSerialized()

    subject.subscribe {
        println("Observer 1: $it")
    }

    subject.onNext("Alpha")
    subject.onNext("Beta")
    subject.onNext("Gamma")
    subject.onComplete()

    subject.subscribe {
        println("Observer 2: $it")
    }


}

/**
 * INFO BehaviorSubject
 *
 * BehaviorSubject will replay the last emitted item to each new Observer downstream.
 * This is somewhat like putting replay(1).autoConnect() after a PublishSubject,
 * but it consolidates these operations into a single optimized Subject implementation that subscribes eagerly to the source:
 */
private fun testBehaviorSubject() {

    val subject = BehaviorSubject.create<String>()
    subject.subscribe {
        println("Observer 1:  $it")
    }

    subject.onNext("Alpha")
    subject.onNext("Beta")
    subject.onNext("Gamma")

    // 🔥 replay(1).autoConnect() returns the same values for Observer 2
    subject.subscribe {
        println("Observer 2:  $it")
    }

    /*
        Prints:
        Observer 1:   Alpha
        Observer 1:   Beta
        Observer 1:   Gamma
        Observer 2:   Gamma
     */
}

/**
 * INFO ReplaySubject
 *
 * ReplaySubject is similar to PublishSubject followed by a cache() operator.
 * It immediately captures emissions regardless of the presence of downstream Observers
 * and optimizes the caching to occur inside the Subject itself:
 */
fun testReplaySubject() {

    val subject = ReplaySubject.create<String>()
    subject.subscribe {
        println("Observer 1:  $it")
    }

    subject.onNext("Alpha")
    subject.onNext("Beta")
    subject.onNext("Gamma")

    subject.subscribe {
        println("Observer 2:  $it")
    }

    /*
        Obviously, just like using a parameterless replay() or a cache() operator,
        you need to be wary of using this with a large volume of emissions or infinite sources
        because it will cache them all and take up memory.
     */

}

/**
 * INFO testAsyncSubject()
 * AsyncSubject has a highly tailored, finite-specific behavior:
 * it will only push the last value it receives, followed by an onComplete() event
 */
fun testAsyncSubject() {

    val subject = AsyncSubject.create<String>()
    subject.subscribe(
        {
            println("Observer 1 onNext():  $it")

        },
        {

        },
        {
            println("Observer 1 onComplete()")

        }
    )
    subject.onNext("Alpha")
    subject.onNext("Beta")
    subject.onNext("Gamma")

    subject.onComplete()

    subject.subscribe(
        {
            println("Observer 2 onNext():  $it")

        },
        {

        },
        {
            println("Observer 2 onComplete()")

        }
    )

    // 🔥 Invoking takeLast(1).replay(1) on Observable gives the same result

    /*
        Prints:
        Observer 1 onNext():  Gamma
        Observer 1 onComplete()
        Observer 2 onNext():  Gamma
        Observer 2 onComplete()
     */

    /*
        INFO
         As you can tell from the preceding command, the last value to be pushed to AsyncSubject
         was Gamma before onComplete() was called.
         Therefore, it only emitted Gamma to all Observers.
         This is a Subject you do not want to use with infinite sources
         since it only emits when onComplete() is called.
     */

    /*
        INFO
         AsyncSubject resembles CompletableFuture from Java 8 as it will do a computation
         that you can choose to observe for completion and get the value.
         You can also imitate AsyncSubject using takeLast(1).replay(1) on an Observable.
         Try to use this approach first before resorting to AsyncSubject.
     */
}


/**
 *  INFO
 *  A UnicastSubject, like all Subjects, will be used to observe and subscribe to the sources.
 *  But it will buffer all the emissions it receives until an Observer subscribes to it,
 *  and then it will release all these emissions to the Observer and clear its cache
 */
fun testUnicastSubject() {

    val subject = UnicastSubject.create<String>()

    Observable.interval(300, TimeUnit.MILLISECONDS)
        .map { l -> "${(l + 1) * 300} milliseconds" }
        .subscribe(subject)

    sleep(2000)

    subject.subscribe {
        println("Observer 1:  $it")
    }

    sleep(2000)

    /*
        Prints:
        Observer 1:  300 milliseconds
        Observer 1:  600 milliseconds
        Observer 1:  900 milliseconds
        Observer 1:  1200 milliseconds
        Observer 1:  1500 milliseconds
        Observer 1:  1800 milliseconds

        // 🔥 Up to this point emissions will be fired instantly since here subject subscribes to Observable

        Observer 1:  2100 milliseconds
        Observer 1:  2400 milliseconds
        Observer 1:  2700 milliseconds
        Observer 1:  3000 milliseconds
        Observer 1:  3300 milliseconds
        Observer 1:  3600 milliseconds
        Observer 1:  3900 milliseconds
     */

    /*
        INFO
         When you run this code, you will see that after 2 seconds,
         the first six emissions are released immediately to the Observer when it subscribes.
         Then, it will receive live emissions from that point on.
         But there is one important property of UnicastSubject; it will only work with
         one Observer and will throw an error for any subsequent ones.
         Logically, this makes sense because it is designed to release buffered emissions
         from its internal queue once it gets an Observer. But when these cached emissions are released,
         they cannot be released again to a second Observer since they are already gone.

          If you want a second Observer to receive missed emissions, you might as well use ReplaySubject.
          The benefit of UnicastSubject is that it clears its buffer,
          and consequently frees the memory used for that buffer, once it gets an Observer.
     */

}

/**
 * If you want to support more than one Observer and just let subsequent Observers
 * receive the live emissions without receiving the missed emissions,
 * you can trick it by calling publish() to create a single Observer proxy that multicasts
 * to more than one Observer
 */
private fun testUnicastSubjectWithMultipleObservers() {

    val subject = UnicastSubject.create<String>()

    Observable.interval(300, TimeUnit.MILLISECONDS)
        .map { l -> "${(l + 1) * 300} milliseconds" }
        .subscribe(subject)

    sleep(1000)

    //multicast to support multiple Observers
    val multicast = subject.publish().autoConnect()

    //bring in first Observer
    multicast.subscribe {
        println("🚗 Observer 1:  $it")
    }

    sleep(1000)

    //bring in second Observer
    multicast.subscribe {
        println("🤑 Observer 2:  $it")
    }

    sleep(1000)

    /*
        Prints:
        🚗 Observer 1:  300 milliseconds
        🚗 Observer 1:  600 milliseconds
        🚗 Observer 1:  900 milliseconds

        // 🔥 Up to this point emissions will be fired instantly since here subject subscribes to Observable

        🚗 Observer 1:  1200 milliseconds
        🚗 Observer 1:  1500 milliseconds
        🚗 Observer 1:  1800 milliseconds
        🚗 Observer 1:  2100 milliseconds
        🤑 Observer 2:  2100 milliseconds
        🚗 Observer 1:  2400 milliseconds
        🤑 Observer 2:  2400 milliseconds
        🚗 Observer 1:  2700 milliseconds
        🤑 Observer 2:  2700 milliseconds
        🚗 Observer 1:  3000 milliseconds
        🤑 Observer 2:  3000 milliseconds

     */


}