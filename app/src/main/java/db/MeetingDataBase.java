package db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import dao.MeetingDao;
import table.Meeting;

@Database(entities = {Meeting.class}, version = 4)
public abstract class MeetingDataBase extends RoomDatabase {
    private static MeetingDataBase instance;
    private static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    public static synchronized MeetingDataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MeetingDataBase.class, "meeting_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    public abstract MeetingDao meetingDao();

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private MeetingDao meetingDao;

        private PopulateDbAsyncTask(MeetingDataBase db) {
            meetingDao = db.meetingDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}
