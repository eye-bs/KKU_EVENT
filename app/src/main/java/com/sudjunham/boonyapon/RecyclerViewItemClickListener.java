package com.sudjunham.boonyapon;

import android.view.View;

public interface RecyclerViewItemClickListener
{
    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
}