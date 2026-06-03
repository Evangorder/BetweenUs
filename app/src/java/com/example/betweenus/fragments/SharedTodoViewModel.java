package com.example.betweenus.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.model.Task;

import java.util.List;

public class SharedTodoViewModel extends ViewModel {

    private MutableLiveData<List<Task>> tasks = new MutableLiveData<>();

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public void loadTasks(DatabaseHelper dbHelper, int userId) {
        List<Task> taskList = dbHelper.getTasksForUser(userId);
        tasks.setValue(taskList);
    }
}