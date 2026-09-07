package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.model.ExportFormat
import com.example.model.StoryCustomization
import com.example.model.YouTubeVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

object StoryExportHelper {

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Odkaz byl zkopírován do schránky! 📋", Toast.LENGTH_SHORT).show()
    }

    fun openInYouTube(context: Context, videoId: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
        try {
            context.startActivity(appIntent)
        } catch (_: Exception) {
            context.startActivity(webIntent)
        }
    }

    private suspend fun fetchThumbnailBitmap(context: Context, url: String): Bitmap? = withContext(Dispatchers.IO) {
        // Try Coil ImageLoader first
        try {
            val loader = ImageLoader(context)
            val req = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false) // Must be non-hardware bitmap to draw on software Canvas
                .build()
            val result = loader.execute(req)
            if (result is SuccessResult) {
                val drawable = result.drawable
                if (drawable is BitmapDrawable) {
                    return@withContext drawable.bitmap
                }
            }
        } catch (_: Exception) {
            // Fallback to direct network download
        }

        // Direct HTTP connection fallback
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.connect()
            val input = connection.inputStream
            val bmp = BitmapFactory.decodeStream(input)
            input.close()
            return@withContext bmp
        } catch (_: Exception) {
            null
        }
    }

    suspend fun renderStoryBitmap(
        context: Context,
        video: YouTubeVideo,
        custom: StoryCustomization
    ): Bitmap = withContext(Dispatchers.Default) {
        // Standard Instagram Story 9:16 Resolution
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val template = custom.template

        // 1. Fetch real Video Thumbnail Bitmap early (needed for both background and card)
        val thumbBitmap = fetchThumbnailBitmap(context, video.thumbnailUrl)

        // 2. Draw Full Background (Blurred thumbnail or smooth vertical gradient)
        if (template.isBlurredThumbnailBg && thumbBitmap != null) {
            drawBlurredBackground(canvas, thumbBitmap, width, height)
        } else {
            val bgGradient = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                template.gradientIntColors,
                null,
                Shader.TileMode.CLAMP
            )
            val bgPaint = Paint().apply {
                isAntiAlias = true
                isDither = true
                shader = bgGradient
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        }

        // 3. Draw Centered Video Card
        val cardWidth = 940f
        val cardLeft = (width - cardWidth) / 2f
        val cardRight = cardLeft + cardWidth

        // 16:9 Thumbnail dimensions inside card
        val thumbPadding = 32f
        val thumbWidth = cardWidth - (thumbPadding * 2f)
        val thumbHeight = thumbWidth * (9f / 16f)

        val titleTextSize = 44f
        val metaTextSize = 32f
        val cardContentHeight = thumbHeight + thumbPadding + 40f + 120f + 50f // thumb + padding + text area
        val cardTop = (height - cardContentHeight) / 2f
        val cardBottom = cardTop + cardContentHeight
        val cardRect = RectF(cardLeft, cardTop, cardRight, cardBottom)

        // Draw Soft Drop Shadow for floating card
        val shadowPaint = Paint().apply {
            isAntiAlias = true
            color = AndroidColor.parseColor("#40000000")
            maskFilter = android.graphics.BlurMaskFilter(32f, android.graphics.BlurMaskFilter.Blur.NORMAL)
        }
        val shadowRect = RectF(cardLeft + 6f, cardTop + 16f, cardRight - 6f, cardBottom + 20f)
        canvas.drawRoundRect(shadowRect, 44f, 44f, shadowPaint)

        // Draw Card Background
        val cardBgPaint = Paint().apply {
            isAntiAlias = true
            color = template.cardBackgroundInt
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(cardRect, 44f, 44f, cardBgPaint)

        // 4. Draw Video Thumbnail inside Card (Rounded Corners) - Clean without red play button
        val thumbTop = cardTop + thumbPadding
        val thumbLeft = cardLeft + thumbPadding
        val thumbRight = thumbLeft + thumbWidth
        val thumbBottom = thumbTop + thumbHeight
        val thumbRect = RectF(thumbLeft, thumbTop, thumbRight, thumbBottom)

        if (thumbBitmap != null) {
            drawRoundedBitmap(canvas, thumbBitmap, thumbRect, 28f)
        } else {
            // Fallback dark placeholder with video color
            val fallbackPaint = Paint().apply {
                isAntiAlias = true
                color = AndroidColor.parseColor("#151515")
            }
            canvas.drawRoundRect(thumbRect, 28f, 28f, fallbackPaint)
        }

        // Draw Duration Badge in bottom-left of thumbnail if available (like YouTube)
        if (video.formattedDuration.isNotEmpty()) {
            val durPaint = Paint().apply {
                isAntiAlias = true
                textSize = 28f
                color = AndroidColor.WHITE
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val durText = video.formattedDuration
            val durTextWidth = durPaint.measureText(durText)
            val badgePaddingH = 14f
            val badgeHeight = 44f
            val durRect = RectF(
                thumbLeft + 16f,
                thumbBottom - badgeHeight - 16f,
                thumbLeft + 16f + durTextWidth + (badgePaddingH * 2f),
                thumbBottom - 16f
            )
            val durBgPaint = Paint().apply {
                isAntiAlias = true
                color = AndroidColor.parseColor("#D9000000")
            }
            canvas.drawRoundRect(durRect, 10f, 10f, durBgPaint)
            canvas.drawText(durText, durRect.left + badgePaddingH, durRect.bottom - 13f, durPaint)
        }

        // 5. Draw Video Title Below Thumbnail
        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = AndroidColor.WHITE
            textSize = titleTextSize
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val textLeft = thumbLeft + 12f
        val textMaxRight = thumbRight - 12f
        val maxTextWidth = textMaxRight - textLeft
        var textY = thumbBottom + 64f

        // Split video title into up to 2 lines
        val words = video.title.split(" ")
        var line1 = ""
        var line2 = ""

        for (word in words) {
            val testLine = if (line1.isEmpty()) word else "$line1 $word"
            if (titlePaint.measureText(testLine) <= maxTextWidth) {
                line1 = testLine
            } else {
                if (line2.isEmpty()) {
                    line2 = word
                } else {
                    val testLine2 = "$line2 $word"
                    if (titlePaint.measureText("$testLine2...") <= maxTextWidth) {
                        line2 = testLine2
                    } else {
                        line2 = "$line2..."
                        break
                    }
                }
            }
        }

        canvas.drawText(line1, textLeft, textY, titlePaint)
        if (line2.isNotEmpty()) {
            textY += 54f
            canvas.drawText(line2, textLeft, textY, titlePaint)
        }

        // 6. Draw Channel name, Views & Upload Date (channel name + views together separated by •)
        textY += 56f
        val metaPaint = Paint().apply {
            isAntiAlias = true
            color = AndroidColor.parseColor("#B3B0C2")
            textSize = metaTextSize
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val metaParts = mutableListOf<String>()
        if (video.channelTitle.isNotBlank()) metaParts.add(video.channelTitle)
        if (video.viewCount.isNotBlank()) metaParts.add(video.viewCount)

        if (metaParts.isNotEmpty()) {
            val metaString = metaParts.joinToString(" • ")
            val clippedMeta = if (metaPaint.measureText(metaString) > maxTextWidth) {
                val shortParts = mutableListOf<String>()
                if (video.channelTitle.isNotBlank()) shortParts.add(video.channelTitle)
                if (video.viewCount.isNotBlank()) shortParts.add(video.viewCount)
                val shortString = shortParts.joinToString(" • ")
                if (metaPaint.measureText(shortString) > maxTextWidth) {
                    "${video.channelTitle} • ${video.viewCount}".take(42) + "..."
                } else {
                    shortString
                }
            } else {
                metaString
            }
            canvas.drawText(clippedMeta, textLeft, textY, metaPaint)
        }

        bitmap
    }

    private fun drawRoundedBitmap(canvas: Canvas, source: Bitmap, destRect: RectF, cornerRadius: Float) {
        val output = Bitmap.createBitmap(
            destRect.width().toInt(),
            destRect.height().toInt(),
            Bitmap.Config.ARGB_8888
        )
        val outputCanvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, output.width, output.height)
        val rectF = RectF(rect)

        // Draw destination rounded rect
        outputCanvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)

        // Set transfer mode to SRC_IN to mask the bitmap into the rounded rect
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        // Scale and center crop the source bitmap into output
        val srcRect = calculateCenterCropRect(source.width, source.height, output.width, output.height)
        outputCanvas.drawBitmap(source, srcRect, rect, paint)

        // Draw final masked bitmap onto main canvas
        canvas.drawBitmap(output, destRect.left, destRect.top, null)
        output.recycle()
    }

    private fun calculateCenterCropRect(srcW: Int, srcH: Int, dstW: Int, dstH: Int): Rect {
        val srcAspect = srcW.toFloat() / srcH.toFloat()
        val dstAspect = dstW.toFloat() / dstH.toFloat()

        return if (srcAspect > dstAspect) {
            // Source is wider, crop sides
            val cropW = (srcH * dstAspect).toInt()
            val left = (srcW - cropW) / 2
            Rect(left, 0, left + cropW, srcH)
        } else {
            // Source is taller, crop top/bottom
            val cropH = (srcW / dstAspect).toInt()
            val top = (srcH - cropH) / 2
            Rect(0, top, srcW, top + cropH)
        }
    }

    private fun drawBlurredBackground(canvas: Canvas, source: Bitmap, width: Int, height: Int) {
        try {
            // Downsample aggressively (e.g. 18x32 or 27x48) for smooth box-blur / pixel diffusion
            val smallW = 36
            val smallH = 64
            val scaledDown = Bitmap.createScaledBitmap(source, smallW, smallH, true)
            val fastBlurred = fastBlur(scaledDown, 12)

            // Draw scaled back up to full 1080x1920 with bilinear interpolation (FILTER_BITMAP_FLAG)
            val bgPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
                isDither = true
            }
            val dstRect = Rect(0, 0, width, height)
            val srcCrop = calculateCenterCropRect(fastBlurred.width, fastBlurred.height, width, height)
            canvas.drawBitmap(fastBlurred, srcCrop, dstRect, bgPaint)

            // Draw dark vignette overlay for legibility
            val overlayPaint = Paint().apply {
                color = AndroidColor.parseColor("#99050508") // ~60% dark overlay
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

            if (fastBlurred != scaledDown) {
                scaledDown.recycle()
            }
            fastBlurred.recycle()
        } catch (_: Exception) {
            // Fallback gradient if blur fails
            val fallbackPaint = Paint().apply {
                color = AndroidColor.parseColor("#15131C")
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fallbackPaint)
        }
    }

    // StackBlur / BoxBlur implementation for Android Bitmaps without RenderScript dependencies
    private fun fastBlur(sentBitmap: Bitmap, radius: Int): Bitmap {
        val bitmap = sentBitmap.copy(sentBitmap.config ?: Bitmap.Config.ARGB_8888, true)
        if (radius < 1) return bitmap

        val w = bitmap.width
        val h = bitmap.height
        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1

        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(maxOf(w, h))

        var divsum = (div + 1) shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        for (idx in 0 until 256 * divsum) {
            dv[idx] = idx / divsum
        }

        yw = 0
        yi = 0

        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int

        for (curY in 0 until h) {
            rinsum = 0
            ginsum = 0
            binsum = 0
            routsum = 0
            goutsum = 0
            boutsum = 0
            rsum = 0
            gsum = 0
            bsum = 0
            for (curI in -radius..radius) {
                p = pix[yi + minOf(wm, maxOf(curI, 0))]
                sir = stack[curI + radius]
                sir[0] = (p and 0xff0000) shr 16
                sir[1] = (p and 0x00ff00) shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - kotlin.math.abs(curI)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (curI > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
            }
            stackpointer = radius

            for (curX in 0 until w) {
                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (curY == 0) {
                    vmin[curX] = minOf(curX + radius + 1, wm)
                }
                p = pix[yw + vmin[curX]]

                sir[0] = (p and 0xff0000) shr 16
                sir[1] = (p and 0x00ff00) shr 8
                sir[2] = p and 0x0000ff

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi++
            }
            yw += w
        }

        for (curX in 0 until w) {
            rinsum = 0
            ginsum = 0
            binsum = 0
            routsum = 0
            goutsum = 0
            boutsum = 0
            rsum = 0
            gsum = 0
            bsum = 0
            yp = -radius * w
            for (curI in -radius..radius) {
                yi = maxOf(0, yp) + curX
                sir = stack[curI + radius]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - kotlin.math.abs(curI)
                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs
                if (curI > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                if (curI < hm) {
                    yp += w
                }
            }
            yi = curX
            stackpointer = radius
            for (curY in 0 until h) {
                pix[yi] = (-0x1000000 and pix[yi]) or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (curX == 0) {
                    vmin[curY] = minOf(curY + r1, hm) * w
                }
                p = curX + vmin[curY]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi += w
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h)
        return bitmap
    }

    suspend fun saveStoryToGallery(
        context: Context,
        bitmap: Bitmap,
        format: ExportFormat = ExportFormat.PNG,
        title: String = "uStories_${System.currentTimeMillis()}"
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            val filename = "${title}.${format.extension}"
            val mimeType = format.mimeType
            val compressFormat = if (format == ExportFormat.PNG) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
            val quality = if (format == ExportFormat.PNG) 100 else 95

            var fos: OutputStream? = null
            var imageUri: Uri? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = android.content.ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/uStories")
                }
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri)
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/uStories"
                val file = File(imagesDir)
                if (!file.exists()) file.mkdirs()
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
                imageUri = Uri.fromFile(image)
            }

            fos?.use {
                bitmap.compress(compressFormat, quality, it)
            }
            imageUri
        } catch (_: Exception) {
            null
        }
    }

    suspend fun shareStoryToInstagramOrOthers(
        context: Context,
        bitmap: Bitmap,
        videoUrl: String,
        storyTitle: String,
        format: ExportFormat = ExportFormat.PNG
    ) = withContext(Dispatchers.IO) {
        try {
            val cachePath = File(context.cacheDir, "shared_stories")
            cachePath.mkdirs()
            val filename = "story_${System.currentTimeMillis()}.${format.extension}"
            val file = File(cachePath, filename)
            val stream = FileOutputStream(file)
            val compressFormat = if (format == ExportFormat.PNG) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
            bitmap.compress(compressFormat, 100, stream)
            stream.close()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val mime = format.mimeType

            val instagramIntent = Intent("com.instagram.share.ADD_TO_STORY").apply {
                setDataAndType(contentUri, mime)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                putExtra("interactive_asset_uri", contentUri)
                putExtra("top_background_color", "#000000")
                putExtra("bottom_background_color", "#000000")
                putExtra("content_url", videoUrl)
            }

            val packageManager = context.packageManager
            val canOpenInstagram = instagramIntent.resolveActivity(packageManager) != null

            if (canOpenInstagram) {
                withContext(Dispatchers.Main) {
                    context.startActivity(instagramIntent)
                }
            } else {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = mime
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_TEXT, "🎬 $storyTitle\n🔗 $videoUrl")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                withContext(Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(shareIntent, "Sdílet do Instagram Stories nebo aplikace"))
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Chyba při sdílení: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    suspend fun openSystemShareSheet(
        context: Context,
        bitmap: Bitmap,
        videoUrl: String,
        storyTitle: String,
        format: ExportFormat = ExportFormat.PNG
    ) = withContext(Dispatchers.IO) {
        try {
            val cachePath = File(context.cacheDir, "shared_stories")
            cachePath.mkdirs()
            val filename = "share_${System.currentTimeMillis()}.${format.extension}"
            val file = File(cachePath, filename)
            val stream = FileOutputStream(file)
            val compressFormat = if (format == ExportFormat.PNG) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
            bitmap.compress(compressFormat, 100, stream)
            stream.close()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val mime = format.mimeType
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mime
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, "🎬 $storyTitle\n🔗 $videoUrl")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            withContext(Dispatchers.Main) {
                val chooser = Intent.createChooser(shareIntent, "Sdílet 9:16 Story")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Chyba při sdílení: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
