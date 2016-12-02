package t.a.m.com.doch1.common.validators;

import android.support.design.widget.TextInputLayout;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Morad on 11/30/2016.
 */
public abstract class BaseValidator {
    protected TextInputLayout mErrorContainer;
    protected EditText mEditText;
    protected String mErrorMessage = "";
    protected String mEmptyMessage = null;

    public BaseValidator(TextInputLayout errorContainer) {
        mErrorContainer = errorContainer;
        ViewGroup viewGroup = (ViewGroup) mErrorContainer.getChildAt(0);
        if(viewGroup.getChildCount() > 0 && viewGroup.getChildAt(0) instanceof EditText){
            mEditText = (EditText) viewGroup.getChildAt(0);
        }
    }

    protected abstract boolean isValid(CharSequence charSequence);

    public boolean validate(CharSequence charSequence) {
        if (mEmptyMessage != null && (charSequence == null || charSequence.length() == 0)) {
            mErrorContainer.setError(mEmptyMessage);
            return false;
        } else if (isValid(charSequence)) {
            mErrorContainer.setError("");
            return true;
        } else {
            mErrorContainer.setError(mErrorMessage);
            return false;
        }
    }

    public boolean validate(){
        if(mEditText != null){
            return validate(mEditText.getText());
        }

        return false;
    }

    public String getValue(){
        if(mEditText != null){
            return mEditText.getText().toString();
        }
        return "";
    }

    public void setValue(CharSequence value){
        if(mEditText != null){
            mEditText.setText(value);
        }
    }
}
