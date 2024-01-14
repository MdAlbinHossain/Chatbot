package bd.com.albin.chatbot.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri

fun imageUriToBitmap(context: Context, uri: Uri): Bitmap {
    val source = ImageDecoder.createSource(context.contentResolver, uri)
    val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
        decoder.setTargetSize(512, 512)
    }
    return bitmap
}