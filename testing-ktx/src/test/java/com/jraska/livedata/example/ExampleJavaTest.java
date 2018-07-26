package com.jraska.livedata.example;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import com.jraska.livedata.TestObserver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExampleJavaTest {
  @Rule public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

  private ExampleCounter counter;

  @Before
  public void setUp() {
    counter = new ExampleCounter();
  }

  @Test
  public void directAssertion() {
    LiveData<Integer> liveData = counter.counterLiveData();

    TestObserver.test(liveData)
      .assertNoValue()
      .assertHistorySize(0)
      .assertNever(value -> value > 0);

    counter.triggerCountingToFour();

    TestObserver.test(counter.counterLiveData())
      .assertHasValue()
      .assertValue(value -> value > 3)
      .assertValue(4)
      .assertHistorySize(1) // notice the history size is one since we created new observer
      .assertNever(value -> value > 4);
  }

  @Test
  public void counterHistoryTest() {
    LiveData<Integer> liveData = counter.counterLiveData();
    TestObserver<Integer> testObserver = TestObserver.test(liveData);

    testObserver.assertNoValue()
      .assertHistorySize(0)
      .assertNever(value -> value > 0);

    counter.triggerCountingToFour();

    testObserver.assertHasValue()
      .assertValue(value -> value > 3)
      .assertValue(4)
      .assertHistorySize(4)
      .assertNever(value -> value > 4);
  }

  @Test
  public void usingAssertJ() {
    LiveData<Integer> liveData = counter.counterLiveData();
    TestObserver<Integer> testObserver = TestObserver.test(liveData);

    counter.triggerCountingToFour();

    Integer value = testObserver.value();
    assertThat(value).isEqualTo(4);

    List<Integer> valueHistory = testObserver.valueHistory();
    assertThat(valueHistory).containsExactly(1, 2, 3, 4);

    testObserver.dispose();
    assertThat(liveData.hasObservers()).isFalse();
  }

  @Test
  public void useObserverByYourself() {
    TestObserver<Integer> testObserver = TestObserver.create();

    counter.counterLiveData().observeForever(testObserver);
    counter.triggerCountingToFour();

    testObserver.assertHasValue()
      .assertValue(4)
      .assertHistorySize(4)
      .assertNever(value -> value > 4);

    // Potential remove needs to be handled by you
    counter.counterLiveData().removeObserver(testObserver);
  }
}
