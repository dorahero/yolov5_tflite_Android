package com.modovision.whatthemask.utils

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.modovision.whatthemask.R


class CustomGrid(context: Context, text: ArrayList<String>, imageId: ArrayList<Bitmap>) : BaseAdapter() {
    private val context: Context
    private val text: ArrayList<String>
//    private val imageId: IntArray
    private val imageId: ArrayList<Bitmap>
    override fun getCount(): Int {
        return text.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var grid: View
        // Context 動態放入mainActivity
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) {
            grid = View(context)
            // 將grid_single 動態載入(image+text)
            grid = layoutInflater.inflate(R.layout.grid_view, null)
            val textView = grid.findViewById(R.id.grid_text) as TextView
            val imageView: ImageView = grid.findViewById(R.id.grid_image) as ImageView
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            textView.text = text[position]
            imageView.setImageBitmap(imageId[position])
        } else {
            grid = convertView as View
        }
        return grid
    }

    init {
        this.context = context
        this.text = text
        this.imageId = imageId
    }
}