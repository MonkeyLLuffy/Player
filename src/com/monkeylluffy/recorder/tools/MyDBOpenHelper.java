package com.monkeylluffy.recorder.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBOpenHelper extends SQLiteOpenHelper{

	public MyDBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		 db.execSQL("CREATE TABLE songlist(filePath VARCHAR(40) PRIMARY KEY,fileName VARCHAR(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//db.execSQL("ALTER TABLE songlist ADD filetime VARCHAR(12) NULL");
	}
	

}
