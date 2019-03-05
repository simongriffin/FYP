package com.example.simon.fyp_2;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class InputExpenditure extends AppCompatActivity {


    EditText expenditure;
    String Expenditure, username, formattedDate, date, expense;
    Date today;
    Context cntx = this;
    String lng, lat;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_expenditure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner s = (Spinner) findViewById(R.id.spinner);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expense = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        expenditure = (EditText) findViewById(R.id.expenditure);

        setEdittext();

        username = SaveSharedPreference.getUserName(cntx);
        lat = getIntent().getStringExtra("latitude");
        lng = getIntent().getStringExtra("longitude");
        date = getDate();
    }

    public String getDate(){
        //formattedDate is not getting date assigned to it. Coming up with watches when debugged
        today = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        today = Calendar.getInstance().getTime();
        formattedDate = df.format(today).toString();
        return formattedDate;
    }

    public void onClickSave(View v) {
        Expenditure = expenditure.getText().toString();
        Expenditure = Expenditure.substring(1, (Expenditure.length()));
        try {
            double num = Double.parseDouble(Expenditure);
            if(num==0)
                Toast.makeText(InputExpenditure.this, "Please enter an number", Toast.LENGTH_LONG).show();
            else {
                BackGround b = new BackGround();
                b.execute(username, Expenditure, date, expense, lat, lng);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(InputExpenditure.this, "Please enter an number", Toast.LENGTH_LONG).show();
        }
    }

    public void setEdittext(){
        expenditure.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    expenditure.removeTextChangedListener(this);

                    Locale.setDefault(new Locale("en", "LV"));
                    Currency c  = Currency.getInstance("EUR");
                    System.out.println(c.getSymbol());
                    String symbol = c.getSymbol();
                    String replaceable = String.format("[%s,.\\s]", symbol);
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

                    current = formatted;
                    expenditure.setText(formatted);
                    expenditure.setSelection(formatted.length());
                    expenditure.addTextChangedListener(this);
                }
            }
        });
    }

    class BackGround extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String expenditure = params[1];
            String date = params[2];
            String expense = params[3];
            String latitude = params[4];
            String longitude = params[5];
            String data="";
            int tmp;

            try {
                URL url = new URL("http://expensesapp.000webhostapp.com/saveExpenditure.php");
                String urlParams = "username="+username+"&expenditure="+expenditure+"&date="+date+"&expense="+expense+"&latitude="+latitude+"&longitude="+longitude;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.                setDoOutput(true);
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
            if(s.equals("")){
                s="Data saved successfully.";
            }
            Toast.makeText(cntx, s, Toast.LENGTH_LONG).show();
        }
    }

}
