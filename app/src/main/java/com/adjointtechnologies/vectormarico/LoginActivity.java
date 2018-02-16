package com.adjointtechnologies.vectormarico;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adjointtechnologies.vectormarico.admin.AdminPage;
import com.adjointtechnologies.vectormarico.database.MapperInfoEntity;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    private ReactiveEntityStore<Persistable> data;
    static final String Tag="LoginActivity";
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data= ((ProductApplication)getApplication()).getData();
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEmailView.getText().toString().isEmpty() || mPasswordView.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter credentials",Toast.LENGTH_SHORT).show();
                    return;
                }
                attempOfflineLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attempOfflineLogin(){
        mLoginFormView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
        HashMap<String,String> loginIds=new HashMap<>();
        loginIds.put("super","1199");
        loginIds.put("AUD999","5566");
        loginIds.put("MAR21","2121");
        loginIds.put("MAR22","2222");
        loginIds.put("MAR23","2323");
        loginIds.put("MAR24","2424");
        loginIds.put("MAR25","2525");
        loginIds.put("MAR26","2626");
        loginIds.put("MAR27","2727");
        loginIds.put("MAR28","2828");
        loginIds.put("MAR29","2929");
        loginIds.put("MAR30","3030");
        loginIds.put("MAR31","3131");
        loginIds.put("admin","admin");
        if(!loginIds.containsKey(mEmailView.getText().toString())){
            Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_SHORT).show();
            mLoginFormView.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.GONE);
            return;
        }
        if(loginIds.get(mEmailView.getText().toString()).contentEquals(mPasswordView.getText().toString())){
            MapperInfoEntity entity=new MapperInfoEntity();
            entity.setAuditId(mEmailView.getText().toString());
            data.insert(entity).subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.io())
                    .subscribe(new Consumer<MapperInfoEntity>() {
                        @Override
                        public void accept(@NonNull MapperInfoEntity auditInfoEntity) throws Exception {
                            ((ProductApplication)getApplication()).vector_id=mEmailView.getText().toString();
                            startNextActivity();
                        }});
        }else {
            mLoginFormView.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private void startNextActivity(){
        if(((ProductApplication)getApplication()).vector_id.startsWith("MAR") || ((ProductApplication)getApplication()).vector_id.contentEquals("AUD999")){
            startActivity(new Intent(getApplicationContext(),HomePage.class));
            finish();
        }else if(((ProductApplication)getApplication()).vector_id.contentEquals("admin") || ((ProductApplication)getApplication()).vector_id.contentEquals("super")){
//            TO DO start admin activity
            startActivity(new Intent(getApplicationContext(),AdminPage.class));
            finish();
        }
    }
}

