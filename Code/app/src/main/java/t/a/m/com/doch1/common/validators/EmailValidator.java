package t.a.m.com.doch1.common.validators;

import android.support.design.widget.TextInputLayout;
import android.util.Patterns;

import t.a.m.com.doch1.R;

/**
 * Created by Morad on 11/30/2016.
 */
public class EmailValidator extends BaseValidator {

    public EmailValidator(TextInputLayout errorContainer) {
        super(errorContainer);
        mErrorMessage = mErrorContainer.getContext().getResources().getString(R.string.invalid_email_address);
        mEmptyMessage = mErrorContainer.getResources().getString(R.string.missing_email_address);
    }

    @Override
    protected boolean isValid(CharSequence charSequence) {
        return Patterns.EMAIL_ADDRESS.matcher(charSequence).matches();
    }
}
