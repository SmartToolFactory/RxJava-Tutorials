package chapter1basics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class Tutorial1_1RxJavaBasics {

    public static void main(String[] args) {
        System.out.println("Initialized...");
//        fakeUserInputWithInterval().blockingSubscribe(line -> System.out.println("Number: " + line));
//        testSingle();

        testObservable();

//        testCreate();

//        testFromIterable();

//        testMultipleSubscription();

//        testMapOperator();

//        testScanOperator();

    }


    public static Observable<Integer> fakeUserInput() {
        Random random = new Random();

        return Observable.just(10, 17, 3, 8)
                .map(number -> random.nextInt(50));
    }

    private static Observable<Integer> fakeUserInputWithInterval() {
        Random random = new Random();


        return Observable.intervalRange(100, 5, 500, 500, TimeUnit.MILLISECONDS)
                .map(Long::intValue);
    }


    private static void testSingle() {
        //Create the observable
        Single<String> testSingle = Single.just("Hello World");

        //Create an observer
        Disposable disposable = testSingle
                .delay(2, TimeUnit.SECONDS, Schedulers.computation())
                .subscribeWith(
                        new DisposableSingleObserver<String>() {

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onSuccess(String value) {
                                System.out.println(value);
                            }
                        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //start observing
        disposable.dispose();
    }


    private static void testObservable() {


        Observable<String> observableString = Observable.just("Alpha", "Beta", "Gamma", "Epsilon");


        // TODO Accept
        Disposable disposable = observableString
                .subscribe(
                        new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                System.out.println("Res: " + s + ", Thread: " + Thread.currentThread().getName());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        });


//        // TODO SubscribeWith
//        Observer<String> stringObserver = observableString.subscribeWith(new Observer<String>() {
//
//            @Override
//            public void onSubscribe(Disposable d) {
//                System.out.println("onSubscribe d: " + d + ", thread: " + Thread.currentThread().getName());
//
//            }
//
//            @Override
//            public void onNext(String s) {
//                System.out.println("onNext(): " + s + ", thread: " + Thread.currentThread().getName());
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//                System.out.println("onComplete(): " + ", thread: " + Thread.currentThread().getName());
//
//            }
//        });
//
//
////        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
////            @Override
////            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
////
////            }
////        });
//
//
//        // TODO subscribe
//        Observable<String> observable = Observable.create(emitter -> {
//            emitter.onNext("Hello");
//            emitter.onNext("Brave");
//            emitter.onNext("A new World");
//
//            emitter.onError(new Exception("Some exception"));
//        });
//
//        Disposable subscribe = observable
//                .filter(it -> it.length() > 5)
//
//                .subscribe(
//                        s -> {
//                            System.out.println("Observable res: onNext(): " + s);
//                        }, error -> {
//                            System.out.println("Observable res onError() error:" + error.getMessage());
//                        }
//                );
//
//
//        subscribe.dispose();
//
//
//        Observable<Integer> observableInteger = Observable.fromArray(1, 2, 3, 4, 5);
//
//        // TODO subscribe
//        observableInteger
//
//                .map(integer -> integer * 2)
//                .filter(integer -> integer > 4)
//
//                .subscribe(new Observer<Integer>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        System.out.println("observableInteger onSubscribe() d: " + d + ", thread: " + Thread.currentThread().getName());
//                    }
//
//                    @Override
//                    public void onNext(Integer integer) {
//                        System.out.println("observableInteger onNext(): " + integer + ", thread: " + Thread.currentThread().getName());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        System.out.println("observableInteger onError() e:" + e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        System.out.println("observableInteger onComplete(): " + ", thread: " + Thread.currentThread().getName());
//                    }
//                });


    }

    private static void testCreate() {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("One");
                emitter.onNext("Two");
                emitter.onNext("Three");

                emitter.onComplete();

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        System.out.println("ObservableEmitter cancel()");
                    }
                });
            }
        });


        observable.subscribe(
                new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("Observable onNext(): " + s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("Observable onError() " + throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("Observable onComplete()");
                    }
                });
    }


    private static void testFromIterable() {

        List<String> SUPER_HEROES = Arrays.asList("Superman", "Batman", "Aquaman", "Asterix", "Captain America");

        Observable<String> observable = Observable.fromIterable(SUPER_HEROES);

        Disposable disposable = observable.map(name -> {

//                    if (name.endsWith("x")) {
//                        throw new RuntimeException("What a terrible failure!");
//                    }
            return name.toUpperCase();
        })
                // Use doOnNext, doOnComplete and doOnError to print messages
                // on each item, when the stream complete, and when an error occurs
                .doOnNext(s -> System.out.println(">> " + s))
                .doOnComplete(() -> System.out.println("Completion... not called"))
                .doOnError(err -> System.out.println("Oh no! " + err.getMessage()))
//                .subscribe()
                // 🔥 This subscribe() calls onNext() and does not prevent doOnNext to be called
                .subscribe(s -> {
                    System.out.println("Subscribe onNext(): " + s);
                }, throwable -> {
                    System.out.println("Error: " + throwable.getMessage());
                });


        disposable.dispose();

    }


    /**
     * Cold Observable with multiple subscriptions
     */
    private static void testMultipleSubscription() {

        // INFO 🔥
        Observable<String> source = Observable.just("Value1", "Value2", "Value3");
        source.subscribe(s -> System.out.println("Observer1: " + s));
        source.subscribe(s -> System.out.println("Observer2: " + s));

        /*
            Prints:
            Observer1: Value1
            Observer1: Value2
            Observer1: Value3
            Observer2: Value1
            Observer2: Value2
            Observer2: Value3
         */
    }


    private static void testMapOperator() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
                Observable.just("1/3/2016", "5/9/2016", "10/12/2016")
                        .map(s -> LocalDate.parse(s, dtf))
                        .subscribe(i -> System.out.println("RECEIVED: " + i));
    }


    private static void testScanOperator() {
        Disposable disposable = Observable.just(
                "Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .scan(0, new BiFunction<Integer, String, Integer>() {
                    @Override
                    public Integer apply(Integer integer, String s) throws Exception {
                        return integer + 1;
                    }
                })
                .subscribe(s -> System.out.println("RECEIVED: " + s));

        disposable.dispose();
    }

}


