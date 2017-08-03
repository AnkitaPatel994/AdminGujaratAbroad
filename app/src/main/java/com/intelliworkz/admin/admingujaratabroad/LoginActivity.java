package com.intelliworkz.admin.admingujaratabroad;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity{
    EditText txtUsername,txtPassword;
    Button btnLogin;
    TextInputLayout inputLayoutUname,inputLayoutPass;
    TextView tv_swEnglish,tv_swGujarati;
    Locale myLocale;
    public static String str_language_Code="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_login);
        setContentView(R.layout.activity_login);

        inputLayoutUname = (TextInputLayout) findViewById(R.id.input_layout_uname);
        inputLayoutPass = (TextInputLayout) findViewById(R.id.input_layout_pass);

        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        txtUsername.addTextChangedListener(new MyTextWatcher(txtUsername));
        txtPassword.addTextChangedListener(new MyTextWatcher(txtPassword));

        tv_swEnglish = (TextView) findViewById(R.id.tv_sw_english);
        tv_swGujarati = (TextView) findViewById(R.id.tv_sw_gujarati);

        if(str_language_Code.equals("1"))
        {
            tv_swEnglish.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv_swEnglish.setTextColor(getResources().getColor(R.color.colorWhite));
            tv_swGujarati.setBackgroundColor(getResources().getColor(R.color.sw_color));
            tv_swGujarati.setTextColor(getResources().getColor(R.color.colorBlack));
        }
        else if(str_language_Code.equals("2"))
        {
            tv_swGujarati.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv_swGujarati.setTextColor(getResources().getColor(R.color.colorWhite));
            tv_swEnglish.setBackgroundColor(getResources().getColor(R.color.sw_color));
            tv_swEnglish.setTextColor(getResources().getColor(R.color.colorBlack));
        }


        tv_swEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_language_Code = "1";
                changeLanguageDLG();
            }
        });

        tv_swGujarati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_language_Code = "2";
                changeLanguageDLG();
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtUsername.getText().toString().equals("admin") && txtPassword.getText().toString().equals("admin"))
                {
                    Intent i=new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(i);
                    finish();
                }
                else if (!validateUname())
                {
                    return;
                }
                else if (!validatePassword())
                {
                    return;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }

    private boolean validateUname() {

        if (txtUsername.getText().toString().trim().isEmpty())
        {
            inputLayoutUname.setError(getString(R.string.err_msg_uname));
            requestFocus(txtUsername);
            return false;
        }
        else if (!txtUsername.getText().toString().trim().equals("admin"))
        {
            inputLayoutUname.setError(getString(R.string.err_msg_uname));
            requestFocus(txtUsername);
            return false;
        }
        else
        {
            inputLayoutUname.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {

        if (txtPassword.getText().toString().trim().isEmpty())
        {
            inputLayoutPass.setError(getString(R.string.err_msg_pass));
            requestFocus(txtPassword);
            return false;
        }
        else if (!txtPassword.getText().toString().trim().equals("admin"))
        {
            inputLayoutPass.setError(getString(R.string.err_msg_pass));
            requestFocus(txtPassword);
            return false;
        }
        else
        {
            inputLayoutPass.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public class MyTextWatcher implements TextWatcher {

        private View view;

        public MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.txtUsername:
                    validateUname();
                    break;
                case R.id.txtPassword:
                    validatePassword();
                    break;
            }
        }
    }
    private void changeLanguageDLG() {

        if (str_language_Code.equals("1"))
        {
            myLocale = new Locale("en"); //english
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            str_language_Code = "1";
            Intent refresh = new Intent(LoginActivity.this, LoginActivity.class);
            startActivity(refresh);
            finish();
        }
        else
        {
            myLocale = new Locale("guj");  //gujarati
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            str_language_Code = "2";
            Intent refresh = new Intent(LoginActivity.this, LoginActivity.class);
            startActivity(refresh);
            finish();
        }
    }
}
