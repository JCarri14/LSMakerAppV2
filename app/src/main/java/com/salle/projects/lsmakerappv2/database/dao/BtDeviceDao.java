package com.salle.projects.lsmakerappv2.database.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.salle.projects.lsmakerappv2.model.BtDevice;

import java.util.List;

@Dao
public interface BtDeviceDao {

    @Insert
    void insert(BtDevice device);

    @Update
    void update(BtDevice device);

    @Delete
    void delete(BtDevice device);

    @Query("DELETE FROM devices")
    void deleteAll();

    @Query("SELECT * FROM devices")
    LiveData<List<BtDevice>> getAllDevices();

    @Query("SELECT * FROM devices AS d WHERE d.name LIKE :name LIMIT 1")
    BtDevice getDeviceByName(String name);

}
