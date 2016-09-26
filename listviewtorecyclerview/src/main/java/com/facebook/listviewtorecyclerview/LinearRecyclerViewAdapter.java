/*
 * Copyright 2016-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.listviewtorecyclerview;

import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.listviewtorecyclerview.LinearRecyclerViewAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link RecyclerView.Adapter} which wraps a {@link ScrollingViewProxy.Adapter} and adds support
 * for header and footer views.
 */
class LinearRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<ViewHolder> {

  static class ViewHolder<VH extends RecyclerView.ViewHolder>
      extends RecyclerView.ViewHolder {

    ViewHolder(View itemView) {
      super(itemView);
    }
  }

  private final LinearRecyclerView mRecyclerView;
  private final ScrollingViewProxy.Adapter mAdapter;
  private List<View> mHeaderViews;
  private List<View> mFooterViews;
  private boolean mObservingListAdapter;
  private int mObserverCount;

  private final DataSetObserver mObserver = new DataSetObserver() {
    @Override
    public void onChanged() {
      RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
      if (layoutManager != null) {
        layoutManager.assertNotInLayoutOrScroll(
            "Do not call notifyDataSetChanged() while scrolling or in layout.");
      }
      notifyDataSetChanged();
    }

    @Override
    public void onInvalidated() {
      notifyDataSetChanged();
    }
  };

  public LinearRecyclerViewAdapter(
      LinearRecyclerView recyclerView,
      ScrollingViewProxy.Adapter adapter) {
    mRecyclerView = recyclerView;
    mAdapter = adapter;
    mHeaderViews = Collections.emptyList();
    mFooterViews = Collections.emptyList();
    setHasStableIds(mAdapter.hasStableIds());
  }

  public void setHeaderViews(ArrayList<View> headerViews) {
    if (headerViews == null) {
      mHeaderViews = Collections.emptyList();
    } else {
      mHeaderViews = headerViews;
    }
    notifyDataSetChanged();
  }

  public void setFooterViews(ArrayList<View> footerViews) {
    if (footerViews == null) {
      mFooterViews = Collections.emptyList();
    } else {
      mFooterViews = footerViews;
    }
    notifyDataSetChanged();
  }

  public Object getItem(int position) {
    int headersCount = mHeaderViews.size();
    if (position < headersCount || position >= mAdapter.getCount() + headersCount) {
      return null;
    }
    return mAdapter.getItem(position - headersCount);
  }

  @Override
  public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
    mObserverCount++;
    super.registerAdapterDataObserver(observer);
    if (!mObservingListAdapter) {
      mAdapter.registerDataSetObserver(mObserver);
      mObservingListAdapter = true;
    }
  }

  @Override
  public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
    mObserverCount--;
    super.unregisterAdapterDataObserver(observer);
    if (mObservingListAdapter && mObserverCount == 0) {
      mAdapter.unregisterDataSetObserver(mObserver);
      mObservingListAdapter = false;
    }
  }

  @Override
  public int getItemViewType(int position) {
    int headersCount = mHeaderViews.size();
    int adapterCount = mAdapter.getCount();
    if (position < headersCount) {
      return translateHeaderPositionToId(position);
    }
    int adjustedPosition = position - headersCount;
    if (adjustedPosition >= adapterCount) {
      return translateFooterPositionToId(position);
    }
    return mAdapter.getItemViewType(adjustedPosition);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType < 0) {
      View view = findAuxiliaryView(viewType);
      return new ViewHolder(view);
    }

    return new ViewHolder(mAdapter.createView(viewType, parent));
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    int adjustedPosition = position - mHeaderViews.size();
    if (adjustedPosition < 0 || adjustedPosition >= mAdapter.getCount()) {
      return;
    }
    mAdapter.getView(adjustedPosition, holder.itemView, mRecyclerView);
  }

  @Override
  public int getItemCount() {
    return mHeaderViews.size() + mAdapter.getCount() + mFooterViews.size();
  }

  public int getHeaderItemCount() {
    return mHeaderViews.size();
  }

  @Override
  public long getItemId(int position) {
    int headersCount = mHeaderViews.size();
    int adapterCount = mAdapter.getCount();
    if (position < headersCount) {
      return translateHeaderPositionToId(position);
    }
    int adjustedPosition = position - headersCount;
    if (adjustedPosition >= adapterCount) {
      return translateFooterPositionToId(position);
    }
    return mAdapter.getItemId(adjustedPosition);
  }

  /**
   * Returns whether or not the adapter this is delegating to has content.
   *
   * LinearRecyclerViewAdapter implements Headers and Footers differently than ListView did.
   * Because of this, we need a way to determine if the wrapped adapter has content.
   * This gives us the ability to properly implement "Empty Views" for our RecyclerView.
   *
   * @return Whether or not the adapter this is delegating to has content.
   */
  protected boolean hasContent() {
    return mAdapter.getCount() > 0;
  }

  private View findAuxiliaryView(int viewType) {
    if (viewType % 2 == 0) {
      int index = (-viewType / 2) - 1;
      return mFooterViews.get(index);
    } else {
      int index = -((viewType + 1) / 2);
      return mHeaderViews.get(index);
    }
  }

  /** Embeds headers views positions to ids in -1, -3, -5, ... */
  private int translateHeaderPositionToId(int position) {
    return -1 - 2 * position;
  }

  /** Embeds footer views positions to ids in -2, -4, -6, ... */
  private int translateFooterPositionToId(int position) {
    int i = position - mAdapter.getCount() - mHeaderViews.size();
    return - 2 * (i + 1);
  }
}
