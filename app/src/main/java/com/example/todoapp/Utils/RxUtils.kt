package com.example.todoapp.Utils

import java.util.concurrent.Callable
import io.reactivex.Observable

class RxUtils {
    companion object {
        fun <T> makeObservable(func: Callable<T>): Observable<T> {
            return Observable.create{ e ->
                    try {
                        val observed = func.call()
                        if (observed != null) {
                            // to make defaultIfEmpty work
                            e.onNext(observed)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
        }
    }
}