package com.noproblem.smarthospitaladmin.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Victor Artemyev on 21/09/2016.
 */

public final class ImagePicker {

    private static final String TAG = ImagePicker.class.getSimpleName();

    private static final int RC_PICK_PICTURE = 7458;
    private static final int RC_TAKE_PICTURE = 7459;

    private static final int QUALITY_75 = 75;

    private static final String KEY_CAMERA_IMAGE_PATH = "com.vitman.PHOTO_URI";

    public static final class Source {
        public static final String CAMERA = "CAMERA";
        public static final String GALLERY = "GALLERY";
    }

    public interface OnImagePickedListener {

        void onImagePicked(File imageFile, String source);

        void onCanceled(String source);

        void onError(Throwable throwable, String source);
    }

    public static void openGallery(Activity activity) {
        Intent intent = createGalleryIntent();
        activity.startActivityForResult(intent, RC_PICK_PICTURE);
    }

    public static void openGallery(Fragment fragment) {
        Intent intent = createGalleryIntent();
        fragment.startActivityForResult(intent, RC_PICK_PICTURE);
    }

    public static void openGallery(android.app.Fragment fragment) {
        Intent intent = createGalleryIntent();
        fragment.startActivityForResult(intent, RC_PICK_PICTURE);
    }

    public static void openCamera(Activity activity) {
        Intent intent = createCameraIntent(activity);
        activity.startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    public static void openCamera(android.support.v4.app.Fragment fragment) {
        Intent intent = createCameraIntent(fragment.getActivity());
        fragment.startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    public static void openCamera(android.app.Fragment fragment) {
        Intent intent = createCameraIntent(fragment.getActivity());
        fragment.startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    private static Intent createGalleryIntent() {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    private static Intent createCameraIntent(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri capturedImageUri = createCameraPictureFile(context);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        return intent;
    }

    private static Uri createCameraPictureFile(Context context) {
        File imagePath = createCameraFile();
        Uri uri = Uri.fromFile(imagePath);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(KEY_CAMERA_IMAGE_PATH, uri.getPath());
        editor.apply();
        return uri;
    }

    private static File createCameraFile() {
        File publicPictureDirection = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File mediaStorageDir = new File(publicPictureDirection, "TakeStock");
        if (!mediaStorageDir.exists()) {
            boolean isCreated = mediaStorageDir.mkdirs();
            Log.d(ImagePicker.class.getSimpleName(), mediaStorageDir.getAbsolutePath() + " : " + isCreated);
        }
        return new File(
                mediaStorageDir.getPath() + File.separator
                        + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()) + ".jpg");
    }

    public static void handleActivityResult(int requestCode, int resultCode, Intent data,
                                            @NonNull Context context,
                                            @NonNull OnImagePickedListener onImagePickedListener) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_PICK_PICTURE) {
                onPictureReturnedFromGallery(data, context, onImagePickedListener);
            } else if (requestCode == RC_TAKE_PICTURE) {
                onPictureReturnedFromCamera(context, onImagePickedListener);
            }
        } else {
            if (requestCode == RC_PICK_PICTURE) {
                onImagePickedListener.onCanceled(Source.GALLERY);
            } else if (requestCode == RC_TAKE_PICTURE) {
                onImagePickedListener.onCanceled(Source.CAMERA);
            }
        }
    }

    private static void onPictureReturnedFromGallery(Intent data, Context context, OnImagePickedListener listener) {
        File file = pickGalleryImageFile(context, data.getData());
        if (file == null) {
            listener.onCanceled(Source.GALLERY);
        } else {
            try {
                File tempFile = processToTempFile(context, file);
                listener.onImagePicked(tempFile, Source.GALLERY);
            } catch (IOException e) {
                Log.e(TAG, "onPictureReturnedFromGallery", e);
                listener.onError(e, Source.GALLERY);
            }
        }
    }

    @Nullable private static File pickGalleryImageFile(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        File imageFile = new File(cursor.getString(columnIndex));
        cursor.close();
        return imageFile;
    }

    private static void onPictureReturnedFromCamera(Context context, OnImagePickedListener listener) {
        File file = takeCameraImageFile(context);
        try {
            File tempFile = processToTempFile(context, file);
            listener.onImagePicked(tempFile, Source.CAMERA);
        } catch (IOException e) {
            Log.e(TAG, "onPictureReturnedFromCamera", e);
            listener.onError(e, Source.CAMERA);
        }
    }

    private static File takeCameraImageFile(Context context) {
        String path = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_CAMERA_IMAGE_PATH, "");
        return new File(path);
    }

    private static File processToTempFile(Context context, File imageFile) throws IOException {
        Bitmap bitmap = bitmapFromFile(imageFile);
        bitmap = rotateBitmapPerOrientation(bitmap, imageFile);
        return bitmapToFile(bitmap, createTempFile(context));
    }

    private static File createTempFile(Context context) {
        return new File(context.getCacheDir().getPath() + File.separator + UUID.randomUUID().toString() + ".jpg");
    }

    private static final int MAX_WIDTH = 480;
    private static final int MAX_HEIGHT = 480;

    private static final int ORIENTATION_ROTATE_90 = ExifInterface.ORIENTATION_ROTATE_90;
    private static final int ORIENTATION_ROTATE_180 = ExifInterface.ORIENTATION_ROTATE_180;

    private static Bitmap rotateBitmapPerOrientation(Bitmap target, File file) throws IOException {

        int orientation = getBitmapOrientation(file);
        switch (orientation) {
            case ORIENTATION_ROTATE_90:
                return rotateBitmapPerAngle(target, 90);

            case ORIENTATION_ROTATE_180:
                return rotateBitmapPerAngle(target, 180);

            default:
                return target;
        }
    }

    private static int getBitmapOrientation(File file) throws IOException {
        ExifInterface exifInterface = new ExifInterface(file.getPath());
        return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }

    private static Bitmap rotateBitmapPerAngle(Bitmap target, int angle) {
        Bitmap retVal;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(target, 0, 0, target.getWidth(), target.getHeight(), matrix, true);
        target.recycle();

        return retVal;
    }

    private static Bitmap bitmapFromFile(File file) {
        return bitmapFromFile(file, MAX_WIDTH, MAX_HEIGHT);
    }

    private static Bitmap bitmapFromFile(File file, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static File bitmapToFile(Bitmap bitmap, File file) throws IOException {
        return bitmapToFile(bitmap, file, QUALITY_75);
    }

    private static File bitmapToFile(Bitmap bitmap, File file, int quality) throws IOException {
        return bitmapToFile(bitmap, file, quality, Bitmap.CompressFormat.JPEG);
    }

    private static File bitmapToFile(Bitmap bitmap, File file, int quality,
                                     Bitmap.CompressFormat format) throws IOException {
        FileOutputStream outStream = new FileOutputStream(file);
        bitmap.compress(format, quality, outStream);
        outStream.flush();
        outStream.close();
        return file;
    }
}
