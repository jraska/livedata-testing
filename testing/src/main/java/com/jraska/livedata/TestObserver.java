package com.jraska.livedata;


import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class TestObserver<T> implements Observer<T> {
  private final List<T> valueHistory = new ArrayList<>();
  private final LiveData<T> observedLiveData;

  private CountDownLatch valueLatch = new CountDownLatch(1);

  private TestObserver(LiveData<T> observedLiveData) {
    this.observedLiveData = observedLiveData;
  }

  @Override
  public void onChanged(@Nullable T value) {
    valueHistory.add(value);
    valueLatch.countDown();
  }

  public T value() {
    assertHasValue();
    return valueHistory.get(valueHistory.size() - 1);
  }

  public List<T> valueHistory() {
    return Collections.unmodifiableList(valueHistory);
  }

  @Deprecated // TODO: Add some lovely JavaDoc ;) The feature of remove will be dropped
  public TestObserver<T> dispose() {
    observedLiveData.removeObserver(this);
    return this;
  }

  public TestObserver<T> assertHasValue() {
    if (valueHistory.isEmpty()) {
      throw fail("Observer never received any value");
    }

    return this;
  }

  public TestObserver<T> assertNoValue() {
    return assertHistorySize(0);
  }

  public TestObserver<T> assertHistorySize(int expectedSize) {
    int size = valueHistory.size();
    if (size != expectedSize) {
      throw fail("History size differ; Expected: " + expectedSize + ", Actual: " + size);
    }
    return this;
  }

  public TestObserver<T> assertValue(T expected) {
    T value = value();

    if (expected == null && value == null) {
      return this;
    }

    if (!value.equals(expected)) {
      throw fail("Expected: " + valueAndClass(expected) + ", Actual: " + valueAndClass(value));
    }

    return this;
  }

  public TestObserver<T> assertValue(Function<T, Boolean> valuePredicate) {
    T value = value();

    if (!valuePredicate.apply(value)) {
      throw fail("Value not present");
    }

    return this;
  }

  public TestObserver<T> assertNever(Function<T, Boolean> valuePredicate) {
    int size = valueHistory.size();
    for (int valueIndex = 0; valueIndex < size; valueIndex++) {
      T value = this.valueHistory.get(valueIndex);
      if (valuePredicate.apply(value)) {
        throw fail("Value at position " + valueIndex + " matches predicate "
          + valuePredicate.toString() + ", which was not expected.");
      }
    }

    return this;
  }

  /**
   * Awaits until this TestObserver has any value.
   *
   * If this TestObserver has already value then this method returns immediately.
   *
   * @return this
   * @throws InterruptedException if the current thread is interrupted while waiting
   */
  public TestObserver<T> awaitValue() throws InterruptedException {
    valueLatch.await();
    return this;
  }

  /**
   * Awaits the specified amount of time or until this TestObserver has any value.
   *
   * If this TestObserver has already value then this method returns immediately.
   *
   * @return this
   * @throws InterruptedException if the current thread is interrupted while waiting
   */
  public TestObserver<T> awaitValue(long timeout, TimeUnit timeUnit) throws InterruptedException {
    valueLatch.await(timeout, timeUnit);
    return this;
  }

  /**
   * Awaits until this TestObserver receives next value.
   *
   * If this TestObserver has already value then it awaits for another one.
   *
   * @return this
   * @throws InterruptedException if the current thread is interrupted while waiting
   */
  public TestObserver<T> awaitNextValue() throws InterruptedException {
    return withNewLatch().awaitValue();
  }


  /**
   * Awaits the specified amount of time or until this TestObserver receives next value.
   *
   * If this TestObserver has already value then it awaits for another one.
   *
   * @return this
   * @throws InterruptedException if the current thread is interrupted while waiting
   */
  public TestObserver<T> awaitNextValue(long timeout, TimeUnit timeUnit) throws InterruptedException {
    return withNewLatch().awaitValue(timeout, timeUnit);
  }

  private TestObserver<T> withNewLatch() {
    valueLatch = new CountDownLatch(1);
    return this;
  }

  private AssertionError fail(String message) {
    return new AssertionError(message);
  }

  private static String valueAndClass(Object value) {
    if (value != null) {
      return value + " (class: " + value.getClass().getSimpleName() + ")";
    }
    return "null";
  }

  public static <T> TestObserver<T> create() {
    return new TestObserver<>(new MutableLiveData<>());
  }

  public static <T> TestObserver<T> test(LiveData<T> liveData) {
    TestObserver<T> observer = new TestObserver<>(liveData);
    liveData.observeForever(observer);
    return observer;
  }
}
