# LiveData Testing
TestObserver to easily test LiveData and make assertions on them.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jraska.livedata/testing-ktx/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jraska.livedata/testing-ktx)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg) ](https://github.com/jraska/livedata-testing/blob/master/LICENSE)
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-LiveData%20Testing-green.svg?style=flat )]( https://android-arsenal.com/details/1/7255 )

Read [Medium Article](https://medium.com/@josef.raska/effective-livedata-and-viewmodel-testing-17f25069fcd4) for more info.

[![Explanatory Diagram](img/livedata-testing.png)](https://medium.com/@josef.raska/effective-livedata-and-viewmodel-testing-17f25069fcd4)

## Usage

Having `LiveData<Integer>` of counter from 0 to 4:

Kotlin - see [ExampleTest.kt](https://github.com/jraska/livedata-testing/blob/master/testing-ktx/src/test/java/com/jraska/livedata/example/ExampleTest.kt)
```java
liveData.test()
  .awaitValue()
  .assertHasValue()
  .assertValue { it > 3 }
  .assertValue(4)
  .assertHistorySize(5)
  .assertNever { it > 4 }


// Assertion on structures with a lot of nesting
viewLiveData.map { it.items[0].header.title }
  .assertValue("Expected title")
```

Java - see [ExampleTest.java](https://github.com/jraska/livedata-testing/blob/master/testing-ktx/src/test/java/com/jraska/livedata/example/ExampleJavaTest.java)
```java
TestObserver.test(liveData)
  .awaitValue()
  .assertHasValue()
  .assertValue(value -> value > 3)
  .assertValue(4)
  .assertHistorySize(5)
  .assertNever(value -> value > 4);
```

Don't forget to use `InstantTaskExecutorRule` from `androidx.arch.core:core-testing` to make your LiveData test run properly.

## Download

##### Kotlin users:
```groovy
testImplementation 'com.jraska.livedata:testing-ktx:1.2.0'
```

##### Java users:
```groovy
testImplementation 'com.jraska.livedata:testing:1.2.0'
```

## [Philosophy](https://medium.com/@josef.raska/effective-livedata-and-viewmodel-testing-17f25069fcd4)

This library is created in a belief that to effective and valuable test should be fast to write and model real code usage.
As by Architecture components spec Activity should communicate with its ViewModel only through observing LiveData.
TestObserver in this case simulates the Activity and by testing LiveData, we could test our whole logic except the View where the responsibility belongs to Activity.
Key ideas:
* Test pretends to be an Activity
* No Android framework mocking or Robolectric - just standard fast JUnit tests
* Fluent API inspired by RxJava TestObserver
* Easy to write fast executing tests - possibly TDD
