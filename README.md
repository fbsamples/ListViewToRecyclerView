ListViewToRecyclerView is an utility to assist with migrations from Androids ListView widget to the new RecyclerView widget which is not backwards compatible. 

ListViewToRecyclerView provides an opaque interface for a scrolling view, ScrollingViewProxy, that can be backed by either a ListView or a RecyclerView. ScrollingViewProxy uses the same method signatures as ListView so it easy to replace all calls to ListView with calls to ListViewProxy, the ListView implmentation of ScrollingViewProxy.

Next, RecyclerViewProxy, the RecyclerView implementation of ScrollingViewProxy, can be swapped in to complete the migration. To assist with migrations, RecyclerViewProxy backports a few features of ListView, such as onItemClickListeners and header/footer views, that are missing from RecyclerView.

Example usage can be seen in ScrollingActivity.

For more information, please refer to the presentation at DroidCon 2016 http://sched.droidcon.nyc/showSession/72048.
