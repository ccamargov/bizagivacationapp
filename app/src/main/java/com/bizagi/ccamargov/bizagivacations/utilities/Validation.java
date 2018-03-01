package com.bizagi.ccamargov.bizagivacations.utilities;


import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
import com.bizagi.ccamargov.bizagivacations.R;

import java.util.regex.Pattern;

public class Validation {

    private String EMAIL_REGEX;
    private String REQUIRED_MSG;
    private String EMAIL_MSG;

    public Validation(Context current) {
        this.EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]" +
                "+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        this.REQUIRED_MSG = current.getResources().getString(R.string.required_field);
        this.EMAIL_MSG = current.getResources().getString(R.string.invalid_email);
    }

    public boolean isEmailAddress(EditText editText,
                                  TextInputLayout editLayout) {
        return isValid(editText, editLayout, EMAIL_REGEX, EMAIL_MSG);
    }

    private boolean isValid(EditText editText, TextInputLayout editLayout,
                            String regex, String errMsg) {
        String text = editText.getText().toString().trim();
        editLayout.setError(null);
        if (!hasText(editText, editLayout)) return false;
        if (!Pattern.matches(regex, text)) {
            editLayout.setError(errMsg);
            return false;
        }
        return true;
    }

    public boolean hasText(EditText editText, TextInputLayout editLayout) {
        String text = editText.getText().toString().trim();
        editLayout.setError(null);
        if (text.length() == 0) {
            editLayout.setError(REQUIRED_MSG);
            return false;
        }
        return true;
    }

}