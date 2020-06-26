package com.salle.projects.lsmakerappv2.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.salle.projects.lsmakerappv2.database.repository.BtDeviceRepository;
import com.salle.projects.lsmakerappv2.model.BtDevice;

import java.util.ArrayList;
import java.util.List;

public class OptionViewModel extends AndroidViewModel {

    private BtDeviceRepository repository;
    private MutableLiveData<List<BtDevice>> mDevices;

    public OptionViewModel(@NonNull Application application) {
        super(application);
        repository = new BtDeviceRepository(application);
        mDevices = repository.getAllDevices();
    }

    public MutableLiveData<List<BtDevice>> getDevices() {
        if (mDevices == null) {
            mDevices = new MutableLiveData<List<BtDevice>>();
            mDevices.setValue(new ArrayList<>());
        }
        return mDevices;
    }

    public void insert(BtDevice device) {
        repository.insert(device);
    }

    public void update(BtDevice device) {
        repository.update(device);
    }

    public void delete(BtDevice device) {
        repository.delete(device);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
