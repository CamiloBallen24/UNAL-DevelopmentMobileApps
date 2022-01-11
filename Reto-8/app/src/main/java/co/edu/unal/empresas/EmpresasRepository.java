package co.edu.unal.empresas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class EmpresasRepository {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EmpresasEntry.TABLE_NAME + " (" +
                    EmpresasEntry._ID + " INTEGER PRIMARY KEY," +
                    EmpresasEntry.COLUMN_NAME_NAME + " TEXT," +
                    EmpresasEntry.COLUMN_NAME_URL + " TEXT," +
                    EmpresasEntry.COLUMN_NAME_PHONE + " TEXT," +
                    EmpresasEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    EmpresasEntry.COLUMN_NAME_SERVICES + " TEXT," +
                    EmpresasEntry.COLUMN_NAME_CLASSIFICATION + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EmpresasEntry.TABLE_NAME;

    private EmpresasReaderDbHelper dbHelper;

    public EmpresasRepository(Context context) {
        this.dbHelper = new EmpresasReaderDbHelper(context);
    }


    public Empresa createEmpresa(Empresa empresa){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EmpresasEntry.COLUMN_NAME_NAME, empresa.getName());
        values.put(EmpresasEntry.COLUMN_NAME_URL, empresa.getUrl());
        values.put(EmpresasEntry.COLUMN_NAME_PHONE, empresa.getPhone());
        values.put(EmpresasEntry.COLUMN_NAME_EMAIL, empresa.getEmail());
        values.put(EmpresasEntry.COLUMN_NAME_SERVICES, empresa.getServices());
        values.put(EmpresasEntry.COLUMN_NAME_CLASSIFICATION, empresa.getClassification());

        long newRowId = db.insert(EmpresasEntry.TABLE_NAME, null, values);
        empresa.setID((int) newRowId);
        return empresa;
    }

    public ArrayList<Empresa> getEmpresas(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
            BaseColumns._ID,
            EmpresasEntry.COLUMN_NAME_NAME,
            EmpresasEntry.COLUMN_NAME_URL,
            EmpresasEntry.COLUMN_NAME_PHONE,
            EmpresasEntry.COLUMN_NAME_EMAIL,
            EmpresasEntry.COLUMN_NAME_SERVICES,
            EmpresasEntry.COLUMN_NAME_CLASSIFICATION
        };

        String sortOrder = EmpresasEntry.COLUMN_NAME_NAME + " DESC";

        Cursor cursor = db.query(
                EmpresasEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );



        ArrayList empresas = new ArrayList<Empresa>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(EmpresasEntry.COLUMN_NAME_NAME));
            String url = cursor.getString(cursor.getColumnIndexOrThrow(EmpresasEntry.COLUMN_NAME_URL));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(EmpresasEntry.COLUMN_NAME_PHONE));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(EmpresasEntry.COLUMN_NAME_EMAIL));
            String services = cursor.getString(cursor.getColumnIndexOrThrow(EmpresasEntry.COLUMN_NAME_SERVICES));
            String classification = cursor.getString(cursor.getColumnIndexOrThrow(EmpresasEntry.COLUMN_NAME_CLASSIFICATION));

            Empresa empresa = new Empresa((int)itemId, name, url, phone, email, services, classification);
            empresas.add(empresa);
        }
        cursor.close();

        return empresas;
    }

    public int deleteEmpresa(Empresa empresa){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = BaseColumns._ID + " LIKE ?";
        String[] selectionArgs = { empresa.getID().toString() };

        int deletedRows = db.delete(EmpresasEntry.TABLE_NAME, selection, selectionArgs);
        return deletedRows;
    }

    public int updateEmpresa(Empresa empresa){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EmpresasEntry.COLUMN_NAME_NAME, empresa.getName());
        values.put(EmpresasEntry.COLUMN_NAME_URL, empresa.getUrl());
        values.put(EmpresasEntry.COLUMN_NAME_PHONE, empresa.getPhone());
        values.put(EmpresasEntry.COLUMN_NAME_EMAIL, empresa.getEmail());
        values.put(EmpresasEntry.COLUMN_NAME_SERVICES, empresa.getServices());
        values.put(EmpresasEntry.COLUMN_NAME_CLASSIFICATION, empresa.getClassification());


        String selection = BaseColumns._ID + " LIKE ?";
        String[] selectionArgs = { empresa.getID().toString() };

        int count = db.update(
                EmpresasEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        return count;
    }

    private static class EmpresasEntry implements BaseColumns {
        public static final String TABLE_NAME = "empresas";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_SERVICES = "services";
        public static final String COLUMN_NAME_CLASSIFICATION = "classification";
    }



    private class EmpresasReaderDbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Empresas.db";

        public EmpresasReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }


    }
}

