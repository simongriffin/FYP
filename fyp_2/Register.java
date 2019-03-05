package com.example.simon.fyp_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Register extends Activity {

    EditText username, password, email;
    String Username, Password, Email;
    Context ctx=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_register);
        username = (EditText) findViewById(R.id.register_name);
        password = (EditText) findViewById(R.id.register_password);
        email = (EditText) findViewById(R.id.register_email);
    }

    public void onClickRegister(View v){
        Username = username.getText().toString();
        Password = password.getText().toString();
        Email = email.getText().toString();
        // If any of the above field are empty, ask the user to fill out the field
        if(Username.isEmpty() || Password.isEmpty() || Email.isEmpty())
            Toast.makeText(ctx, "Please enter a username, password and email", Toast.LENGTH_LONG).show();
        else {
            BackGround b = new BackGround();
            b.execute(Username, Password, Email);
        }
    }

    class BackGround extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            String data="";
            int tmp;

            try {
                URL url = new URL("http://expensesapp.000webhostapp.com/register.php");
                String urlParams = "username="+username+"&password="+password+"&email="+email;

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

        // need to change this code so that it takes into account a user choosing a name which has already been taken
        // Get JSONObject, if it's null, allow user to contiue to homepage, if not, do nothing
        @Override
        protected void onPostExecute(String s) {
            if(s.equals("")) {
                s = "Data saved successfully.";
                Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();

                Intent Home = new Intent(ctx, Home.class);
                Home.putExtra("username", username.getText().toString());
                Home.putExtra("password", password.getText().toString());
                Home.putExtra("email", email.getText().toString());
                SaveSharedPreference.setUserName(ctx, Username);
                startActivity(Home);
            } else {
                String error = "Username already chosen. Please chose another.";
                Toast.makeText(ctx, error, Toast.LENGTH_LONG).show();
            }


        }
    }

}