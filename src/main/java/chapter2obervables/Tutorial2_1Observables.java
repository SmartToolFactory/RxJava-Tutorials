package chapter2obervables;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class Tutorial2_1Observables {

    public static void main(String[] args) {

//        testObservableCreate();
//        testObservableCreateWithTry();
        testObservableWithOperators();

    }

    private static void testObservableCreate() {
        Observable<String> source = Observable.create(emitter -> {
            emitter.onNext("Alpha");
            emitter.onNext("Beta");
            emitter.onNext("Gamma");
            emitter.onNext("Delta");
            emitter.onNext("Epsilon");
            emitter.onComplete();
        });
        source.subscribe(s -> System.out.println("RECEIVED: " + s));
    }


    private static void testObservableCreateWithTry() {
        Observable<String> source = Observable.create(emitter -> {
            try {
                emitter.onNext("Alpha");
                emitter.onNext("Beta");
                emitter.onNext("Gamma");
                emitter.onNext("Delta");
                emitter.onNext("Epsilon");
                emitter.onComplete();
            } catch (Throwable e) {
                emitter.onError(e);
            }
        });
        source.subscribe(s -> System.out.println("RECEIVED: " + s),
                Throwable::printStackTrace);
    }

    private static void testObservableWithOperators() {

        Observable<String> source = Observable.create(emitter -> {

            try {
                emitter.onNext("Alpha");
                emitter.onNext("Beta");
                emitter.onNext("Gamma");
                emitter.onNext("Delta");
                emitter.onNext("Epsilon");
                emitter.onComplete();
            } catch (Throwable e) {
                emitter.onError(e);
            }

        });

        // Map NEW observable to length of the Strings

        // 🔥 Explicit Declaration
        Observable<Integer> lengths = source.map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return s.length();
            }
        });

        // 🔥 Lambda Declaration
        //  Observable<Integer> lengths = source.map(String::length);


        // Filter out strings that length is less than 5

        // 🔥 Explicit Declaration
        Observable<Integer> filtered = lengths.filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return (integer >= 5);
            }
        });

        // 🔥 Lambda Declaration
//        Observable<Integer> filtered = lengths.filter(i -> i >= 5);

        // Writes length of the original emitted strings
//        filtered.subscribe(s -> System.out.println("RECEIVED: " + s));

        // Same as the one above
//        source.map(String::length)
//                .filter(i -> i >= 5)
//                .subscribe(s -> System.out.println("RECEIVED: " + s));
    }

    private void testObservableJust() {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");
        source.map(String::length).filter(i -> i >= 5)
                .subscribe(s -> System.out.println("RECEIVED: " + s));
    }
}

