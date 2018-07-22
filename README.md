# LiveData Testing
TestObserver to easily test LiveData and make assertions on them.

[![CircleCI](https://circleci.com/gh/jraska/livedata-testing.svg?style=svg)](https://circleci.com/gh/jraska/livedata-testing)
[![Download](https://api.bintray.com/packages/jraska/maven/com.jraska.livedata%3Atesting-ktx/images/download.svg)](https://bintray.com/jraska/maven/com.jraska.livedata%3Atesting-ktx/_latestVersion)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg) ](https://github.com/jraska/Falcon/blob/master/LICENSE)

## Usage

Having `LiveData<Integer>` of counter from 1 to 4:
Kotlin:
```java
liveData.test()
      .assertHasValue()
      .assertValue(4)
      .assertValue { it > 3 }
      .assertNever { it > 4 }
      .assertHistorySize(5)
```

Java almost the same:
```java
TestObserver.test(liveData)
      .assertHasValue()
      .assertValue(4)
      .assertValue { it > 3 }
      .assertNever { it > 4 }
      .assertHistorySize(5);
```

Don't forget to use `InstantTaskExecutorRule` from `android.arch.core:core-testing` to make your LiveData test run properly.

<!-- TODO(josef): Example tests -->

## Download

Grab via Gradle:
```groovy
testImplementation 'com.jraska.livedata:testing:0.2.0'
testImplementation 'com.jraska.livedata:testing-ktx:0.2.0' // If you are Kotlin positive
```

## Philosophy

This library is created in a belief that to effective and valuable test should be fast to write and model real code usage.
As by Architecture components spec Activity should communicate with its ViewModel only through subscribing to LiveData.
TestObserver in this case simulates teh activity and by testing LiveData, we can test our whole logic except the View where the responsibility belongs to Activity.
Key ideas:
* No Mocking or Robolectric
* Fluent API
* Easy to write tests
