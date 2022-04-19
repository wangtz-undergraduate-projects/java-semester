package com.wudaokou.easylearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.retrofit.BackendService;
import com.wudaokou.easylearn.retrofit.ChangePassParam;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    TextInputEditText oldPasswordInput;
    TextInputLayout oldPasswordLayout;
    TextInputEditText passwordInput;
    TextInputLayout passwordLayout;
    TextInputEditText confirmPasswordInput;
    TextInputLayout confirmPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        oldPasswordInput = findViewById(R.id.oldPasswordInput);
        oldPasswordLayout = findViewById(R.id.oldPasswordLayout);
        passwordInput = findViewById(R.id.passwordInput);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        setTextLayoutError();
    }

    private void setTextLayoutError() {
        oldPasswordInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                oldPasswordLayout.setError(null);
                return;
            }
            Editable oldPasswordEdit = oldPasswordInput.getText();
            if(oldPasswordEdit == null) return;
            String oldPass = oldPasswordEdit.toString();
            if(oldPass.isEmpty()) {
                oldPasswordLayout.setError("请输入原密码");
            }
        });
        oldPasswordInput.setOnClickListener(v -> oldPasswordLayout.setError(null));
        passwordInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                passwordLayout.setError(null);
                return;
            }
            Editable passwordEdit = passwordInput.getText();
            if(passwordEdit == null) return;
            String password = passwordEdit.toString();
            if(password.isEmpty()){
                passwordLayout.setError("请输入新密码");
            }
        });
        passwordInput.setOnClickListener(v -> passwordLayout.setError(null));
        passwordInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                passwordLayout.setError(null);
                String pass = Objects.requireNonNull(passwordInput.getText()).toString();
                String confirmPass = Objects.requireNonNull(confirmPasswordInput.getText()).toString();
                if(confirmPass.isEmpty() && confirmPasswordLayout.getError() == null) return;
                if(pass.equals(confirmPass)){
                    confirmPasswordLayout.setError(null);
                }else{
                    confirmPasswordLayout.setError("密码不一致");
                }
            }
        });

        confirmPasswordInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(Objects.requireNonNull(passwordInput.getText()).toString())){
                    confirmPasswordLayout.setError(null);
                }else{
                    confirmPasswordLayout.setError("密码不一致");
                }
            }
        });


    }

    public void changePassword(View view) {
        if(confirmPasswordLayout.getError() != null) return;
        Editable oldPassEdit = oldPasswordInput.getText();
        if(oldPassEdit == null) return;
        String oldPass = oldPassEdit.toString();
        if(oldPass.isEmpty()){
            oldPasswordLayout.setError("请输入原密码");
            return;
        }
        Editable passwordEdit = passwordInput.getText();
        if(passwordEdit == null) return;
        String password = passwordEdit.toString();
        if(password.isEmpty()) {
            passwordLayout.setError("请输入新密码");
            return;
        }

        String username = sharedPreferences.getString("username", "");
        String hashedOldPass = LoginActivity.hashPassword(oldPass);
        String hashedPassword = LoginActivity.hashPassword(password);



        new Retrofit.Builder().baseUrl(Constant.backendBaseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(BackendService.class)
                .changePassword(new ChangePassParam(username, hashedOldPass, hashedPassword))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        int code = response.code();
                        if(code == 406){
                            oldPasswordLayout.setError("密码错误");
                            return;
                        }
                        Toast.makeText(ChangePasswordActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                        ChangePasswordActivity.this.finish();
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) { }
                });

    }
}