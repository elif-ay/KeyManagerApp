package com.gtu.keymanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.w3c.dom.Text;

import java.util.List;

public class ActivityHelper extends Activity {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);


        list = findViewById(R.id.listView);

        KeyDatabaseHelper databaseHelper = new KeyDatabaseHelper(this);

        List<KeyModel> allRecords = databaseHelper.getAllRecords();

        ArrayAdapter<KeyModel> itemsAdapter = new ArrayAdapter<KeyModel>(this, android.R.layout.simple_list_item_1, allRecords);

        list.setAdapter(itemsAdapter);


    }
}
