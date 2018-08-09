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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    String Name,Email,UserName,Password,ConPassword;
    EditText et_Name,et_Email,et_SignUserName,et_SignPassword,et_SignConPassword;
    TextView tv_SignSignUp;
    private Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        et_Name = (EditText)findViewById(R.id.et_Name);
        et_Email = (EditText)findViewById(R.id.et_Email);
        et_SignUserName = (EditText)findViewById(R.id.et_SignUserName);
        et_SignPassword = (EditText)findViewById(R.id.et_SignPassword);
        et_SignConPassword = (EditText)findViewById(R.id.et_SignConPassword);
        tv_SignSignUp = (TextView)findViewById(R.id.tv_SignSignUp);


        tv_SignSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Name = et_Name.getText().toString();
                Email= et_Email.getText().toString();
                UserName = et_SignUserName.getText().toString();
                Password = et_SignPassword.getText().toString();
                ConPassword = et_SignConPassword.getText().toString();

                String MobilePattern = "[0-9]{10}";
                String PincodePattern = "[0-9]{6}";

                if (Name.equals("")) {
                    et_Name.requestFocus();
                    et_Name.setError("Enter Name");
                }
                else if (Email.equals("")){
                    et_Email.requestFocus();
                    et_Email.setError("Enter Email or Mobile No.");
                }/*else  if(!isValidEmail(Email)){
                    et_Email.requestFocus();
                    et_Email.setError("Enter a Valid Email");
                }*/else if (UserName.equals("")){
                    et_SignUserName.requestFocus();
                    et_SignUserName.setError("Enter Username");

                }else if (Password.equals("")){
                    et_SignPassword.requestFocus();
                    et_SignPassword.setError("Enter Password");

                }else if (ConPassword.equals("")){
                    et_SignConPassword.requestFocus();
                    et_SignConPassword.setError("Enter Password");

                }else if (!Password.equals(ConPassword)){
                    et_SignPassword.requestFocus();
                    et_SignPassword.setError("Password Not Matched");

                }else {

                    loadingDialog = ProgressDialog.show(SignupActivity.this, "Please wait", "Loading...");
                    loadingDialog.show();
                    RegistrationTask();
                }
            }
        });


    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN ="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void disp(String s){
        Toast.makeText(this,s, Toast.LENGTH_SHORT).show();
    }

    public void RegistrationTask(){

        final StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Config.SignUpURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            if (null != result) {
                                JSONObject obj = new JSONObject(result);
                                String Status = obj.getString("status").toString();
                                String message = obj.getString("message").toString();
                                if (Status.equals("true")) {
                                    disp(message);
                                    Intent i=new Intent(SignupActivity.this,MainActivity.class);
                                    startActivity(i);

                                } else {
                                    loadingDialog.dismiss();
                                    disp(""+message);
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
                params.put("name", Name);
                params.put("username", UserName);
                params.put("password", Password);
                params.put("email_mobile", Email);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(true);
        requestQueue.add(stringRequest);

    }
}
