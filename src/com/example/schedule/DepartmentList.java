package com.example.schedule;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.ListActivity;
import android.content.Intent;

public class DepartmentList extends ListActivity
{

	String[] departments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		DatabaseHandler db = new DatabaseHandler(this);
		departments = db.getDepartments();
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, departments);
		setListAdapter(adapter);
	}
	
	protected void onListItemClick(ListView listView, View view, int position, long id)
	{
    	
		super.onListItemClick(listView, view, position, id);
		
		String department = departments[position];
		
		Intent intent = new Intent(this, MyListActivity.class);
		intent.putExtra("department", department);
		
		startActivity(intent);
		
	}
}
