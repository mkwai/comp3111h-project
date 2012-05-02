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
    private static final FriendTimeTable FTimeT = new FriendTimeTable();
    private static final GoogleAddTable GoogleAddT= new GoogleAddTable();
    private static final GoogleUpdateTable GoogleUpdateT= new GoogleUpdateTable();
    private static final GoogleDeleteTable GoogleDeleteT= new GoogleDeleteTable();

    private static final TaskTable TaskT = new TaskTable();
    
    private static interface Table{
    	public String[] getFields();
    	public String getName();
    	public String getCreate();
    }
    
    private static class GoogleAddTable implements Table{
    	public String[] getFields(){
    		return new String[] {"eventID"};
    	}

    	public String getName(){return "GoogleAddTable";}
    	public String getCreate(){
    		return "create table GoogleAddTable "+
    				"(eventID INT);";
    	}
    }
    private static class GoogleUpdateTable implements Table{
    	public String[] getFields(){
    		return new String[] {"eventID"};
    	}

    	public String getName(){return "GoogleUpdateTable";}
    	public String getCreate(){
    		return "create table GoogleUpdateTable "+
    				"(eventID INT);";
    	}
    }
    private static class GoogleDeleteTable implements Table{
    	public String[] getFields(){
    		return new String[] {"eventID"};
    	}

    	public String getName(){return "GoogleDeleteTable";}
    	public String getCreate(){
    		return "create table GoogleDeleteTable "+
    				"(eventID INT);";
    	}
    }

    private static class TimeTable implements Table{
    	public String[] getFields(){
    		return new String[] {"eventID", "title","startDate","endDate", "startTime",
    				"endTime", "private", "location","reminder","milliS", "contact"};
    	}

    	public String getName(){return "TimeTable";}
    	public String getCreate(){
    		return "create table TimeTable "+
    				"(eventID INT, title TEXT, startDate INT, endDate INT, startTime TEXT, endTime TEXT, private INT, location TEXT, reminder TEXT, milliS TEXT, contact TEXT);";
    	}
    }

    private static class TaskTable implements Table{
    	public String[] getFields(){
    		return new String[] {"taskID", "title","deadlineDate","deadlineTime","location", "progress", "reminder","milliS"};
    	}

    	public String getName(){return "TaskTable";}
    	public String getCreate(){
    		return "create table TaskTable "+
    				"(taskID INT, title TEXT, deadlineDate TEXT, deadlineTime TEXT, location TEXT, progress INT, reminder INT, milliS TEXT);";
    	}
    }
    
    
    private static class FriendTable implements Table{
		public String[] getFields() {
			return new String[] {"id","name","lastUpdate", "picLink"};
		}
		public String getName() {return "FriendTable";}
		public String getCreate() {
			return "create table FriendTable "+
					"(id TEXT, name TEXT, lastUpdate TEXT, picLink TEXT);";
		}
    	
    }
    
    private static class FriendTimeTable implements Table{
    	public String[] getFields(){
    		return new String[] {"friendID","title", "startDate", "endDate","startTime", "endTime", "location"};
    	}
    	public String getName(){return "FriendTimeTable";}
    	public String getCreate(){
			return "create table FriendTimeTable "+
					"(friendID TEXT, title TEXT, startDate TEXT, endDate TEXT, startTime TEXT, endTime TEXT, location TEXT);";
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
    	Ts.add(TimeT); Ts.add(FriendT); Ts.add(OtherT); Ts.add(FTimeT);Ts.add(TaskT);
    	Ts.add(GoogleAddT);Ts.add(GoogleUpdateT);Ts.add(GoogleDeleteT);
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
    	String k = id;
    	if(id!=null)
    		k = key+"= '"+id+"' ";
        return mDb.delete(T.getName(), k, null) > 0;
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
    public JSONArray fetchConditional(String tableName, String condition, String order){
    	Table T = getTable(tableName);
    	JSONArray JA = new JSONArray();
    	Cursor mCursor = mDb.query(T.getName(), T.getFields(), condition, null, null, null, order);mCursor.moveToFirst();
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
    // generate 26chars random event id
    public String GiveEventID(){
    	Random x = new Random();
    	String pool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    	String newid = "";
    	for(int i = 0;i<26;i++){
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
    
    public void Query(String cmd, Object[] o){
    	mDb.execSQL(cmd, o);
    }
    
    public int updateConditional(String tableName, String condition, String[] fields, String[] params) {
        Table T = getTable(tableName);
        ContentValues args = new ContentValues();
    	for(int i = 0;i<fields.length;i++){
    		args.put(fields[i], params[i]);
    	}
        return mDb.update(tableName, args, condition, null) ;
    }
}
