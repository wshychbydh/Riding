package cool.eye.ridding.zone.card.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import cool.eye.ridding.R;
import cooleye.scan.camera.CameraManager;
import cooleye.scan.camera.PreviewFrameShotListener;
import cooleye.scan.camera.Size;
import cooleye.scan.decode.DecodeListener;
import cooleye.scan.decode.DecodeThread;
import cooleye.scan.decode.LuminanceSource;
import cooleye.scan.decode.PlanarYUVLuminanceSource;
import cooleye.scan.decode.RGBLuminanceSourcePixels;
import cooleye.scan.util.BeepManager;
import cooleye.scan.util.DocumentUtil;
import cooleye.scan.view.CaptureView;

public class CaptureActivity extends Activity implements SurfaceHolder.Callback,
        PreviewFrameShotListener, DecodeListener,
        OnCheckedChangeListener, OnClickListener {

    private static final long VIBRATE_DURATION = 200L;
    private static final int REQUEST_CODE_ALBUM = 0;
    private SurfaceView previewSv;
    private CaptureView captureView;
    private CheckBox flashCb;
    private Button albumBtn;
    private BeepManager mBeepManager;

    private CameraManager mCameraManager;
    private DecodeThread mDecodeThread;
    private Rect previewFrameRect = null;
    private boolean isDecoding = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        titleTv.setText("扫描二维码");
        previewSv = (SurfaceView) findViewById(R.id.sv_preview);
        captureView = (CaptureView) findViewById(R.id.cv_capture);
        flashCb = (CheckBox) findViewById(R.id.cb_capture_flash);
        flashCb.setOnCheckedChangeListener(this);
        flashCb.setEnabled(false);
        findViewById(R.id.iv_back).setOnClickListener(this);
        albumBtn = (Button) findViewById(R.id.btn_album);
        albumBtn.setOnClickListener(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            albumBtn.setVisibility(View.GONE);
        }
        previewSv.getHolder().addCallback(this);
        mCameraManager = new CameraManager(this);
        mCameraManager.setPreviewFrameShotListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBeepManager == null) {
            mBeepManager = new BeepManager(this);
        } else {
            mBeepManager.updatePrefs();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBeepManager.close();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCameraManager.initCamera(holder);
        if (!mCameraManager.isCameraAvailable()) {
            Toast.makeText(CaptureActivity.this, R.string.capture_camera_failed, Toast
                    .LENGTH_SHORT).show();
            finish();
            return;
        }
        if (mCameraManager.isFlashlightAvailable()) {
            flashCb.setEnabled(true);
        }
        mCameraManager.startPreview();
        if (!isDecoding) {
            mCameraManager.requestPreviewFrameShot();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCameraManager.stopPreview();
        if (mDecodeThread != null) {
            mDecodeThread.cancel();
        }
        mCameraManager.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPreviewFrame(byte[] data, Size dataSize) {
        if (mDecodeThread != null) {
            mDecodeThread.cancel();
        }
        if (previewFrameRect == null) {
            previewFrameRect = mCameraManager.getPreviewFrameRect(captureView.getFrameRect());
        }
        PlanarYUVLuminanceSource luminanceSource = new PlanarYUVLuminanceSource(data, dataSize,
                previewFrameRect);
        mDecodeThread = new DecodeThread(luminanceSource, CaptureActivity.this);
        isDecoding = true;
        mDecodeThread.execute();
    }

    @Override
    public void onDecodeSuccess(Result result, LuminanceSource source, Bitmap bitmap) {
        mBeepManager.playBeepSoundAndVibrate();
        isDecoding = false;
        if (bitmap.getWidth() > 100 || bitmap.getHeight() > 100) {
            Matrix matrix = new Matrix();
            matrix.postScale(100f / bitmap.getWidth(), 100f / bitmap.getHeight());
            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap
                    .getHeight(), matrix, true);
            bitmap.recycle();
            bitmap = resizeBmp;
        }
        ScanResultActivity.launch(this, bitmap, result.getText());
//        Intent resultData = new Intent();
//        resultData.putExtra(EXTRA_RESULT, result.getText());
//        resultData.putExtra(EXTRA_BITMAP, bitmap);
//        setResult(RESULT_OK, resultData);
//        finish();
    }

    @Override
    public void onDecodeFailed(LuminanceSource source) {
        if (source instanceof RGBLuminanceSourcePixels) {
            Toast.makeText(CaptureActivity.this, R.string.capture_decode_failed, Toast
                    .LENGTH_SHORT).show();
        }
        isDecoding = false;
        mCameraManager.requestPreviewFrameShot();
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint point) {
        captureView.addPossibleResultPoint(point);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mCameraManager.enableFlashlight();
        } else {
            mCameraManager.disableFlashlight();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_album:
                Intent intent;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                }
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra("return-data", true);
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ALBUM && resultCode == RESULT_OK && data != null) {
            Bitmap cameraBitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String path = DocumentUtil.getPath(CaptureActivity.this, data.getData());
                cameraBitmap = DocumentUtil.getBitmap(path);
            } else {
                // Not supported in SDK lower that KitKat
                return;
            }
            if (cameraBitmap != null) {
                if (mDecodeThread != null) {
                    mDecodeThread.cancel();
                }
                int width = cameraBitmap.getWidth();
                int height = cameraBitmap.getHeight();
                int[] pixels = new int[width * height];
                cameraBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                RGBLuminanceSourcePixels luminanceSource = new RGBLuminanceSourcePixels(pixels, new Size
                        (width, height));
                mDecodeThread = new DecodeThread(luminanceSource, CaptureActivity.this);
                isDecoding = true;
                mDecodeThread.execute();
            }
        }
    }
}
