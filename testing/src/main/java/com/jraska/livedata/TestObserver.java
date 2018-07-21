package com.jraska.livedata;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TestObserver<T> implements Observer<T> {
  private final List<T> valueHistory = new ArrayList<>();
  private final LiveData<T> observedLiveData;

  private TestObserver(LiveData<T> observedLiveData) {
    this.observedLiveData = observedLiveData;
  }

  @Override
  public void onChanged(@Nullable T value) {
    valueHistory.add(value);
  }

  public T value() {
    assertHasValue();
    return valueHistory.get(valueHistory.size() - 1);
  }

  public List<T> valueHistory() {
    return Collections.unmodifiableList(valueHistory);
  }

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
