# LiveData Testing
TestObserver to easily test LiveData and make assertions on them.

[![CircleCI](https://circleci.com/gh/jraska/livedata-testing.svg?style=svg)](https://circleci.com/gh/jraska/livedata-testing)
[![Download](https://api.bintray.com/packages/jraska/maven/com.jraska.livedata%3Atesting-ktx/images/download.svg)](https://bintray.com/jraska/maven/com.jraska.livedata%3Atesting-ktx/_latestVersion)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg) ](https://github.com/jraska/Falcon/blob/master/LICENSE)

Read [Medium Article](https://medium.com/@josef.raska/effective-livedata-and-viewmodel-testing-17f25069fcd4) for more info.

## Usage

Having `LiveData<Integer>` of counter from 0 to 4:

Kotlin - see [ExampleTest.kt](https://github.com/jraska/livedata-testing/blob/master/testing-ktx/src/test/java/com/jraska/livedata/example/ExampleTest.kt)
```java
liveData.test()
      .assertHasValue()
      .assertValue { it > 3 }
      .assertValue(4)
      .assertHistorySize(5)
      .assertNever { it > 4 }
```

Java - see [ExampleTest.java](https://github.com/jraska/livedata-testing/blob/master/testing-ktx/src/test/java/com/jraska/livedata/example/ExampleJavaTest.java)
```java
TestObserver.test(liveData)
  .assertHasValue()
  .assertValue(value -> value > 3)
  .assertValue(4)
  .assertHistorySize(5)
  .assertNever(value -> value > 4);
```

Don't forget to use `InstantTaskExecutorRule` from `android.arch.core:core-testing` to make your LiveData test run properly.

## Download

Grab via Gradle:
```groovy
testImplementation 'com.jraska.livedata:testing:0.2.0'
testImplementation 'com.jraska.livedata:testing-ktx:0.2.0' // If you are Kotlin positive
```

## Philosophy

This library is created in a belief that to effective and valuable test should be fast to write and model real code usage.
As by Architecture components spec Activity should communicate with its ViewModel only through observing LiveData.
TestObserver in this case simulates the Activity and by testing LiveData, we could test our whole logic except the View where the responsibility belongs to Activity.
Key ideas:
* Test pretends to be an Activity
* No Android framework mocking or Robolectric - just standard fast JUnit tests
* Fluent API inspired by RxJava TestObserver
* Easy to write fast executing tests - possibly TDD
