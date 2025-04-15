package com.example.app0.ui.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.app0.data.Local.Entity.PlantBuddy;
import com.example.app0.data.Repository.PlantBuddyRepository;
import com.example.app0.data.Repository.PlantBuddyRepository.InsertStatus;

import java.util.List;

public class PlantBuddyViewModel extends AndroidViewModel {

    private final PlantBuddyRepository repository;

    public PlantBuddyViewModel(@NonNull Application application) {
        super(application);
        repository = new PlantBuddyRepository(application);
    }

    // Forward insert to the repository
    public void insert(PlantBuddy plantBuddy, InsertStatus status) {
        repository.insert(plantBuddy, status);
    }

    // Forward update to the repository
    public void update(PlantBuddy plantBuddy) {
        repository.update(plantBuddy);
    }

    // Forward delete to the repository
    public void deleteAll() {repository.deleteAll();}

    // Retrieve PlantBuddy
    public LiveData<List<PlantBuddy>> getPlantBuddy() {
        return repository.getPlantBuddy();
    }
}
