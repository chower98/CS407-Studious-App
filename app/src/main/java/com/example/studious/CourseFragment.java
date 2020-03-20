package com.example.studious;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class CourseFragment extends DialogFragment {
    View view;
    Button delete;

    private DBHelper dbHelper;
    private final static String DATABASE_NAME = "data";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_course, container, false);

        // get the reference of Button
        delete = (Button) view.findViewById(R.id.delete);

        // perform setOnClickListener on delete button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: implement delete
                Context context = getApplicationContext();
                SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
                dbHelper = new DBHelper(sqLiteDatabase);
            }
        });
        return view;
    }
}
