package t.a.m.com.doch1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import t.a.m.com.doch1.common.connection.UserUtil;
import t.a.m.com.doch1.common.validators.EmailValidator;
import t.a.m.com.doch1.common.validators.PasswordValidator;
import t.a.m.com.doch1.management.GroupManagementActivity;

public class LoginActivity extends Activity implements View.OnClickListener, FirebaseAuth.AuthStateListener, OnFailureListener, OnSuccessListener<AuthResult> {

    private Button btnSignIn;
    private TextView btnForgotPassword;
    private EmailValidator mEmailValidator;
    private PasswordValidator mPassValidator;
    private ProgressDialog mLoginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailValidator = new EmailValidator((TextInputLayout) findViewById(R.id.email_layout));
        mPassValidator = new PasswordValidator((TextInputLayout) findViewById(R.id.password_layout), 4);

        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(this);

        btnForgotPassword = (TextView) findViewById(R.id.btn_forgot_password);
        btnForgotPassword.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_sign_in : checkLogin(view); break;
            case R.id.btn_forgot_password : forgotPassword(); break;
        }
    }

    public void checkLogin(View arg0) {

        // hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);

        // validate
        if(mEmailValidator.validate() && mPassValidator.validate()){
            mLoginDialog = ProgressDialog.show(this, "", "Login...");
            FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmailValidator.getValue(), mPassValidator.getValue())
                    .addOnFailureListener(this)
                    .addOnSuccessListener(this);
        }
    }

    private void forgotPassword() {
        if (mEmailValidator.validate()) {
            final ProgressDialog dialog = ProgressDialog.show(this, "Reset Password", String.format("send password reset email for %s.", mEmailValidator.getValue()));
            FirebaseAuth.getInstance().sendPasswordResetEmail(mEmailValidator.getValue())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "password reset email sent successfully.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong email address.", Toast.LENGTH_LONG).show();
                            }
                            dialog.dismiss();
                        }
                    });

        }

    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() != null){
            onSignin();
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        dismissLoginDialog();
        Snackbar.make(btnSignIn, this.getResources().getString(R.string.invalid_sigin), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(AuthResult authResult) {
        dismissLoginDialog();
        onSignin();
    }

    private void dismissLoginDialog(){
        if(mLoginDialog != null){
            mLoginDialog.dismiss();
            mLoginDialog = null;
        }
    }

    private void onSignin(){
        UserUtil.init();
        LoginActivity.this.startActivity(new Intent(this, DrawerActivity.class));
        //LoginActivity.this.startActivity(new Intent(this, GroupManagementActivity.class));
        finish();
    }
}
