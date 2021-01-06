package com.ebabu.event365live.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.ebabu.event365live.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {


    public static boolean emailValidator(Context context, String email) {

        if (email.isEmpty()) {
            ShowToast.infoToast(context, context.getString(R.string.error_please_enter_valid));
            return false;
        }

        if (email.startsWith(".") || email.startsWith("_") || email.contains("..") || email.contains("__")
                || email.contains("._") || email.contains("_.") || email.contains(".@") ||
                email.contains("_@") || email.contains("--") || email.contains(".-") || email.contains("-.") || email.contains("-@") || email.contains("@-")) {

            ShowToast.infoToast(context, context.getString(R.string.error_please_enter_valid));
            return false;
        }

        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9]+(\\.[_A-Za-z0-9]+)*@[-A-Za-z0-9]+(\\.[A-Za-z]{2,}){1,}$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            ShowToast.infoToast(context, context.getString(R.string.error_please_enter_valid));
            return false;
        }

        return true;
    }

    public static boolean passwordValidator(Context context, String pass) {

        if (pass.isEmpty())  {
            ShowToast.infoToast(context, context.getString(R.string.error_please_enter_password));
            return false;
        }

        if (pass.startsWith(" ") || pass.endsWith(" ")) {
            ShowToast.infoToast(context, context.getString(R.string.please_enter_valid_password));
            return false;
        }
        if (pass.length() < 6) {
            ShowToast.infoToast(context, context.getString(R.string.error_password_short));
            return false;
        }

       /* Pattern pattern;
        Matcher matcher;
        // final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
        final String PASSWORD_PATTERN = "^(?=.*?[A-Za-z])(?=.*?[0-9]).{6,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(mPassword);
        boolean success = matcher.matches();

        if (!success) {
            inputLayoutMobile.setError(context.getString(R.string.error_valid_password, fieldName.toLowerCase()));
        }

        return success;*/
        return true;
    }


    public static boolean validateLastName(String lastName) {
        return lastName.matches("[a-zA-z]+([ '-][a-zA-Z]+)*");
    }

    public static boolean validateRC(String lastName) {
        return lastName.matches("^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$");
    }

    public static boolean validateLicense(String lastName) {
        return lastName.matches("^[A-Z]{2}[-][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$");
    }

    public static boolean validateMobileNumber(Context context, String mob) {

        if (mob.isEmpty()) {
            ShowToast.errorToast(context, context.getString(R.string.error_please_enter_valid_no));
            return false;
        }

        if (!mob.matches("^[6-9]\\d{9}$")) {
            ShowToast.errorToast(context, context.getString(R.string.error_valid));
            return false;
        }

        return true;
    }


    public static boolean isValidMobile(Activity activity, String phone) {
        boolean  isValid = android.util.Patterns.PHONE.matcher(phone).matches();
        if(isValid){
            return true;
        }
        ShowToast.errorToast(activity,activity.getString(R.string.error_please_enter_valid_no));
        return false;
    }




    public static boolean validateField(Context context, TextInputLayout inputLayoutMobile, String mName, String fieldName, int maxLength) {


        if (mName.length() < 3) {
            inputLayoutMobile.setError(context.getString(R.string.error_name_toshort, fieldName));
            return false;
        }


        if (mName.length() > maxLength) {
            inputLayoutMobile.setError(context.getString(R.string.error_name_to_long, fieldName.toLowerCase(), maxLength));
            return false;
        }


        return true;
    }


    public static boolean validateCoordinate(String value) {
        String[] arrOfStr = value.split(" ");

        return arrOfStr[0].matches("^([0-8]?[0-9]|90)°([0-5]?[0-9]')?([0-5]?[0-9](.[0-9])?\")?([NEWSnews]{1})$");

    }


    public static boolean validateName(Context context, String userName) {


        if (TextUtils.isEmpty(userName)) {
            ShowToast.infoToast(context, context.getString(R.string.error_please_enter_name));
            return false;
        }


        if (userName.startsWith(" ") || userName.endsWith(" ") || userName.length()<2 ) {
            ShowToast.infoToast(context, context.getString(R.string.please_enter_valid_name));
            return false;
        }

        return true;
    }

    public static String capsFirstWord(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }






    public static boolean isValidGST(String gst) {
        boolean isValidGst = false;
        String reggst = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9]{1}Z[0-9A-Z]{1}?$";
        CharSequence inputStr = gst;
        Pattern pattern = Pattern.compile(reggst, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValidGst = true;
        }
        return isValidGst;
    }

    public static boolean isValidPAN(String pan) {
        boolean isValidGst = false;
        String reggst = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        CharSequence inputStr = pan;
        Pattern pattern = Pattern.compile(reggst, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValidGst = true;
        }
        return isValidGst;
    }

    public static boolean isPANBelongsToGST(String pan, String gst) {
        boolean isValidPan = false;

        if (gst != null && gst.length() > 3) {
            String panFromGst = gst.substring(2, 12);
            if (pan.equals(panFromGst)) {
                isValidPan = true;
            }
        }

        return isValidPan;
    }

    public static boolean isValidIfsc(String ifscCode){
        return ifscCode.matches("^[A-Za-z]{4}0[A-Z0-9a-z]{6}$");
    }

    public static boolean isValidAccountNumber(String accountNumber){
        return accountNumber.matches("^\\d{9,18}$");
    }

    public static boolean emailValidatorWithoutToast(Context context, String email) {

        if (email.isEmpty()) {
            return false;
        }

        if (email.startsWith(".") || email.startsWith("_") || email.contains("..") || email.contains("__")
                || email.contains("._") || email.contains("_.") || email.contains(".@") ||
                email.contains("_@") || email.contains("--") || email.contains(".-") || email.contains("-.") || email.contains("-@") || email.contains("@-")) {
            return false;
        }

        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9]+(\\.[_A-Za-z0-9]+)*@[-A-Za-z0-9]+(\\.[A-Za-z]{2,}){1,}$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();
    }


    public static boolean passwordValidatorWithoutToast(Context context, String pass) {

        if (pass.isEmpty()) {
            return false;
        }

        return pass.length() >= 6;/* Pattern pattern;
        Matcher matcher;
        // final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
        final String PASSWORD_PATTERN = "^(?=.*?[A-Za-z])(?=.*?[0-9]).{6,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(mPassword);
        boolean success = matcher.matches();

        if (!success) {
            inputLayoutMobile.setError(context.getString(R.string.error_valid_password, fieldName.toLowerCase()));
        }

        return success;*/
    }


}
