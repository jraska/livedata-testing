package com.jraska.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.junit.Rule
import org.junit.Test

class TestLifecycleTest {
  @get:Rule val testRule = InstantTaskExecutorRule()

  private val testLiveData = MutableLiveData<Int>()

  @Test
  fun whenUpdatingLifecycle_thenLiveDataUpdates() {
    val testLifecycle = TestLifecycle.initialized()
    val testObserver = TestObserver.create<Int>()
    testLiveData.observe(testLifecycle, testObserver)

    testLiveData.value = 1
    testObserver.assertNoValue()

    testLifecycle.create()
    testObserver.assertNoValue()

    testLifecycle.start()
    testObserver.assertValue(1)

    testLiveData.value = 2
    testObserver.assertValue(2)

    testLifecycle.stop()
    testLiveData.value = 3
    testObserver.assertValue(2)

    testLifecycle.resume()
    testObserver.assertValue(3)
  }

  @Test
  fun whenResumed_thenDeliversImmediately() {
    val testLifecycle = TestLifecycle.resumed()
    val testObserver = TestObserver.create<Int>()
    testLiveData.observe(testLifecycle, testObserver)

    testLiveData.value = 1
    testObserver.assertValue(1)
  }
}
