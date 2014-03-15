package com.github.davidmoten.rx;

import rx.Observable;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Converts an Operation (a function converting one Observable into another)
 * into an {@link Operator}.
 * 
 * @param <R>
 *            to type
 * @param <T>
 *            from type
 */
public class OperationToOperator<R, T> implements Operator<R, T> {

    public static <R, T> Operator<R, T> toOperator(Func1<Observable<T>, Observable<R>> operation) {
        return new OperationToOperator<R, T>(operation);
    }

    /**
     * The operation to convert.
     */
    private final Func1<Observable<T>, Observable<R>> operation;

    /**
     * Constructor.
     * 
     * @param operation
     *            to be converted into {@link Operator}
     */
    public OperationToOperator(Func1<Observable<T>, Observable<R>> operation) {
        this.operation = operation;
    }

    @Override
    public Subscriber<? super T> call(Subscriber<? super R> subscriber) {
        final PublishSubject<T> subject = PublishSubject.create();
        Subscriber<T> result = createSubscriber(subject);
        subscriber.add(result);
        operation.call(subject).subscribe(subscriber);
        return result;
    }

    /**
     * Creates a subscriber that passes all events on to the subject.
     * 
     * @param subject
     *            receives all events.
     * @return
     */
    private static <T> Subscriber<T> createSubscriber(final PublishSubject<T> subject) {
        return new Subscriber<T>() {

            @Override
            public void onCompleted() {
                subject.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subject.onError(e);
            }

            @Override
            public void onNext(T t) {
                subject.onNext(t);
            }
        };
    }
}