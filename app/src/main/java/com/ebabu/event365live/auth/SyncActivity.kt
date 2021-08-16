package com.ebabu.event365live.auth

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ebabu.event365live.R
import com.kienht.bubblepicker.BubblePickerListener
import com.kienht.bubblepicker.adapter.BubblePickerAdapter
import com.kienht.bubblepicker.model.PickerItem
import kotlinx.android.synthetic.main.activity_demo.*

/**
 * @author kienht
 * @since 20/07/2018
 */
class SyncActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        val titles = resources.getStringArray(R.array.countries)

        bubbleDemo.adapter = object : BubblePickerAdapter {
            override val totalCount = titles.size

            override fun getItem(position: Int): PickerItem {
                return PickerItem().apply {
                    title = titles[position]
                }
            }
        }

        bubbleDemo.bubbleSize = 5
        bubbleDemo.listener = object : BubblePickerListener {
            override fun onBubbleDeselected(item: PickerItem) {
            }

            override fun onBubbleSelected(item: PickerItem) {
                Log.d("MUKEE",item.title)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bubbleDemo.onResume()
    }

    override fun onPause() {
        super.onPause()
        bubbleDemo.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}