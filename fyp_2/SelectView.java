package com.example.simon.fyp_2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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

public class SelectView extends AppCompatActivity {

    String username, dataSel, timeSel, PHP_URL, xAxisLabel;
    int totalExpFlag, dateFormat;
    Context cont = this;
    Button indiv;
    String myJSON;
    ArrayList<String> dateArr, expenseArr;
    ArrayList<Float> expenditureArr;

    private static final String TAG_RESULTS = "user_data";
    private static final String TAG_EXPENDITURE = "expenditure";
    private static final String TAG_DATE = "date";
    private static final String TAG_EXPENSE = "expense";

    JSONArray expensesJSON = null;

    Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = getIntent().getStringExtra("username");

        okBtn = (Button) findViewById(R.id.okayBtn);
        indiv = (Button) findViewById(R.id.indBtn);

        // Spinners and their listeners
        Spinner dataSpn = (Spinner) findViewById(R.id.dataSpn);
        dataSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataSel = parent.getSelectedItem().toString();
                if (timeSel != null)
                    getSelections();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(SelectView.this, "Please select an expense", Toast.LENGTH_LONG).show();
            }
        });

        Spinner timeSpn = (Spinner) findViewById(R.id.timeSpn);
        timeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeSel = parent.getSelectedItem().toString();
                if (dataSel!=null)
                    getSelections();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void onClickOk(View v){
        getSelections();
        BackGround b = new BackGround();
        b.execute(username);
    }

    public void onClickIndiv(View v){
        PHP_URL = "http://expensesapp.000webhostapp.com/Individual.php";
        totalExpFlag = 0;
        dateFormat = 1;
        BackGround b = new BackGround();
        b.execute(username);
    }

    public void getSelections(){
        // switch-case to get PHP needed to obtain data from database
        switch (dataSel){
            case "Total expenses":
                switch (timeSel){
                    case "Day": PHP_URL = "http://expensesapp.000webhostapp.com/DayTotal.php";
                                totalExpFlag = 1;
                                xAxisLabel = "Date: Month-Day";
                                dateFormat = 1;
                        break;
                    case "Week": PHP_URL = "http://expensesapp.000webhostapp.com/WeekTotal.php";
                                totalExpFlag = 1;
                                xAxisLabel = "Date: Year/Week of year";
                                dateFormat = 0;
                        break;
                    case "Month": PHP_URL = "http://expensesapp.000webhostapp.com/MonthTotal.php";
                                totalExpFlag = 1;
                                xAxisLabel = "Date: Year/Month of year";
                                dateFormat = 0;
                        break;
                }
                break;
            case "Expenses by category":
                switch (timeSel){
                    case "Day": PHP_URL = "http://expensesapp.000webhostapp.com/DayCategory.php";
                                totalExpFlag = 0;
                                xAxisLabel = "Date: Month-Day";
                                dateFormat = 1;
                        break;
                    case "Week": PHP_URL = "http://expensesapp.000webhostapp.com/WeekCategory.php";
                                totalExpFlag = 0;
                                xAxisLabel = "Date: Year/Week of year";
                                dateFormat = 0;
                        break;
                    case "Month": PHP_URL = "http://expensesapp.000webhostapp.com/MonthCategory.php";
                                totalExpFlag = 0;
                                xAxisLabel = "Date: Year/Month of year";
                                dateFormat = 0;
                        break;
                }
                break;
        }
    }

    public void goToChart(){
        Intent View = new Intent(cont, ViewExpenditure.class);
        View.putStringArrayListExtra("expenseArr", expenseArr);
        View.putStringArrayListExtra("dateArr", dateArr);
        View.putExtra("expenditureArr", expenditureArr);
        View.putExtra("totalExpFlag", totalExpFlag);
        View.putExtra("xAxisLabel", xAxisLabel);
        View.putExtra("dateFormat", dateFormat);
        startActivity(View);
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
                expensesJSON = jsonObj.getJSONArray(TAG_RESULTS);

                dateArr = new ArrayList<>();
                expenseArr = new ArrayList<>();
                expenditureArr = new ArrayList<>();
                for (int i = 0; i < expensesJSON.length(); i++) {
                    JSONObject c = expensesJSON.getJSONObject(i);
                    dateArr.add(c.getString(TAG_DATE));
                    expenseArr.add(c.getString(TAG_EXPENSE));
                    expenditureArr.add(Float.parseFloat(c.getString(TAG_EXPENDITURE)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            goToChart();
        }
    }

}
