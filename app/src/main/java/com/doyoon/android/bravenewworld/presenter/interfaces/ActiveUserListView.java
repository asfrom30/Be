package com.doyoon.android.bravenewworld.presenter.interfaces;

/**
 * Created by DOYOON on 7/16/2017.
 */

public interface ActiveUserListView {

    void notifyListDataSetChanged();
    void notifyListDataRemoved(int position);
    void notifyListDataAdded(int position);
}
