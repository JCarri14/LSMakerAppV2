package com.salle.projects.lsmakerappv2.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.salle.projects.lsmakerappv2.database.LSMakerDatabase;
import com.salle.projects.lsmakerappv2.database.dao.BtDeviceDao;
import com.salle.projects.lsmakerappv2.model.BtDevice;

import java.util.List;

public class BtDeviceRepository {

    private BtDeviceDao deviceDao;
    private MutableLiveData<List<BtDevice>> allDevices;

    public BtDeviceRepository(Application application) {
        LSMakerDatabase db = LSMakerDatabase.getInstance(application);
        this.deviceDao = db.deviceDao();
        new GetAllDevicesAsyncTask(this.deviceDao).execute();
    }

    public void insert(BtDevice device) {
        new InsertDeviceAsyncTask(this.deviceDao).execute(device);
    }

    public void update(BtDevice device) {
        new UpdateDeviceAsyncTask(this.deviceDao).execute(device);
    }

    public void delete(BtDevice device) {
        new DeleteDeviceAsyncTask(this.deviceDao).execute(device);
    }

    public void deleteAll() {
        new DeleteAllDevicesAsyncTask(this.deviceDao).execute();
    }

    public MutableLiveData<List<BtDevice>> getAllDevices() {
        return this.allDevices;
    }

    private static class InsertDeviceAsyncTask extends AsyncTask<BtDevice, Void, Void> {

        private BtDeviceDao deviceDao;

        public InsertDeviceAsyncTask(BtDeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(BtDevice... btDevices) {
            deviceDao.instert(btDevices[0]);
            return null;
        }
    }

    private static class UpdateDeviceAsyncTask extends AsyncTask<BtDevice, Void, Void> {

        private BtDeviceDao deviceDao;

        public UpdateDeviceAsyncTask(BtDeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(BtDevice... btDevices) {
            deviceDao.update(btDevices[0]);
            return null;
        }
    }

    private static class DeleteDeviceAsyncTask extends AsyncTask<BtDevice, Void, Void> {

        private BtDeviceDao deviceDao;

        public DeleteDeviceAsyncTask(BtDeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(BtDevice... btDevices) {
            deviceDao.delete(btDevices[0]);
            return null;
        }
    }

    private static class DeleteAllDevicesAsyncTask extends AsyncTask<Void, Void, Void> {

        private BtDeviceDao deviceDao;

        public DeleteAllDevicesAsyncTask(BtDeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            deviceDao.deleteAll();
            return null;
        }
    }

    private static class GetAllDevicesAsyncTask extends AsyncTask<Void, Void, LiveData<List<BtDevice>>> {

        private BtDeviceDao deviceDao;

        public GetAllDevicesAsyncTask(BtDeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected LiveData<List<BtDevice>> doInBackground(Void... voids) {
            return deviceDao.getAllDevices();
        }
    }
}