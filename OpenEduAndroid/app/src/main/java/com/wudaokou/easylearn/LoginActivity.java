package com.wudaokou.easylearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.retrofit.BackendService;
import com.wudaokou.easylearn.retrofit.CheckUsernameReturn;
import com.wudaokou.easylearn.retrofit.LoginParam;
import com.wudaokou.easylearn.retrofit.LoginReturn;

import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    TextInputEditText usernameInput;
    TextInputLayout usernameLayout;
    TextInputEditText passwordInput;
    TextInputLayout passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        usernameInput = findViewById(R.id.usernameInput);
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordInput = findViewById(R.id.passwordInput);
        passwordLayout = findViewById(R.id.passwordLayout);

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
            if(username.isEmpty())
                usernameLayout.setError("请输入用户名");
        });
        usernameInput.setOnClickListener(v -> usernameLayout.setError(null));
        passwordInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) passwordLayout.setError(null);
        });
        passwordInput.setOnClickListener(v -> passwordLayout.setError(null));
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void loginButtonOnClick(View view){
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

        String hashedPassword = hashPassword(password);

        new Retrofit.Builder().baseUrl(Constant.backendBaseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(BackendService.class)
                .userLogin(new LoginParam(username, hashedPassword))
                .enqueue(new Callback<LoginReturn>() {
            @Override
            public void onResponse(@NotNull Call<LoginReturn> call, @NotNull Response<LoginReturn> response) {
                int code = response.code();
                if(code == 406){
                    passwordLayout.setError("密码错误");
                    return;
                }
                if(code == 404){
                    usernameLayout.setError("用户不存在");
                    return;
                }
                LoginReturn rsp = response.body();
                if(rsp == null){
                    Toast.makeText(LoginActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                String token = rsp.getToken();
                // 保存
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", "Bearer " + token);
                editor.putString("username", username);
                editor.apply();

                Constant.backendToken = token;

                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                LoginActivity.this.finish();
            }

            @Override
            public void onFailure(@NotNull Call<LoginReturn> call, @NotNull Throwable t) { }
        });


    }

    public void registerButtonOnClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public static String hashPassword(String password){
        // iteration times
        final int ITERATION_COUNT = 8192;
        // number of bits
        final int KEY_LENGTH = 128;
        // number of bytes
        final int HASH_BYTE_ARRAY_LENGTH = KEY_LENGTH / 8;
        // one byte corresponds to two hexadecimal digits
        final int HEX_DIGITS_LENGTH = KEY_LENGTH / 4;

        String salt = "fifthLearn";
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        byte[] hash = null;
        try {
            hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        assert hash != null;

        // convert byte array to hex string
        char[] hexDigits = new char[HEX_DIGITS_LENGTH];
        for(int i = 0; i < HASH_BYTE_ARRAY_LENGTH; i++){
            hexDigits[2*i] = Character.forDigit((hash[i]>>4) & 0xF, 16);
            hexDigits[2*i+1] = Character.forDigit(hash[i] & 0xF, 16);
        }
        return new String(hexDigits);
    }
}