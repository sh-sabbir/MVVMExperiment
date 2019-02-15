package com.codeian.mvvm;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import static androidx.room.Room.databaseBuilder;

@Database(entities = {Note.class},version = 1)
public abstract class NoteDatabase   extends RoomDatabase {

    private static NoteDatabase instance;

    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context){
        if(instance == null){

            instance = databaseBuilder(context.getApplicationContext(),
                NoteDatabase.class, "note_database")
                .fallbackToDestructiveMigration()
                .addCallback(roomCallback)
                .build();
        }

        return instance;
    }

    private static NoteDatabase.Callback roomCallback = new RoomDatabase.Callback(){

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsynctask(instance).execute();
        }

    };

    private static class PopulateDbAsynctask extends AsyncTask<Void,Void,Void>{
        private NoteDao noteDao;

        private PopulateDbAsynctask(NoteDatabase db){
            noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title","Here Goes Your Note",1));
            return null;
        }
    }
}
