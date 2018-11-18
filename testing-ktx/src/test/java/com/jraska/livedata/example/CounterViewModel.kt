package com.jraska.livedata.example

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.atomic.AtomicInteger

class CounterViewModel {
  private val counterData = MutableLiveData<Int>()
  private val labelData = MutableLiveData<String>()
  private val counter = AtomicInteger()

  init {
    counterData.value = counter.get()
  }

  fun counterLiveData(): LiveData<Int> {
    return counterData
  }

  fun plusButtonClicked() {
    counterData.value = counter.incrementAndGet()
  }

  fun minusButtonClicked() {
    counterData.value = counter.decrementAndGet()
  }

  fun counterLabel(): LiveData<String> {
    return labelData
  }

  fun asyncUpdateLabel(label: String) {
    val runnable = Runnable {
      Thread.sleep(10)
      labelData.postValue(label) }

    Thread(runnable).start()
  }
}
