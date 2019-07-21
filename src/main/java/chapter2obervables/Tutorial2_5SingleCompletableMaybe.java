package chapter2obervables;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class Tutorial2_5SingleCompletableMaybe {

    public static void main(String[] args) {
//        testSingle();
//        testSingleFromObservable();


        testMaybe();
//        testMaybeFromObservable();
    }


    private static void testSingle() {
        Disposable hello = Single.just("Hello")
                .map(String::length)
                .subscribe(
                        // onSuccess
                        s ->
                                System.out.println("onSuccess " + s),
                        // onError
                        error ->
                                System.out.println("onError() " + error.getMessage())
                );


        Single.just("").subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    private static void testSingleFromObservable() {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma");

        source.first("Nil") //returns a Single
                .subscribe(System.out::println);
    }


    private static void testMaybe() {

        // has emission
        Maybe<Integer> presentSource = Maybe.just(100);
        presentSource.subscribe(s -> System.out.println("Process 1 received: " + s),
                Throwable::printStackTrace,
                () -> System.out.println("Process 1 done!"));

        // has emission
//        Maybe<Integer> emptySource = Maybe.just(12);

        //no emission
        Maybe<Integer> emptySource = Maybe.empty();

        emptySource.subscribe(
                s -> System.out.println("Process 2 received: " + s),
                Throwable::printStackTrace,
                () -> System.out.println("Process 2 done!")
        );


    }


    private static void testMaybeFromObservable() {

        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");

        source
                .firstElement() // Transforms Observable into Maybe
                .subscribe(
                        s -> System.out.println("RECEIVED " + s),
                        Throwable::printStackTrace,
                        () -> System.out.println("Done!")
                );

    }



}


