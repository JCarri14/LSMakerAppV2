package com.salle.projects.lsmakerappv2.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.salle.projects.lsmakerappv2.database.dao.BtDeviceDao;
import com.salle.projects.lsmakerappv2.model.BtDevice;

import java.util.List;

@Database(entities = {BtDevice.class}, version = 1, exportSchema = false)
public abstract class LSMakerDatabase extends RoomDatabase {

    private static LSMakerDatabase instance;
    private static Object mutex = new Object();

    public abstract BtDeviceDao deviceDao();

    public static LSMakerDatabase getInstance(Context context) {
        LSMakerDatabase result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    result = Room.databaseBuilder(context.getApplicationContext(),
                            LSMakerDatabase.class, "lsmaker_db")
                            .fallbackToDestructiveMigration()
                            .build();
                    instance = result;
                }
            }
        }
        return result;
    }
    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {

        private BtDeviceDao deviceDao;

        private PopulateDBAsyncTask(LSMakerDatabase db) {
            deviceDao = db.deviceDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            deviceDao.insert(new BtDevice("DeviceTest", "34t45f4", 14));
            return null;
        }
    }
}
