package cool.eye.ridding.zone.helper

import com.facebook.drawee.view.SimpleDraweeView
import java.io.File


/**
 * Created by cool on 17-2-8.
 */
object ImageHelper {
    fun loadImage(draweeView: SimpleDraweeView, file: File) {
        draweeView.setImageURI("file://${file.path}")
//        val request = ImageRequestBuilder
//                .newBuilderWithSource(Uri.parse("file://${file.path}"))
//              //  .setResizeOptions(ResizeOptions(320, 320))
//                .build()
//        val controller = Fresco.newDraweeControllerBuilder().setOldController(draweeView.controller).setImageRequest(request)
//                .build()
//        draweeView.controller = controller
    }
}