package com.example.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper
{
	
	private static final int    DATABASE_VERSION   = 1;
	private static final String DATABASE_NAME      = "schedule";
	private static final String COURSES_TABLE      = "courses";
	private static final String UPDATE_LOG_TABLE   = "update_log";
	
	
	//--------------------------------------------------------------------------------
	public DatabaseHandler(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	} // DatabaseHandler()

	
	//--------------------------------------------------------------------------------
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		
		String createCourseTable;
		String createUpdateLogTable;
		
		createCourseTable = "CREATE TABLE " + COURSES_TABLE + " " +
							"( " +
							"   id          TEXT  NOT NULL, " +
							"   group_id    TEXT  NOT NULL, " +
							"   semester    TEXT  NOT NULL, " +
							"   year        INT   NOT NULL, " +
							"   crn         INT   NOT NULL, " +
							"   department  TEXT  NOT NULL, " +
							"   number      INT   NOT NULL, " +
							"   title       TEXT  NOT NULL, " +
							"   type        TEXT  NOT NULL, " +
							"   days        TEXT  NOT NULL, " +
							"   begin_time  TEXT  NOT NULL, " +
							"   end_time    TEXT  NULL, " +
							"   building    TEXT  NULL, " +
							"   room        TEXT  NULL, " +
							"   professor   TEXT  NULL, " +
							"   max_enroll  INT   NULL, " +
							"   seats_taken INT   NULL, " +
							"   PRIMARY KEY (semester, year, crn, days, begin_time) " +
							")";
		
		createUpdateLogTable = "CREATE TABLE " + UPDATE_LOG_TABLE + " " +
							   "( " +
							   "   last_updated  INT NOT NULL, " +
							   "   PRIMARY KEY (last_updated) " +
							   ")";
		
		db.execSQL(createCourseTable);
		db.execSQL(createUpdateLogTable);
		
	} // onCreate()

	
	//--------------------------------------------------------------------------------
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) 
	{
				            
	} // onUpgrade()
	
	
	//--------------------------------------------------------------------------------
	public List<Map<String, String>> getUniqueCourses(String department)
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor;
		
		if (department != null)
		{
			cursor = db.query(true, COURSES_TABLE, new String[] {"title", "department"}, "department = ?", new String[] {department}, null, null, "title", null, null);
		}
		else
		{
			cursor = db.query(true, COURSES_TABLE, new String[] {"title", "department"}, null, null, null, null, "title", null, null);
		}

		List<Map<String, String>> courseTitles = new ArrayList<Map<String, String>>(cursor.getCount());
		
		while (cursor.moveToNext())
		{
			Map<String, String> data = new HashMap<String, String>(cursor.getColumnCount());
			data.put("title", cursor.getString(cursor.getColumnIndex("title")));
			data.put("department", cursor.getString(cursor.getColumnIndex("department")));
			
			courseTitles.add(data);
		}
		
		cursor.close();
		
		return courseTitles;
		
	} // getUniqueCourses()
	
	
	//--------------------------------------------------------------------------------
	public List<Map<String, String>> getCourseSections(String courseTitle, String department)
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor;
		
		cursor = db.query(COURSES_TABLE, new String[] {"group_id", "days", "begin_time"}, "title = ? AND type = ? AND department = ?", new String[] {courseTitle, "course", department}, null, null, null);
		
		List<Map<String, String>> sections = new ArrayList<Map<String, String>>(cursor.getCount());
		
		while (cursor.moveToNext())
		{
			Map<String, String> data = new HashMap<String, String>(cursor.getColumnCount());
			data.put("group_id", cursor.getString(cursor.getColumnIndex("group_id")));
			data.put("days", cursor.getString(cursor.getColumnIndex("days")));
			data.put("begin_time", cursor.getString(cursor.getColumnIndex("begin_time")));
			
			sections.add(data);
		}
		
		cursor.close();
		
		return sections;
		
	} // getCourseSections()
	
	
	//--------------------------------------------------------------------------------
	public List<Map<String, String>> getCoursesForGroupId(String groupId)
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(COURSES_TABLE, new String[] {"crn", "department", "number", "type", "days", "begin_time", "professor"}, "group_id = ?", new String[] {groupId}, null, null, null);
	
		List<Map<String, String>> courses = new ArrayList<Map<String, String>>(cursor.getCount());
		
		while (cursor.moveToNext())
		{
			
			Map<String, String> course = new HashMap<String, String>(cursor.getColumnCount());
			
			course.put("crn", cursor.getString(cursor.getColumnIndex("crn")));
			course.put("department", cursor.getString(cursor.getColumnIndex("department")));
			course.put("number", cursor.getString(cursor.getColumnIndex("number")));
			course.put("type", cursor.getString(cursor.getColumnIndex("type")));
			course.put("days", cursor.getString(cursor.getColumnIndex("days")));
			course.put("begin_time", cursor.getString(cursor.getColumnIndex("begin_time")));
			course.put("professor", cursor.getString(cursor.getColumnIndex("professor")));
			course.put("print_days", cursor.getString(cursor.getColumnIndex("days")) + " " + cursor.getString(cursor.getColumnIndex("begin_time")));
			
			String type = cursor.getString(cursor.getColumnIndex("type"));
			
			if (type.equalsIgnoreCase("course"))
			{
				course.put("print_type", "Course Meeting");
			}
			else if (type.equalsIgnoreCase("lab"))
			{
				course.put("print_type", "Lab Meeting");
			}
			else
			{
				course.put("print_type", type);
			}
			
			courses.add(course);
			
		}
		
		cursor.close();
		
		return courses;
		
	} // getCoursesForGroupId()
	
	
	//--------------------------------------------------------------------------------
	public String[] getDepartments()
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(true, COURSES_TABLE, new String[] {"department"}, null, null, null, null, null, null, null);
		
		String[] departments = new String[cursor.getCount()];
		
		int i = 0;
		while (cursor.moveToNext())
		{
			departments[i] = cursor.getString(cursor.getColumnIndex("department"));
			i++;
		}
		
		cursor.close();
		
		return departments;
		
	} // getDepartments()
	
	
	//--------------------------------------------------------------------------------
	public boolean updateDatabase(List<ContentValues> courses)
	{
		
		boolean updatedSuccessfully = false;
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		// start a new database transaction
		// that is, either do all the following commands or do none of them
		db.beginTransaction();
		
		try
		{
			
			// delete all records from COURSES_TABLE
			db.execSQL("DELETE FROM " + COURSES_TABLE);
			
			// for each 
			for (ContentValues course : courses)
			{
				db.insert(COURSES_TABLE, null, course);
			}
			
			db.setTransactionSuccessful();
			updatedSuccessfully = true;
			
		}
		catch (Exception e)
		{
			updatedSuccessfully = false;
		}
		finally
		{
			db.endTransaction();
		}
		
		return updatedSuccessfully;
		
	} // updateDatabase()
	
	
	public void addDatabaseLastUpdated()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Date dateTime = new Date();
		
		ContentValues values = new ContentValues();
		values.put("last_updated", dateTime.getTime());
		
		db.insert(UPDATE_LOG_TABLE, null, values);
	}
	
	public long getLastUpdated()
	{
		
		long lastUpdated = 0;
		String sql;
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		sql = "SELECT MAX(last_updated) AS last_updated FROM " + UPDATE_LOG_TABLE;
		Cursor cursor = db.rawQuery(sql, null);
		
		cursor.moveToFirst();
		
		lastUpdated = cursor.getLong(cursor.getColumnIndex("last_updated"));
		
		cursor.close();
		
		return lastUpdated;
		
	}
	
}
