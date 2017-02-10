package cool.eye.ridding.zone.card.helper

import android.graphics.BitmapFactory
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import java.io.File


/**
 * Created by cool on 17-2-8.
 */
object DecodeHelper {
    fun decode(file: File): String {
        val hints = mutableMapOf<DecodeHintType, String>()
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8")
        val bitmap = BitmapFactory.decodeFile(file.path)
        val source = RGBLuminanceSource(bitmap)
        val bitmap1 = BinaryBitmap(HybridBinarizer(source))
        val reader = QRCodeReader()
        try {
            return reader.decode(bitmap1, hints).text
        } catch (e: NotFoundException) {
            e.printStackTrace()
        } catch (e: ChecksumException) {
            e.printStackTrace()
        } catch (e: FormatException) {
            e.printStackTrace()
        }
        return ""
    }
}