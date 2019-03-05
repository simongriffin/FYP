package com.example.simon.fyp_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends Activity {

    EditText usernameET, passwordET;
    String Username, Password;
    Context ctx=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);
        usernameET = (EditText) findViewById(R.id.main_name);
        passwordET = (EditText) findViewById(R.id.main_password);
    }

    // Exit the app if back button is pressed
    @Override
    public void onBackPressed() {
        Intent exit = new Intent(Intent.ACTION_MAIN);
        exit.addCategory(Intent.CATEGORY_HOME);
        startActivity(exit);
    }

    public void onClickRegister(View v){
        Intent register = new Intent(this,Register.class);
        startActivity(register);
    }

    public void onClickLogin(View v){
        Username = usernameET.getText().toString();
        Password = passwordET.getText().toString();
        BackGround b = new BackGround();
        b.execute(Username, Password);
    }

    class BackGround extends AsyncTask<String, String, String> {

        String USERNAME =null; // Attributes of BackGround
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String data="";
            int tmp;

            try {
                // http://expensesapp.000webhostapp.com
                URL url = new URL("http://expensesapp.000webhostapp.com/login.php");
                String urlParams = "username="+username+"&password="+password;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while((tmp=is.read())!=-1){
                    data+= (char)tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject root = new JSONObject(s);
                JSONObject user_data = root.getJSONObject("user_data");
                USERNAME = user_data.getString("username");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // check to see if password and name don't match, SQL query gives back null
            if(USERNAME ==null){
                Toast.makeText(getApplicationContext(), "Incorrect username/password", Toast.LENGTH_LONG).show();
            }
            else {
                Intent Home = new Intent(ctx, Home.class);
                SaveSharedPreference.setUserName(ctx, Username);
                startActivity(Home);
            }

        }
    }
}