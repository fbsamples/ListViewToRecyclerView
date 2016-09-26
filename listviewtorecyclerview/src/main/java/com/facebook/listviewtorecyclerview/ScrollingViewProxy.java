/*
 * Copyright 2016-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.listviewtorecyclerview;

import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * An opaque interface for a scrolling view that can be backed by either a {@link ListView}
 * or a {@link RecyclerView}.
 *
 * This class simplifies migrations from ListView to the newer RecyclerView which is not backwards
 * compatible. All of the method signatures here match those in ListView so its easy to replace all
 * calls to ListView with calls to ScrollingViewProxy.
 *
 * Once an app is using {@link ListViewProxy}, the ListView version of ScrollingViewProxy,
 * the app can be switched to RecyclerView by means of {@link RecyclerViewProxy}, the RecyclerView
 * version of ScrollingViewProxy.
 *
 * ScrollingViewProxy allows you to use your existing {@link ListAdapter} inside of a RecyclerView
 * with one caveat - the Adapter must be updated to implement {@link ScrollingViewProxy.Adapter}
 * which breaks up calls to {@link ListAdapter#getView(int, View, ViewGroup)} into
 * {@link Adapter#createView(int, ViewGroup)} and
 * {@link Adapter#bindView(int, Object, View, int, ViewGroup)}.
 */
public interface ScrollingViewProxy {

  /**
   * An Adapter which can be used in either a ListView or a RecyclerView. It breaks up calls
   * to to {@link ListAdapter#getView(int, View, ViewGroup)} into
   * {@link Adapter#createView(int, ViewGroup)} and
   * {@link Adapter#bindView(int, Object, View, int, ViewGroup)} to more closely match the api
   * provided in {@link RecyclerView.Adapter}.
   *
   * Either extend {@link BaseAdapter} or implement
   * {@link ListAdapter#getView(int, View, ViewGroup)} in a similar manner to
   * {@link BaseAdapter#getView(int, View, ViewGroup)}.
   */
  interface Adapter extends ListAdapter {
    /**
     * Callback to create a new view.
     * Corresponds to {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}.
     * @param itemViewType the type of view to create.
     * @param parent the view which will be the parent of the created view.
     * @return a newly created view.
     */
    View createView(int itemViewType, ViewGroup parent);

    /**
     * Callback to bind a view for a given position.
     * Corresponds to {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     * @param position the position of the view in the Adapter.
     * @param item the data to bind.
     * @param view the view to bind to.
     * @param itemViewType the type of view.
     * @param parent the future parent of the view.
     */
    void bindView(
        int position, Object item, View view, int itemViewType, ViewGroup parent);
  }

  /**
   * Basic implementation of Adapter which maps the getView into bindView and createView.
   */
  abstract class BaseAdapter implements Adapter {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      int itemViewType = getItemViewType(position);
      if (convertView == null) {
        convertView = createView(itemViewType, parent);
      }
      bindView(position, getItem(position), convertView, itemViewType, parent);
      return convertView;
    }
  }

  /** @See {@link AbsListView.OnScrollListener}. */
  interface OnScrollListener {

    int SCROLL_STATE_IDLE = 0;

    int SCROLL_STATE_TOUCH_SCROLL = 1;

    int SCROLL_STATE_FLING = 2;

    void onScrollStateChanged(ScrollingViewProxy ScrollingViewProxy, int scrollState);

    void onScroll(
        ScrollingViewProxy ScrollingViewProxy,
        int firstItemIndex,
        int visibleItemCount,
        int totalItemCount);
  }

  /** @See {@link AbsListView.RecyclerListener}. */
  interface RecyclerListener {

    void onMovedToScrapHeap(View view);
  }

  /** @See {@link AbsListView.OnItemClickListener}. */
  interface OnItemClickListener {

    void onItemClick(ViewGroup parent, View view, int position, long id);
  }

  /** @See {@link AbsListView.OnItemLongClickListener}. */
  interface OnItemLongClickListener {

    boolean onItemLongClick(ViewGroup parent, View view, int position, long id);
  }

  int CHOICE_MODE_NONE = 0;

  int CHOICE_MODE_SINGLE = 1;

  int CHOICE_MODE_MULTIPLE = 2;

  int CHOICE_MODE_MULTIPLE_MODAL = 3;

  /**
   * @return The view being used under this proxy.
   */
  View getView();

  /**
   * @return The view group being used under this proxy.
   */
  ViewGroup getViewGroup();

  /**
   * @return The {@link AbsListView} backing this proxy.
   * @throws UnsupportedOperationException if this proxy is not backed by a ListView.
   *
   * DEPRECATED: obviously this method does not work for RecyclerViewProxy.
   */
  @Deprecated
  AbsListView getAbsListView();

  /**
   * @return The {@link ListView} backing this proxy.
   * @throws UnsupportedOperationException if this proxy is not backed by a ListView.
   *
   * DEPRECATED: obviously this method does not work for RecyclerViewProxy.
   */
  @Deprecated
  ListView getListView();

  ListAdapter getAdapter();

  void addHeaderView(View v, Object data, boolean isSelectable);

  void addFooterView(View v, Object data, boolean isSelectable);

  void removeHeaderView(View view);

  void removeFooterView(View view);

  void setOnScrollListener(OnScrollListener onScrollListener);

  void setVisibility(int visibility);

  int getTop();

  int getBottom();

  <T extends View> T getViewById(int id);

  int getHeight();

  void getLocationOnScreen(int[] location);

  int getPaddingBottom();

  int getScrollX();

  int getScrollY();

  void scrollTo(int x, int y);

  void scrollBy(int dx, int dy);

  void smoothScrollBy(int i, int scrollDuration);

  void smoothScrollToPositionFromTop(int position, int i);

  boolean getClipToPadding();

  void setClipToPadding(boolean clipToPadding);

  void setPadding(int leftPixels, int topPixels, int rightPixels, int bottomPixels);

  int getPaddingTop();

  int getPaddingLeft();

  int getPaddingRight();

  int getPositionForView(View itemView);

  void post(Runnable runnable);

  void setSelector(Drawable selectorDrawable);

  void setVerticalScrollBarEnabled(boolean enabled);

  void setOnItemClickListener(OnItemClickListener onItemClickListener);

  void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener);

  void addHeaderView(View view);

  void setRecyclerListener(RecyclerListener recyclerListener);

  void setClickable(boolean isClickable);

  void setLongClickable(boolean isLongClickable);

  void setDividerHeight(int height);

  void setItemsCanFocus(boolean itemsCanFocus);

  void smoothScrollToPosition(int position);

  int getChildCount();

  View getChildAt(int i);

  int getFirstVisiblePosition();

  int getLastVisiblePosition();

  int getCount();

  boolean isEmpty();

  Object getItemAtPosition(int position);

  int getHeaderViewsCount();

  int getFooterViewsCount();

  void addFooterView(View view);

  void setEmptyView(View empty);

  void setOnTouchListener(View.OnTouchListener onTouchListener);

  void setScrollingCacheEnabled(boolean enabled);

  void setAdapter(Adapter adapter);

  void setSelection(int position);

  void setSelectionFromTop(int position, int offset);

  void setSelectionAfterHeaderView();

  void destroyDrawingCache();

  long getItemIdAtPosition(int position);

  Parcelable onSaveInstanceState();

  void onRestoreInstanceState(Parcelable state);

  int getChoiceMode();

  void setChoiceMode(int choiceMode);
}
