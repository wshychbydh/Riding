package cool.eye.ridding.zone.photo;

import android.content.Intent;

/**
 * 选择照片
 *
 * @author ycb
 * @date 2015-1-26
 */
public interface ITakePhoto {

    /**
     * 拍照标识
     */
    int TAKE_PHOTO = 2001;
    int SELECT_ALBUM = 2002;
    int ADJUST_PHOTO = 2003;

    // 拍照
    void onTakePhoto(Intent intent, int resultCode);

    // 从相册选取
    void onSelectAlbum(Intent intent, int resultCode);

    // 调整照片
    void onPhotoAdjusted(Intent intent, int resultCode);
}