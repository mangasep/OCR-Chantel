package com.example.barnaclebot.chantel.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.barnaclebot.chantel.R;
import com.example.barnaclebot.chantel.util.SharedPrefManager;
import com.example.barnaclebot.chantel.util.api.BaseApiService;
import com.example.barnaclebot.chantel.util.api.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_username) EditText etUsername;
    @BindView(R.id.et_password) EditText etPassword;
    @BindView(R.id.btnLogin) Button btnLogin;

    ProgressDialog loading;
    Context mContext;
    BaseApiService mApiService;
    SharedPrefManager sharedPrefManager;
    String client_id, client_secret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        client_id = "KEMGQCSNVGAVZJYVAMVYZFPABMOQHJCR";
        client_secret = "5159315585c0f6e4724bf57035638298";

        ButterKnife.bind(this);
        mContext = this;
        mApiService = UtilsApi.getAPIService(); //init api util
        sharedPrefManager = new SharedPrefManager(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
                requestLogin();
            }
        });

        //cek session
        if (sharedPrefManager.getSPSudahLogin()){
            startActivity(new Intent(LoginActivity.this, CameraActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    private void requestLogin(){
        mApiService.loginRequest(client_id,client_secret, etUsername.getText().toString(), etPassword.getText().toString()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            loading.dismiss();
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("status").equals("200")){
                                    // response from API
                                    Toast.makeText(mContext, "Successful Login", Toast.LENGTH_SHORT).show();
                                    String token = jsonRESULTS.getJSONObject("data").getString("access_token");
                                    sharedPrefManager.saveSPString(SharedPrefManager.USERNAME,etUsername.getText().toString());
                                    sharedPrefManager.saveSPString(SharedPrefManager.TOKEN, token);
                                    // trigger session login
                                    sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, true);
                                    startActivity(new Intent(mContext, CameraActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                } else if(jsonRESULTS.getString("status").equals("500")){
                                   // String error_message = jsonRESULTS.getString("error_msg");
                                    Toast.makeText(mContext, "username or password inncorect", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        loading.dismiss();
                        Toast.makeText(mContext, "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
