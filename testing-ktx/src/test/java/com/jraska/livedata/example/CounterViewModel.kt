package com.jraska.livedata.example

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.atomic.AtomicInteger

class CounterViewModel {
  private val counterData = MutableLiveData<Int>()
  private val labelData = MutableLiveData<ScreenData>()
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

  fun counterLabel(): LiveData<ScreenData> {
    return labelData
  }

  fun asyncUpdateLabel(label: String) {
    val runnable = Runnable {
      Thread.sleep(10)
      labelData.postValue(ScreenData(Labels(label))) }

    Thread(runnable).start()
  }

  class Labels(val counterLabel: String)

  class ScreenData(val labels: Labels)
}
