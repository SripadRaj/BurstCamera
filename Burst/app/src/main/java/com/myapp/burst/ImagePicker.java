package com.myapp.burst;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Component to pick Images in android from gallery as well as from camera.
 * <p/>
 * Created & Developed by Manu Sharma on 8/23/2016.
 */
public class ImagePicker {

    public static final String DEFAULT_FILE_LOCATION = "Burst/JPG/";

    public static final String DEFAULT_PREFIX = "Picture";

    public static final String DEFAULT_EXTENSION = ".jpg";

    private Context mContext;

    private static ImagePicker singleInstance;



    private ImagePicker(Context context) {
        mContext = context;
    }

    public static ImagePicker getInstance(Context context) {
        if (singleInstance == null) {
            singleInstance = new ImagePicker(context);
        }
        return singleInstance;
    }

    public interface ImagePickerDialogListener{
        void onCameraSelected();

        void onGallerySelected();

        void onDeleteSelected();
    }

    public static Boolean openCameraGalleryChooserDialog(Activity activity, final ImagePickerDialogListener imagePickerDialogListener) {
        String[] items = new String[]{"Camera", "Gallery","Remove picture"};
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Select source");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            if(imagePickerDialogListener!=null){
                                imagePickerDialogListener.onCameraSelected();
                            }
                            break;

                        case 1:
                            if(imagePickerDialogListener!=null){
                                imagePickerDialogListener.onGallerySelected();
                            }
                            break;

                        case 2:
                            if(imagePickerDialogListener!=null){
                                imagePickerDialogListener.onDeleteSelected();
                            }
                            break;
                    }
                }
            });
            builder.create().show();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static Boolean openCameraGalleryChooser(Activity activity, final ImagePickerDialogListener imagePickerDialogListener) {
        String[] items = new String[]{"Camera", "Gallery"};
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Select source");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            if(imagePickerDialogListener!=null){
                                imagePickerDialogListener.onCameraSelected();
                            }
                            break;

                        case 1:
                            if(imagePickerDialogListener!=null){
                                imagePickerDialogListener.onGallerySelected();
                            }
                            break;
                    }
                }
            });
            builder.create().show();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void openGalleryForOneImageSelection(Fragment fragment, int RequestCode) throws NullPointerException {
        if (fragment == null) {
            //fragment is null. startActivityForResult cannot be called.
            throw new NullPointerException("fragment is null. startActivityForResult cannot be called.");
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        fragment.startActivityForResult(Intent.createChooser(intent, "Choose File"), RequestCode);
    }

    public void openGalleryForMultipleImageSelection(Fragment fragment, int RequestCode) throws NullPointerException {
        if (fragment == null) {
            //fragment is null. startActivityForResult cannot be called.
            throw new NullPointerException("fragment is null. startActivityForResult cannot be called.");
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);

        //enabling for multiple image selection. NOTE: Only works on API level 18 and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        //Starting Gallery for getting the images
        fragment.startActivityForResult(Intent.createChooser(intent, "Select File"), RequestCode);
    }

    public void openCameraForImageThumbnailCapturing(Fragment fragment, int RequestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fragment.startActivityForResult(intent, RequestCode);
    }

    public Uri openCameraForImageCapturing(Fragment fragment, int requestCode, String directory, String prefix, String extension) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = null;
        Uri mImageUri;
        try {
            // place where to store camera taken picture
            photo = this.createFile(directory, prefix, extension);
            photo.delete();
        } catch (Exception e) {
            e.printStackTrace();
//            Log.v(TAG, "Can't create file to take picture!");
//            Toast.makeText(activity, "Please check SD card! Image shot is impossible!", 10000);
//            return false;
        }
        mImageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        fragment.startActivityForResult(intent, requestCode);
        Log.e("IMAGE URI", "" + mImageUri.toString());
        return mImageUri;
    }

    public Bitmap processCameraImageWithoutCompression(Intent data) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException("parameter (Intent data) is null.");
        }
        try {
            return (Bitmap) data.getExtras().get("data");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap processCameraImageWithoutCompression(Context context, Uri uri) throws NullPointerException {
        if (uri == null) {
            throw new NullPointerException("parameter uri is null.");
        }
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap processCameraImageWithCompression(Intent data, int compressionPercentage) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException("parameter (Intent data) is null.");
        }
        try {
            Bitmap bm = (Bitmap) data.getExtras().get("data");

            bm = Bitmap.createScaledBitmap(bm, bm.getWidth() / 2, bm.getHeight() / 2, true);
            String imageFileName = getFilename(null);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFileName);

            bm.compress(Bitmap.CompressFormat.JPEG, compressionPercentage, fileOutputStream);
            bm.recycle();
            bm = null;

            Bitmap compressedBitmap = BitmapFactory.decodeFile(imageFileName);

            //delete the file.
            File file = new File(imageFileName);
            file.delete();

            return compressedBitmap;
        } catch (IllegalArgumentException e) {
            if (compressionPercentage < 0 || compressionPercentage > 100) {
                throw new IllegalArgumentException("compression percentage must be 0..100");
            } else {
                e.printStackTrace();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap processCameraImageWithCompression(Context context, File uri, int compressionPercentage) throws NullPointerException {
        if (uri == null) {
            throw new NullPointerException("parameter uri is null.");
        }
        try {
            Bitmap bm = BitmapFactory.decodeFile(uri.getAbsolutePath());

            bm = Bitmap.createScaledBitmap(bm, bm.getWidth() / 2, bm.getHeight() / 2, true);
            String imageFileName = getFilename(null);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFileName);

            bm.compress(Bitmap.CompressFormat.JPEG, compressionPercentage, fileOutputStream);
            bm.recycle();
            bm = null;

            Bitmap compressedBitmap = BitmapFactory.decodeFile(imageFileName);

            //delete the file.
            File file = new File(imageFileName);
            file.delete();

            try {
                ExifInterface exifInterface = new ExifInterface(uri.getPath());
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        compressedBitmap = rotateImage(compressedBitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        compressedBitmap = rotateImage(compressedBitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        compressedBitmap = rotateImage(compressedBitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                        break;

                    default:
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return compressedBitmap;
        } catch (IllegalArgumentException e) {
            if (compressionPercentage < 0 || compressionPercentage > 100) {
                throw new IllegalArgumentException("compression percentage must be 0..100");
            } else {
                e.printStackTrace();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    public Bitmap processGalleryImageWithCompression(final Intent data, int requiredSize, int minimumScaleFactor, int compressionPercentage) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException("parameter (Intent data) is null.");
        }

        try {
            Uri selectedImage = data.getData();
            //just getting bounds for the image.
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(selectedImage), null, o);

            /*Scaling factor processing start.*/
            final int REQUIRED_SIZE = requiredSize;

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = minimumScaleFactor;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
                //Scaling down
            }
            /*Scaling factor processing end.*/

            //Getting image as scaled image.
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(selectedImage), null, o2);

            //Creating output file to save compressed version.
            String imageFileName = getFilename(null);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFileName);

            //Compressing bitmap to 50%.
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressionPercentage, fileOutputStream);
            bitmap.recycle();
            bitmap = null;

            Bitmap compressedBitmap = BitmapFactory.decodeFile(imageFileName);

            //delete the file.
            File file = new File(imageFileName);
            file.delete();

            return compressedBitmap;
        } catch (IllegalArgumentException e) {
            if (compressionPercentage < 0 || compressionPercentage > 100) {
                throw new IllegalArgumentException("compression percentage must be 0..100");
            } else {
                e.printStackTrace();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Uri performCrop(Activity activity, Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            // retrieve data on return
            cropIntent.putExtra("return-data", true);

            File f = new File(Environment.getExternalStorageDirectory(),
                    "/temporary_holder.jpg");
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Log.e("io", ex.getMessage());
            }

            Uri uri = Uri.fromFile(f);

            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            // start the activity - we handle returning in onActivityResult
            activity.startActivityForResult(cropIntent, 99);
            return  uri;
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }
    }

    public String getFilename(@Nullable String locationForFiles) {
        File file = null;

        if (locationForFiles == null || locationForFiles.length() == 0) {
            file = new File(Environment.getExternalStorageDirectory().getPath(), DEFAULT_FILE_LOCATION);//if No name is provided.
        } else {
            file = new File(Environment.getExternalStorageDirectory().getPath(), locationForFiles);
        }

        if (!file.exists()) {
            file.mkdirs();//creating folder if not already exist.
        }

        //generating file name including folder name
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");

        return uriSting;

    }

    public File createFile(@Nullable String locationForFiles, @Nullable String prefix, @Nullable String extensionName) throws IOException {
        if (locationForFiles == null || locationForFiles.length() == 0) {
            locationForFiles = mContext.getCacheDir().getAbsolutePath();//if No name is provided.
        }

        if (prefix == null || prefix.length() == 0) {
            prefix = DEFAULT_PREFIX;//if No name is provided.
        }

        if (extensionName == null || extensionName.length() == 0) {
            extensionName = DEFAULT_EXTENSION;//if No extension is provided.
        }

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/"+locationForFiles);

        if (!file.exists()) {
            file.mkdirs();//creating folder if not already exist.
        }

        return File.createTempFile(prefix, extensionName, file);

    }

    public File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }


    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final String TAG = "ImagePicker";
    private static final String TEMP_IMAGE_NAME = "tempImage";

    public static int minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY;

    public static Bitmap getImageFromResult(Context context, int resultCode,
                                            Intent imageReturnedIntent) {
        Log.d(TAG, "getImageFromResult, resultCode: " + resultCode);
        Bitmap bm = null;
        File imageFile = getTempFile(context);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage;
            boolean isCamera = (imageReturnedIntent == null ||
                    imageReturnedIntent.getData() == null ||
                    imageReturnedIntent.getData().equals(Uri.fromFile(imageFile)));
            if (isCamera) {     /** CAMERA **/
                selectedImage = Uri.fromFile(imageFile);
            } else {            /** ALBUM **/
                selectedImage = imageReturnedIntent.getData();
            }
            Log.d(TAG, "selectedImage: " + selectedImage);

            bm = getImageResized(context, selectedImage);
            int rotation = getRotation(context, selectedImage, isCamera);
            bm = rotate(bm, rotation);
        }
        return bm;
    }


    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);

        Log.d(TAG, options.inSampleSize + " sample method bitmap ... " +
                actuallyUsableBitmap.getWidth() + " " + actuallyUsableBitmap.getHeight());

        return actuallyUsableBitmap;
    }

    /**
     * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
     **/
    private static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm = null;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            Log.d(TAG, "resizer: new bitmap width = " + bm.getWidth());
            i++;
        } while (bm.getWidth() < minWidthQuality && i < sampleSizes.length);
        return bm;
    }


    private static int getRotation(Context context, Uri imageUri, boolean isCamera) {
        int rotation;
        rotation = getRotationFromCamera(context, imageUri);
        Log.d(TAG, "Image rotation: " + rotation);
        return rotation;
    }

    public static int getRotationFromCamera(Context context, Uri imageFile) {
        int rotate = 0;
        try {

            context.getContentResolver().notifyChange(imageFile, null);
            ExifInterface exif = new ExifInterface(imageFile.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    private static Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            return bmOut;
        }
        return bm;
    }

    private static File getTempFile(Context context) {
        File imageFile = new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME);
        imageFile.getParentFile().mkdirs();
        return imageFile;
    }


}
