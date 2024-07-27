package com.example.twitturin.core.extensions

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.twitturin.R

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

fun Spinner.reportSpinner(context: Context,feedbackSpinner: Spinner, feedbackTopicLayout2: View) {
    ArrayAdapter.createFromResource(
        context, R.array.feedback_array, android.R.layout.simple_spinner_item
    ).also { adapter ->
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        feedbackSpinner.adapter = adapter
    }

    feedbackSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
            if (parent?.getItemAtPosition(position).toString() == "Other") {
                feedbackTopicLayout2.beVisible()
            } else {
                feedbackTopicLayout2.beGone()
            }
        }
        override fun onNothingSelected(p0: AdapterView<*>?) {}
    }
}