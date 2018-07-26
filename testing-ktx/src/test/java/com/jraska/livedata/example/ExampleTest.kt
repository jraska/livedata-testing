package com.jraska.livedata.example

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ExampleTest {
  @get:Rule val testRule = InstantTaskExecutorRule()

  private lateinit var counter: ExampleCounter

  @Before
  fun before() {
    counter = ExampleCounter()
  }

  @Test
  fun directAssertion() {
    counter.counterLiveData()
      .test()
      .assertNoValue()
      .assertHistorySize(0)
      .assertNever { it > 0 }

    counter.triggerCountingToFour()

    counter.counterLiveData()
      .test()
      .assertHasValue()
      .assertValue { it > 3 }
      .assertValue(4)
      .assertHistorySize(1) // notice the history size is one since we created new observer
      .assertNever { it > 4 }
  }

  @Test
  fun counterHistoryTest() {
    val testObserver = counter.counterLiveData().test()

    testObserver.assertNoValue()
      .assertHistorySize(0)
      .assertNever { it > 0 }

    counter.triggerCountingToFour()

    testObserver.assertHasValue()
      .assertValue { it > 3 }
      .assertValue(4)
      .assertHistorySize(4)
      .assertNever { it > 4 }
  }

  @Test
  fun usingAssertJ() {
    val testObserver = counter.counterLiveData().test()

    counter.triggerCountingToFour()

    val value = testObserver.value()
    assertThat(value).isEqualTo(4)

    val valueHistory = testObserver.valueHistory()
    assertThat(valueHistory).containsExactly(1, 2, 3, 4)

    testObserver.dispose()
    assertThat(counter.counterLiveData().hasObservers()).isFalse()
  }
}
