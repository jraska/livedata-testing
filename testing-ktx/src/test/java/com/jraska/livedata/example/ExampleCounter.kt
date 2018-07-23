package com.jraska.livedata.example

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

class ExampleCounter {
  private val counterData = MutableLiveData<Int>()

  fun triggerCountingToFour() {
    counterData.apply {
      value = 1
      value = 2
      value = 3
      value = 4
    }
  }

  fun counterLiveData(): LiveData<Int> {
    return counterData
  }
}
