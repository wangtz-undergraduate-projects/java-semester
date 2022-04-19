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
import com.wudaokou.easylearn.retrofit.CheckUsernameReturn;
import com.wudaokou.easylearn.retrofit.LoginParam;
import com.wudaokou.easylearn.retrofit.LoginReturn;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    TextInputEditText usernameInput;
    TextInputLayout usernameLayout;
    TextInputEditText passwordInput;
    TextInputLayout passwordLayout;
    TextInputEditText confirmPasswordInput;
    TextInputLayout confirmPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        usernameInput = findViewById(R.id.usernameInput);
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordInput = findViewById(R.id.passwordInput);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        setTextLayoutError();
    }

    private void setTextLayoutError() {
        usernameInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                usernameLayout.setError(null);
                return;
            }
            Editable usernameEdit = usernameInput.getText();
            if(usernameEdit == null) return;
            String username = usernameEdit.toString();
            if(username.isEmpty()) {
                usernameLayout.setError("请输入用户名");
                return;
            }
            if(!Pattern.matches("^[0-9a-zA-Z_]+$", username)) {
                usernameLayout.setError("不符合格式：字母、数字或下划线");
                return;
            }
            new Retrofit.Builder().baseUrl(Constant.backendBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create()).build()
                    .create(BackendService.class)
                    .checkUsername(new LoginParam(username, ""))
                    .enqueue(new Callback<CheckUsernameReturn>() {
                @Override
                public void onResponse(@NotNull Call<CheckUsernameReturn> call, @NotNull Response<CheckUsernameReturn> response) {
                    CheckUsernameReturn rsp = response.body();
                    if(rsp == null) return;
                    if(rsp.isValid()) usernameLayout.setError(null);
                    else usernameLayout.setError("用户名已存在");
                }
                @Override
                public void onFailure(@NotNull Call<CheckUsernameReturn> call, @NotNull Throwable t) {}
            });

        });
        usernameInput.setOnClickListener(v -> usernameLayout.setError(null));
        passwordInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                passwordLayout.setError(null);
                return;
            }
            Editable passwordEdit = passwordInput.getText();
            if(passwordEdit == null) return;
            String password = passwordEdit.toString();
            if(password.isEmpty()){
                passwordLayout.setError("请输入密码");
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

    public void register(View view) {
        if(confirmPasswordLayout.getError() != null) return;
        Editable usernameEdit = usernameInput.getText();
        if(usernameEdit == null) return;
        String username = usernameEdit.toString();
        if(username.isEmpty()){
            usernameLayout.setError("请输入用户名");
            return;
        }
        Editable passwordEdit = passwordInput.getText();
        if(passwordEdit == null) return;
        String password = passwordEdit.toString();
        if(password.isEmpty()) {
            passwordLayout.setError("请输入密码");
            return;
        }

        String hashedPassword = LoginActivity.hashPassword(password);

        new Retrofit.Builder().baseUrl(Constant.backendBaseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(BackendService.class)
                .userRegister(new LoginParam(username, hashedPassword))
                .enqueue(new Callback<LoginReturn>() {
            @Override
            public void onResponse(@NotNull Call<LoginReturn> call, @NotNull Response<LoginReturn> response) {
                int code = response.code();
                if(code == 409){
                    usernameLayout.setError("用户名已存在");
                    return;
                }
                LoginReturn rsp = response.body();
                if(rsp == null){
                    Toast.makeText(RegisterActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                String token = rsp.getToken();
                // 保存
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", "Bearer " + token);
                editor.putString("username", username);
                editor.apply();

                Constant.backendToken = token;

                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

                RegisterActivity.this.finish();
            }

            @Override
            public void onFailure(@NotNull Call<LoginReturn> call, @NotNull Throwable t) { }
        });

    }
}