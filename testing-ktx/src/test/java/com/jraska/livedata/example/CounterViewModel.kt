package com.jraska.livedata.example

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class CounterViewModel {
  private val counterData = MutableLiveData<Int>()

  init {
    counterData.value = 0
  }

  fun counterLiveData(): LiveData<Int> {
    return counterData
  }

  fun plusButtonClicked() {
    counterData.value = counterData.value!! + 1
  }

  fun minusButtonClicked() {
    val value = counterData.value!!

    if (value > 0)
      counterData.value = value - 1
  }
}
