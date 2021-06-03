package com.example.sqlitecrud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EmployeeListActivity extends AppCompatActivity {

    ListView listView;
    SQLiteDatabase myDatabase;
    List<Employee> employeeList;
    EmployeeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        listView = findViewById(R.id.listViewRecorde);
        employeeList = new ArrayList<>();

        myDatabase = openOrCreateDatabase(MainActivity.DATABASE_NAME,MODE_PRIVATE,null);

        showEmployeesFromDatabase();

    }

    private void showEmployeesFromDatabase() {
        Cursor cursorEmployee = myDatabase.rawQuery("SELECT * FROM "+MainActivity.TABLE_NAME,
                null);

        if (cursorEmployee.moveToFirst()) {
            do {
                employeeList.add(new Employee(
                        cursorEmployee.getInt(0),
                        cursorEmployee.getString(1),
                        cursorEmployee.getString(2),
                        cursorEmployee.getString(3),
                        cursorEmployee.getDouble(4)));

            } while (cursorEmployee.moveToNext());
             cursorEmployee.close();
            adapter = new EmployeeAdapter(this, R.layout.list_layout_employee,
                    employeeList, myDatabase);

            listView.setAdapter(adapter);

        }
    }

}