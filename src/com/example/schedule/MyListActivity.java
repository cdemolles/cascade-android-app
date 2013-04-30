package com.example.schedule;

import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MyListActivity extends ListActivity
{

	List<Map<String, String>> courses;
	SimpleAdapter adapter;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_course_list);
	    
	    String department;
	    Intent intent = getIntent();
	    
	    if (intent.hasExtra("department"))
	    {
	    	department = intent.getStringExtra("department");
	    }
	    else
	    {
	    	department = null;
	    }
	    
	    DatabaseHandler db = new DatabaseHandler(this);
	    courses = db.getUniqueCourses(department);
	    //db.close();
	    
	    adapter = new SimpleAdapter(this, courses, android.R.layout.simple_list_item_2, new String[] {"title", "department"}, new int[] {android.R.id.text1, android.R.id.text2});
	    setListAdapter(adapter);
	    
	    EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
		
		
		inputSearch.addTextChangedListener(new TextWatcher() {
			 
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                MyListActivity.this.adapter.getFilter().filter(cs);
            }
 
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
 
            }

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
        });
	    
	}
	
	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id)
	{
		super.onListItemClick(listView, view, position, id);
		
		Map<String, String> data = courses.get(position);
		
		Intent intent = new Intent(this, CourseList.class);
		intent.putExtra("title", data.get("title"));
		intent.putExtra("department", data.get("department"));
		
		startActivity(intent);
	}
	
}
