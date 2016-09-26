/*
 * Copyright 2016-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.listviewtorecyclerview;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Implementation of {@link ScrollingViewProxy} for a {@link ListView}.
 *
 * @see ScrollingViewProxy for more information.
 */
public class ListViewProxy implements ScrollingViewProxy {

  private final ListView mListView;

  public ListViewProxy(ListView listView) {
    mListView = listView;
  }

  @Override
  public View getView() {
    return mListView;
  }

  @Override
  public ViewGroup getViewGroup() {
    return mListView;
  }

  @Override
  public AbsListView getAbsListView() {
    return mListView;
  }

  @Override
  public ListView getListView() {
    return mListView;
  }

  @Override
  public int getHeight() {
    return mListView.getHeight();
  }

  @Override
  public void getLocationOnScreen(int[] location) {
    mListView.getLocationOnScreen(location);
  }

  @Override
  public void removeHeaderView(View view) {
    mListView.removeHeaderView(view);
  }

  @Override
  public void removeFooterView(View view) {
    mListView.removeFooterView(view);
  }

  @Override
  public void smoothScrollBy(final int i, final int scrollDuration) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        mListView.smoothScrollBy(i, scrollDuration);
      }
    };
    post(runnable);
  }

  @Override
  public int getPaddingBottom() {
    return mListView.getPaddingBottom();
  }

  @Override
  public int getScrollX() {
    return mListView.getScrollX();
  }

  @Override
  public int getScrollY() {
    return mListView.getScrollY();
  }

  @Override
  public void scrollTo(int x, int y) {
    mListView.scrollTo(x, y);
  }

  @Override
  public void scrollBy(int dx, int dy) {
    throw new UnsupportedOperationException("This function is not supported yet.");
  }

  @Override
  public void post(Runnable runnable) {
    mListView.post(runnable);
  }

  @Override
  public int getPaddingTop() {
    return mListView.getPaddingTop();
  }

  @Override
  public void setClipToPadding(boolean clipToPadding) {
    mListView.setClipToPadding(clipToPadding);
  }

  @Override
  public void setPadding(int leftPixels, int topPixels, int rightPixels, int bottomPixels) {
    mListView.setPadding(leftPixels, topPixels, rightPixels, bottomPixels);
  }

  @Override
  public int getPaddingLeft() {
    return mListView.getPaddingLeft();
  }

  @Override
  public int getPaddingRight() {
    return mListView.getPaddingRight();
  }

  @Override
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public boolean getClipToPadding() {
    return mListView.getClipToPadding();
  }

  @Override
  public int getPositionForView(View itemView) {
    return mListView.getPositionForView(itemView);
  }

  @Override
  public void setSelector(Drawable selectorDrawable) {
    mListView.setSelector(selectorDrawable);
  }

  @Override
  public void setVerticalScrollBarEnabled(boolean enabled) {
    mListView.setVerticalScrollBarEnabled(enabled);
  }

  @Override
  public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
    if (onItemClickListener == null) {
      mListView.setOnItemClickListener(null);
      return;
    }

    mListView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(
              AdapterView<?> parent, View view, int position, long id) {
            onItemClickListener.onItemClick(parent, view, position, id);
          }
        });
  }

  @Override
  public void setOnItemLongClickListener(final OnItemLongClickListener onItemLongClickListener) {
    if (onItemLongClickListener == null) {
      mListView.setOnItemLongClickListener(null);
      return;
    }

    mListView.setOnItemLongClickListener(
        new AdapterView.OnItemLongClickListener() {
          @Override
          public boolean onItemLongClick(
              AdapterView<?> parent, View view, int position, long id) {
            return onItemLongClickListener.onItemLongClick(parent, view, position, id);
          }
        });
  }

  @Override
  public void setVisibility(int visibility) {
    mListView.setVisibility(visibility);
  }

  @Override
  public void smoothScrollToPositionFromTop(int position, int i) {
    mListView.smoothScrollToPositionFromTop(position, i);
  }

  @Override
  public void addHeaderView(View view) {
    mListView.addHeaderView(view);
  }

  @Override
  public void setRecyclerListener(final RecyclerListener recyclerListener) {
    mListView.setRecyclerListener(
        new AbsListView.RecyclerListener() {
          @Override
          public void onMovedToScrapHeap(View view) {
            recyclerListener.onMovedToScrapHeap(view);
          }
        });
  }

  @Override
  public void setClickable(boolean isClickable) {
    mListView.setClickable(isClickable);
  }

  @Override
  public void setLongClickable(boolean isLongClickable) {
    mListView.setLongClickable(isLongClickable);
  }

  @Override
  public void setDividerHeight(int height) {
    mListView.setDividerHeight(height);
  }

  @Override
  public void setItemsCanFocus(boolean itemsCanFocus) {
    mListView.setItemsCanFocus(itemsCanFocus);
  }

  @Override
  public void addHeaderView(View view, Object data, boolean isSelectable) {
    mListView.addHeaderView(view, data, isSelectable);
  }

  @Override
  public void addFooterView(View view, Object data, boolean isSelectable) {
    mListView.addFooterView(view, data, isSelectable);
  }

  @Override
  public void setOnScrollListener(OnScrollListener onScrollListener) {
    DelegatingOnScrollListener delegate = onScrollListener != null
        ? new DelegatingOnScrollListener(onScrollListener, this)
        : null;
    mListView.setOnScrollListener(delegate);
  }

  @Override
  public int getTop() {
    return mListView.getTop();
  }

  @Override
  public int getBottom() {
    return mListView.getBottom();
  }

  @Override
  public <T extends View> T getViewById(int id) {
    return (T) mListView.findViewById(id);
  }

  @Override
  public void smoothScrollToPosition(int position) {
    mListView.smoothScrollToPosition(position);
  }

  @Override
  public ListAdapter getAdapter() {
    return mListView.getAdapter();
  }

  @Override
  public int getChildCount() {
    return mListView.getChildCount();
  }

  @Override
  public View getChildAt(int i) {
    return mListView.getChildAt(i);
  }

  @Override
  public int getFirstVisiblePosition() {
    return mListView.getFirstVisiblePosition();
  }

  @Override
  public int getLastVisiblePosition() {
    return mListView.getLastVisiblePosition();
  }

  @Override
  public int getCount() {
    return mListView.getAdapter() == null ? 0 : mListView.getAdapter().getCount();
  }

  @Override
  public boolean isEmpty() {
    return mListView.getAdapter().isEmpty();
  }

  @Override
  public Object getItemAtPosition(int position) {
    return mListView.getItemAtPosition(position);
  }

  @Override
  public int getHeaderViewsCount() {
    return mListView.getHeaderViewsCount();
  }

  @Override
  public int getFooterViewsCount() {
    return mListView.getFooterViewsCount();
  }

  @Override
  public void addFooterView(View view) {
    mListView.addFooterView(view);
  }

  @Override
  public void setEmptyView(View empty) {
    mListView.setEmptyView(empty);
  }

  @Override
  public void setScrollingCacheEnabled(boolean b) {
    mListView.setScrollingCacheEnabled(b);
  }

  @Override
  public void setAdapter(Adapter adapter) {
    mListView.setAdapter(adapter);
  }

  @Override
  public void setSelection(int position) {
    mListView.setSelection(position);
  }

  @Override
  public void setSelectionFromTop(int position, int offset) {
    mListView.setSelectionFromTop(position, offset);
  }

  @Override
  public void setSelectionAfterHeaderView() {
    mListView.setSelectionAfterHeaderView();
  }

  @Override
  public void destroyDrawingCache() {
    mListView.destroyDrawingCache();
  }

  @Override
  public void setOnTouchListener(View.OnTouchListener onTouchListener) {
    mListView.setOnTouchListener(onTouchListener);
  }

  @Override
  public long getItemIdAtPosition(int position) {
    return mListView.getItemIdAtPosition(position);
  }

  @Override
  public Parcelable onSaveInstanceState() {
    return mListView.onSaveInstanceState();
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    mListView.onRestoreInstanceState(state);
  }

  @Override
  public int getChoiceMode() {
    return mListView.getChoiceMode();
  }

  @Override
  public void setChoiceMode(int choiceMode) {
    mListView.setChoiceMode(choiceMode);
  }

  public static class DelegatingOnScrollListener implements AbsListView.OnScrollListener {

    private final OnScrollListener mOnScrollListener;
    private final ListViewProxy mListViewProxy;

    public DelegatingOnScrollListener(OnScrollListener onScrollListener, ListViewProxy proxy) {
      mOnScrollListener = onScrollListener;
      mListViewProxy = proxy;
    }

    @Override
    public void onScrollStateChanged(
        AbsListView view,
        int scrollState) {
      mOnScrollListener.onScrollStateChanged(mListViewProxy, scrollState);
    }

    @Override
    public void onScroll(
        AbsListView view,
        int firstVisibleItem,
        int visibleItemCount,
        int totalItemCount) {
      mOnScrollListener.onScroll(
          mListViewProxy,
          firstVisibleItem,
          visibleItemCount,
          totalItemCount);
    }
  }
}
