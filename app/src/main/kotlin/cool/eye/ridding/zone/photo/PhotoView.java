package cool.eye.ridding.zone.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;
import cool.eye.ridding.R;
import cool.eye.ridding.zone.helper.ICallback;
import cool.eye.ridding.zone.helper.LocalStorage;

/**
 * @author ycb
 * @date 2015-1-26
 */
public class PhotoView extends LinearLayout implements OnClickListener {

    public static final int SURE = -1; // 确定拍照/选照片
    public static final int CANCEL = 0; // 取消

    private Context mContext;
    private IPhotoCallback mPhotoCallback;
    private ICallback mClickCallback;
    private boolean mIsCut = true; // 是否剪切
    private Uri mUri;
    private float mRatio = 1; // 剪切照片时x/y的比例
    private IUploadCallback mUploadCallback;

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

    public PhotoView setUploadCallback(IUploadCallback uploadCallback) {
        mUploadCallback = uploadCallback;
        return this;
    }

    public PhotoView isCut(final boolean cut) {
        this.mIsCut = cut;
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
        File file = new File(LocalStorage.composePhotoImageFile());
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
    }

    /**
     * 选择相册
     */
    public void selectAlbum() {
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
            Uri data = intent.getData();
            mUri = data == null ? mUri : data;
            tryUpload();
        }

        private void dealImage() {
            if (mIsCut) {
                adjustPhotoSize((Activity) mContext, mUri, mRatio);
            } else {
                tryUpload();
            }
        }
    };

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

    private void tryUpload() {
        if (mUploadCallback == null) {
            if (mPhotoCallback != null) {
                mPhotoCallback.onSelected(decodeUriAsBitmap(mUri));
            }
        } else {
            String path = getPathByUri(getContext(), mUri).replace("file://", "");
            final UploadProgressDialog dialog = new UploadProgressDialog(getContext());
            dialog.show();
            final BmobFile bmobFile = new BmobFile(new File(path));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        mUploadCallback.onSucceed(bmobFile.getFileUrl());
                    } else {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                    dialog.updateProgress(value);
                }
            });
        }
    }

    public static String getPathByUri(Context cxt, Uri uri) {
        String img_path = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        try {
            if (uri.toString().toLowerCase().endsWith(".jpg")
                    || uri.toString().toLowerCase().endsWith(".png")
                    || uri.toString().toLowerCase().endsWith(".jpeg")) {
                img_path = uri.toString() + "";
            } else {
                Cursor cursor = ((Activity) cxt).managedQuery(uri, proj, null, null, null);
                int actual_image_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                img_path = cursor.getString(actual_image_column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img_path;
    }

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
