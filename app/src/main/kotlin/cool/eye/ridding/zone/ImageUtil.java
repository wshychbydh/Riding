//package cool.eye.ridding.zone;
//
//import android.annotation.SuppressLint;
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.Config;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.PorterDuff.Mode;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.drawable.Drawable;
//import android.media.ExifInterface;
//import android.media.ThumbnailUtils;
//import android.net.Uri;
//import android.os.Build;
//import android.provider.MediaStore;
//import android.provider.MediaStore.Images;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.lykj.ycb.config.BaseConstant;
//import com.lykj.ycb.config.ICallback;
//import com.lykj.ycb.downloader.ImageDowanloadUtil;
//
//import java.io.BufferedOutputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import okhttp3.internal.Util;
//
//import static cool.eye.ridding.zone.LocalStorage.IMAGE_SUFF;
//
//@TargetApi(Build.VERSION_CODES.GINGERBREAD)
//@SuppressLint("DefaultLocale")
//public class ImageUtil {
//
//	public final static int IMAGE_MAX_WIDTH = 1080;
//	public final static int IMAGE_MAX_HEIGHT = 1920;
//	public final static int IMAGE_GRID_WIDTH = 480;
//	public final static int IMAGE_GRID_HEIGHT = 480;
//	public static final int IMAGE_ICON_WIDTH = 320;
//	public static final int IMAGE_ICON_HEIGHT = 320;
//
//	public final static int IMAGE_DEFAULT = -1; // 默认为大图?
//	public final static int IMAGE_GRID = 0; // gridview，listview等显示时的缩略图
//	public final static int IMAGE_ICON = 1; // 拍照时生成的缩略图?
//	public final static int IMAGE_VIDEO = 2; // 视频的缩略图
//
//	/**
//	 * 保存图片至指定目?
//	 *
//	 * @param inputStream
//	 * @param imgFile
//	 *            (the imgage file to be saved)
//	 * @return
//	 * @throws Exception
//	 */
//	public static boolean saveImage(InputStream inputStream, File imgFile) throws Exception {
//		if (imgFile == null || !imgFile.exists()) {
//			return false;
//		}
//		try {
//			if (!canSave(inputStream)) {
//				throw new Exception("内存溢出");
//			}
//			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgFile));
//			byte[] content = new byte[1024];
//			int length = 0;
//			while ((length = inputStream.read(content)) != -1) {
//				bos.write(content, 0, length);
//			}
//			bos.flush();
//			bos.close();
//			inputStream.close();
//			return true;
//		} finally {
//			System.gc();
//		}
//	}
//
//	/**
//	 * 保存图片至指定目�?
//	 *
//	 * @param bmp
//	 * @param imgFile
//	 *            (the imgage file to be saved)
//	 * @return
//	 */
//	public static boolean saveImage(Bitmap bmp, File imgFile) {
//		if (bmp == null || imgFile == null || !imgFile.exists()) {
//			return false;
//		}
//
//		try {
//			if (!canSave(bmp)) {
//				throw new Exception("内存溢出");
//			}
//			FileOutputStream fos = new FileOutputStream(imgFile);
//			BufferedOutputStream bos = new BufferedOutputStream(fos);
//			bmp.compress(Bitmap.CompressFormat.PNG, 85, bos);
//			bos.flush();
//			bos.close();
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			bmp.recycle();
//			bmp = null;
//			System.gc();
//		}
//		return false;
//	}
//
//	/**
//	 * @param The
//	 *            resource id of the image data
//	 *
//	 * @return
//	 */
//	public static String getCarmeraPath(Context context, int id) {
//		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);
//		String imgPath = saveBitmapToCarmera(context, bmp);
//		if (imgPath != null && !imgPath.isEmpty()) {
//			File file = new File(imgPath);
//			if (file != null) {
//				file.delete();
//			}
//		}
//		String carmeraPath = imgPath.substring(0, imgPath.lastIndexOf("/") + 1);
//		return carmeraPath;
//	}
//
//	public static String saveBitmapToCarmera(Context cxt, Bitmap bmp) {
//		if (!canSave(bmp)) {
//			return "内存溢出";
//		}
//		String title = String.valueOf(System.currentTimeMillis()) + LocalStorage.IMAGE_SUFF;
//		ContentResolver cr = cxt.getContentResolver();
//		String str = Images.Media.insertImage(cr, bmp, title, "");
//		Uri uri = Uri.parse(str);
//		bmp.recycle();
//		bmp = null;
//		System.gc();
//		return uriToStr(cxt, uri);
//	}
//
//	public static String uriToStr(Context cxt, Uri uri) {
//		String img_path = null;
//		String[] proj = {Images.Media.DATA};
//		try {
//			if (uri.toString().toLowerCase().endsWith(".jpg")
//					|| uri.toString().toLowerCase().endsWith(".png")
//					|| uri.toString().toLowerCase().endsWith(".jpeg")) {
//				img_path = uri.toString() + "";
//			} else {
//				Cursor actualimagecursor = ((Activity) cxt).managedQuery(uri, proj, null, null,
//						null);
//
//				int actual_image_column_index = actualimagecursor
//						.getColumnIndexOrThrow(Images.Media.DATA);
//
//				actualimagecursor.moveToFirst();
//
//				img_path = actualimagecursor.getString(actual_image_column_index);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return img_path;
//	}
//
//	public static Drawable getImageFromUrl(String imageUrl) {
//		URL m;
//		InputStream i = null;
//		try {
//			m = new URL(imageUrl);
//			i = (InputStream) m.getContent();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Drawable d = Drawable.createFromStream(i, "resource");
//		return d;
//	}
//
//	/**
//	 * 判断sd卡的存储空间是否能保存该�?
//	 *
//	 * @param bitmap
//	 * @return
//	 */
//	private static boolean canSave(Bitmap bitmap) {
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
//		return StorageUtil.getAvailableExternalMemorySize() > bos.size();
//	}
//
//	/**
//	 * 判断sd卡的存储空间是否能保存该�?
//	 *
//	 * @param bitmap
//	 * @return
//	 * @throws IOException
//	 */
//	private static boolean canSave(InputStream inputStream) throws IOException {
//		return StorageUtil.getAvailableExternalMemorySize() > inputStream.available();
//	}
//
//	/**
//	 * 通过文件路径获取bitmap对象
//	 *
//	 * @param path
//	 * @param tag
//	 *            {0,1,2分别代表：默认缩略图，拍照缩略图，视频缩略图}
//	 * @return
//	 * @see 类属性IMAGE_TAG
//	 */
//
//	public static Bitmap getThumbnail(final String path, int tag) {
//		try {
//			if (!new File(path).exists()) {
//				return null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		if (tag == IMAGE_VIDEO) {
//			return ThumbnailUtils.createVideoThumbnail(path, Images.Thumbnails.MINI_KIND);
//		} else {
//			return getBitmapFromFile(path, tag);
//		}
//		// Bitmap bitmap = BitmapFactory.decodeFile(path);
//		// if (tag == IMAGE_ICON) {
//		// return ThumbnailUtils.extractThumbnail(bitmap, IMAGE_SHOT_WIDTH,
//		// IMAGE_SHOT_HEIGHT);
//		// } else {
//		// return ThumbnailUtils.extractThumbnail(bitmap, IMAGE_GRID_WIDTH,
//		// IMAGE_GRID_HEIGHT);
//		// }
//	}
//
//	/**
//	 * 通过图片地址获取bitmap对象
//	 *
//	 * @param path
//	 * @return
//	 */
//	private static Bitmap getBitmapFromFile(final String path, int tag) {
//		try {
//			int width;
//			int height;
//			if (tag == IMAGE_ICON) {
//				width = IMAGE_ICON_WIDTH;
//				height = IMAGE_ICON_HEIGHT;
//			} else {
//				width = IMAGE_GRID_WIDTH;
//				height = IMAGE_GRID_HEIGHT;
//			}
//
//			BitmapFactory.Options opts = null;
//			opts = new BitmapFactory.Options();
//			opts.inJustDecodeBounds = true;
//			BitmapFactory.decodeFile(path, opts);
//			if (opts.outWidth > width && opts.outHeight > height) {
//				// 计算图片缩放比例
//				final int minSideLength = Math.min(width, height);
//				opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
//			} else {
//				opts.inSampleSize = 1;
//			}
//			// final int minSideLength = Math.min(width, height);
//			// opts.inSampleSize = computeSampleSize(opts, minSideLength, width
//			// * height);
//			opts.inJustDecodeBounds = false;
//			opts.inInputShareable = true;
//			opts.inPurgeable = true;
//
//			return BitmapFactory.decodeFile(path, opts);
//		} catch (OutOfMemoryError e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	/**
//	 * 通过图片地址获取bitmap对象
//	 *
//	 * @param path
//	 * @return
//	 */
//	public static Bitmap getOrgBitmapFromFile(final String path, final int width, final int height) {
//		try {
//
//			BitmapFactory.Options opts = null;
//			opts = new BitmapFactory.Options();
//			opts.inJustDecodeBounds = true;
//			BitmapFactory.decodeFile(path, opts);
//			if (opts.outWidth > width && opts.outHeight > height) {
//				// 计算图片缩放比例
//				final int minSideLength = Math.min(width, height);
//				final int minBitmap = Math.min(opts.outWidth, opts.outHeight);
//				opts.inSampleSize = (int) ((float) minBitmap / (float) minSideLength);
//			} else {
//				opts.inSampleSize = 1;
//			}
//			// final int minSideLength = Math.min(width, height);
//			// opts.inSampleSize = computeSampleSize(opts, minSideLength, width
//			// * height);
//			opts.inJustDecodeBounds = false;
//			opts.inInputShareable = true;
//			opts.inPurgeable = true;
//
//			return BitmapFactory.decodeFile(path, opts);
//		} catch (OutOfMemoryError e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	/**
//	 * 通过图片地址获取bitmap对象
//	 *
//	 * @param path
//	 * @param width
//	 *            (if width <100 or width > 1080 ,it will be set to 1080)
//	 * @param height
//	 *            (if height <200 or heith > 1920 ,it will be set to 1920)
//	 * @return
//	 */
//	public static Bitmap getBitmapFromFile(final String path, int width, int height) {
//		try {
//			if (!Util.isEmpty(path) && new File(path).exists()) {
//				width = width < 200 ? IMAGE_GRID_WIDTH : (width > IMAGE_MAX_WIDTH
//						? IMAGE_MAX_WIDTH
//						: width);
//				height = height < 200 ? IMAGE_GRID_HEIGHT : (height > IMAGE_MAX_HEIGHT
//						? IMAGE_MAX_HEIGHT
//						: height);
//				BitmapFactory.Options opts = new BitmapFactory.Options();
//				opts.inJustDecodeBounds = true;
//				BitmapFactory.decodeFile(path, opts);
//				if (opts.outWidth > width && opts.outHeight > height) {
//					// 计算图片缩放比例
//					final int minSideLength = Math.min(width, height);
//					opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
//				} else {
//					opts.inSampleSize = 1;
//				}
//
//				// BitmapFactory.Options opts = new BitmapFactory.Options();
//				// opts.inJustDecodeBounds = true;
//				// BitmapFactory.decodeFile(path, opts);
//				// // 计算图片缩放比例
//				// final int minSideLength = Math.min(width, height);
//				// opts.inSampleSize = computeSampleSize(opts, minSideLength,
//				// width * height);
//				opts.inJustDecodeBounds = false;
//				opts.inInputShareable = true;
//				opts.inPurgeable = true;
//				Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
//
//				if (bitmap != null) {
//					int orientation = 1;
//					try {
//						ExifInterface exif = new ExifInterface(path);
//						orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					Matrix matrix = new Matrix();
//					switch (orientation) {
//						case 1 :
//							break;
//						case 2 :
//							matrix.invert(matrix);
//							break;
//						case 3 :
//							matrix.setRotate(180);
//							break;
//						case 4 :
//							matrix.invert(matrix);
//							matrix.setRotate(180);
//							break;
//						case 5 :
//							matrix.setRotate(90);
//							matrix.invert(matrix);
//							break;
//						case 6 :
//							matrix.setRotate(90);
//							break;
//						case 7 :
//							matrix.invert(matrix);
//							matrix.setRotate(90);
//							break;
//						case 8 :
//							matrix.setRotate(270);
//							break;
//						default :
//							break;
//					}
//
//					Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//							bitmap.getHeight(), matrix, true);
//					// int imageWidth = bmp.getWidth();
//					// if (imageWidth < IMAGE_DEFAULT_WIDTH) {
//					// float sclae = ((float) IMAGE_DEFAULT_WIDTH)
//					// / ((float) imageWidth);
//					// matrix = new Matrix();
//					// matrix.setScale(sclae, sclae);
//					// bmp = Bitmap.createBitmap(bmp, 0, 0,
//					// bmp.getWidth(), bmp.getHeight(), matrix,
//					// true);
//					// }
//					if (bmp != bitmap) {
//						bitmap.recycle();
//						bitmap = null;
//						System.gc();
//					}
//					return bmp;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public static Bitmap getBitmapFromStream(final InputStream is, int width, int height) {
//		try {
//			if (is != null) {
//				width = width < 200 ? IMAGE_GRID_WIDTH : (width > IMAGE_MAX_WIDTH
//						? IMAGE_MAX_WIDTH
//						: width);
//				height = height < 200 ? IMAGE_GRID_HEIGHT : (height > IMAGE_MAX_HEIGHT
//						? IMAGE_MAX_HEIGHT
//						: height);
//				// 因为流不能重复利用，�?��转换成字节数�?
//				byte[] data = Util.readStream(is);
//				System.out.println(" data.length--->" + data.length);
//				BitmapFactory.Options opts = new BitmapFactory.Options();
//				opts.inJustDecodeBounds = true;
//				BitmapFactory.decodeByteArray(data, 0, data.length, opts);
//				if (opts.outWidth > width && opts.outHeight > height) {
//					// 计算图片缩放比例
//					final int minSideLength = Math.min(width, height);
//					opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
//				} else {
//					opts.inSampleSize = 1;
//				}
//				opts.inJustDecodeBounds = false;
//				opts.inInputShareable = true;
//				opts.inPurgeable = true;
//				return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
//			}
//		} catch (Exception e) {
//			System.out.println("************" + e.getMessage());
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public static Bitmap getBitmapFromStream(final InputStream is, int tag) {
//		try {
//			if (is != null) {
//				int width;
//				int height;
//				if (tag == IMAGE_ICON) {
//					width = IMAGE_ICON_WIDTH;
//					height = IMAGE_ICON_HEIGHT;
//				} else {
//					width = IMAGE_GRID_WIDTH;
//					height = IMAGE_GRID_HEIGHT;
//				}
//				// 因为流不能重复利用，�?��转换成字节数�?
//				byte[] data = Util.readStream(is);
//				BitmapFactory.Options opts = new BitmapFactory.Options();
//				opts.inJustDecodeBounds = true;
//				BitmapFactory.decodeByteArray(data, 0, data.length, opts);
//				if (opts.outWidth > width && opts.outHeight > height) {
//					// 计算图片缩放比例
//					final int minSideLength = Math.min(width, height);
//					opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
//				} else {
//					opts.inSampleSize = 1;
//				}
//				opts.inJustDecodeBounds = false;
//				opts.inInputShareable = true;
//				opts.inPurgeable = true;
//				return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public static Bitmap scaleBitmap(Bitmap bmp, float minSize) {
//		if (bmp != null) {
//			int width = bmp.getWidth();
//			if (width < minSize) {
//				float sclae = minSize / (float) width;
//				Matrix matrix = new Matrix();
//				matrix.setScale(sclae, sclae);
//				return Bitmap
//						.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
//			}
//		}
//		return bmp;
//	}
//
//	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
//			int maxNumOfPixels) {
//		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
//
//		int roundedSize;
//		if (initialSize <= 8) {
//			roundedSize = 1;
//			while (roundedSize < initialSize) {
//				roundedSize <<= 1;
//			}
//		} else {
//			roundedSize = (initialSize + 7) / 8 * 8;
//		}
//
//		return roundedSize;
//	}
//
//	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
//			int maxNumOfPixels) {
//		double w = options.outWidth;
//		double h = options.outHeight;
//
//		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h
//				/ maxNumOfPixels));
//		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
//				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
//
//		if (upperBound < lowerBound) {
//			// return the larger one when there is no overlapping zone.
//			return lowerBound;
//		}
//
//		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
//			return 1;
//		} else if (minSideLength == -1) {
//			return lowerBound;
//		} else {
//			return upperBound;
//		}
//	}
//
//	/**
//	 * 计算view的宽高?
//	 *
//	 * @param view
//	 */
//	public static void measureView(View view) {
//		if (view == null) {
//			return;
//		}
//		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//	}
//
//	/**
//	 * 从view 得到图片
//	 *
//	 * @param view
//	 * @return
//	 */
//	public static Bitmap getBitmapFromView(View view) {
//		if (view == null) {
//			return null;
//		}
//		view.destroyDrawingCache();
//		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//		view.setDrawingCacheEnabled(true);
//		Bitmap bitmap = view.getDrawingCache(true);
//		return bitmap;
//	}
//
//	/**
//	 * 旋转Bitmap
//	 *
//	 * @param b
//	 * @param rotateDegree
//	 * @param fileter
//	 *            (true if the source should be filtered. Only applies if the
//	 *            matrix contains more than just translation.)
//	 * @return
//	 */
//	public static Bitmap getRotateBitmap(Bitmap bmp, float rotateDegree, boolean fileter) {
//		Matrix matrix = new Matrix();
//		matrix.postRotate(rotateDegree);
//		Bitmap mbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix,
//				fileter);
//		if (mbmp != bmp) {
//			bmp.recycle();
//			bmp = null;
//			System.gc();
//		}
//		return mbmp;
//	}
//
//	/**
//	 * 图片圆角
//	 *
//	 * @param bitmap
//	 * @return
//	 */
//	public static Bitmap getRoundCornerBitmap(Bitmap bitmap) {
//		return getRoundCornerBitmap(bitmap, -1);
//	}
//
//	/**
//	 * 图片圆角
//	 *
//	 * @param bitmap
//	 * @param pixels
//	 * @return
//	 */
//	public static Bitmap getRoundCornerBitmap(Bitmap bitmap, int pixels) {
//		if (bitmap == null) {
//			return null;
//		}
//		int width = bitmap.getWidth();
//		int height = bitmap.getHeight();
//		if (width != height) {
//			int min = Math.min(width, height);
//			bitmap = ThumbnailUtils.extractThumbnail(bitmap, min, min);
//			width = bitmap.getWidth();
//			height = bitmap.getHeight();
//		}
//
//		// 放大位图
//		// width = width < 480? 480 : width;
//		// height = height < 480? 480 : height;
//		// int imageWidth = bmp.getWidth();
//		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		if (output == null) {
//			return null;
//		}
//		Canvas canvas = new Canvas(output);
//		final int color = 0xff424242;
//		final Paint paint = new Paint();
//		final Rect rect = new Rect(0, 0, width, height);
//		final RectF rectF = new RectF(rect);
//		paint.setAntiAlias(true);
//		canvas.drawARGB(0, 0, 0, 0);
//		paint.setColor(color);
//		if (pixels <= 5 || pixels > width / 2 || pixels > height / 2) {
//			pixels = width / 2;
//		}
//		canvas.drawRoundRect(rectF, pixels, pixels, paint);
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		canvas.drawBitmap(bitmap, rect, rect, paint);
//		if (output != bitmap) {
//			bitmap.recycle();
//			bitmap = null;
//			System.gc();
//		}
//		return output;
//	}
//
//	public static Bitmap getBitmapFromUri(final Context context, final Uri uri) {
//		try {
//			// 读取uri�?��的图�?
//			Bitmap bitmap = Images.Media.getBitmap(context.getContentResolver(), uri);
//			return bitmap;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	public Bitmap getBitmapFromUri(Context context, Uri uri, int width, int height) {
//		if (uri != null) {
//			try {
//				BitmapFactory.Options opts = null;
//				if (width > 0 && height > 0) {
//					opts = new BitmapFactory.Options();
//					opts.inJustDecodeBounds = true;
//					ContentResolver cr = context.getContentResolver();
//					BitmapFactory.decodeStream(cr.openInputStream(uri), null, opts);
//					// 计算图片缩放比例
//					final int minSideLength = Math.min(width, height);
//					opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
//					opts.inJustDecodeBounds = false;
//					opts.inInputShareable = true;
//					opts.inPurgeable = true;
//					Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, opts);
//					if (bitmap != null) {
//
//						int degress = 0;
//						try {
//							String[] querys = {Images.Media.ORIENTATION};
//							Cursor cur = ((Activity) context).managedQuery(uri, querys, null, null,
//									null);
//							if (cur != null && cur.getCount() > 0) {
//								cur.moveToFirst();
//								degress = cur
//										.getInt(cur
//												.getColumnIndexOrThrow(Images.Media.ORIENTATION));
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						Matrix matrix = new Matrix();
//						matrix.setRotate(degress);
//						Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//								bitmap.getHeight(), matrix, true);
//						int imageWidth = bmp.getWidth();
//						int imageHeight = bmp.getHeight();
//						if (imageWidth < 480) {
//							float sclae = (float) 480 / imageWidth;
//							matrix = new Matrix();
//							matrix.setScale(sclae, sclae);
//							bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
//									matrix, true);
//							imageWidth = bmp.getWidth();
//							imageHeight = bmp.getHeight();
//						}
//						return bmp;
//					}
//				}
//			} catch (OutOfMemoryError e) {
//				e.printStackTrace();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
//
//	public static void releaseImageView(final ImageView imageView) {
//		if (imageView != null) {
//			Drawable d = imageView.getDrawable();
//			if (d != null)
//				d.setCallback(null);
//			imageView.setImageDrawable(null);
//			imageView.setBackgroundDrawable(null);
//		}
//	}
//
//	/**
//	 * 裁剪图片方法实现
//	 *
//	 * @param uri
//	 */
//	public static void adjustPhotoSize(final Activity activity, final Uri uri, float ratio) {
//		if (activity == null || uri == null) {
//			return;
//		}
//		// DisplayMetrics dm = Util.getScreenMetrics(context);
//		Intent intent = new Intent("com.android.camera.action.CROP");
//		intent.setDataAndType(uri, "image/*");
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", ratio);
//		intent.putExtra("aspectY", 1);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//		// intent.putExtra("outputX", dm.widthPixels); // 宽，必须设置，否则选择大图片时可能会卡机
//		// intent.putExtra("outputY", dm.heightPixels); // 高，必须设置，否则选择大图片时可能会卡机
//		intent.putExtra("outputX", 480); // 宽，必须设置，否则选择大图片时可能会卡机
//		intent.putExtra("outputY", ratio > 1 ? 600 : 480); // 高，必须设置，否则选择大图片时可能会卡机
//
//		// 加上下面的这两句之后，系统就会把图片给拉伸?
//		intent.putExtra("scale", false);
//		intent.putExtra("scaleUpIfNeeded", false);
//
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());// 图片格式
//		intent.putExtra("noFaceDetection", true);// 取消人脸识别
//		intent.putExtra("return-data", false);
//		activity.startActivityForResult(intent, ITakePhoto.ADJUST_PHOTO);
//	}
//}
