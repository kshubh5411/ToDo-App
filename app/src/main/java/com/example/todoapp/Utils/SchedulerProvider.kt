package com.example.todoapp.Utils


import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.Nullable
import io.reactivex.schedulers.Schedulers

class SchedulerProvider{}  // Prevent direct instantiation.
/*private constructor() : BaseSchedulerProvider {

    @NonNull
    override
    fun computation(): Scheduler {
        return Schedulers.computation()
    }

    @NonNull
    override
    fun io(): Scheduler {
        return Schedulers.io()
    }

    @NonNull
    override
    fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    companion object {
        private lateinit var INSTANCE: SchedulerProvider

        @get:Synchronized
        val instance: SchedulerProvider
            get() {
                if (INSTANCE == null) {
                    INSTANCE = SchedulerProvider()
                }
                return INSTANCE
            }
    }
}
*/
