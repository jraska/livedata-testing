package com.jraska.livedata.example;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import com.jraska.livedata.TestLifecycle;
import com.jraska.livedata.TestObserver;
import com.jraska.livedata.example.CounterViewModel.ScreenData;
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
      .doOnChanged(value -> assertThat(value).isPositive())
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

    liveData.removeObserver(testObserver);
    assertThat(liveData.hasObservers()).isFalse();
  }

  @Test
  public void useObserverWithLifecycle() {
    TestObserver<Integer> testObserver = TestObserver.create();
    TestLifecycle testLifecycle = TestLifecycle.initialized();

    viewModel.counterLiveData().observe(testLifecycle, testObserver);

    viewModel.plusButtonClicked();
    viewModel.minusButtonClicked();
    testObserver.assertNoValue();

    testLifecycle.resume();
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
    LiveData<ScreenData> labelData = viewModel.counterLabel();

    TestObserver<String> testObserver = TestObserver.test(labelData)
      .map(input -> input.getLabels().getCounterLabel())
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
