package com.example.simon.fyp_2;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ViewExpenditure extends AppCompatActivity {

    ArrayList<BarEntry> expenses;
    ArrayList<String> expenseArr, dateArr;
    ArrayList<Float> expenditureArr;
    BarChart barChart;
    int totalExpFlag, dateFormat;
    String xAxisLabel;
    int[] colours;
    BarDataSet expenseDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenditure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        expenseArr = getIntent().getStringArrayListExtra("expenseArr");
        dateArr = getIntent().getStringArrayListExtra("dateArr");
        expenditureArr = (ArrayList<Float>) getIntent().getSerializableExtra("expenditureArr");
        totalExpFlag = getIntent().getIntExtra("totalExpFlag", 1);
        xAxisLabel = getIntent().getStringExtra("xAxisLabel");
        dateFormat = getIntent().getIntExtra("dateFormat", 0);

        // Set chart data if none has been set, i.e. user hasn't logged any expenditure
        if (expenditureArr.isEmpty())
            setValuesForNullChart();

        barChart = (BarChart) findViewById(R.id.bargraph);
        createBarchart();
    }

    public void setValuesForNullChart() {
        expenseArr.add("");
        dateArr.add("            ");
        expenditureArr.add((float) 0);
        totalExpFlag = 1;
        xAxisLabel = "";
        dateFormat = 1;
    }

    public void createBarchart() {
        getColours();
        getChartData();
        setChartData();
        setChartStyle();
        setChartLegend();
        setChartLegend();
    }

    public void getColours() {
        colours = new int[expenseArr.size()];
        if (totalExpFlag != 1) {
            //set colours array based on expense
            for (int i = 0; i < expenseArr.size(); i++) {
                switch (expenseArr.get(i)) {
                    case "Accomodation":
                        colours[i] = Color.RED;
                        break;
                    case "Shopping":
                        colours[i] = Color.rgb(255, 146, 21);
                        break;
                    case "Meals":
                        colours[i] = Color.YELLOW;
                        break;
                    case "Drink":
                        colours[i] = Color.rgb(0, 204, 0);
                        break;
                    case "Transport":
                        colours[i] = Color.BLUE;
                        break;
                    case "Excursions":
                        colours[i] = Color.CYAN;
                        break;
                    case "Other":
                        colours[i] = Color.rgb(255, 0, 255);
                        break;
                }
            }
        }
    }

    public void getChartData() {
        expenses = new ArrayList<>();

        for (int i = 0; i < expenditureArr.size(); i++) {
            expenses.add(new BarEntry(expenditureArr.get(i), i));
        }
        expenseDataSet = new BarDataSet(expenses, "Expenses");
        if (totalExpFlag != 1)
            expenseDataSet.setColors(colours);
        else
            expenseDataSet.setColor(Color.rgb(0, 204, 0));

        for (int i = 0; i < expenditureArr.size(); i++) {
            if (dateFormat == 1) {
                String date = dateArr.get(i).substring(5, 10);
                dateArr.set(i, date);
            }
        }
    }

    public void setChartData() {
        BarData theData = new BarData(dateArr, expenseDataSet);
        barChart.setData(theData);
    }

    public void setChartStyle() {
        // get the median and multiply it by 3 to get the visible range of Y values we'll use to show most information
        double median = median(expenditureArr);
        double YRangeVisible = median * 3;

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setVisibleYRangeMaximum((float) YRangeVisible, YAxis.AxisDependency.LEFT);
        barChart.setVisibleXRangeMaximum(10);
        barChart.getAxisLeft().setAxisMinValue(0);
        barChart.getAxisRight().setAxisMinValue(0);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setTextSize(8f); // set font size to allow all dates to be shown
    }

    public void setChartLegend() {
        // Create legend for barchart
        Legend l = barChart.getLegend();
        l.setFormSize(10f); // set the size of the legend forms/shapes
        l.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setTextSize(12f);
        l.setTextColor(Color.BLACK);
        l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
        l.setYEntrySpace(5f); // set the space between the legend entries on the y-axis
        l.setWordWrapEnabled(true);
        l.setStackSpace(2);

        if (totalExpFlag != 1) {
            // set custom labels and colors
            int[] coloursLegend = {Color.RED, Color.rgb(255, 146, 21), Color.YELLOW, Color.rgb(0, 204, 0), Color.BLUE, Color.CYAN, Color.rgb(255, 0, 255), Color.TRANSPARENT, Color.TRANSPARENT};
            l.setCustom(coloursLegend, new String[]{"Accomodation", "Shopping", "Meals", "Drink", "Transport", "Excursions", "Other", "X-Axis: " + xAxisLabel, "Y-Axis: Money(€)"});
        } else {
            // set custom labels
            int[] coloursLegend = {Color.TRANSPARENT, Color.TRANSPARENT};
            l.setCustom(coloursLegend, new String[]{"X-Axis: " + xAxisLabel, "Y-Axis: Money(€)"});
        }
    }

    public double median(ArrayList<Float> expenditure) {
        Collections.sort(expenditure); // sorts expenditureArr expenditures in ascending order
        int middle = expenditure.size() / 2;

        if (expenditure.size() % 2 == 1) {
            return expenditure.get(middle);
        } else {
            return (expenditure.get(middle - 1) + expenditure.get(middle)) / 2.0;
        }
    }
}

