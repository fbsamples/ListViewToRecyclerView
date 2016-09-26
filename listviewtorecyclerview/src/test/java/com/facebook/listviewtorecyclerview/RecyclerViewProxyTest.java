/*
 * Copyright 2016-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.listviewtorecyclerview;

import android.app.Activity;
import android.database.DataSetObserver;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.listviewtorecyclerview.ScrollingViewProxy.OnItemClickListener;
import com.facebook.listviewtorecyclerview.ScrollingViewProxy.OnItemLongClickListener;
import com.facebook.listviewtorecyclerview.ScrollingViewProxy.OnScrollListener;
import com.facebook.listviewtorecyclerview.ScrollingViewProxy.RecyclerListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests {@link RecyclerViewProxy}.
 */
@Config(manifest=Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class RecyclerViewProxyTest {

  @Mock public LinearRecyclerView mRecyclerView;
  @Mock public LinearLayoutManager mLayoutManager;
  @Mock public View.OnTouchListener mOnTouchListener;
  @Mock public OnItemClickListener mOnItemClickListener;
  @Mock public OnItemLongClickListener mOnItemLongClickListener;
  @Mock public View mChildView;
  @Mock public Object mItem;
  @Mock public MotionEvent mMotionEvent;
  @Mock public ScrollingViewProxy.Adapter mAdapter;

  @Captor ArgumentCaptor<RecyclerView.OnScrollListener> mOnScrollListenerCaptor;
  @Captor ArgumentCaptor<LinearRecyclerView.OnItemClickListener> mViewOnItemClickListenerCaptor;
  @Captor ArgumentCaptor<LinearRecyclerView.OnItemLongClickListener>
      mViewOnItemLongClickListenerCaptor;

  private final View mFirstHeaderView = mock(View.class);
  private final View mSecondHeaderView = mock(View.class);

  private RecyclerViewProxy mRecyclerViewProxy;
  private Activity mActivity;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mActivity = Robolectric.buildActivity(Activity.class).create().get();
    when(mRecyclerView.getLayoutManager()).thenReturn(mLayoutManager);
    when(mRecyclerView.getLinearLayoutManager()).thenReturn(mLayoutManager);
    when(mRecyclerView.getContext()).thenReturn(mActivity);
    mRecyclerViewProxy = new RecyclerViewProxy(mRecyclerView);
  }

  @Test
  public void testGetView() {
    assertThat(mRecyclerViewProxy.getView()).isEqualTo(mRecyclerView);
  }

  @Test
  public void testAddScrollListener() {
    OnScrollListener onScrollListener = mock(OnScrollListener.class);
    mRecyclerViewProxy.setOnScrollListener(onScrollListener);

    verify(mRecyclerView).addOnScrollListener(mOnScrollListenerCaptor.capture());
    mOnScrollListenerCaptor.getValue().onScrolled(mRecyclerView, 0, 20);
    verify(onScrollListener)
        .onScroll(same(mRecyclerViewProxy), anyInt(), anyInt(), anyInt());
  }

  @Test
  public void testNullScrollListener() {
    OnScrollListener onScrollListener = mock(OnScrollListener.class);
    mRecyclerViewProxy.setOnScrollListener(onScrollListener);
    mRecyclerViewProxy.setOnScrollListener(null);

    verify(mRecyclerView).addOnScrollListener(mOnScrollListenerCaptor.capture());
    mOnScrollListenerCaptor.getValue().onScrolled(mRecyclerView, 0, 20);
    verifyZeroInteractions(onScrollListener);
  }

  @Test
  public void testSetRecyclerListener() {
    mRecyclerViewProxy.setRecyclerListener(mock(RecyclerListener.class));

    verify(mRecyclerView).setRecyclerListener(any(RecyclerView.RecyclerListener.class));
  }

  @Test
  public void testSetAdapter() {
    mRecyclerViewProxy.setAdapter(mAdapter);

    verify(mRecyclerView).setAdapter(any(Adapter.class));
  }

  @Test
  public void testSetAdapterNull() {
    mRecyclerViewProxy.setAdapter(mAdapter);
    mRecyclerViewProxy.setAdapter(null);

    verify(mRecyclerView).setAdapter(null);
  }

  @Test(expected = Exception.class)
  public void testCrashForNonLinearLayoutManagerOnInit() {
    LinearRecyclerView recyclerView = createLinearRecyclerView();
    when(recyclerView.getLayoutManager()).thenReturn(mock(RecyclerView.LayoutManager.class));
    new RecyclerViewProxy(recyclerView);
  }

  @Test
  public void testDelegatedAdapterObservesDelegate() {
    ArgumentCaptor<Adapter> recyclerAdapter = ArgumentCaptor.forClass(Adapter.class);
    RecyclerView.AdapterDataObserver mockObserver = mock(RecyclerView.AdapterDataObserver.class);
    ArgumentCaptor<DataSetObserver> observer = ArgumentCaptor.forClass(DataSetObserver.class);

    mRecyclerViewProxy.setAdapter(mAdapter);
    verify(mRecyclerView).setAdapter(recyclerAdapter.capture());

    recyclerAdapter.getValue().registerAdapterDataObserver(mockObserver);
    verify(mAdapter).registerDataSetObserver(observer.capture());

    observer.getValue().onChanged();
    verify(mockObserver).onChanged();
  }

  @Test
  public void testScrollTo() {
    when(mRecyclerView.getScrollX()).thenReturn(10);
    when(mRecyclerView.getScrollY()).thenReturn(20);

    mRecyclerViewProxy.scrollTo(15, 25);

    verify(mRecyclerView).scrollBy(5, 5);
  }

  @Test
  public void testSetOnTouchListener() {
    mRecyclerViewProxy.setOnTouchListener(mOnTouchListener);

    verify(mRecyclerView).setOnTouchListener(eq(mOnTouchListener));
  }

  @Test
  public void testSetOnItemClickListener() {
    mRecyclerViewProxy.setOnItemClickListener(mOnItemClickListener);

    verify(mRecyclerView).setOnItemClickListener(mViewOnItemClickListenerCaptor.capture());

    mViewOnItemClickListenerCaptor.getValue().onItemClick(mRecyclerView, mChildView, 0, 0);

    verify(mOnItemClickListener).onItemClick(mRecyclerView, mChildView, 0, 0);
  }

  @Test
  public void testSetOnItemLongClickListener() {
    mRecyclerViewProxy.setOnItemLongClickListener(mOnItemLongClickListener);

    verify(mRecyclerView).setOnItemLongClickListener(mViewOnItemLongClickListenerCaptor.capture());

    mViewOnItemLongClickListenerCaptor.getValue().onItemLongClick(mRecyclerView, mChildView, 0, 0);

    verify(mOnItemLongClickListener).onItemLongClick(mRecyclerView, mChildView, 0, 0);
  }

  @Test
  public void testHeaderAndFooterViews() {
    LinearRecyclerView recyclerView = createLinearRecyclerView();
    RecyclerViewProxy proxy = new RecyclerViewProxy(recyclerView);

    when(mAdapter.getCount()).thenReturn(5);
    proxy.setAdapter(mAdapter);
    View[] headers = new View[] { mock(View.class), mock(View.class) };
    View[] footers = new View[] { mock(View.class), mock(View.class) };
    for (View view : headers) {
      proxy.addHeaderView(view);
    }
    for (View view : footers) {
      proxy.addFooterView(view);
    }

    Adapter adapter = recyclerView.getAdapter();
    assertThat(adapter.getItemCount()).isEqualTo(5 + headers.length + footers.length);
    assertThat(dumpViewHolder(recyclerView, 0).itemView).isSameAs(headers[0]);
    assertThat(dumpViewHolder(recyclerView, 1).itemView).isSameAs(headers[1]);
    assertThat(dumpViewHolder(recyclerView, 7).itemView).isSameAs(footers[0]);
    assertThat(dumpViewHolder(recyclerView, 8).itemView).isSameAs(footers[1]);
  }

  @Test
  public void testHeaderAndFooterViewsAreKept() {
    LinearRecyclerView recyclerView = createLinearRecyclerView();
    RecyclerViewProxy proxy = new RecyclerViewProxy(recyclerView);

    when(mAdapter.getCount()).thenReturn(5);
    proxy.addHeaderView(mock(View.class));
    proxy.addFooterView(mock(View.class));
    proxy.setAdapter(mAdapter);

    assertThat(recyclerView.getAdapter().getItemCount()).isEqualTo(7);
  }

  @Test
  public void testAddAndRemoveHeadersAndFooters() {
    LinearRecyclerView recyclerView = createLinearRecyclerView();
    RecyclerViewProxy proxy = new RecyclerViewProxy(recyclerView);

    when(mAdapter.getCount()).thenReturn(5);
    View header = mock(View.class);
    View footer = mock(View.class);
    proxy.addHeaderView(header);
    proxy.addFooterView(footer);
    proxy.setAdapter(mAdapter);

    proxy.removeHeaderView(header);
    proxy.removeFooterView(footer);

    assertThat(recyclerView.getAdapter().getItemCount()).isEqualTo(5);
  }

  @Test
  public void testGetHeaderViewsCount() {
    mRecyclerViewProxy.addHeaderView(mFirstHeaderView);
    mRecyclerViewProxy.addHeaderView(mSecondHeaderView);

    assertThat(mRecyclerViewProxy.getHeaderViewsCount()).isEqualTo(2);
  }

  @Test
  public void testDestroyDrawingCache() {
    mRecyclerViewProxy.destroyDrawingCache();

    verify(mRecyclerView).destroyDrawingCache();
  }

  @Test
  public void testSetSelection() {
    mRecyclerViewProxy.setSelection(0);

    verify(mRecyclerView).setSelection(0);
  }

  @Test
  public void testSetSelectionFromTop() {
    when(mRecyclerView.getClipToPadding()).thenReturn(true);

    mRecyclerViewProxy.setSelectionFromTop(0, 1);

    verify(mRecyclerView).setSelectionFromTop(0, 1);
  }

  @Test
  public void testSetSelectionAfterHeaderView() {
    mRecyclerViewProxy.addHeaderView(mFirstHeaderView);
    mRecyclerViewProxy.addHeaderView(mSecondHeaderView);
    mRecyclerViewProxy.setSelectionAfterHeaderView();

    verify(mRecyclerView).setSelection(2);
  }

  @Test
  public void testGetItemAtPosition() {
    when(mAdapter.getCount()).thenReturn(2);
    when(mAdapter.getItem(1)).thenReturn(mItem);

    mRecyclerViewProxy.setAdapter(mAdapter);

    assertThat(mRecyclerViewProxy.getItemAtPosition(1)).isSameAs(mItem);
  }

  @Test
  public void testGetItemAtPositionWithoutAdapter() {
    assertThat(mRecyclerViewProxy.getItemAtPosition(1)).isNull();
  }

  @Test
  public void testGetItemAtNegativePosition() {
    when(mAdapter.getItem(-1)).thenReturn(mItem);

    mRecyclerViewProxy.setAdapter(mAdapter);

    assertThat(mRecyclerViewProxy.getItemAtPosition(-1)).isNull();
  }

  @Test
  public void getPositionForView() {
    int childViewPosition = 15;
    when(mRecyclerView.getChildLayoutPosition(any(View.class))).thenReturn(childViewPosition);

    assertThat(mRecyclerViewProxy.getPositionForView(mChildView)).isEqualTo(childViewPosition);
  }

  @Test
  public void testSmoothScrollToPositionFromTopWithDelta() {
    int position = 5;
    int delta = 32;
    mRecyclerViewProxy.smoothScrollToPositionFromTop(position, delta);

    verify(mLayoutManager).scrollToPositionWithOffset(position, delta);
  }

  private RecyclerView.ViewHolder dumpViewHolder(
      LinearRecyclerView recyclerView,
      int position) {
    Adapter adapter = recyclerView.getAdapter();
    int itemViewType = adapter.getItemViewType(position);
    return adapter.onCreateViewHolder(recyclerView, itemViewType);
  }

  private LinearRecyclerView createLinearRecyclerView() {
    LinearRecyclerView recyclerView = new LinearRecyclerView(mActivity);
    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    return recyclerView;
  }
}
