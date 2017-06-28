package com.example.kapils.myexpensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION =6;
    private static final String DATABASE_NAME ="transactions.db";

    public final String TABLE_TRANSACTION ="transactions";
    public final String COLUMN_ID ="_id";
    public final String COLUMN_TITLE ="title";
    public final String COLUMN_DESC ="description";
    public final String COLUMN_TYPE ="type";
    public final String COLUMN_CATEGORY ="category";
    public final String COLUMN_AMOUNT ="amount";
    public final String COLUMN_DATE ="tdate";

    public final String TABLE_CATEGORY ="category";
    public final String COLUMN_CAT_NAME ="catname";
    public final String COLUMN_CAT_ID ="cat_id";


    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_TRANSACTION + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT , "
                + COLUMN_DESC + " TEXT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_CATEGORY + " TEXT, "
                + COLUMN_AMOUNT + " REAL, "
                + COLUMN_DATE + " DATE"
                +"); "

                ;

        db.execSQL(query);

        query = "create table "+TABLE_CATEGORY + "("
                +COLUMN_CAT_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CAT_NAME + " REAL );";

        db.execSQL(query);


        addCategory("Misc",db);
        addCategory("Automobile",db);
        addCategory("Nice",db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CATEGORY);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }


    public boolean addExpense(String title, String desc, String type, float amount, String cat, String date){
        boolean b;

        try {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_DESC, desc);
            values.put(COLUMN_TYPE, type);
            values.put(COLUMN_AMOUNT, amount);
            values.put(COLUMN_CATEGORY, cat);
            values.put(COLUMN_DATE, date);

            db.insert(TABLE_TRANSACTION, null, values);
            db.close();
            b=true;
        }catch (Exception e) {
            b = false;
        }

        return b;
    }

    public Cursor getAllExpense(){
        String query = "SELECT * FROM "+TABLE_TRANSACTION;

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(query, null);
    }

    public Cursor getQueryExpense(String s){
        String query = "SELECT * FROM "+TABLE_TRANSACTION+" "+s+" order by (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2)) desc";

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(query, null);
    }

    public boolean updateExpense(int searchid,String title, String desc, float amount, String cat, String date){
        boolean b;

        try {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_DESC, desc);
            values.put(COLUMN_AMOUNT, amount);
            values.put(COLUMN_CATEGORY, cat);
            values.put(COLUMN_DATE, date);

            db.update(TABLE_TRANSACTION,values,COLUMN_ID+"="+searchid,null);
            db.close();
            b=true;
        }catch (Exception e) {
            b = false;
        }

        return b;
    }

    public boolean deleteExpense(int itemid){
        boolean check = false;

        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_TRANSACTION, COLUMN_ID + "=" + itemid, null);
            check = true;
        }catch (Exception e){
            check = false;
        }

        return check;
    }

    public Cursor getAllCategories(){
        String query = "SELECT "+COLUMN_CAT_ID+" as _id,"+COLUMN_CAT_NAME+" FROM "+TABLE_CATEGORY;

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(query, null);

        return c;
    }

    public void addCategory(String cat, SQLiteDatabase db){
        if(db==null){
            db = getWritableDatabase();
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAT_NAME,cat);
        db.insert(TABLE_CATEGORY,null,values);
    }

    public boolean deleteCategory(String s){
        boolean check = false;

        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_TRANSACTION, COLUMN_CATEGORY+"='" + s+"'", null);
            db.delete(TABLE_CATEGORY, COLUMN_CAT_NAME + "='" + s+"'", null);
            check = true;
        }catch (Exception e){
            check = false;
        }

        return check;
    }
}
