package chapter2obervables;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class Tutorial2_5SingleCompletableMaybe {

    public static void main(String[] args) {
//        testSingle();
        testSingleFromObservable();
    }


    private static void testSingle() {
        Single.just("Hello")
                .map(String::length)
                .subscribe(
                        // onSuccess
                        s ->
                                System.out.println("onSuccess " + s),
                        // onError
                        error ->
                                System.out.println("onError() " + error.getMessage())
                );
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

        //no emission
        Maybe<Integer> emptySource = Maybe.empty();

        emptySource.subscribe(
                s -> System.out.println("Process 2 received: " + s),
                Throwable::printStackTrace,
                () -> System.out.println("Process 2 done!")
        );

    }


}


