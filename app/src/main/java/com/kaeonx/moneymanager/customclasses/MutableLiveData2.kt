package com.kaeonx.moneymanager.customclasses

import androidx.lifecycle.LiveData

// Courtesy of https://proandroiddev.com/improving-livedata-nullability-in-kotlin-45751a2bafb7
@Suppress("UNCHECKED_CAST")
class MutableLiveData2<T>(value: T) : LiveData<T>(value) {

    override fun getValue(): T = super.getValue() as T
    public override fun setValue(value: T) = super.setValue(value)
    public override fun postValue(value: T) = super.postValue(value)

}