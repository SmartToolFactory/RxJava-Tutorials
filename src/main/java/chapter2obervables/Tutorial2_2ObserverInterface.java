package chapter2obervables;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class Tutorial2_2ObserverInterface {

    public static void main(String[] args) {

        testObserver();
        testObserverLambda();
    }


    private static void testObserver() {

        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");

        Observer<Integer> myObserver = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                //do nothing with Disposable, disregard for now
            }

            @Override
            public void onNext(Integer value) {
                System.out.println("RECEIVED: " + value);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Done!");
            }
        };

        source
                .map(String::length)
                .filter(i -> i >= 5)
                .subscribe(myObserver);
    }

    /**
     * This method accepts 3 lambdas the <strong>onNext</strong> lambda, the <strong>onError</strong> lambda
     * , and the <strong>onComplete</strong> lambda.
     */
    private static void testObserverLambda() {
        Observable<String> source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");

        // INFO Lambdas for onNext, onError, onComplete methods
//        Consumer<Integer> onNext = i -> System.out.println("RECEIVED: " + i);
//        Consumer<Throwable> onError = Throwable::printStackTrace;
//        Action onComplete = () -> System.out.println("Done!");

        source.map(String::length).filter(i -> i >= 5)
                .subscribe(
                        // onNext
                        i -> System.out.println("RECEIVED: " + i),
                        // onError
                        Throwable::printStackTrace,
                        // onComplete
                        () -> System.out.println("Done!")
                );
    }

}


