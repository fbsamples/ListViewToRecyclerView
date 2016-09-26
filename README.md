# ListViewToRecyclerView
### Authors
* Omer Strulovich (strulovich)
* Jason Sendros
* Jingbo Yang
* Benjamin Jaeger (jaegs)

## Overview
ListViewToRecyclerView is a sample utility to assist with migrations from Android's ListView widget to the new RecyclerView widget which is not backwards compatible.

ListViewToRecyclerView provides an opaque interface for a scrolling view, ScrollingViewProxy, that can be backed by either a ListView or a RecyclerView. ScrollingViewProxy uses the same method signatures as ListView so it easy to replace all calls to ListView with calls to ListViewProxy, the ListView implementation of ScrollingViewProxy.

Next, RecyclerViewProxy, the RecyclerView implementation of ScrollingViewProxy, can be swapped in to complete the migration. To assist with migrations, RecyclerViewProxy backports a few features of ListView, such as onItemClickListeners and header/footer views, that are missing from RecyclerView.

Please note, though, that every app's usage of ListView is different, hence the required migration steps will differ accordingly.

## Sample Usage
Example usage can be seen in [ScrollingActivity](https://github.com/fbsamples/ListViewToRecyclerView/blob/master/app/src/main/java/com/facebook/sampleapp/ScrollingActivity.java). The sample app can be run through [Android Studio](https://developer.android.com/studio/index.html).

## Additional Resources
For more information, please refer to [the presentation at DroidCon 2016](http://sched.droidcon.nyc/showSession/72048).
