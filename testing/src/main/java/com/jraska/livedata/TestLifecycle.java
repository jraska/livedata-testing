package com.jraska.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.*;

public final class TestLifecycle implements LifecycleOwner {
  private final LifecycleRegistry registry = new LifecycleRegistry(this);

  private TestLifecycle() {
  }

  public TestLifecycle create() {
    return handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
  }

  public TestLifecycle start() {
    return handleLifecycleEvent(Lifecycle.Event.ON_START);
  }

  public TestLifecycle resume() {
    return handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
  }

  public TestLifecycle pause() {
    return handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
  }

  public TestLifecycle stop() {
    return handleLifecycleEvent(Lifecycle.Event.ON_STOP);
  }

  public TestLifecycle destroy() {
    return handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
  }

  @NonNull
  public Lifecycle.State getCurrentState() {
    return registry.getCurrentState();
  }

  private TestLifecycle handleLifecycleEvent(@NonNull Lifecycle.Event event) {
    registry.handleLifecycleEvent(event);
    return this;
  }

  @NonNull
  @Override
  public Lifecycle getLifecycle() {
    return registry;
  }

  public static TestLifecycle initialized() {
    return new TestLifecycle();
  }

  public static TestLifecycle resumed() {
    return initialized().resume();
  }
}
