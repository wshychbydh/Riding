package cool.eye.ridding.zone.card.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import android.widget.CompoundButton.OnCheckedChangeListener
import com.eye.cool.permission.Permission
import com.eye.cool.permission.PermissionHelper
import com.google.zxing.Result
import com.google.zxing.ResultPoint
import cool.eye.ridding.R
import cool.eye.ridding.util.ToastHelper
import cooleye.scan.camera.CameraManager
import cooleye.scan.camera.PreviewFrameShotListener
import cooleye.scan.camera.Size
import cooleye.scan.decode.*
import cooleye.scan.util.BeepManager
import cooleye.scan.util.DocumentUtil
import cooleye.scan.view.CaptureView

class CaptureActivity : Activity(), SurfaceHolder.Callback, PreviewFrameShotListener, DecodeListener, OnCheckedChangeListener, OnClickListener {
  private var previewSv: SurfaceView? = null
  private var captureView: CaptureView? = null
  private var flashCb: CheckBox? = null
  private var albumBtn: Button? = null
  private var mBeepManager: BeepManager? = null

  private var mCameraManager: CameraManager? = null
  private var mDecodeThread: DecodeThread? = null
  private var previewFrameRect: Rect? = null
  private var isDecoding = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_capture)
    val titleTv = findViewById<TextView>(R.id.tv_title)
    titleTv.text = "扫描二维码"
    previewSv = findViewById(R.id.sv_preview)
    captureView = findViewById(R.id.cv_capture)
    flashCb = findViewById(R.id.cb_capture_flash)
    flashCb!!.setOnCheckedChangeListener(this)
    flashCb!!.isEnabled = false
    findViewById<View>(R.id.iv_back).setOnClickListener(this)
    albumBtn = findViewById(R.id.btn_album)
    albumBtn!!.setOnClickListener(this)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      albumBtn!!.visibility = View.GONE
    }
    PermissionHelper.Builder(this)
        .showRationaleWhenRequest(false)
        .permissions(Permission.CAMERA)
        .permissionCallback {
          if (it) {
            previewSv!!.holder.addCallback(this@CaptureActivity)
            mCameraManager = CameraManager(this@CaptureActivity)
            mCameraManager!!.setPreviewFrameShotListener(this@CaptureActivity)
          } else {
            finish()
          }
          null
        }
        .build()
        .request()
  }

  override fun onResume() {
    super.onResume()
    if (mBeepManager == null) {
      mBeepManager = BeepManager(this)
    } else {
      mBeepManager!!.updatePrefs()
    }
  }

  override fun onPause() {
    super.onPause()
    mBeepManager!!.close()
  }

  override fun surfaceCreated(holder: SurfaceHolder) {
    mCameraManager!!.initCamera(holder)
    if (!mCameraManager!!.isCameraAvailable) {
      Toast.makeText(this@CaptureActivity, R.string.capture_camera_failed, Toast
          .LENGTH_SHORT).show()
      finish()
      return
    }
    if (mCameraManager!!.isFlashlightAvailable) {
      flashCb!!.isEnabled = true
    }
    mCameraManager!!.startPreview()
    if (!isDecoding) {
      mCameraManager!!.requestPreviewFrameShot()
    }
  }

  override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

  }

  override fun surfaceDestroyed(holder: SurfaceHolder) {
    mCameraManager!!.stopPreview()
    if (mDecodeThread != null) {
      mDecodeThread!!.cancel()
    }
    mCameraManager!!.release()
  }

  override fun onDestroy() {
    super.onDestroy()
  }

  override fun onPreviewFrame(data: ByteArray, dataSize: Size) {
    if (mDecodeThread != null) {
      mDecodeThread!!.cancel()
    }
    if (previewFrameRect == null) {
      previewFrameRect = mCameraManager!!.getPreviewFrameRect(captureView!!.frameRect)
    }
    val luminanceSource = PlanarYUVLuminanceSource(data, dataSize,
        previewFrameRect!!)
    mDecodeThread = DecodeThread(luminanceSource, this@CaptureActivity)
    isDecoding = true
    mDecodeThread!!.execute()
  }

  override fun onDecodeSuccess(result: Result, source: LuminanceSource, bitmap: Bitmap) {
    var bitmap = bitmap
    mBeepManager!!.playBeepSoundAndVibrate()
    isDecoding = false
    if (bitmap.width > 100 || bitmap.height > 100) {
      val matrix = Matrix()
      matrix.postScale(100f / bitmap.width, 100f / bitmap.height)
      val resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap
          .height, matrix, true)
      bitmap.recycle()
      bitmap = resizeBmp
    }
    ScanResultActivity.launch(this, bitmap, result.text)
    //        Intent resultData = new Intent();
    //        resultData.putExtra(EXTRA_RESULT, result.getText());
    //        resultData.putExtra(EXTRA_BITMAP, bitmap);
    //        setResult(RESULT_OK, resultData);
    //        finish();
  }

  override fun onDecodeFailed(source: LuminanceSource) {
    if (source is RGBLuminanceSourcePixels) {
      Toast.makeText(this@CaptureActivity, R.string.capture_decode_failed, Toast
          .LENGTH_SHORT).show()
    }
    isDecoding = false
    mCameraManager!!.requestPreviewFrameShot()
  }

  override fun foundPossibleResultPoint(point: ResultPoint) {
    captureView!!.addPossibleResultPoint(point)
  }

  override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
    if (isChecked) {
      mCameraManager!!.enableFlashlight()
    } else {
      mCameraManager!!.disableFlashlight()
    }
  }


  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      finish()
      return true
    }
    return super.onKeyDown(keyCode, event)
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.iv_back -> finish()
      R.id.btn_album -> {
        val intent: Intent
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
          intent = Intent(Intent.ACTION_GET_CONTENT)
        } else {
          intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent.putExtra("return-data", true)
        startActivityForResult(intent, REQUEST_CODE_ALBUM)
      }
      else -> {
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE_ALBUM && resultCode == RESULT_OK && data != null) {
      val cameraBitmap: Bitmap?
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        val path = DocumentUtil.getPath(this@CaptureActivity, data.data)
        cameraBitmap = DocumentUtil.getBitmap(path)
      } else {
        // Not supported in SDK lower that KitKat
        return
      }
      if (cameraBitmap != null) {
        if (mDecodeThread != null) {
          mDecodeThread!!.cancel()
        }
        val width = cameraBitmap.width
        val height = cameraBitmap.height
        val pixels = IntArray(width * height)
        cameraBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val luminanceSource = RGBLuminanceSourcePixels(pixels, Size(width, height))
        mDecodeThread = DecodeThread(luminanceSource, this@CaptureActivity)
        isDecoding = true
        mDecodeThread!!.execute()
      }
    }
  }

  companion object {

    private val VIBRATE_DURATION = 200L
    private val REQUEST_CODE_ALBUM = 0
  }
}
