package com.example.betweenus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.betweenus.fragments.MemoriesFragment;
import com.example.betweenus.fragments.ProductivityFragment;
import com.example.betweenus.fragments.ProfileFragment;
import com.example.betweenus.fragments.HomeFragment;
import com.example.betweenus.model.ShopItem;
import com.example.betweenus.model.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.betweenus.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDBHelper;
    public SQLiteDatabase db;
    private ActivityMainBinding binding;
    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createDB();
        testDatabaseQuery();
        testLogin();
        //testCreateTask();
        testGetRandomSuggestion();
        testAvatarOwnership();
        //testAddAvatar();
        testGetUserID();
        testChangePoints();
        //testBuyItem();
        testGetShopItems();
        //testUpdateTaskGoal();

        myDBHelper.insertSampleData();



        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.navView, navController);
        }

        requestNotificationPermission();
    }

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
                return;
            }
        }

        // Permission already granted OR Android < 13
        showWelcomeNotification();
    }

    private void showWelcomeNotification() {
        NotificationHelper.showNotification(
                this,
                "Welcome!",
                "Log your mood & post a memory for your friends to see!"
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showWelcomeNotification();
            }
        }
    }

    public void createDB() {
        myDBHelper = new DatabaseHelper(this);
        try {
            myDBHelper.createDatabase();
        } catch (IOException e) {
            throw new Error ("Unable to create database");
        }
        try{
            db = myDBHelper.getWritableDatabase();
        } catch (SQLException sqle) {
        }

    }

    public long registerUser(String name, String phone, String email,
                             String password, String bio, int avatar) {

        return myDBHelper.insertUser(name, phone, email, password, bio, avatar);
    }

    public Cursor getUserByName(String username) {
        return myDBHelper.getUserByName(username);
    }

    public Cursor getAllUsers() {
        return myDBHelper.getUsers();
    }

    public Cursor searchUsers(String query) {
        return myDBHelper.searchUsers(query);
    }

    public void updateUserBio(int userID, String newBio) {
        myDBHelper.updateUserBio(userID, newBio);
    }

    public void updateUserField(String column,
                                Object newValue,
                                String whereColumn,
                                Object whereValue) {

        myDBHelper.updateColumnValue(column, newValue, whereColumn, whereValue);
    }

    public long addFriend(int firstUserID, int secondUserID) {
        return myDBHelper.newFriend(firstUserID, secondUserID);
    }

    public Cursor getUserFriends(int userID) {
        return myDBHelper.getUserFriends(userID);
    }

    public Task createTask(int creatorID,
                           String goal,
                           int isPrivate,
                           String dueDate) {

        return myDBHelper.createTask2(creatorID, goal, isPrivate, dueDate);
    }

    public Cursor getUserTasks(int userID) {
        return myDBHelper.getUserTasks(String.valueOf(userID));
    }

    public void completeTask(int taskID) {
        myDBHelper.markTaskComplete(taskID);
    }

    public boolean updateTaskDueDate(int taskID, String newDueDate){ return myDBHelper.updateTaskDueDate(taskID, newDueDate);}

    public boolean updateTaskGoal(int taskID, String newGoal){ return myDBHelper.updateTaskGoal(taskID, newGoal);}

    public void testUpdateTaskGoal(){
        updateTaskGoal(1, "Test Change");
        updateTaskDueDate(1, "Test Due Date");
    }

    public String getUserSchedule(int userID) {
        return myDBHelper.getUserSchedule(userID);
    }

    public long postComment(int userID, int postID, String commentBody){
        return myDBHelper.postComment(postID, userID, commentBody);
    }

    public Cursor getComments(int postID){
        return myDBHelper.getPostComments(postID);
    }

    public boolean login(int userID, String password){
        return myDBHelper.login(userID, password);
    }

    //public Task createTask(Integer taskCreator, String goal, Integer isPrivate, String dueDate){
      //  return myDBHelper.createTask(taskCreator, goal, isPrivate, dueDate);
    //}

    public String getRandomSuggestion(){
        return myDBHelper.getRandomSuggestion();
    }

    //function for adding or spending points (use negative values when adding points to a user's balance)
    //this function returns true if points were successfully added/deducted and false if the user's balance would go negative
    public boolean changePoints(int UserID, int points){ return myDBHelper.spendUserPoints(UserID, points);}
    //call changePoints first and ensure it succeeds before calling this function, if changePoints returns false then the user has insufficient points
    //if this function returns false, the user either already owns the item or there was an error
//    public boolean buyShopItem(int UserID, int ShopItemId){ return myDBHelper.buyItem(UserID, ShopItemId);}

    public int getUserPoints(int UserID){ return myDBHelper.getUserPoints(UserID);}
    public ArrayList<ShopItem> getOwnedShopItems(int UserID){
        return myDBHelper.getShopItemsOwned(UserID);
    }

    public void testGetShopItems() {
        // 1. Retrieve all shop items
       ArrayList<ShopItem> shopItems = myDBHelper.getAllShopItems();

        // 2. Print the results
        System.out.println("Shop Items:");

        for (ShopItem item : shopItems) {
            System.out.println("ID: " + item.getItemID() +
                    ", Name: " + item.getItemName() +
                    ", Description: " + item.getItemDescription() +
                    ", Price: " + item.getItemPrice() +
                    ", ImagePath: " + item.getImagePath());
        }
    }


    public void testChangePoints(){
        System.out.println(getUserPoints(1));
        changePoints(1, 1);
        System.out.println(getUserPoints(1));
        changePoints(1,-1);
        System.out.println(getUserPoints(1));
    }

//    public void testBuyItem() {
//        int userID = 1;
//        int itemID = 2;
//
//        // 1. Get owned items BEFORE
//        ArrayList<Integer> beforeItems = getOwnedShopItems(userID);
//        System.out.println("Before purchase: " + beforeItems);
//
//        // 2. Attempt to buy the item
//        boolean bought = myDBHelper.buyItem(userID, itemID);
//        System.out.println("Item bought: " + bought);
//
//        // 3. Get owned items AFTER
//        ArrayList<Integer> afterItems = getOwnedShopItems(userID);
//        System.out.println("After purchase: " + afterItems);
//    }


    public void testGetRandomSuggestion(){
        System.out.println(getRandomSuggestion());
    }

    public void testCreateTask(){
        Task testTask = createTask(1, "Test task creation", 0, "2026-033-24 12:00:00");
        System.out.println(testTask.getTaskGoal());
    }

    private void testDatabaseQuery() {

        try {
            SQLiteDatabase database = myDBHelper.getWritableDatabase();

            Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table';", null);

            if (cursor.moveToFirst()) {
                do {
                    String tableName = cursor.getString(0);
                    Log.d("DB_TEST", "Table found: " + tableName);
                } while (cursor.moveToNext());
            } else {
                Log.d("DB_TEST", "No tables found.");
            }

            cursor.close();

        } catch (Exception e) {
            Log.e("DB_TEST", "Database query failed", e);
        }
    }
    private void testLogin(){
       if (login(1, "pass") == true){
           Log.e("LOG_TEST","Login Test Successful");
       }
       else Log.e("LOG_TEST","Login Test unsuccessful");
    }

    public ArrayList<Integer> getOwnedAvatars(int UserID){
        return myDBHelper.getUserAvatars(UserID);
    }

    public boolean addAvatar(int UserID, int AvatarID){
        return myDBHelper.addAvatar(UserID, AvatarID);
    }

    private int getIntUserID(String userName){
        return myDBHelper.getIntUserID(userName);
    }
    private void testAddAvatar(){
        testAvatarOwnership();
        System.out.println(addAvatar(1,2));
        testAvatarOwnership();
    }


    private void testAvatarOwnership(){
         ArrayList<Integer> testList = new ArrayList<Integer>();
        testList = getOwnedAvatars(1);
        System.out.println(testList);
    }
//    private void testShopItemOwnership(){
//        ArrayList<Integer> testList = new ArrayList<Integer>();
//        testList = getOwnedShopItems(1);
//        System.out.println(testList);
//    }

    private void testGetUserID(){
        //System.out.println("Testing getUserID");
        System.out.println(getIntUserID("Alex"));
        //System.out.println("Done");
    }
}
