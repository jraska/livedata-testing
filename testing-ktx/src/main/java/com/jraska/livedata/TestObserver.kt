package com.jraska.livedata

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.test(): TestObserver<T> {
  return TestObserver.test(this)
}
