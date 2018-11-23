package com.jraska.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.MutableLiveData
import org.junit.Rule
import org.junit.Test

class TestLifecycleTest {
  @get:Rule val testRule = InstantTaskExecutorRule()

  private val testLiveData = MutableLiveData<Int>()

  @Test
  fun whenUpdatingLifecycle_thenLiveDataUpdates() {
    val testLifecycle = TestLifecycle.create()
    val testObserver = TestObserver.create<Int>()
    testLiveData.observe(testLifecycle, testObserver)

    testLiveData.value = 1
    testObserver.assertNoValue()

    testLifecycle.handleLifecycleEvent(ON_CREATE)
    testObserver.assertNoValue()

    testLifecycle.handleLifecycleEvent(ON_START)
    testObserver.assertValue(1)

    testLiveData.value = 2
    testObserver.assertValue(2)

    testLifecycle.handleLifecycleEvent(ON_STOP)
    testLiveData.value = 3
    testObserver.assertValue(2)

    testLifecycle.handleLifecycleEvent(ON_RESUME)
    testObserver.assertValue(3)
  }

  @Test
  fun whenResumed_thenDeliversImmediately() {
    val testLifecycle = TestLifecycle.createresumed()
    val testObserver = TestObserver.create<Int>()
    testLiveData.observe(testLifecycle, testObserver)

    testLiveData.value = 1
    testObserver.assertValue(1)
  }
}
