package com.example.schedule;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class CourseList extends ListActivity 
{

	String                    courseTitle;
	String                    department;
	List<Map<String, String>> courseSections;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		courseTitle = intent.getStringExtra("title");
		department = intent.getStringExtra("department");
		
		DatabaseHandler db = new DatabaseHandler(this);
		courseSections = db.getCourseSections(courseTitle, department);
		db.close();
		
		SimpleAdapter adapter = new SimpleAdapter(this, courseSections, android.R.layout.simple_list_item_2, new String[] {"days", "begin_time"}, new int[] {android.R.id.text1, android.R.id.text2});
		setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id)
	{
		
		super.onListItemClick(listView, view, position, id);
		
		String groupId = courseSections.get(position).get("group_id");
		
		Intent intent = new Intent(this, CourseDescription.class);
		intent.putExtra("title", courseTitle);
		intent.putExtra("group_id", groupId);
		
		startActivity(intent);
		
	}

}
