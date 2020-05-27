package com.example.covidcare;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ModelView extends AndroidViewModel {
    private static final String TAG = "ModelView";

    public DeviceRepository getRepository() {
        return repository;
    }

    private DeviceRepository repository;
    private LiveData<List<Device>> allDevices;


    public ServiceConnection getServiceConnection(){
        return repository.getServiceConnection();
    }

    public LiveData<MyService.MyBinder> getBinder(){
        return repository.getBinder();
    }





    public ModelView(@NonNull Application application) {
        super(application);
        repository = new DeviceRepository(application);
        allDevices = repository.getAllDevices();
    }


    public void insert(Device device) {
        repository.insert(device);
    }
    public void update(Device device) {
        repository.update(device);
    }
    public void delete(Device device) {
        repository.delete(device);
    }
    public void deleteAllDevices() {
        repository.deleteAllDevices();
    }
    public LiveData<List<Device>> getAllDevices() {
        return allDevices;
    }


}