package com.example.twitturin.core.extensions

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

fun Spinner.populateFromResource(
    context: Context,
    arrayResId: Int,
    dropDownLayoutResId: Int = android.R.layout.simple_spinner_dropdown_item,
    itemLayoutResId: Int = android.R.layout.simple_spinner_item,
    onItemSelected: ((Spinner, Int, Long) -> Unit)? = null
) {
    ArrayAdapter.createFromResource(
        context,
        arrayResId,
        itemLayoutResId
    ).also { adapter ->
        adapter.setDropDownViewResource(dropDownLayoutResId)
        this.adapter = adapter
    }

    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            onItemSelected?.invoke(this@populateFromResource, position, id)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}