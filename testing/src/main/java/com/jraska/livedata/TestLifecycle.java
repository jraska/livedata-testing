package com.jraska.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public final class TestLifecycle implements LifecycleOwner {
  private final LifecycleRegistry registry = new LifecycleRegistry(this);

  private TestLifecycle() {
  }

  public TestLifecycle handleLifecycleEvent(@NonNull Lifecycle.Event event) {
    registry.handleLifecycleEvent(event);
    return this;
  }

  @NonNull
  public Lifecycle.State getCurrentState() {
    return registry.getCurrentState();
  }

  @NonNull
  @Override
  public Lifecycle getLifecycle() {
    return registry;
  }

  public static TestLifecycle create() {
    return new TestLifecycle();
  }
}
