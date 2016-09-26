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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Implementation of {@link ScrollingViewProxy} for a {@link RecyclerView}.
 *
 * RecyclerViewProxy backports features of {@link ListView} that are not available in
 * {@link RecyclerView}. These features include header/footer views, empty view,
 * item click/long click listeners.
 *
 * @see ScrollingViewProxy for more information.
 */
public class RecyclerViewProxy implements ScrollingViewProxy {

  private static final String UNIMPLEMENTED_METHOD =
      "RecyclerViewProxy has not yet implemented this method.";

  private final LinearRecyclerView mRecyclerView;

  private OnScrollListener mScrollListener;
  private LinearRecyclerViewAdapter mRecyclerViewAdapter;
  private ListAdapter mListViewAdapter;
  private ArrayList<View> mHeaderViews = new ArrayList<>();
  private ArrayList<View> mFooterViews = new ArrayList<>();

  private int mFirstVisibleItem;
  private int mLastVisibleItem;

  public RecyclerViewProxy(LinearRecyclerView recyclerView) {
    if (!(recyclerView.getLayoutManager() instanceof LinearLayoutManager)) {
      throw new IllegalStateException();
    }
    mRecyclerView = recyclerView;
    RecyclerView.OnScrollListener delegatedListener = createDelegatingScrollListener();
    mRecyclerView.addOnScrollListener(delegatedListener);
  }

  @Override
  public View getView() {
    return mRecyclerView;
  }

  @Override
  public ViewGroup getViewGroup() {
    return mRecyclerView;
  }

  @Override
  public AbsListView getAbsListView() {
    throw new UnsupportedOperationException("RecyclerViewProxy has no AbsListview to expose.");
  }

  @Override
  public ListView getListView() {
    throw new UnsupportedOperationException("RecyclerViewProxy has no ListView to expose.");
  }

  @Override
  public void addHeaderView(View view, Object data, boolean isSelectable) {
    mHeaderViews.add(view);
    updateAuxiliaryViews();
  }

  @Override
  public void addFooterView(View view, Object data, boolean isSelectable) {
    mFooterViews.add(view);
    updateAuxiliaryViews();
  }

  @Override
  public void removeHeaderView(View view) {
    mHeaderViews.remove(view);
    updateAuxiliaryViews();
  }

  @Override
  public void removeFooterView(View view) {
    mFooterViews.remove(view);
    updateAuxiliaryViews();
  }

  @Override
  public void setOnScrollListener(OnScrollListener onScrollListener) {
    mScrollListener = onScrollListener;
  }

  @Override
  public void setVerticalScrollBarEnabled(boolean enabled) {
    mRecyclerView.setVerticalScrollBarEnabled(enabled);
  }

  @Override
  public int getTop() {
    return mRecyclerView.getTop();
  }

  @Override
  public int getBottom() {
    return mRecyclerView.getBottom();
  }

  @Override
  public <T extends View> T getViewById(int id) {
    return (T) mRecyclerView.findViewById(id);
  }

  @Override
  public int getHeight() {
    return mRecyclerView.getHeight();
  }

  @Override
  public void getLocationOnScreen(int[] location) {
    mRecyclerView.getLocationOnScreen(location);
  }

  @Override
  public int getPaddingBottom() {
    return mRecyclerView.getPaddingBottom();
  }

  /**
   * {@param scrollDuration} is ignored because RecyclerView doesn't expose a way to scroll over
   * a duration.
   * @param i vertical number of pixels to scroll by
   * @param scrollDuration ignored
   */
  @Override
  public void smoothScrollBy(int i, int scrollDuration) {
    mRecyclerView.smoothScrollBy(0, i);
  }

  @Override
  public void smoothScrollToPositionFromTop(int position, int i) {
    mRecyclerView.getLinearLayoutManager().scrollToPositionWithOffset(position, i);
  }

  @Override
  public int getPaddingTop() {
    return mRecyclerView.getPaddingTop();
  }

  @Override
  public int getPaddingLeft() {
    return mRecyclerView.getPaddingLeft();
  }

  @Override
  public int getPaddingRight() {
    return mRecyclerView.getPaddingRight();
  }

  @Override
  public int getPositionForView(View itemView) {
    return mRecyclerView.getChildLayoutPosition(itemView);
  }

  @Override
  public boolean getClipToPadding() {
    return mRecyclerView.getClipToPadding();
  }

  @Override
  public void setClipToPadding(boolean clipToPadding) {
    mRecyclerView.setClipToPadding(clipToPadding);
  }

  @Override
  public void setPadding(int leftPixels, int topPixels, int rightPixels, int bottomPixels) {
    mRecyclerView.setPadding(leftPixels, topPixels, rightPixels, bottomPixels);
  }

  @Override
  public void post(Runnable runnable) {
    mRecyclerView.post(runnable);
  }

  @Override
  public void setSelector(Drawable selectorDrawable) {
    unimplemented();
  }

  @Override
  public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
    LinearRecyclerView.OnItemClickListener viewOnItemClickListener = onItemClickListener == null
        ? null
        : new LinearRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearRecyclerView parent, View view, int position, long id) {
              onItemClickListener.onItemClick(parent, view, position, id);
            }
          };
    mRecyclerView.setOnItemClickListener(viewOnItemClickListener);
  }

  @Override
  public void setOnItemLongClickListener(final OnItemLongClickListener onItemLongClickListener) {
    LinearRecyclerView.OnItemLongClickListener viewOnItemLongClickListener =
        onItemLongClickListener == null
            ? null
            : new LinearRecyclerView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(
                    LinearRecyclerView parent,
                    View view,
                    int position,
                    long id) {
                  return onItemLongClickListener.onItemLongClick(parent, view, position, id);
                }
              };

    mRecyclerView.setOnItemLongClickListener(viewOnItemLongClickListener);
  }

  @Override
  public void setVisibility(int visibility) {
    mRecyclerView.setVisibility(visibility);
  }

  @Override
  public void addHeaderView(View view) {
    addHeaderView(view, null, true);
  }

  @Override
  public void setRecyclerListener(final RecyclerListener recyclerListener) {
    RecyclerView.RecyclerListener viewRecyclerListener = recyclerListener == null
        ? null
        : new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
              recyclerListener.onMovedToScrapHeap(viewHolder.itemView);
            }
          };

    mRecyclerView.setRecyclerListener(viewRecyclerListener);
  }

  @Override
  public void setClickable(boolean isClickable) {
    mRecyclerView.setClickable(isClickable);
  }

  @Override
  public void setLongClickable(boolean isLongClickable) {
    mRecyclerView.setLongClickable(isLongClickable);
  }

  @Override
  public void setDividerHeight(int height) {
    unimplemented();
  }

  @Override
  public void setItemsCanFocus(boolean itemsCanFocus) {
    unimplemented();
  }

  @Override
  public void smoothScrollToPosition(int position) {
    mRecyclerView.smoothScrollToPosition(position);
  }

  @Override
  public ListAdapter getAdapter() {
    return mListViewAdapter;
  }

  @Override
  public int getChildCount() {
    return mRecyclerView.getChildCount();
  }

  @Override
  public View getChildAt(int i) {
    return mRecyclerView.getChildAt(i);
  }

  @Override
  public int getFirstVisiblePosition() {
    return mRecyclerView.getLinearLayoutManager().findFirstVisibleItemPosition();
  }

  @Override
  public int getLastVisiblePosition() {
    return mRecyclerView.getLastVisiblePosition();
  }

  @Override
  public int getCount() {
    return mRecyclerViewAdapter != null ? mRecyclerViewAdapter.getItemCount() : 0;
  }

  @Override
  public boolean isEmpty() {
    return mRecyclerViewAdapter == null || mRecyclerViewAdapter.getItemCount() == 0;
  }

  @Override
  public Object getItemAtPosition(int position) {
    return mRecyclerViewAdapter != null ? mRecyclerViewAdapter.getItem(position) : null;
  }

  @Override
  public int getHeaderViewsCount() {
    return mHeaderViews.size();
  }

  @Override
  public int getFooterViewsCount() {
    return mFooterViews.size();
  }

  @Override
  public void scrollTo(int x, int y) {
    int dx = x - getScrollX();
    int dy = y - getScrollY();

    mRecyclerView.scrollBy(dx, dy);
  }

  @Override
  public void scrollBy(int dx, int dy) {
    mRecyclerView.scrollBy(dx, dy);
  }

  @Override
  public int getScrollX() {
    return mRecyclerView.getScrollX();
  }

  @Override
  public int getScrollY() {
    return mRecyclerView.getScrollY();
  }

  @Override
  public void addFooterView(View view) {
    addFooterView(view, null, true);
  }

  @Override
  public void setEmptyView(View empty) {
    mRecyclerView.setEmptyView(empty);
  }

  @Override
  public void setOnTouchListener(View.OnTouchListener onTouchListener) {
    mRecyclerView.setOnTouchListener(onTouchListener);
  }

  @Override
  public void setScrollingCacheEnabled(boolean b) {
    // Ignore for RecyclerView. There's no such thing and RecyclerView doesn't need this to function
  }

  @Override
  public void setAdapter(Adapter adapter) {
    if (adapter == null) {
      mRecyclerViewAdapter = null;
      mListViewAdapter = null;
      mRecyclerView.setAdapter(null);
      return;
    }

    mListViewAdapter = adapter;
    mRecyclerViewAdapter = new LinearRecyclerViewAdapter(mRecyclerView, adapter);
    updateAuxiliaryViews();
    mRecyclerView.setAdapter(mRecyclerViewAdapter);
  }

  @Override
  public void setSelection(int position) {
    mRecyclerView.setSelection(position);
  }

  @Override
  public void setSelectionFromTop(int position, int offset) {
    mRecyclerView.setSelectionFromTop(position, offset);
  }

  @Override
  public void setSelectionAfterHeaderView() {
    mRecyclerView.setSelection(getHeaderViewsCount());
  }

  @Override
  public void destroyDrawingCache() {
    mRecyclerView.destroyDrawingCache();
  }

  @Override
  public long getItemIdAtPosition(int position) {
    return getAdapter() != null ? getAdapter().getItemId(position) : ListView.INVALID_ROW_ID;
  }

  @Override
  public Parcelable onSaveInstanceState() {
    return mRecyclerView.onSaveInstanceState();
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    mRecyclerView.onRestoreInstanceState(state);
  }

  @Override
  public int getChoiceMode() {
    unimplemented();
    return 0;
  }

  /**
   * Not fully implemented, only updates the choice mode state value and does not do any
   * selection/checking of items.
   */
  @Override
  public void setChoiceMode(int choiceMode) {
    unimplemented();
  }

  private void updateAuxiliaryViews() {
    if (mRecyclerViewAdapter == null) {
      return;
    }
    mRecyclerViewAdapter.setHeaderViews(mHeaderViews);
    mRecyclerViewAdapter.setFooterViews(mFooterViews);
  }

  private final void unimplemented() throws UnsupportedOperationException {
    throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD);
  }

  private RecyclerView.OnScrollListener createDelegatingScrollListener() {
    return new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (mScrollListener != null) {
          mScrollListener.onScrollStateChanged(RecyclerViewProxy.this, newState);
        }
      }

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        // If we get NO_POSITION, the adapter was refreshed and the UI didn't catch up yet.
        if (mFirstVisibleItem == NO_POSITION) {
          return;
        }

        mLastVisibleItem = layoutManager.findLastVisibleItemPosition();
        int visibleItemCount = mLastVisibleItem - mFirstVisibleItem + 1;
        int adapterSize = getCount();
        if (mScrollListener != null) {
          mScrollListener.onScroll(
              RecyclerViewProxy.this,
              mFirstVisibleItem,
              visibleItemCount,
              adapterSize);
        }
      }
    };
  }
}
