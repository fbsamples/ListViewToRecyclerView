/*
 * Copyright 2016-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.listviewtorecyclerview;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import static android.view.GestureDetector.SimpleOnGestureListener;

/**
 * A {@code RecyclerView} that adds support for OnItemClickListeners and Empty Views.
 * This RecyclerView is intended to always be used with a {@link LinearLayoutManager}
 * and inside of {@link RecyclerViewProxy}.
 */
public class LinearRecyclerView extends RecyclerView {

  private final Handler mGuiHandler = new Handler(Looper.getMainLooper());

  private final GestureDetector mClickGestureDetector =
      new GestureDetector(getContext(), new ClickGestureListener(), mGuiHandler);
  private final GestureDetector  mLongClickGestureDetector =
      new GestureDetector(getContext(), new LongClickGestureListener(), mGuiHandler);
  private final OnItemTouchListener mOnItemTouchListenerForClick =
      new OnItemTouchListenerForClick();
  private final OnItemTouchListener mOnItemTouchListenerForLongClick =
      new OnItemTouchListenerForLongClick();
  private final EmptyAdapterDataObserver mEmptyAdapterDataObserver = new EmptyAdapterDataObserver();

  private View mEmptyView;
  private OnItemClickListener mOnItemClickListener;
  private OnItemLongClickListener mOnItemLongClickListener;
  private boolean mIsClippedToPadding;
  private boolean mHasOnItemClickListener;
  private boolean mHasOnItemLongClickListener;
  private int mContainerVisibility;

  /**
   * Interface definition for a callback to be invoked when an item in this
   * RecyclerView has been clicked.
   */
  public interface OnItemClickListener {

    /**
     * Called when an item in the {@code RecyclerView} has been clicked.
     *
     * @param parent the recycler view
     * @param view the view that was clicked
     * @param position the adapter position of the item that was clicked
     * @param id the item id of the item or {@link #NO_ID} if it doesn't have an id
     */
    void onItemClick(LinearRecyclerView parent, View view, int position, long id);
  }

  /**
   * Interface definition for a callback to be invoked when an item in this
   * RecyclerView has been long clicked.
   */
  public interface OnItemLongClickListener {

    /**
     * Called when an item in the {@code RecyclerView} has been long-clicked.
     *
     * @param parent the recycler view
     * @param view the view that was long clicked
     * @param position the adapter position of the item that was long-clicked
     * @param id the item id of the item or {@link #NO_ID} if it doesn't have an id
     * @return the result is currently ignored.
     */
    boolean onItemLongClick(LinearRecyclerView parent, View view, int position, long id);
  }

  public LinearRecyclerView(Context context) {
    super(context);
    init();
  }

  public LinearRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public LinearRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    mContainerVisibility = super.getVisibility();
  }

  @Override
  public void setAdapter(Adapter adapter) {
    final Adapter oldAdapter = getAdapter();
    if (oldAdapter != null) {
      oldAdapter.unregisterAdapterDataObserver(mEmptyAdapterDataObserver);
    }
    super.setAdapter(adapter);
    if (adapter != null) {
      adapter.registerAdapterDataObserver(mEmptyAdapterDataObserver);
    }

    updateViewVisibility();
  }

  @Override
  public Parcelable onSaveInstanceState() {
    return super.onSaveInstanceState();
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    super.onRestoreInstanceState(state);
  }

  /**
   * Now that we can have empty views covering the RecyclerView, the visibility of this view is
   * not enough to determine whether the component as a whole is visible or not. setVisibility will
   * either ensure that the whole component is invisible or the proper part of the component is
   * visible.
   */
  @Override
  public void setVisibility(int visibility) {
    mContainerVisibility = visibility;
    updateViewVisibility();
  }

  /**
   * Returns VISIBLE if this OR the empty view is visible, otherwise it returns the overridden
   * visibility.
   */
  @Override
  public int getVisibility() {
    return mContainerVisibility;
  }

  /**
   * Similar to {@link ListView}.setEmptyView(), if the adapter is empty, show the {@code emptyView}
   * instead.
   * @param emptyView The {@link View} to show when the adapter is empty, or null to use default
   * behavior.
   */
  public void setEmptyView(View emptyView) {
    mEmptyView = emptyView;
    updateViewVisibility();
  }

  private void updateViewVisibility() {
    if (mEmptyView == null) {
      super.setVisibility(mContainerVisibility);
      return;
    }

    boolean emptyViewVisible = !hasContent(getAdapter());
    mEmptyView.setVisibility(emptyViewVisible ? mContainerVisibility : GONE);
    super.setVisibility(emptyViewVisible ? GONE : mContainerVisibility);
  }

  /**
   * ListView shows its Empty View when its adapter is empty. ListView also does not put headers
   * and footers into its adapter. We needed to emulate headers and footers,
   * but we did so by adding elements to the adapter. To determine "emptiness", we need special
   * behavior for LinearRecyclerViewAdapter, which is handled in this method.
   */
  private static boolean hasContent(Adapter adapter) {
    if (adapter == null) {
      return false;
    }

    if (adapter instanceof LinearRecyclerViewAdapter) {
      return ((LinearRecyclerViewAdapter) adapter).hasContent();
    }

    return adapter.getItemCount() > 0;
  }

  public void setOnItemClickListener(final OnItemClickListener listener) {
    if (listener == null && mHasOnItemClickListener) {
      removeOnItemTouchListener(mOnItemTouchListenerForClick);
    }

    if (!mHasOnItemClickListener && listener != null) {
      addOnItemTouchListener(mOnItemTouchListenerForClick);
    }

    mOnItemClickListener = listener;
    mHasOnItemClickListener = listener != null;
  }

  public void setOnItemLongClickListener(OnItemLongClickListener listener) {
    if (!isLongClickable()) {
      setLongClickable(true);
    }

    if (listener == null && mHasOnItemLongClickListener) {
      removeOnItemTouchListener(mOnItemTouchListenerForLongClick);
    }

    if (!mHasOnItemLongClickListener && listener != null) {
      addOnItemTouchListener(mOnItemTouchListenerForLongClick);
    }

    mOnItemLongClickListener = listener;
    mHasOnItemLongClickListener = listener != null;
  }

  public LinearLayoutManager getLinearLayoutManager() {
    return (LinearLayoutManager) getLayoutManager();
  }

  public int getLastVisiblePosition() {
    return getLinearLayoutManager().findLastVisibleItemPosition();
  }

  @Override
  public void setClipToPadding(boolean clipToPadding) {
    mIsClippedToPadding = clipToPadding;
    super.setClipToPadding(clipToPadding);
  }

  @Override
  public boolean getClipToPadding() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      return super.getClipToPadding();
    }
    return mIsClippedToPadding;
  }

  public void setSelection(int position) {
    scrollToPosition(position);
  }

  public void setSelectionFromTop(int position, int offset) {
    getLinearLayoutManager().scrollToPositionWithOffset(position, offset);
  }

  private class ClickGestureListener extends SimpleOnGestureListener {

    public ClickGestureListener() {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      View childView = findChildViewUnder(e.getX(), e.getY());

      if (childView != null && mOnItemClickListener != null) {
        int childPosition = getChildAdapterPosition(childView);
        if (childPosition != NO_POSITION) {
          mOnItemClickListener.onItemClick(
              LinearRecyclerView.this,
              childView,
              childPosition,
              getChildItemId(childView));
        }
      }
      return true;
    }
  }

  private class EmptyAdapterDataObserver extends RecyclerView.AdapterDataObserver {
    @Override
    public void onChanged() {
      updateViewVisibility();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      updateViewVisibility();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      updateViewVisibility();
    }
  }

  private class LongClickGestureListener extends SimpleOnGestureListener {

    public LongClickGestureListener() {}

    @Override
    public void onLongPress(MotionEvent e) {
      View childView = findChildViewUnder(e.getX(), e.getY());
      if (childView == null || mOnItemLongClickListener == null) {
        return;
      }

      int childPosition = getChildAdapterPosition(childView);
      if (childPosition == NO_POSITION) {
        return;
      }
      boolean result = mOnItemLongClickListener.onItemLongClick(
          LinearRecyclerView.this,
          childView,
          childPosition,
          getChildItemId(childView));
      if (result) {
        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
      }
    }
  }

  private class OnItemTouchListenerForClick implements OnItemTouchListener {

    public OnItemTouchListenerForClick() {}

    @Override
    public boolean onInterceptTouchEvent(RecyclerView v, MotionEvent e) {
      mClickGestureDetector.onTouchEvent(e);
      return false;
    }

    @Override
    public void onTouchEvent(RecyclerView v, MotionEvent e) {}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
  }

  private class OnItemTouchListenerForLongClick implements OnItemTouchListener {

    public OnItemTouchListenerForLongClick() {}

    @Override
    public boolean onInterceptTouchEvent(RecyclerView v, MotionEvent e) {
      mLongClickGestureDetector.onTouchEvent(e);
      return false;
    }

    @Override
    public void onTouchEvent(RecyclerView v, MotionEvent e) {}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
  }
}
