package chapter2obervables;


import io.reactivex.Observable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class Tutorial2_4OtherObservableSources {

    // Variables for Defer example
    private static int start = 1;
    private static int count = 3;

    public static void main(String[] args) throws InterruptedException {

//        testObservableRange();

//        testObservableInterval();
//        testObservableIntervalMultiple();

//        testObservableFuture();

//        testObservableEmpty();
//        testObservableNever();

//        testObservableDefer();


        observeCallable();

    }


    private static void testObservableRange() {

        System.out.println("testObservableRange()");

//        Observable.range(1, 10)
//                .subscribe(s -> System.out.println("RECEIVED: " + s));

        Observable.range(5, 3)
                .subscribe(s -> System.out.println("RECEIVED: " + s));

    }

    private static void testObservableInterval() {

        System.out.println("testObservableInterval()");

        Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(s -> System.out.println(s + " Mississippi"));

        // This is for program not exiting because interval is executed in Scheduler thread
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testObservableIntervalMultiple() throws InterruptedException {

        System.out.println("testObservableIntervalMultiple()");


        Observable<Long> seconds = Observable.interval(1,
                TimeUnit.SECONDS);
        //Observer 1
        seconds.subscribe(l -> System.out.println("Observer 1: " + l));
        //sleep 5 seconds
        sleep(5000);

        // WARNING Observable2 starts from 0
        //Observer 2
        seconds.subscribe(l -> System.out.println("Observer 2: " + l + " sec"));
        //sleep 5 seconds
        sleep(5000);
    }

    private static void testObservableFuture() {

        System.out.println("testObservableFuture()");

        // Future awaits for asynchronous task to end returns result with future.get()
        Future<String> future = new Future<String>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public String get() throws InterruptedException, ExecutionException {
                Thread.sleep(5000);
                return "I am from the future";
            }

            @Override
            public String get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                Thread.sleep(5000);
                return "I am from the future";
            }

        };
        Observable.fromFuture(future)
                .subscribe(System.out::println);

    }


    private static void testObservableEmpty() {

        System.out.println("testObservableEmpty()");

        Observable<String> empty = Observable.empty();
        empty.subscribe(System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("Done!"));
    }

    private static void testObservableNever() throws InterruptedException {

        System.out.println("testObservableNever()");

        Observable<String> empty = Observable.never();
        empty.subscribe(System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("Done!"));
        sleep(5000);

    }


    /**
     * If you subscribe to this Observable, modify the count, and then subscribe again,
     * you will find that the second Observer does not see this change:
     *
     * <p></p>
     * To remedy this problem of Observable sources not capturing state changes,
     * you can create a fresh Observable for each subscription.
     * This can be achieved using {@link Observable#defer(Callable)},
     * which accepts a lambda instructing how to create an Observable for every subscription.
     * Because this creates a new Observable each time,
     * it will reflect any changes driving its parameters:
     */


    private static void testObservableDefer() {


        System.out.println("testObservableDefer()");

        Observable<Integer> source = Observable.range(start, count);
        source.subscribe(i -> System.out.println("Observer 1: " + i));
        //modify count
        count = 5;
        source.subscribe(i -> System.out.println("Observer 2: " + i));

        /*
            Prints:
            Observer 1: 1
            Observer 1: 2
            Observer 1: 3

            Observer 2: 1
            Observer 2: 2
            Observer 2: 3
         */


        Observable<Integer> sourceDefer = Observable.defer(() ->
                Observable.range(start, count));
        sourceDefer.subscribe(i -> System.out.println("Defer Observer 1: " + i));
        //modify count
        count = 5;
        sourceDefer.subscribe(i -> System.out.println("Defer Observer 2: " + i));

        /*
            Prints
            Observer 1: 1
            Observer 1: 2
            Observer 1: 3

            Observer 2: 1
            Observer 2: 2
            Observer 2: 3
            Observer 2: 4
            Observer 2: 5
         */
    }


    private static void observeCallable() {

        // 🔥 Throws ArithmeticException
//        Observable.just(1 / 0)
//                .subscribe(
//                        i -> System.out.println("RECEIVED: " + i),
//                        e -> System.out.println("Error Captured: " + e)
//                );


        Observable.fromCallable(() -> 1 / 0)
                .subscribe(
                        i -> System.out.println("Received: " + i),
                        e -> System.out.println("Error Captured: " + e)
                );
    }
}


