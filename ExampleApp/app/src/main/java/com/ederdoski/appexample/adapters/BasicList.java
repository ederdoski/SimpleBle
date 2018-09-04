package com.ederdoski.appexample.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by codeorchop on 12/01/16.
 */
public abstract class BasicList extends BaseAdapter {

    private ArrayList<?> items;
    private int R_layout_IdView;
    private Activity act;

    public BasicList(Activity act, int R_layout_IdView, ArrayList<?> items) {
        super();
        this.act   = act;
        this.items = items;
        this.R_layout_IdView = R_layout_IdView;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater vi = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R_layout_IdView, null);
        }
        onItem(items.get(position), view, position);
        return view;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int posicion) {
        return items.get(posicion);
    }

    @Override
    public long getItemId(int posicion) {
        return posicion;
    }

    public abstract void onItem(Object item, View view, int position);

}
