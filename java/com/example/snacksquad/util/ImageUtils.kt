package com.example.snacksquad.util

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView

object ImageUtils {
    fun loadOptimizedImage(context: Context, resourceId: Int, imageView: ImageView) {
        imageView.tag = resourceId
        val targetWidth = imageView.width
        val targetHeight = imageView.height

        if (targetWidth <= 0 || targetHeight <= 0) {
            imageView.post {
                if (imageView.tag == resourceId) {
                    loadOptimizedImage(context, resourceId, imageView)
                }
            }
            return
        }

        val boundsOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeResource(context.resources, resourceId, boundsOptions)

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(boundsOptions, targetWidth, targetHeight)
            inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
        }
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, decodeOptions)
        if (imageView.tag == resourceId) {
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        requestedWidth: Int,
        requestedHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > requestedHeight || width > requestedWidth) {
            var halfHeight = height / 2
            var halfWidth = width / 2

            while (
                halfHeight / inSampleSize >= requestedHeight &&
                halfWidth / inSampleSize >= requestedWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
