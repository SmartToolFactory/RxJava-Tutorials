package chapter2obervables;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.ResourceObserver;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class Tutorial2_6Disposing {

    // This is from testDisposableFromObserver method
    private Disposable disposable;


    private static final CompositeDisposable disposables
            = new CompositeDisposable();

    public static void main(String[] args) throws InterruptedException {
//        testDisposableInterval();

//        testResourceObserver();

        testCompositeDisposable();
    }

    private static void testDisposableInterval() throws InterruptedException {
        Observable<Long> seconds =
                Observable.interval(1, TimeUnit.SECONDS);


        Disposable disposable =
                seconds.subscribe(l -> System.out.println("Received: " + l));
        //sleep 5 seconds
        sleep(5000);

        //dispose and stop emissions
        disposable.dispose();

        if (disposable.isDisposed()) {
            System.out.println("Disposable is disposed!");
        }

        //sleep 5 seconds to prove
        //there are no more emissions
        sleep(5000);

    }


    private void testDisposableFromObserverOnSubscribe() {

        Observer observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            public void onNext(Integer value) {
                //has access to Disposable
            }

            @Override
            public void onError(Throwable e) {
                //has access to Disposable
            }

            @Override
            public void onComplete() {
                //has access to Disposable
            }
        };
    }

    /**
     * {@link ResourceObserver} returns when subscribed with {@link Observable#subscribeWith(Observer)}
     */
    private static void testResourceObserver() {

        System.out.println("testResourceObserver");

        Observable<Long> source =
                Observable.interval(1, TimeUnit.SECONDS);


        ResourceObserver<Long> myObserver = new ResourceObserver<Long>() {
            @Override
            public void onNext(Long value) {
                System.out.println(" onNext()" + value);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError e: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete()");
            }
        };

        // INFO 🔥🔥 ResourceObserver implements both Observab
        //capture Disposable
        Disposable disposable = source.subscribeWith(myObserver);

    }

    private static void testCompositeDisposable() throws InterruptedException {

        Observable<Long> seconds =
                Observable.interval(1, TimeUnit.SECONDS);
        //subscribe and capture disposables
        Disposable disposable1 =
                seconds.subscribe(l -> System.out.println("Observer 1: " +
                        l));
        Disposable disposable2 =
                seconds.subscribe(l -> System.out.println("Observer 2: " +
                        l));


        //put both disposables into CompositeDisposable
        disposables.addAll(disposable1, disposable2);

        //sleep 5 seconds
        sleep(5000);

        //dispose all disposables
        disposables.dispose();

        if (disposables.isDisposed()) {
            System.out.println("CompositeDisposable is disposed!");
        }

        //sleep 5 seconds to prove
        //there are no more emissions
        sleep(5000);

    }


    private static void testDisposableFromCreate() {

        Observable<Integer> source = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {



                try {

                    for (int i = 0; i < 1000; i++) {
                        while (!observableEmitter.isDisposed()) {
                            observableEmitter.onNext(i);
                        }
                    }

                    observableEmitter.onComplete();

                } catch (Throwable e) {
                    observableEmitter.onError(e);
                }
            }
        });





    }

}
