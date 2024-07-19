package com.example.twitturin.core.extensions

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.vertical() = this.apply {
    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
}

fun RecyclerView.grid() = this.apply {
    layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
}