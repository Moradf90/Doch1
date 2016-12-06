package t.a.m.com.doch1.common.validators;

import android.support.design.widget.TextInputLayout;

import t.a.m.com.doch1.R;

/**
 * Created by Morad on 12/6/2016.
 */
public class PhoneValidator extends BaseValidator {
    public PhoneValidator(TextInputLayout errorContainer) {
        super(errorContainer);
        mErrorMessage = mErrorContainer.getContext().getResources().getString(R.string.invalid_phone_number);
        mEmptyMessage = mErrorContainer.getResources().getString(R.string.missing_phone_number);
    }

    @Override
    protected boolean isValid(CharSequence charSequence) {
        String value = charSequence.toString();
        return value.matches("\\d{10}") || value.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}");
    }
}
