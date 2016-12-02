package t.a.m.com.doch1.common.validators;

import android.support.design.widget.TextInputLayout;

import t.a.m.com.doch1.R;

/**
 * Created by Morad on 12/2/2016.
 */
public class NotEmptyValidator extends BaseValidator {

    public NotEmptyValidator(TextInputLayout errorContainer) {
        super(errorContainer);
        mEmptyMessage = mErrorContainer.getResources().getString(R.string.missing_data_string);
    }

    @Override
    protected boolean isValid(CharSequence charSequence) {
        return true;
    }
}
