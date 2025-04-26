package com.pedrotejon.ac2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "treino.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "treinos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "nome";
    private static final String COLUMN_TIME = "tempo";

    public BancoHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_TIME + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long inserirTreino(String nome, int tempo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, nome);
        values.put(COLUMN_TIME, tempo);
        return db.insert(TABLE_NAME, null, values);
    }

    public Cursor obterTreinoPorId(int id)
    {
        var cursor = this.listarTreinos();
        if (cursor.moveToFirst()) {
            do {
                int idTreino = cursor.getInt(0);
                if (idTreino == id) {
                    break;
                }
            } while (cursor.moveToNext());
        }
        return cursor;
    }

    public Cursor listarTreinos()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;

        return db.rawQuery(query, null);
    }

    public int atualizarTreino(int id, String nome , int tempo)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(COLUMN_NAME, nome);
        values.put(COLUMN_TIME, tempo);

        return db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int excluirTreino(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
}
