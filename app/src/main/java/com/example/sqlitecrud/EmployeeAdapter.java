package com.example.sqlitecrud;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class EmployeeAdapter extends ArrayAdapter<Employee> {
    Context context;
    int layoutResource;
    List<Employee> employeeList;
    SQLiteDatabase myDatabase;


    public EmployeeAdapter(Context context, int resource, List<Employee> employeeList, SQLiteDatabase myDatabase) {
        super(context, resource, employeeList);
        this.context = context;
        this.layoutResource = resource;
        this.employeeList = employeeList;
        this.myDatabase = myDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutResource, null);

        Employee employee = employeeList.get(position);

        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewDepartment = view.findViewById(R.id.textViewDepartment);
        TextView textViewJoiningDate = view.findViewById(R.id.textViewJoiningDate);
        TextView textViewSalary = view.findViewById(R.id.textViewSalary);

        textViewName.setText("Name : " + employee.getName());
        textViewDepartment.setText("Department  : " + employee.getDept());
        textViewJoiningDate.setText("Joining date:  : " + employee.getJoiningDate().toString());
        textViewSalary.setText("Salary : N" + String.valueOf(employee.getSalary()));

        Button buttonDelete = view.findViewById(R.id.buttonDelete);
        Button buttonEdit = view.findViewById(R.id.buttonEdit);

        buttonEdit.setOnClickListener(v -> updateDB(employee));

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you Sure?");

                builder.setNegativeButton("No", new DialogInterface.OnClickListener(
                ) {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(
                ) {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sql = "DELETE FROM " + MainActivity.TABLE_NAME + " WHERE id = ?";
                        myDatabase.execSQL(sql, new Integer[]{employee.getId()});
                        Toast.makeText(context, employee.getName() + "'s details has  been removed", Toast.LENGTH_SHORT).show();
                        reloadDB();
                    }
                });
                //build & show this when its completely ready with all their functions
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return view;
    }

    private void updateDB(final Employee employee) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.update_employee_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        EditText editTextName = view.findViewById(R.id.editText2Name);
        Spinner spinnerDepartment = view.findViewById(R.id.spinner2Department);
        EditText editTextSalary = view.findViewById(R.id.editText2Salary);

        editTextName.setText(employee.getName());
        editTextSalary.setText(String.valueOf(employee.getSalary()));

        Button buttonUpdate = view.findViewById(R.id.submitButton);
        buttonUpdate.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String dept = spinnerDepartment.getSelectedItem().toString();
            String salary = editTextSalary.getText().toString();

            if (name.isEmpty()) {
                editTextName.setError("Name can't be blank");
                editTextName.requestFocus();
                return;
            }

            if (salary.isEmpty()) {
                editTextSalary.setError("Enter proper salary");
                editTextSalary.requestFocus();
                return;
            }


            String sql = "UPDATE " + MainActivity.TABLE_NAME + "\n" +
                    "SET name = ?, \n" +
                    "department = ?, \n" +
                    "salary = ? \n" +  // use ',' only to separate columns. I  mean u cnt use ',' before WHERE
                    "WHERE id = ?;";
            myDatabase.execSQL(sql, new String[]{name, dept, salary, String.valueOf(employee.getId())});

            alertDialog.dismiss();
            reloadDB();
            Toast.makeText(context, "Employee Updated", Toast.LENGTH_SHORT).show();
        });


    }

    private void reloadDB() {

        Cursor cursor = myDatabase.rawQuery("SELECT * FROM " + MainActivity.TABLE_NAME, null);
        employeeList.clear();
        if (cursor.moveToFirst()) {
            do {
                employeeList.add(new Employee(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4)

                ));

            } while (cursor.moveToNext());
        }
        cursor.close();
        notifyDataSetChanged();
    }
}
