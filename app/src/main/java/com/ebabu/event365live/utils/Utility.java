package com.ebabu.event365live.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ebabu.event365live.R;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Utility {

    private static Snackbar snackbar;
    private static long mLastClickTime = 0;
    public static boolean isAskQuestion;
    public static boolean isDeleteQuestion;
    public static boolean isEditQuestion;
    public static boolean isSubmitLayout;
    public static boolean isAskQuestionSearch;
    public static String startDate = "";
    public static String filterStartDate = "";
    public static String endDate = "";
    public static String miles = "500";
    public static String cost = "4000";

    // Check internet connection
    public static boolean isNetworkAvailable(Context context, boolean showToast) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = false;

        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (showToast && !isConnected)
            showToastConnection(context);

        return isConnected;
    }

    public static boolean isNetworkAvailable(Context context) {
        return isNetworkAvailable(context, false);
    }

    // Show Snackbar for alert
    public static void ShowSnackbar(Context context, View v, String msg) {

        snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
                .setAction(context.getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });

        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.blue_bg));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snackbar.getView().setBackground(context.getDrawable(R.drawable.snackbar_bg));
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(12, 12, 12, 12);
        snackbar.getView().setLayoutParams(params);

        int snackbarTextId = R.id.snackbar_text;
        TextView textView = snackbar.getView().findViewById(snackbarTextId);
        textView.setMaxLines(3);
        snackbar.show();
    }

    // Show Toast
    public static void ShowToast(Context context, String msg, int duration) {
        if (msg == null)
            return;

        if (msg.isEmpty())
            return;

        int gravity = Gravity.BOTTOM; // the position of toast
        int xOffset = 0; // horizontal offset from current gravity
        int yOffset = 40;

        Toast toast = Toast.makeText(context, msg, duration);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.show();
    }

    public static void showToastConnection(Context context) {
        ShowToast(context, context.getString(R.string.connection), 1000);
    }

    // Animation for VIEW
    public static void View_Animation(Context context, int animation, View v) {
        Animation animation1 = AnimationUtils.loadAnimation(context, animation);
        v.startAnimation(animation1);
    }


    // Set toolbar with back icon


    // Start SMS receiver
    public static void startSMSReceiver(Context context) {
        SmsRetrieverClient client = SmsRetriever.getClient(context);

        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    // Increase Clicking Time
    public static boolean buttonClickTime() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    // Check value null and return
    public static String checkNull(String value) {
        return value == null ? "" : value;
    }


    // Soft keyboard hide
    public static void hideKeyboardFrom(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);

        View view1 = activity.getCurrentFocus();
        if (view1 == null) {
            view1 = new View(activity);
        }
        imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
    }


    // Call resend email verify link


    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    public static int dpToPx(float dp, Context context) {
        return dpToPx(dp, context.getResources());
    }


    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static int getWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
        //  int displayHeight = displayMetrics.heightPixels;
    }


    public static SimpleDateFormat sServerDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    public static SimpleDateFormat sShowDateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
    public static SimpleDateFormat sShowDateMonthFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    public static String convertFormat(String date, SimpleDateFormat sourceDateFormat, SimpleDateFormat desiredDateFormat) {
        // Validating if the supplied parameters is null
        if (date == null) {
            return null;
        }
        // Create SimpleDateFormat object with source string date format
        // Parse the string into Date object
        Date dateObj = null;
        try {
            dateObj = sourceDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        // Create SimpleDateFormat object with desired date format
        // Parse the date into another format
        return desiredDateFormat.format(dateObj);
    }

    public static String convertServerDate(String date) {
        if (date != null && !date.isEmpty()) {
            return convertFormat(date, sServerDateFormat, sShowDateFormat);
        } else {
            return "";
        }
    }

    public static String convertServerDateYear(String date) {
        if (date != null && !date.isEmpty()) {
            return convertFormat(date, sServerDateFormat, sShowDateMonthFormat);
        } else {
            return "";
        }
    }

    public static String setTime(String created_at) {
        if (created_at == null)
            return "";

        return Utility.convertServerDateYear(created_at);
    }


    public static void datePickerDialog(Context mContext, final TextInputEditText etEventDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etEventDate.setText(sdf.format(calendar.getTime()));
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public static void timePickerDialog(Context mContext, final TextInputEditText etEventTime) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String myFormat = "h:mm a"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);
                etEventTime.setText(sdf.format(calendar.getTimeInMillis()));
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    public static Bitmap rotateImage(final Bitmap bitmap,
                                     final File fileWithExifInfo) {
        if (bitmap == null) {
            return null;
        }
        Bitmap rotatedBitmap = bitmap;
        int newOrintation = 0;
        try {
            // orientation = getImageOrientation(fileWithExifInfo.getAbsolutePath());
            int orientation = 0;
            try {
                ExifInterface exif = new ExifInterface(fileWithExifInfo.getAbsolutePath());
                orientation = exif
                        .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    newOrintation = 0;
                    return bitmap;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    newOrintation = 180;
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    newOrintation = 180;
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    newOrintation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    newOrintation = 90;
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    newOrintation = -90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    newOrintation = -90;
                    break;
                default:
                    return bitmap;
            }

            if (newOrintation != 0) {
                matrix.postRotate(newOrintation, (float) bitmap.getWidth() / 2,
                        (float) bitmap.getHeight() / 2);
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                // return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    public static final class GetFilePathFromDevice {

        /**
         * Get file path from URI
         *
         * @param context context of Activity
         * @param uri     uri of file
         * @return path of given URI
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public static String getPath(final Context context, final Uri uri) {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }

        public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        public static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }
    }

    // Read more method post
    public static void addReadMoreTextPost(final String text, final TextView textView, final Context appContext, final int counter) {
        //status 1 for timeline
        SpannableString ss = new SpannableString(text.substring(0, counter) + appContext.getString(R.string.txt_see_more));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                addReadLessTextPost(text, textView, appContext, counter);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ds.setColor(appContext.getResources().getColor(R.color.blue_bg, appContext.getTheme()));
                } else {
                    ds.setColor(appContext.getResources().getColor(R.color.blue_bg));
                }
            }
        };
        ss.setSpan(clickableSpan, ss.length() - 10, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // Read less method post
    public static void addReadLessTextPost(final String text, final TextView textView, final Context appContext, final int counter) {
        textView.setText(text);
        SpannableString ss = new SpannableString(text + "  " + appContext.getString(R.string.txt_see_less));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                addReadMoreTextPost(text, textView, appContext, counter);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ds.setColor(appContext.getResources().getColor(R.color.blue_bg, appContext.getTheme()));
                } else {
                    ds.setColor(appContext.getResources().getColor(R.color.blue_bg));
                }
            }
        };
        ss.setSpan(clickableSpan, ss.length() - 10, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // UTC to local date
    public static String chageUTC_ToLocalDate(SimpleDateFormat dateFormaterServer, String date, SimpleDateFormat dateFormaterConvert) {
        String returnValue = "";
        try {

            if (date == null || date.isEmpty())
                return "";

            dateFormaterServer.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = dateFormaterServer.parse(date);

            dateFormaterConvert.setTimeZone(TimeZone.getDefault());
            returnValue = dateFormaterConvert.format(value);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    // Getting how much time ago from now
    public static String getTimeAgo(Context context, Calendar yourCalenderDate) {
        Calendar currentCal = Calendar.getInstance();
        CharSequence ago = DateUtils.getRelativeTimeSpanString(yourCalenderDate.getTimeInMillis(), currentCal.getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
        return ago.toString();
    }

    // String request body for part array
    public static Map<String, RequestBody> createPartFromArray(String key, ArrayList<String> myList) {
        Map<String, RequestBody> ReturnData = new HashMap<String, RequestBody>();
        for (int i = 0; i < myList.size(); i++) {
            ReturnData.put(key + "[" + i + "]", Utility.getStringRequestBody(myList.get(i)));
        }
        return ReturnData;
    }

    // String request body
    public static RequestBody getStringRequestBody(String value) {
        MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
        return RequestBody.create(MEDIA_TYPE_TEXT, value == null ? "" : value);
    }

    // Uri compress method and return file change size
    public static File compressURIChangeSize(Context context, Uri URI, String mili_second) {
        File imageFile = null;

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), URI);
            bitmap = ConvertBitmap.Mytransform(bitmap);

            ByteArrayOutputStream datasecond = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, datasecond);

            byte[] bitmapdata = datasecond.toByteArray();

            // write the bytes in file
            imageFile = new File(Environment.getExternalStorageDirectory() + File.separator + mili_second + context.getString(R.string.app_name).concat(".jpeg"));

            FileOutputStream fo = new FileOutputStream(imageFile);
            fo.write(bitmapdata);
            fo.close();

            return imageFile;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageFile;
    }


    // Get mime type
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null && !extension.equals("")) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } else if (url.lastIndexOf(".") != -1) {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        } else {
            type = null;
        }

        return type;
    }

    // File request body for part
    public static MultipartBody.Part getFileRequestBody_part(String key, File file) {
        RequestBody userPhotoRequestBody = RequestBody.create(MediaType.parse(getMimeType(file.getAbsolutePath())), file);
        MultipartBody.Part fileToUploadPart = MultipartBody.Part.createFormData(key, file.getName(), userPhotoRequestBody);

        return fileToUploadPart;
    }

    // File request body for part array
    public static MultipartBody.Part[] getFileRequestBody_partmultiple(String key, ArrayList<File> FileArray) {

        MultipartBody.Part[] parts = new MultipartBody.Part[FileArray.size()];

        for (int i = 0; i < FileArray.size(); i++) {
            parts[i] = getFileRequestBody_part(key, FileArray.get(i));
        }

        return parts;
    }

    // Get mime type of file
    public static String getMimeTypeFile(Uri uri, Context context) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // Uri compress method and return file change size
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static File videoThumbnail(Context context, Uri URI, String mili_second) {
        File imageFile = null;
        try {
            String path = Utility.GetFilePathFromDevice.getPath(context, URI);
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            bitmap = ConvertBitmap.Mytransform(bitmap);
            ByteArrayOutputStream datasecond = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, datasecond);

            byte[] bitmapdata = datasecond.toByteArray();

            // write the bytes in file
            imageFile = new File(Environment.getExternalStorageDirectory() + File.separator + mili_second + context.getString(R.string.app_name).concat(".jpeg"));

            FileOutputStream fo = new FileOutputStream(imageFile);
            fo.write(bitmapdata);
            fo.close();
            return imageFile;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFile;
    }


    public static void getThisWeekDate() {
        String selectedStartDate = "", selectedEndDate = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date todayThisWeek = calendar.getTime();
        selectedStartDate = formatter.format(todayThisWeek);
        int getDayOfTheWeek = formatter.getCalendar().get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_WEEK, 7);
        Date data1 = calendar.getTime();
        selectedEndDate = formatter.format(data1);
        Utility.startDate = localToUTC(todayThisWeek);
        Utility.endDate = localToUTC(data1);

        Log.d("nlkfnaklnkfl", Utility.startDate + " this week: : " + Utility.endDate);

    }

    public static String localToUTC(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date); //get UTC date from local dateTime
    }


    public static String toUpperCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0)))
                    .append(s.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }


}