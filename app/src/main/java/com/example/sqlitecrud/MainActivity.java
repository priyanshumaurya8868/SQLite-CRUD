package com.example.sqlitecrud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

public static final String DATABASE_NAME ="myEmployeesDatabase";
    public static final String TABLE_NAME = "employees";
EditText editTextName;
EditText editTextSalary;
Spinner spinnerDepartment;
TextView textViewViewemployees;

SQLiteDatabase myDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.editText2Name);
        editTextSalary = findViewById(R.id.editText2Salary);
        textViewViewemployees = findViewById(R.id.textViewViewEmployees);
       spinnerDepartment = findViewById(R.id.spinner2Department);

        findViewById(R.id.submitButton).setOnClickListener(this);
        textViewViewemployees.setOnClickListener(this);
        //creating a database
        myDatabase = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        createEmployeeTable();

 }
    //this method will validate the name and salary
    //dept does not need validation as it is a spinner and it cannot be empty
    public boolean inputAreCorrect(String name,String salary){
 boolean res = true;
        if(name.isEmpty())
        {
            editTextName.setError("Please enter a name");
            editTextName.requestFocus();
           res = false;
        }
        if (salary.isEmpty() || Integer.parseInt(salary) <= 0){
            editTextSalary.setError("Please enter salary");
            editTextSalary.requestFocus();
           res = false;
        }
        return res;
    }


    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.submitButton:
             addEmployee();
             break;

         case  R.id.textViewViewEmployees:
             startActivity(new Intent(this, EmployeeListActivity.class));
             break;

     }

   }
    //In this method we will do the create operation
    public void addEmployee() {

        String name = editTextName.getText().toString();
        String salary = editTextSalary.getText().toString();
        String dept = spinnerDepartment.getSelectedItem().toString();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        String joiningDate =  sdf.format(calendar.getTime());

        if(inputAreCorrect(name,salary)){

            String sQLQuery =  "INSERT INTO "+TABLE_NAME+"('name', 'department', 'joiningdate', 'salary') VALUES (?, ?, ?, ?)";
            //using the same method execsql for inserting values

            //first arg is the sql string and second is the parameters that is to be binded with the query
            myDatabase.execSQL(sQLQuery,new String[]{name ,dept ,joiningDate ,salary});
            clearChild();
            Toast.makeText(this,"Employee Added Successfully",Toast.LENGTH_SHORT).show();
        }
    }
    //this method will create the table
    //as we are going to call this method everytime we will launch the application
    //I have added IF NOT EXISTS to the SQL
    //so it will only create the table when the table is not already created
    private  void createEmployeeTable(){


        myDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (\n" +
                      "    id INTEGER NOT NULL CONSTRAINT employees_pk  PRIMARY KEY AUTOINCREMENT,\n" +
                        "    name varchar(200) NOT NULL,\n" +
                        "    department varchar(200) NOT NULL,\n" +
                        "    joiningdate datetime NOT NULL,\n" +
                        "    salary double NOT NULL\n" +
                        ");"
        );


    //Remember we use execSQL() method only for creating table, inserting or updating records. We cannot use it to retrieve values.
}

 private void clearChild(){
        editTextName.setText("");
        editTextSalary.setText("");
 }
}