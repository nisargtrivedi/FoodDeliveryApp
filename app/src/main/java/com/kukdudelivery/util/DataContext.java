package com.kukdudelivery.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.mobandme.ada.ObjectContext;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.io.File;


public class DataContext extends ObjectContext {

    final static String DATABASE_FOLDER  = "%s/kukdu/";
    final static String DATABASE_NAME    = "kukdu.db";
    final static int    DATABASE_VERSION = 4;

//    public ObjectSet<Areatbl> areatbls;
//    public ObjectSet<CartTbl> cartTbls;


    public SQLiteDatabase database;
    public DataContext(Context pContext) {
      super(pContext, DATABASE_NAME, DATABASE_VERSION);
       // super(pContext, String.format("%s%s", getDataBaseFolder(), DATABASE_NAME), DATABASE_VERSION);
        initializeContext();
        //database=this.getReadableDatabase();
    }
    public SQLiteDatabase getReadDataBase()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return db;
    }

    @Override
    protected void onPopulate(SQLiteDatabase pDatabase, int action) {
        //database=pDatabase;
        try {
            AppLogger.info("On DB Populate:" + action);
        }
        catch (Exception e) {
            ExceptionsHelper.manage(getContext(), e);
        }
    }

    @Override
    protected void onError(Exception pException) {
        ExceptionsHelper.manage(getContext(), pException);
    }

    private void initializeContext() {
        try {
            initializeObjectSets();

            //Enable DataBase Transactions to be used by the Save process.
            this.setUseTransactions(true);

            //Enable the creation of DataBase table indexes.
            this.setUseTableIndexes(true);

            //Enable LazyLoading capabilities.
            //this.useLazyLoading(true);

            //Set a custom encryption algorithm.
            this.setEncryptionAlgorithm("AES");

            //Set a custom encryption master pass phrase.
            this.setMasterEncryptionKey("com.sms.app.items");

            //Initialize ObjectSets instances.
//            initializeObjectSets();

        } catch (Exception e) {
            ExceptionsHelper.manage(e);
        }
    }

    private static String getDataBaseFolder() {
        String folderPath = "";
        try {
            folderPath = String.format(DATABASE_FOLDER, Environment.getExternalStorageDirectory().getAbsolutePath());
            File dbFolder = new File(folderPath);
            if (!dbFolder.exists()) {
                dbFolder.mkdirs();
            }
        } catch (Exception e) {
            ExceptionsHelper.manage(e);
        }
        return folderPath;
    }

    private void initializeObjectSets() throws AdaFrameworkException {
        /*areatbls = new ObjectSet<Areatbl>(Areatbl.class,this);
        cartTbls = new ObjectSet<CartTbl>(CartTbl.class,this);*/

    }
}

