package chapter2obervables;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

public class Tutorial2_3ColdHotObservables {

    public static void main(String[] args) throws InterruptedException {

        // Cold Observables
//        testColdObservable();
//        testColdObservableWithOperators();
//        testColdObservableInterval();
//        testColdObservableCreate();


        // Hot Observables
//        testConnectObservable();
//        testConnectObservableCreate();
        testConnectObservableInterval();

    }


    /*
     *** COLD OBSERVABLES ***
     */

    /**
     * Cold observables start from 0 even they are subscribed to {@link Observable} later on
     */
    private static void testColdObservable() {

        System.out.println("testColdObservable()");


        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        //first observer
        source.subscribe(s -> System.out.println("Observer 1 Received: " + s));
        //second observer
        source.subscribe(s -> System.out.println("Observer 2 Received: " + s));

        /*
            Prints:
            Observer 1 Received: Alpha
            Observer 1 Received: Beta
            Observer 1 Received: Gamma
            Observer 1 Received: Delta
            Observer 1 Received: Epsilon

            Observer 2 Received: Alpha
            Observer 2 Received: Beta
            Observer 2 Received: Gamma
            Observer 2 Received: Delta
            Observer 2 Received: Epsilon
         */

    }

    private static void testColdObservableWithOperators() {

        System.out.println("testColdObservableWithOperators()");

        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        //first observer
        source.subscribe(s -> System.out.println("Observer 1 Received: " + s));
        //second observer
        source.map(String::length).filter(i -> i >= 5)
                .subscribe(s -> System.out.println("Observer 2 Received: " + s));

        /*
           Prints:

           Observer 1 Received: Alpha
           Observer 1 Received: Beta
           Observer 1 Received: Gamma
           Observer 1 Received: Delta
           Observer 1 Received: Epsilon

           Observer 2 Received: 5
           Observer 2 Received: 5
           Observer 2 Received: 5
           Observer 2 Received: 7
         */
    }


    /**
     * Method to test Cold {@link Observable} within an interval
     *
     * @throws InterruptedException
     */
    private static void testColdObservableInterval() throws InterruptedException {
        System.out.println("testColdObservableInterval()");

        Observable<Long> source = Observable.interval(1, TimeUnit.SECONDS);

        source.subscribe(i -> {
            System.out.println("Observer 1 Received: " + i);
        });

        Thread.sleep(3000);
        source
                .map(i -> i + " sec")

                .subscribeOn(Schedulers.computation())

                .subscribe(s -> {
                    System.out.println("Observer 2 Received: " + s);

                });


        Thread.sleep(5000);


        // 🔥 WARNING Observer2 receives emission starting from 0 instead of continuing where emission is left of
        /*
            Prints:
        Observer 1 Received: 0
        Observer 1 Received: 1
        Observer 1 Received: 2
        Observer 1 Received: 3
        Observer 2 Received: 0 sec 🔥 Observer2 subscribes here
        Observer 1 Received: 4
        Observer 2 Received: 1 sec
        Observer 1 Received: 5
        Observer 2 Received: 2 sec
        Observer 1 Received: 6
        Observer 2 Received: 3 sec
        Observer 1 Received: 7
        Observer 2 Received: 4 sec
            ...
         */


    }

    private static void testColdObservableCreate() {

        System.out.println("testColdObservableCreate()");

        Observable<String> observable = Observable.create(emitter -> {

            System.out.println("observable starting to emit...");
            emitter.onNext("Alpha");
            emitter.onNext("Beta");
            emitter.onNext("Gamma");
            emitter.onNext("Delta");
            emitter.onNext("Epsilon");

            /*
                Prints:
                Observer1 onNext(): Alpha
                Observer1 onNext(): Beta
                Observer1 onNext(): Gamma
                Observer1 onNext(): Delta
                Observer1 onNext(): Epsilon

                Observer2 onNext(): 5
                Observer2 onNext(): 4
                Observer2 onNext(): 5
                Observer2 onNext(): 5
                Observer2 onNext(): 7
             */
        });


        Observer observer1 = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String string) {
                System.out.println("Observer1 onNext(): " + string);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };


        Observer observer2 = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("Observer2 onNext(): " + integer);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        observable.subscribe(observer1);

        observable
                .map(String::length)
                .subscribe(observer2);
    }


    /*
     *** HOT OBSERVABLES ***
     */

    private static void testConnectObservable() {

        System.out.println("testConnectObservable()");
        ConnectableObservable<String> source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .publish();

        //Set up observer 1
        source.subscribe(s -> System.out.println("Observer 1: " + s));
        //Set up observer 2
        source.map(String::length)
                .subscribe(i -> System.out.println("Observer 2: " + i));

        //Fire!
        source.connect();
    }

    /**
     * Method to test {@link ConnectableObservable}. ConnectableObservable is used to convert a <strong>COLD</strong> into <strong>hot</strong> observable
     */
    private static void testConnectObservableCreate() {


        Observable<Integer> cold = Observable.create(subscriber -> {
            for (int i = 0; i <= 1; i++) {
                System.out.println("Source Emit " + i);
                subscriber.onNext(i);
            }
        });


        Consumer<Integer> subscriber1 = integer -> System.out.println("Subscriber 1 :" + integer);
        Consumer<Integer> subscriber2 = integer -> System.out.println("Subscriber 2 :" + integer);

        // 🔥 WARNING: invoking cold.publish() and contactable.connect() causes the observable will emit all the items even if there is no subscriber yet.
        ConnectableObservable<Integer> contactable = cold.publish();


        contactable.subscribe(subscriber1);
        contactable.subscribe(subscriber2);

        // This should be after subscribing
        contactable.connect();

        /*
            Prints:

            Source Emit 0
            Subscriber 1 :0
            Subscriber 2 :0

            Source Emit 1
            Subscriber 1 :1
            Subscriber 2 :1

         */

    }

    private static void testConnectObservableInterval() throws InterruptedException {

        Observable<Long> myObservable = Observable.interval(1, TimeUnit.SECONDS);

        ConnectableObservable<Long> connectibleObservable = myObservable.publish();

        connectibleObservable.subscribe(item -> System.out.println("Observer 1 Received: " + item));

        connectibleObservable.connect();

        Thread.sleep(3000);
        connectibleObservable.subscribe(item -> System.out.println("Observer 2 Received: " + item + " sec"));
        Thread.sleep(5000);

        /*
            Prints:
            Observer 1: 0
            Observer 1: 1
            Observer 1: 2
            Observer 1: 3
            Observer 2: 3 🔥🔥 Observer 2 received the same emission as observer 1 upon subscription
            Observer 1: 4
            Observer 2: 4
            Observer 1: 5
            Observer 2: 5

         */
    }


}
