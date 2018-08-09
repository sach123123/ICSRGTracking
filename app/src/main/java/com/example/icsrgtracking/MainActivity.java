package com.example.icsrgtracking;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView tv_SignUp,tv_Login;
    private Dialog loadingDialog;
    EditText et_UserName,et_Password;
    UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userSessionManager = new UserSessionManager(getApplicationContext());

        tv_SignUp=(TextView)findViewById(R.id.tv_SignUp);
        tv_Login=(TextView)findViewById(R.id.tv_Login);
        et_UserName = (EditText) findViewById(R.id.et_UserName);
        et_Password = (EditText) findViewById(R.id.et_Password);

        if (userSessionManager.isUserLoggedIn()) {
            Intent i = new Intent(MainActivity.this, MapActivity.class);
            startActivity(i);
        }



        tv_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,SignupActivity.class);
                startActivity(i);
            }
        });


        tv_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String UserName = et_UserName.getText().toString();
                String Password = et_Password.getText().toString();
                String MobilePattern = "[0-9]{10}";
                if (UserName.equals("")) {
                    et_UserName.requestFocus();
                    et_UserName.setError("Please Enter Username");
                } else if (Password.equals("")) {
                    et_Password.requestFocus();
                    et_Password.setError("Please Enter Password");
                } else {
                    loadingDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Loading...");
                    loadingDialog.show();
                    Login(UserName,Password);
                }
            }
        });

    }



    public void Login(final String VUserName, final String VPassword) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Config.LoginURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        loadingDialog.dismiss();
                        try {
                            if (null != result) {
                                JSONObject obj = new JSONObject(result);
                                String Status = obj.getString("status").toString();
                               // JSONObject userData = obj.getJSONObject("data");
                                if (Status.equals("true")) {
                                    JSONObject jsonObject=obj.getJSONObject("data");
                                    String strId=jsonObject.getString("id");
                                    String strName=jsonObject.getString("name");
                                    String strUname = jsonObject.getString("username");
                                    String strEmail=jsonObject.getString("email_mobile");
                                    userSessionManager.createUserLoginSession(strId, strUname, strName, strEmail);

                                    Intent i=new Intent(MainActivity.this,MapActivity.class);
                                    startActivity(i);

                                } else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(MainActivity.this,"Invalid Login Details", Toast.LENGTH_SHORT).show();
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            loadingDialog.dismiss();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dismiss();
                        //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", VUserName);
                params.put("password", VPassword);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(true);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


}
