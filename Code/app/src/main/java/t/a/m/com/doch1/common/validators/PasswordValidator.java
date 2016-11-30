package t.a.m.com.doch1.common.validators;

import android.support.design.widget.TextInputLayout;

import t.a.m.com.doch1.R;

/**
 * Created by Morad on 11/30/2016.
 */
public class PasswordValidator extends BaseValidator {

    private int mMinLength;
    public PasswordValidator(TextInputLayout errorContainer, int minLength) {
        super(errorContainer);
        mMinLength = minLength;
        String template = mErrorContainer.getResources().getString(R.string.password_length);
        mErrorMessage = String.format(template, mMinLength);
    }

    @Override
    protected boolean isValid(CharSequence charSequence) {
        return charSequence.length() >= mMinLength;
    }
}
