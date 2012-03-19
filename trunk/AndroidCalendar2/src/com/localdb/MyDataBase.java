package com.localdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDataBase {

    private static final String DATABASE_NAME = "Time.db";
    private static final int DATABASE_VERSION = 2;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;
    
    private static final List<Table> Ts = new ArrayList<Table>();
    private static final TimeTable TimeT = new TimeTable();
    private static final FriendTable FriendT = new FriendTable();
    private static final OtherTable OtherT = new OtherTable();
    
    
    private static interface Table{
    	public String[] getFields();
    	public String getName();
    	public String getCreate();
    }
    
    private static class TimeTable implements Table{
    	public String[] getFields(){
    		return new String[] {"eventID", "title","startDate","endDate", "startTime",
    				"endTime", "private", "location","reminder"};
    	}

    	public String getName(){return "TimeTable";}
    	public String getCreate(){
    		return "create table TimeTable "+
    				"(eventID INT, title TEXT, startDate INT, endDate INT, startTime TEXT, endTime TEXT, private INT, location TEXT, reminder TEXT);";
    	}
    }
    
    private static class FriendTable implements Table{
		public String[] getFields() {
			return new String[] {"friendID","name","isShare","lastUpdate", "picLink"};
		}
		public String getName() {return "FriendTable";}
		public String getCreate() {
			return "create table FriendTable "+
					"(friendID TEXT, name TEXT, isShare TeXT, lastUpdate TEXT, picLink TEXT);";
		}
    	
    }
    
    private static class OtherTable implements Table{

		public String[] getFields() {
			return new String[] {"key","value"};
		}
		public String getName() {return "OtherTable";}

		public String getCreate() {
			return "create table OtherTable "+
					"(key TEXT, value TEXT)";
		}
    	
    }
    
    

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // **************************************
            // delete database every time for testing
            // **************************************
            context.deleteDatabase(DATABASE_NAME);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	for(Table T: Ts){
        		db.execSQL(T.getCreate());
        	}
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("db", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            for(Table T: Ts){
            	db.execSQL("DROP TABLE IF EXISTS "+ T.getName());
            }
            onCreate(db);
        }
    }


    public MyDataBase(Context ctx) {
    	Ts.add(TimeT); Ts.add(FriendT); Ts.add(OtherT);
        this.mCtx = ctx;      
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    public Table getTable(String tableName){
    	for(int i = 0;i<Ts.size();i++){
    		if(Ts.get(i).getName()==tableName)
    			return Ts.get(i);
    	}
    	return null;
    }
    
    // args is insert values for all fields
    public long insert(String tableName, String[] args) {
        ContentValues Values = new ContentValues();
        
        Table T = getTable(tableName);
        
        for(int i = 0;i<T.getFields().length;i++){
        	Values.put(T.getFields()[i], args[i]);
        }
        
        return mDb.insert(T.getName(), null, Values);
    }

    public boolean delete(String tableName, String id) {

    	Table T = getTable(tableName);
    	String key = T.getFields()[0];
        return mDb.delete(T.getName(), key + "=" + id, null) > 0;
    }

    // fields specify what field of table is used. args specify where cause
    public JSONArray fetchAllNotes(String tableName, String[] fields, String[] args) {
    	Table T = getTable(tableName);
    	JSONArray JA = new JSONArray(); 
    	Cursor mCursor = mDb.query(T.getName(), T.getFields(), whereFields(fields), args, null, null, null);
    	mCursor.moveToFirst();
    	try{
    	while(mCursor.isAfterLast()==false){
    		JSONObject JO = new JSONObject();
    		for(int i = 0;i<T.getFields().length;i++){
    			JO.put(T.getFields()[i], mCursor.getString(i));
    		}
    		JA.put(JO);
    		mCursor.moveToNext();
    	}
    	}catch(Exception e){
    	Log.i("inf",Integer.toString(JA.length()));
    	}
    	return JA;
    }
    
    public JSONArray fetchConditional(String tableName, String condition){
    	Table T = getTable(tableName);
    	JSONArray JA = new JSONArray();
    	Cursor mCursor = mDb.query(T.getName(), T.getFields(), condition, null, null, null, null);mCursor.moveToFirst();
    	try{
    	while(mCursor.isAfterLast()==false){
    		JSONObject JO = new JSONObject();
    		for(int i = 0;i<T.getFields().length;i++){
    			JO.put(T.getFields()[i], mCursor.getString(i));
    		}
    		JA.put(JO);
    		mCursor.moveToNext();
    	}
    	}catch(Exception e){
    	Log.i("inf",Integer.toString(JA.length()));
    	}
    	return JA;
    }
    
    // generate 36chars random event id
    public String GiveEventID(){
    	Random x = new Random();
    	String pool = "abcdefghijklmnopqrstuvwxyz0123456789";
    	String newid = "";
    	for(int i = 0;i<36;i++){
    		newid+=pool.charAt(x.nextInt(36));
    	}
    	JSONArray ja = fetchAllNotes(TimeT.getName(),new String[] {TimeT.getFields()[0]},new String[] {newid});
    	if(ja.length()!=0)
    		newid = GiveEventID();
    	return newid;
    	
    }
    
    public String whereFields(String[] fields){
    	if (fields==null) return null;
    	String output = " ";
    	for(int i = 0;i<fields.length;i++){
    		output += fields[i] + "= ? ";
    		if((i+1)!=fields.length) output += " AND ";
    	}
    	return output;
    }

    public boolean update(String tableName, String id, String[] fields, String[] params) {
        Table T = getTable(tableName);
    	ContentValues args = new ContentValues();
    	for(int i = 0;i<fields.length;i++){
    		args.put(fields[i], params[i]);
    	}
    	
        return mDb.update(tableName, args, T.getFields()[0]+" = "+id, null) > 0;
    }
}
