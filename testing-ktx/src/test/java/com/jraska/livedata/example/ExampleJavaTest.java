package com.jraska.livedata.example;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import com.jraska.livedata.TestObserver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExampleJavaTest {
  @Rule public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

  private CounterViewModel viewModel;

  @Before
  public void setUp() {
    viewModel = new CounterViewModel();
  }

  @Test
  public void directAssertion() {
    LiveData<Integer> liveData = viewModel.counterLiveData();

    TestObserver.test(liveData)
      .assertHasValue()
      .assertHistorySize(1)
      .assertNever(value -> value > 0);

    for (int i = 0; i < 4; i++) {
      viewModel.plusButtonClicked();
    }

    TestObserver.test(liveData)
      .assertHasValue()
      .assertValue(value -> value > 3)
      .assertValue(4)
      .assertHistorySize(1) // notice the history size is one since we created new observer
      .assertNever(value -> value > 4);
  }

  @Test
  public void counterHistoryTest() {
    LiveData<Integer> liveData = viewModel.counterLiveData();
    TestObserver<Integer> testObserver = TestObserver.test(liveData);

    testObserver.assertHasValue()
      .assertHistorySize(1)
      .assertNever(value -> value > 0);

    for (int i = 0; i < 4; i++) {
      viewModel.plusButtonClicked();
    }

    testObserver.assertHasValue()
      .assertValue(value -> value > 3)
      .assertValue(4)
      .assertHistorySize(5)
      .assertNever(value -> value > 4);
  }

  @Test
  public void usingAssertJ() {
    LiveData<Integer> liveData = viewModel.counterLiveData();
    TestObserver<Integer> testObserver = TestObserver.test(liveData);

    for (int i = 0; i < 4; i++) {
      viewModel.plusButtonClicked();
    }

    Integer value = testObserver.value();
    assertThat(value).isEqualTo(4);

    List<Integer> valueHistory = testObserver.valueHistory();
    assertThat(valueHistory).containsExactly(0, 1, 2, 3, 4);

    testObserver.dispose();
    assertThat(liveData.hasObservers()).isFalse();
  }

  @Test
  public void useObserverByYourself() {
    TestObserver<Integer> testObserver = TestObserver.create();

    viewModel.counterLiveData().observeForever(testObserver);
    for (int i = 0; i < 4; i++) {
      viewModel.plusButtonClicked();
    }

    testObserver.assertHasValue()
      .assertValue(4)
      .assertHistorySize(5)
      .assertNever(value -> value > 4);

    // Potential remove needs to be handled by you
    viewModel.counterLiveData().removeObserver(testObserver);
  }

  @Test
  public void awaitAsyncValue() throws InterruptedException {
    LiveData<String> labelData = viewModel.counterLabel();

    TestObserver<String> testObserver = TestObserver.test(labelData)
      .assertNoValue();

    viewModel.asyncUpdateLabel("initial");

    testObserver.assertNoValue()
      .awaitValue()
      .assertHasValue();

    viewModel.asyncUpdateLabel("different");

    testObserver
      .assertValue("initial")
      .awaitNextValue()
      .assertValue("different");
  }
}
