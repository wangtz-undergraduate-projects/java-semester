package com.wudaokou.easylearn.utils;

public interface ItemDragListener {
    void onItemMove(int fromPosition, int toPosition);

    void onItemSwiped(int position);
}