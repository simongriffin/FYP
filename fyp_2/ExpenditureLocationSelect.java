package com.example.simon.fyp_2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ExpenditureLocationSelect extends AppCompatActivity {

    String PHP_URL;
    Context cont = this;
    Button myLoc, allUsersLoc;

    ArrayList<String> expenseArr, dateArr;
    ArrayList<Double> latArr, longArr;
    ArrayList<Float>expenditureArr;
    String myJSON, username;
    private static final String TAG_RESULTS = "expenditure";
    private static final String TAG_EXPENDITURE = "expenditure";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LONG = "long";
    private static final String TAG_EXPENSE = "expense";
    private static final String TAG_DATE = "date";

    JSONArray locationJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure_location_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = getIntent().getStringExtra("username");

        myLoc = (Button) findViewById(R.id.myLocBtn);
        allUsersLoc = (Button) findViewById(R.id.allUsersLocBtn);
    }

    public void onClickMyLoc(View v){
        PHP_URL = "http://expensesapp.000webhostapp.com/fetchMyLocation.php";
        BackGround b = new BackGround();
        b.execute(username);
    }

    public void onClickAllUsersLoc(View v){
        PHP_URL = "http://expensesapp.000webhostapp.com/fetchLocation.php";
        BackGround b = new BackGround();
        b.execute(username);
    }

    public void goToGoogleMaps(){
        Intent maps = new Intent(cont, ExpenditureLocations.class);
        //i.putStringArrayListExtra("usernameArr", usernameArr);
        maps.putExtra("latArr", latArr);
        maps.putExtra("longArr", longArr);
        maps.putExtra("expenditureArr", expenditureArr);
        maps.putStringArrayListExtra("expenseArr", expenseArr);
        maps.putStringArrayListExtra("dateArr", dateArr);
        startActivity(maps);
    }

    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String data = "";
            int tmp;

            try {
                URL url = new URL(PHP_URL);
                String urlParams = "username=" + username;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while ((tmp = is.read()) != -1) {
                    data += (char) tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            myJSON = s; // this should give array of data (it does)
            try {
                JSONObject jsonObj = new JSONObject(myJSON);
                locationJSON = jsonObj.getJSONArray(TAG_RESULTS);

                //usernameArr = new ArrayList<>();
                latArr = new ArrayList<>();
                longArr = new ArrayList<>();
                expenseArr = new ArrayList<>();
                expenditureArr = new ArrayList<>();
                dateArr = new ArrayList<>();
                for (int i = 0; i < locationJSON.length(); i++) {
                    JSONObject c = locationJSON.getJSONObject(i);
                    latArr.add(c.getDouble(TAG_LAT));
                    longArr.add(c.getDouble(TAG_LONG));
                    expenseArr.add(c.getString(TAG_EXPENSE));
                    dateArr.add(c.getString(TAG_DATE));
                    expenditureArr.add(Float.parseFloat(c.getString(TAG_EXPENDITURE)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            goToGoogleMaps();
        }
    }

}
