package t.a.m.com.doch1.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import t.a.m.com.doch1.R;

/**
 * Created by Morad on 2/11/2017.
 */
public class MyTextInputLayout extends LinearLayout implements View.OnClickListener, TextWatcher {

    private boolean isEditable;
    private CharSequence mTitle;
    private CharSequence mErrorInputMessage;
    private CharSequence mEmptyInputMessage;
    private CharSequence mSourceText;
    private LinearLayout mEditContainer;
    private LinearLayout mViewContainer;
    private TextView mTextView;
    private TextView mTitleView;
    private ImageButton mEditButton;
    private ImageButton mAcceptButton;
    private ImageButton mCancelButton;
    private AutoCompleteTextView mTextEdit;
    private TextInputLayout mTextInputLayout;
    private IValidator mValidator;
    private IEditCallback mEditCallback;

    public MyTextInputLayout(Context context) {
        super(context);
    }

    public MyTextInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.AppTheme);
    }

    public MyTextInputLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, R.style.AppTheme);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyTextInputLayout, R.attr.myTextInputLayoutStyle, R.style.AppTheme);

        isEditable = a.getBoolean(R.styleable.MyTextInputLayout_editable, false);
        mTitle = a.getString(R.styleable.MyTextInputLayout_title);
        mErrorInputMessage = a.getString(R.styleable.MyTextInputLayout_on_error_message);
        mEmptyInputMessage = a.getString(R.styleable.MyTextInputLayout_on_empty_message);
        mSourceText = a.getString(R.styleable.MyTextInputLayout_text);

        a.recycle();

        init();
    }

    private void init() {
        inflate(getContext(), R.layout.my_text_input_layout, this);
        mEditContainer = (LinearLayout) findViewById(R.id.container_edit);
        mViewContainer = (LinearLayout) findViewById(R.id.container_view);
        mTextView = (TextView) findViewById(R.id.text_view);
        mTitleView = (TextView) findViewById(R.id.title);
        mEditButton = (ImageButton) findViewById(R.id.edit_button);
        mAcceptButton = (ImageButton) findViewById(R.id.accept_button);
        mCancelButton = (ImageButton) findViewById(R.id.cancel_button);
        mTextEdit = (AutoCompleteTextView) findViewById(R.id.text_edit);
        mTextInputLayout = (TextInputLayout) findViewById(R.id.text_edit_layout);

        setTitle(mTitle);
        setText(mSourceText);

        mTextEdit.addTextChangedListener(this);
        mEditButton.setVisibility(isEditable ? VISIBLE : GONE);
        mEditButton.setOnClickListener(this);
        mAcceptButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mEditContainer.setVisibility(GONE);
        mViewContainer.setVisibility(VISIBLE);
    }

    public void setTitle(CharSequence title){
        mTitle = title;
        mTitleView.setVisibility(GONE);
        if(mTitle != null) {
            mTitleView.setVisibility(VISIBLE);
            mTitleView.setText(mTitle);
        }
    }

    public void setEditable(boolean editable){
        isEditable = editable;
        mEditButton.setVisibility(isEditable ? VISIBLE : GONE);
    }

    public void setOnEditCallback(IEditCallback callback){
        mEditCallback = callback;
    }

    public void setText(CharSequence text){
        mSourceText = text;
        if(text == null) text = "";
        mTextEdit.setText(text);
        mTextView.setText(text);
    }

    public CharSequence getText(){
        return mTextView.getText();
    }

    public void setOnErrorMessage(CharSequence msg){
        mErrorInputMessage = msg;
    }

    public void setOnEmptyMessage(CharSequence msg){
        mEmptyInputMessage = msg;
    }

    public void edit(){
        if(isEditable){
            mViewContainer.setVisibility(GONE);
            mEditContainer.setVisibility(VISIBLE);
            mTextEdit.setText(mTextView.getText());
            mTextInputLayout.setError(null);
        }
    }

    public void view(){

        // hide keyboard
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindowToken(), 0);

        mViewContainer.setVisibility(VISIBLE);
        mEditContainer.setVisibility(GONE);
    }

    public void setValidator(IValidator validator){
        mValidator = validator;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_button:
                edit();
                break;
            case R.id.accept_button:
                onAcceptClick();
                break;
            case R.id.cancel_button:
                onCancelClick();
                break;
        }
    }

    private void onCancelClick() {
        view();
    }

    private void onAcceptClick() {

        CharSequence editedText = mTextEdit.getText().toString();
        CharSequence sourceText = mTextView.getText().toString();
        if(sourceText.equals(editedText)){
            view();
        }
        else if(mValidator != null){
            if(mValidator.validate(this, editedText)) {
                mTextView.setText(editedText);
                view();
                if(mEditCallback != null){
                    mEditCallback.onEditFinished(this);
                }
            }
            else {
                if(editedText.length() == 0 && mEmptyInputMessage != null){
                    mTextInputLayout.setError(mEmptyInputMessage);
                }
                else {
                    mTextInputLayout.setError(mErrorInputMessage != null ? mErrorInputMessage : "Invalid Input");
                }
            }
        }
        else if(editedText.length() == 0 && mEmptyInputMessage != null){
            mTextInputLayout.setError(mEmptyInputMessage);
        }
        else {
            mTextView.setText(editedText);
            view();
            if(mEditCallback != null){
                mEditCallback.onEditFinished(this);
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mTextInputLayout.setError(null);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public interface IValidator{
        boolean validate(MyTextInputLayout layout, CharSequence value);
    }

    public interface IEditCallback{
        boolean onEditFinished(MyTextInputLayout layout);
    }
}
