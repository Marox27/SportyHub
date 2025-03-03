package com.example.sportyhub.Adapters;

// En una nueva clase, por ejemplo, MainViewModel.java

import android.app.Application;
import android.content.ClipData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ActivityViewModel extends ViewModel {
    private MutableLiveData<ArrayList<ClipData.Item>> itemListLiveData = new MutableLiveData<>();
   // private DBHelper dbHelper;
/*
    public ActivityViewModel(Application application) {
        dbHelper = new DBHelper(application);
        loadItems();
    }*/

    public LiveData<ArrayList<ClipData.Item>> getItemListLiveData() {
        return itemListLiveData;
    }

    private void loadItems() {
        // Utilizar un Executor para ejecutar la tarea en un hilo separado
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            //ArrayList<ClipData.Item> items = dbHelper.getAllItems();

            // Actualizar LiveData en el hilo principal
            //itemListLiveData.postValue(items);
        });
    }
}
