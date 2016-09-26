/*
 * Copyright 2016-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.sampleapp;

import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.listviewtorecyclerview.LinearRecyclerView;
import com.facebook.listviewtorecyclerview.ListViewProxy;
import com.facebook.listviewtorecyclerview.RecyclerViewProxy;
import com.facebook.listviewtorecyclerview.ScrollingViewProxy;

import java.util.Random;

/**
 * A sample activity to demonstrate how to set up a ListView to RecyclerView migration
 * using {@link ScrollingViewProxy}.
 *
 * Shows the numbers 1 through 10 in randomly either a ListView or RecyclerView.
 */
public class ScrollingActivity extends AppCompatActivity {

  private Random mRandom = new Random();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    boolean isRecyclerView = mRandom.nextBoolean();
    FrameLayout contentView = (FrameLayout) findViewById(android.R.id.content);
    contentView.setBackgroundColor(Color.BLACK);
    ScrollingViewProxy proxy = createScrollingViewProxy(isRecyclerView);
    proxy.setAdapter(new Adapter());
    contentView.addView(proxy.getView());

    TextView header = new TextView(getBaseContext());
    header.setText(isRecyclerView ? "RECYCLERVIEW" : "TEXTVIEW");
    proxy.addHeaderView(header);
  }

  private ScrollingViewProxy createScrollingViewProxy(boolean isRecyclerView) {
    if (isRecyclerView) {
      LinearRecyclerView recyclerView = new LinearRecyclerView(getBaseContext());
      recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
      return new RecyclerViewProxy(recyclerView);
    }
    ListView listView = new ListView(getBaseContext());
    listView.setDividerHeight(0);
    listView.setDivider(null);
    return new ListViewProxy(listView);
  }

  private class Adapter extends ScrollingViewProxy.BaseAdapter {

    @Override
    public View createView(int itemViewType, ViewGroup parent) {
      return new TextView(getBaseContext());
    }

    @Override
    public void bindView(int position, Object item, View view, int itemViewType, ViewGroup parent) {
      ((TextView) view).setText("" + position);
    }

    @Override
    public boolean areAllItemsEnabled() {
      return true;
    }

    @Override
    public boolean isEnabled(int position) {
      return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
      return 10;
    }

    @Override
    public Object getItem(int position) {
      return position;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public boolean hasStableIds() {
      return false;
    }

    @Override
    public int getItemViewType(int position) {
      return 0;
    }

    @Override
    public int getViewTypeCount() {
      return 1;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }
  }
}
