package com.jraska.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

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

    val value = testObserver
      .awaitValue()
      .assertHasValue()
      .assertHistorySize(2)
      .assertValue(4)
      .assertValue { it == 4 }
      .assertValue { it > 3 }
      .value()

    assertThat(value).isEqualTo(4)
  }

  @Test
  fun whenAwaits_thenGetsValue() {
    Thread(setValueAsOne()).start()

    TestObserver.test(testLiveData)
      .assertNoValue()
      .awaitValue()
      .assertHasValue()

    Thread(setValueAsTwo()).start()

    TestObserver.test(testLiveData)
      .assertValue(1)
      .awaitNextValue()
      .assertValue(2)
  }

  @Test
  fun whenAwaitsWithTimeout_thenGetsValue() {
    Thread(setValueAsOne()).start()

    TestObserver.test(testLiveData)
      .awaitValue(100, TimeUnit.MILLISECONDS)
      .assertHasValue()

    Thread(setValueAsTwo()).start()

    TestObserver.test(testLiveData)
      .assertValue(1)
      .awaitNextValue(1, TimeUnit.SECONDS)
      .assertValue(2)
  }

  @Test
  fun whenNullValue_thenValueGot() {
    val testObserver = TestObserver.test(testLiveData)
    testObserver.assertNoValue()

    testLiveData.value = null

    testObserver.assertHasValue()
    val value: Int? = testObserver.value()
    assertThat(value).isNull()
  }

  @Test
  fun whenNullValueInMiddleOfList_thenValueGot() {
    val testObserver = TestObserver.test(testLiveData)
    testObserver.assertNoValue()

    testLiveData.value = 1
    testLiveData.value = null
    testLiveData.value = 2

    testObserver.assertHasValue()
    val value: Int? = testObserver.valueHistory()[1]
    assertThat(value).isNull()
  }

  @Test
  fun whenMap_mapsProperly() {
    val testObserver = TestObserver.test(testLiveData).map { it * 2 }
    testObserver.assertNoValue()

    testLiveData.value = 1
    testLiveData.value = 2

    testObserver.assertValue(4)
    testObserver.map { it.toString() }.assertValue("4")
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

  private fun setValueAsOne() = Runnable {
    Thread.sleep(10)
    testLiveData.value = 1
  }

  private fun setValueAsTwo() = Runnable {
    Thread.sleep(10)
    testLiveData.value = 2
  }
}
