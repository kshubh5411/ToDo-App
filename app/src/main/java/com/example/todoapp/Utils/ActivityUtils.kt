package com.example.todoapp.Utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.reactivex.annotations.NonNull

class ActivityUtils {

    companion object {
        @JvmStatic
        fun replaceFragmentToActivity(
            @NonNull fragmentManager: FragmentManager,
            @NonNull fragment: Fragment,
            frameId: Int,
            tag:String
        ) {
            checkNotNull(fragmentManager)
            checkNotNull(fragment)
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(frameId, fragment,tag)
            transaction.commitAllowingStateLoss()

        }

        @JvmStatic
        fun replaceFragmentToActivityBackstack(
            @NonNull fragmentManager: FragmentManager,
            @NonNull fragment: Fragment,
            frameId: Int,
            tag:String
        ) {
            checkNotNull(fragmentManager)
            checkNotNull(fragment)
            val transaction = fragmentManager.beginTransaction()
            transaction.addToBackStack(tag)
            transaction.replace(frameId, fragment,tag)
            transaction.commitAllowingStateLoss()

        }
        @JvmStatic
        fun removeFragmentToActivity(
            @NonNull fragmentManager: FragmentManager,
            @NonNull fragment: Fragment
        ) {
            checkNotNull(fragmentManager)
            checkNotNull(fragment)
            val transaction = fragmentManager.beginTransaction()
            transaction.remove( fragment)
            transaction.commitAllowingStateLoss()

        }
    }
}