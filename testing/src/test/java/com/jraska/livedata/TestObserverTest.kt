package com.jraska.livedata

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class TestObserverTest {
  @get:Rule val testRule = InstantTaskExecutorRule()

  private val testLiveData = MutableLiveData<Int>()

  @Test
  fun whenDisposes_thenNoObservers() {
    val testObserver = TestObserver.test(testLiveData)

    assertThat(testLiveData.hasObservers()).isTrue()
    testObserver.dispose()
    assertThat(testLiveData.hasObservers()).isFalse()
  }

  @Test
  fun whenValuesPublished_thenHistoryRetained() {
    testLiveData.value = 1
    testLiveData.value = 2

    val testObserver = TestObserver.test(testLiveData)

    testLiveData.apply {
      value = 3
      value = 4
      value = 5
    }

    testObserver.assertHistorySize(4)
    assertThat(testObserver.valueHistory()).containsExactly(2, 3, 4, 5)
  }

  @Test
  fun assertingValuePasses() {
    testLiveData.value = 3

    val value = TestObserver.test(testLiveData)
      .assertHasValue()
      .assertNever { it > 3 }
      .assertValue(3)
      .assertValue { it == 3 }
      .assertHistorySize(1)
      .value()

    assertThat(value).isEqualTo(3)
  }

  @Test
  fun whenValueChanges_valueUpdated() {
    val testObserver = TestObserver.test(testLiveData)
    testObserver.assertNoValue()

    testLiveData.value = 3
    testLiveData.value = 4

    val value = testObserver.assertHasValue()
      .assertHasValue()
      .assertHistorySize(2)
      .assertValue(4)
      .assertValue { it == 4 }
      .assertValue { it > 3 }
      .value()

    assertThat(value).isEqualTo(4)
  }

  @Test(expected = AssertionError::class)
  fun hasValuesAssertionFailsOnNoValue() {
    TestObserver.test(testLiveData).assertHasValue()
  }

  @Test(expected = AssertionError::class)
  fun assertingValueFailsOnOtherValue() {
    testLiveData.value = 1

    TestObserver.test(testLiveData).assertValue(3)
  }

  @Test(expected = AssertionError::class)
  fun assertingValueFailsOnNonMatchingPredicate() {
    testLiveData.value = 1

    TestObserver.test(testLiveData).assertValue { it > 1 }
  }

  @Test(expected = AssertionError::class)
  fun assertingNoValueFailsOnValue() {
    testLiveData.value = 1

    TestObserver.test(testLiveData).assertNoValue()
  }

  @Test(expected = AssertionError::class)
  fun assertingValueCountFailsOnValue() {
    testLiveData.value = 1

    TestObserver.test(testLiveData).assertHistorySize(2)
  }

  @Test(expected = AssertionError::class)
  fun assertingNeverFailsOnPreviousValue() {
    val testObserver = TestObserver.test(testLiveData)

    testLiveData.value = 4
    testLiveData.value = 5

    testObserver.assertNever { it == 4 }
  }
}
