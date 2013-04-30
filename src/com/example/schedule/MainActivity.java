package com.example.schedule;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity
{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        String[] options = new String[] {"Browse All Courses", "Browse Courses by Department"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
        setListAdapter(adapter);
        
        DatabaseHandler db = new DatabaseHandler(this);
        
        long lastUpdated = db.getLastUpdated();
        long today = new Date().getTime();
        long difference = today - lastUpdated;
        
        if (difference / (60 * 60 * 1000) >= 24)
        {
        	DownloadFile downloadFile = new DownloadFile();
            downloadFile.execute("http://apollo.wheatoncollege.edu/schedule/schedule.tsv");
        }
        
    }
    
    protected void onListItemClick(ListView listView, View view, int position, long id)
	{
    	
		super.onListItemClick(listView, view, position, id);
		
		if (position == 0)
		{
			Intent intent = new Intent(this, MyListActivity.class);
			startActivity(intent);
		}
		else if (position == 1)
		{
			Intent intent = new Intent(this, DepartmentList.class);
			startActivity(intent);
		}
		
	}
    
    private class DownloadFile extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... sUrl)
        {
        	
        	boolean successful = false;
        	
            try
            {
            	
                URL           url        = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                
                connection.connect();
                
                DatabaseHandler db     = new DatabaseHandler(MainActivity.this);
                InputStream     input  = new BufferedInputStream(url.openStream());
                BufferedReader  reader = new BufferedReader(new InputStreamReader(input));
                
                try
                {
                	
                	List<ContentValues> fileData = new ArrayList<ContentValues>();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                    	
                    	 String[]      row = line.split("\t");
                    	 ContentValues data = new ContentValues();
                    	 
                         data.put("id", row[0]);
                         data.put("group_id", row[1]);
                         data.put("semester", row[2]);
                         data.put("year", row[3]);
                         data.put("crn", row[4]);
                         data.put("department", row[5]);
                         data.put("number", row[6]);
                         data.put("title", row[7]);
                         data.put("type", row[8]);
                         data.put("days", row[9]);
                         data.put("begin_time", row[10]);
                         data.put("end_time", row[11]);
                         data.put("building", row[12]);
                         data.put("room", row[13]);
                         data.put("professor", row[14]);
                         data.put("max_enroll", row[15]);
                         data.put("seats_taken", row[16]);
                         
                         fileData.add(data);
                         
                    }
                    
                    successful = db.updateDatabase(fileData);
                    
                    if (successful)
                    {
                    	db.addDatabaseLastUpdated();
                    }
                    
                }
                catch (IOException ex)
                {
                    // handle exception
                }
                catch (SQLiteException ex)
                {
                	
                }
                finally 
                {
                	db.close();
                    input.close();
                }
                
            }
            catch (ConnectException e)
            {
            	
            }
            catch (Exception e) 
            {
            	e.printStackTrace();
            }
            
            return successful;
            
        }
        
        @Override
        protected void onPostExecute(Boolean result)
        {
        	if (result.booleanValue() == true)
        	{
        		Toast.makeText(MainActivity.this, "Finished populating database", Toast.LENGTH_LONG).show();
        	}
        	else
        	{
        		Toast.makeText(MainActivity.this, "Could not populate database", Toast.LENGTH_LONG).show();
        	}
        }
        
    }
    
    
	
}
