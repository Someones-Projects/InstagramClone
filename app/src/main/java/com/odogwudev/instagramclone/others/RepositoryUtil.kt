package com.odogwudev.instagramclone.others


import android.util.Log

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "An unknown error occured")
    }
}
