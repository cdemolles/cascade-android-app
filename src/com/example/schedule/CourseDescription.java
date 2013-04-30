package com.example.schedule;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CourseDescription extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_description);
		
		String                    groupId;
		String                    courseTitle;
		List<Map<String, String>> courses;
		
		Intent intent = getIntent();
		groupId = intent.getStringExtra("group_id");
		courseTitle = intent.getStringExtra("title");
		
		DatabaseHandler db = new DatabaseHandler(this);
		courses = db.getCoursesForGroupId(groupId);
		db.close();
		
		String department = courses.get(0).get("department");
		String number     = courses.get(0).get("number");
		
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(courseTitle);
		
		TextView courseDescription = (TextView) findViewById(R.id.departmentCourse);
		courseDescription.setText(department + " " + number);
		
		ListView courseList = (ListView) findViewById(R.id.courseList);
		
		SimpleAdapter adapter = new SimpleAdapter(this, courses, R.layout.multi_lines, new String[] {"print_type", "crn", "print_days", "professor"}, new int[] {R.id.type, R.id.crn, R.id.days, R.id.professor});
		courseList.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course_description, menu);
		return true;
	}

}
