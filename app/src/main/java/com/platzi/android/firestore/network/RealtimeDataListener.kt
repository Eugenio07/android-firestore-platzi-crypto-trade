package com.platzi.android.firestore.network

import java.lang.Exception

interface RealtimeDataListener<T> {
    fun onDataChanged(updatedData: T)
    fun onError(exception: Exception)
}