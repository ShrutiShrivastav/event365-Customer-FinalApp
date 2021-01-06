package com.ebabu.event365live.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConvertBitmap {

    public static Bitmap Mytransform(Bitmap source)
    {
        boolean isLandscape = source.getWidth() > source.getHeight();

        int newWidth, newHeight;
        if (isLandscape)
        {
            newWidth = 640;
            newHeight = Math.round(((float) newWidth / source.getWidth()) * source.getHeight());
        } else
        {
            newHeight = 480;
            newWidth = Math.round(((float) newHeight / source.getHeight()) * source.getWidth());
        }

        Bitmap result = Bitmap.createScaledBitmap(source, newWidth, newHeight, false);

        if (result != source)
            source.recycle();

        return result;
    }

    public static boolean isFileSize(String filepicturePath) {

        try {
            float sizeFile =new File(filepicturePath).length()/1024/1024;

            return !(sizeFile > 10);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static String getDate(String milliSeconds)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter=null;

        SimpleDateFormat formatter_data = new SimpleDateFormat("dd/MM/yyyy");
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));

        Calendar calendar_cal = Calendar.getInstance();
        calendar_cal.setTimeInMillis(System.currentTimeMillis());

        if(formatter_data.format(calendar.getTime()).equalsIgnoreCase(formatter_data.format(calendar_cal.getTime())))
        {
            //today time
            formatter = new SimpleDateFormat("h:mm a");
            return formatter.format(calendar.getTime());

        }else{
            String[] msgTime= formatter_data.format(calendar.getTime()).split("/");
            String[] currentTime=formatter_data.format(calendar_cal.getTime()).split("/");

            if(msgTime[1].equalsIgnoreCase(currentTime[1])){
                if(Integer.parseInt(currentTime[0])- Integer.parseInt(msgTime[0])<7){

                    formatter = new SimpleDateFormat("EEE h:mm a");
                    return formatter.format(calendar.getTime());

                }else {
                    formatter = new SimpleDateFormat("dd-MMM");
                    return formatter.format(calendar.getTime());
                }
            }else{
                formatter = new SimpleDateFormat("dd-MMM");
                return formatter.format(calendar.getTime());
            }

        }

    }

    public static Bitmap rotateImage(final Bitmap bitmap,
                                     final File fileWithExifInfo) {
        if (bitmap == null) {
            return null;
        }
        Bitmap rotatedBitmap = bitmap;
        int orientation = 0;
        try {
            orientation = getImageOrientation(fileWithExifInfo
                    .getAbsolutePath());
            if (orientation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation, (float) bitmap.getWidth() / 2,
                        (float) bitmap.getHeight() / 2);
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    public static int getImageOrientation(final String file) throws IOException {
        ExifInterface exif = new ExifInterface(file);
        int orientation = exif
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return 0;
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }


}