package com.jraska.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class TestObserverTest {
  @get:Rule val testRule = InstantTaskExecutorRule()

  val mutableLiveData = MutableLiveData<Int>()

  @Test
  fun whenTestCalled_thenObserving() {
    val testObserver = mutableLiveData.test()

    assertThat(mutableLiveData.hasObservers()).isTrue()
    testObserver.assertNoValue()

    mutableLiveData.value = 5
    mutableLiveData.value = 6

    assertThat(testObserver.valueHistory()).contains(5, 6)
    testObserver.assertValue(6)

    testObserver.dispose()
    assertThat(mutableLiveData.hasObservers()).isFalse()
  }
}
