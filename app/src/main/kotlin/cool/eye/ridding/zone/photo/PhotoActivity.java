package cool.eye.ridding.zone.photo;

import android.content.Intent;

import cool.eye.ridding.ui.BaseActivity;

/**
 * @author cooleye
 * @cagetory PhotoActivity
 * @2015-11-12
 * @description TODO
 */
public abstract class PhotoActivity extends BaseActivity {

    private ITakePhoto mTakePhoto;

    public void setITakePhoto(ITakePhoto takePhoto) {
        this.mTakePhoto = takePhoto;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (mTakePhoto != null) {
            if (requestCode == ITakePhoto.TAKE_PHOTO) {
                mTakePhoto.onTakePhoto(intent, resultCode);
            } else if (requestCode == ITakePhoto.SELECT_ALBUM) {
                mTakePhoto.onSelectAlbum(intent, resultCode);
            } else if (requestCode == ITakePhoto.ADJUST_PHOTO) {
                mTakePhoto.onPhotoAdjusted(intent, resultCode);
            }
        }
    }
}
