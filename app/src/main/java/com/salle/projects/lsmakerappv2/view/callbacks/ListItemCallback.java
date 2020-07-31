package com.salle.projects.lsmakerappv2.view.callbacks;

public interface ListItemCallback {
    void onItemClick(int index);
    void onItemClick(Object obj);
    void onDeleteItem(Object obj);
}
