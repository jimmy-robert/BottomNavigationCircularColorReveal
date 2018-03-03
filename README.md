# BottomNavigationCircularColorReveal
Build a BottomNavigationView with a circular color reveal animation like in Material Design guidelines demos.

This library does not implement its own BottomNavigationView but uses the one from the Android Support Library.

## Installation

TODO

## Usage

### Sample app

You can look at the sample app to see it in action.

### Code

Define the menu for the `BottomNavigationView`

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@+id/menu_videos"
        android:title="@string/menu_videos"
        android:icon="@drawable/ic_video" />
    <item android:id="@+id/menu_music"
        android:title="@string/menu_music"
        android:icon="@drawable/ic_music" />
    <item android:id="@+id/menu_books"
        android:title="@string/menu_books"
        android:icon="@drawable/ic_book" />
    <item android:id="@+id/menu_newspaper"
        android:title="@string/menu_newspaper"
        android:icon="@drawable/ic_newspaper" />
</menu>
```

Define the colors for your the `BottomNavigationView`

```xml
<array name="menu_colors">
    <item>#445A65</item>
    <item>#00796B</item>
    <item>#8C6E62</item>
    <item>#6E4C42</item>
</array>
```

Create your circular color reveal component and bind it to the `BottomNavigationView`

```kotlin
val colors = resources.getIntArray(R.array.menu_colors)

val reveal = BottomNavigationCircularColorReveal(colors)
reveal.setuptWithBottomNavigationView(bottomNavigationView)
```

The circular color reveal component uses its own `BottomNavigationView.OnNavigationItemSelectedListener`, 
so if you want to define your custom behavior you can set your `BottomNavigationView.OnNavigationItemSelectedListener` on the reveal component.


```kotlin
reveal.setOnNavigationItemSelectedListener {
    // Do your custom operations on item selection here (e.g display a fragment)
    // ...
    // Allow selection
    true
}
```

To disable the reveal component, just unbind it

```kotlin
reveal.unbind()
```
