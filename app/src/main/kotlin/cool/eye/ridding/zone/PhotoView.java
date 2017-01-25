package cool.eye.ridding.zone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cool.eye.ridding.R;

/**
 * @author ycb
 * @date 2015-1-26
 */
public class PhotoView extends LinearLayout implements OnClickListener {

    public static final int SURE = -1; // 确定拍照/选照片
    public static final int CANCEL = 0; // 取消

    private Context mContext;
    private String mPhotoPath;
    private IPhotoCallback mPhotoCallback;
    private ICallback mClickCallback;
    private boolean mIsCut = true; // 是否剪切
    private String mDir;
    private Uri mUri;
    private float mRatio = 1; // 剪切照片时x/y的比例

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public PhotoView setRatio(float ratio) {
        this.mRatio = ratio;
        return this;
    }

    public PhotoView setPhotoCallback(IPhotoCallback photoCallback) {
        this.mPhotoCallback = photoCallback;
        return this;
    }

    public PhotoView setClickCallback(ICallback ICallback) {
        mClickCallback = ICallback;
        return this;
    }

    public PhotoView isCut(final boolean cut) {
        this.mIsCut = cut;
        return this;
    }

    public PhotoView setPhotoDir(final String dir) {
        this.mDir = dir;
        return this;
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.add_photo, null);
        Button takePhoto = (Button) view.findViewById(R.id.take_photo);
        Button selectAlbum = (Button) view.findViewById(R.id.select_album);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        takePhoto.setOnClickListener(this);
        selectAlbum.setOnClickListener(this);
        cancel.setOnClickListener(this);
        this.addView(view, new LayoutParams(-1, -2));
        mDir = LocalStorage.composeImageDir();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.take_photo) {
            takePhoto();
        } else if (v.getId() == R.id.select_album) {
            selectAlbum();
        } else if (v.getId() == R.id.cancel) {
        }
        if (mClickCallback != null) {
            mClickCallback.callback();
        }
    }

    /**
     * 拍照
     */
    public void takePhoto() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File folder = new File(mDir);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(folder.getAbsolutePath()).append(File.separator)
                    .append(System.currentTimeMillis()).append(LocalStorage.IMAGE_SUFF);
            mPhotoPath = sb.toString();
            File file = new File(mPhotoPath);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mUri = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
            ((Activity) mContext).startActivityForResult(intent, ITakePhoto.TAKE_PHOTO);
        } else {
            Toast.makeText(mContext, "没有扩展卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 选择相册
     */
    public void selectAlbum() {
        mPhotoPath = null;
        // Intent intent = new Intent();
        // intent.setType("image/*");
        // intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) mContext).startActivityForResult(intent, ITakePhoto.SELECT_ALBUM);
    }

    /**
     * 拍照后回调接口，在OnActivityResult里面调用，必须手动调用
     */
    public ITakePhoto photoInterface = new ITakePhoto() {

        @Override
        public void onTakePhoto(Intent intent, int resultCode) {
            if (resultCode == SURE) {
                dealImage();
            }
        }

        @Override
        public void onSelectAlbum(Intent intent, int resultCode) {
            if (intent != null) {
                mUri = intent.getData();
                if (mUri != null) {
                    dealImage();
                }
            }
        }

        @Override
        public void onPhotoAdjusted(Intent intent, int resultCode) {
//            if (intent != null) {
//                if (mPhotoCallback != null) {
//                    Bitmap bmp = (Bitmap) intent.getExtras().get("data");
//                    mPhotoCallback.onSelected(bmp);
//                }
//            }
            if (mPhotoCallback != null) {
                Uri data = intent.getData();
                mPhotoCallback.onSelected(decodeUriAsBitmap(data == null ? mUri : data));
            }
        }

        private Bitmap decodeUriAsBitmap(Uri uri) {
            if (uri != null) {
                try {
                    return BitmapFactory.decodeStream(mContext.getContentResolver()
                            .openInputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private void dealImage() {
            if (mIsCut) {
                adjustPhotoSize((Activity) mContext, mUri, mRatio);
            } else {
                if (mPhotoCallback != null) {
                    mPhotoCallback.onSelected(decodeUriAsBitmap(mUri));
                }
            }
        }
    };

//	private void saveAndUpload(final InputStream inputStream) {
//		if (inputStream != null) {
//			File folder = new File(mDir);
//			if (!folder.exists()) {
//				folder.mkdirs();
//			}
//
//			try {
//				StringBuilder sb = new StringBuilder();
//				sb.append(System.currentTimeMillis()).append(LocalStorage.IMAGE_SUFF);
//				File file = new File(folder, sb.toString());
//				file.createNewFile();
//				if (mPhotoPath == null || mPhotoPath.isEmpty()) {
//					boolean result = ImageUtil.saveImage(inputStream, file);
//					if (result) {
//						mPhotoPath = file.getAbsolutePath();
//					}
//				} else {
//					if (mIsCut) {
//						boolean result = ImageUtil.saveImage(inputStream, file);
//						if (result) {
//							new File(mPhotoPath).delete();
//							mPhotoPath = file.getAbsolutePath();
//						}
//					}
//				}
//			} catch (Exception e) {
//				Util.showToast(mContext, "保存图片出错！");
//				e.printStackTrace();
//			}
//		}
//	}

//    private void renameFile(final String path, final String resId) {
//        File file = new File(path);
//        StringBuilder sb = new StringBuilder();
//        sb.append(file.getParent()).append(File.separator).append(resId)
//                .append(LocalStorage.IMAGE_SUFF);
//        file.renameTo(new File(sb.toString()));
//    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    private void adjustPhotoSize(final Activity activity, final Uri uri, float ratio) {
        if (activity == null || uri == null) {
            return;
        }
        // DisplayMetrics dm = Util.getScreenMetrics(context);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", ratio);
        intent.putExtra("aspectY", 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // intent.putExtra("outputX", dm.widthPixels); // 宽，必须设置，否则选择大图片时可能会卡机
        // intent.putExtra("outputY", dm.heightPixels); // 高，必须设置，否则选择大图片时可能会卡机
        intent.putExtra("outputX", 480); // 宽，必须设置，否则选择大图片时可能会卡机
        intent.putExtra("outputY", ratio > 1 ? 600 : 480); // 高，必须设置，否则选择大图片时可能会卡机

        // 加上下面的这两句之后，系统就会把图片给拉伸?
        intent.putExtra("scale", false);
        intent.putExtra("scaleUpIfNeeded", false);

        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", false);
        activity.startActivityForResult(intent, ITakePhoto.ADJUST_PHOTO);
    }
}
