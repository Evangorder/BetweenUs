package com.example.betweenus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.betweenus.model.AvatarGroup;
import com.example.betweenus.model.Comment;
import com.example.betweenus.model.DashboardMessage;
import com.example.betweenus.model.Friend;
import com.example.betweenus.model.MemoriesItem;
import com.example.betweenus.model.Message;
import com.example.betweenus.model.ShopItem;
import com.example.betweenus.model.Task;
import com.example.betweenus.model.User;
import com.example.betweenus.model.UserHistory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {

    public int currentUserId = 1;
    private static final String DATABASE_NAME = "BetweenUs.db";
    public static final int DATABASE_VERSION = 5; // increase this
    //private static String DB_PATH = "/data/data/BetweenUs/databases/";
    private SQLiteDatabase db;
    private Context myContext;
    //Users Table
    private static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "UserID";
    public static final String COL_NAME = "UserName";
    public static final String COL_EMAIL = "UserEmail";
    private static final String COL_PHONE = "UserPhone";
    private static final String COL_BIO = "UserBio";
    private static final String COL_Premium = "IsPremium";
    public static final String COL_AVATAR = "Avatar";
    private static final String COL_MOOD = "CurrentMood";
    private static final String COL_FRIEND_COUNT = "FriendNumber";
    private static final String COL_POINTS = "Points";
    private static final String COL_SCHEDULE = "Schedule"; // Add this at top with other column declarations
    private static final String COL_PASSWORD = "password";

    // avatarownership Table
    private static final String TABLE_AVATAR_OWNERSHIP = "avatarownership";
    private static final String COL_OWNERSHIP_ID = "OwnershipID";
    private static final String COL_User_ID = "UserID";
    private static final String COL_AVATAR_OWNED = "AvatarOwned";

    // shopitemownership Table
    private static final String TABLE_SHOP_ITEM_OWNERSHIP = "shopitemownership";
    private static final String COL_SHOP_ITEM_OWNERSHIP = "OwnershipID";
    private static final String COL_BUYER_ID = "UserID";
    private static final String COL_SHOP_ITEM_OWNED = "ShopItemOwned";

    // Dashboard Table
    private static final String TABLE_DASHBOARD = "dashboard";
    private static final String COL_POST_ID = "PostID";
    private static final String COL_SENDER_ID = "SenderID";
    private static final String COL_DASHBOARD_ID = "DashboardID";
    private static final String COL_POST_TEXT = "PostText";
    private static final String COL_POST_TIME = "PostTime";

    // Friends Table
    private static final String TABLE_FRIENDS = "friends";
    private static final String COL_FRIENDSHIP_ID = "FriendshipID";
    public static final String COL_FIRST_FRIEND_ID = "First_Friend_ID";
    public static final String COL_SECOND_FRIEND_ID = "Second_Friend_ID";
    private static final String COL_FRIENDSHIP_DATE = "Freindship_Date";

    // Memories Table
    public static final String TABLE_MEMORIES = "memories";
    public static final String COL_MEMORY_ID = "MemoryID";
    public static final String COL_POSTER_ID = "PosterID";
    public static final String COL_MEMORY_TITLE = "MemoryTitle";
    public static final String COL_MEMORY_BODY = "MemoryBody";
    public static final String COL_MEDIA_TYPE = "MediaType";
    public static final String COL_MEDIA_URL = "MediaURL";
    public static final String COL_TIME_STAMP = "TimeStamp";
    public static final String COL_LIKES = "Likes";

    // Messages Table
    private static final String TABLE_MESSAGES = "messages";
    private static final String COL_MESSAGE_ID = "MessageID";
    private static final String COL_MESSAGE_SENDER_ID = "SenderID";
    private static final String COL_RECEIVER_ID = "RecieverID";
    private static final String COL_MESSAGE_TEXT = "MessageText";
    private static final String COL_MESSAGE_TIME = "MessageTime";

    // Tasks Table
    private static final String TABLE_TASKS = "tasks";
    private static final String COL_TASK_ID = "TaskID";
    private static final String COL_TASK_CREATOR = "TaskCreator";
    private static final String COL_TASK_GOAL = "TaskGoal";
    private static final String COL_IS_COMPLETE = "IsComplete";
    private static final String COL_IS_PRIVATE = "IsPrivate";
    private static final String COL_DUE_DATE = "DueDate";


    // SubTasks Table
    private static final String TABLE_SUBTASKS = "subtasks";
    private static final String COL_SUBTASK_ID = "SubTaskID";
    private static final String COL_ORIGINAL_TASK_ID = "TaskID";
    private static final String COL_SUBTASK_COMPLETE = "IsComplete";

    // Suggestions Table
    private static final String TABLE_SUGGESTIONS = "suggestions";
    private static final String COL_SUGGESTION_ID = "suggestionID";
    private static final String COL_SUGGESTION = "Suggestion";
    private static final String COL_KEYWORDS = "Keywords";

    // User history Table
    private static final String TABLE_USER_HISTORY = "userhistory";
    private static final String COL_RECORD_ID = "RecordID";
    private static final String COL_USER_HISTORY_ID = "UserID";
    private static final String COL_RECORD_CATEGORY = "RecordCategory";
    private static final String COL_RECORD_VALUE = "RecordValue";
    private static final String COL_TIME_RECORDED = "TimeRecorded";

    // Dashboard Table
    public static final String TABLE_COMMENTS = "comments";
    public static final String COL_COMMENT_ID = "CommentID";
    public static final String COL_COMMENTOR_ID = "CommentorID";
    public static final String COL_COMMENT_MEMORY_ID = "MemoryID";
    public static final String COL_COMMENT_BODY = "CommentBody";
    public static final String COL_COMMENT_TIME_STAMP = "CommentTimeStamp";

    // Shop Table
    public static final String TABLE_SHOP = "shop";
    public static final String COL_ITEM_ID = "itemID";
    public static final String COL_ITEM_NAME = "ItemName";
    public static final String COL_ITEM_PRICE = "ItemPrice";
    public static final String COL_ITEM_DESCRIPTION = "ItemDescription";
    public static final String COL_ITEM_IMAGE_PATH = "ImagePath";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    public void createDatabase() throws IOException {
        File dbFile = myContext.getDatabasePath(DATABASE_NAME);

        // Make sure the /databases/ folder exists
        if (!dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
        }

        // Copy the database from assets if it doesn't exist yet
        if (!dbFile.exists()) {
            copyDatabase(myContext);
        }
    }

    private void copyDatabase(Context context) throws IOException {
        InputStream input = context.getAssets().open(DATABASE_NAME);
        OutputStream output = new FileOutputStream(context.getDatabasePath(DATABASE_NAME));

        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        input.close();
    }


    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            File dbFile = myContext.getDatabasePath(DATABASE_NAME); // correct dynamic path
            checkDB = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            // database does not exist yet
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Copying database so no creation here

    }

    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = myContext.getDatabasePath(DATABASE_NAME).getPath();
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {
        if (db != null) {
            db.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DASHBOARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AVATAR_OWNERSHIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBTASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUGGESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOP_ITEM_OWNERSHIP);
        onCreate(db);
    }


    // Search for users by username or name
    public Cursor searchUsers(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_NAME + " LIKE ? OR " + COL_NAME + " LIKE ?";
        // " ORDER BY " + COL_FOLLOWERS_COUNT + " DESC LIMIT 20";
        return db.rawQuery(searchQuery, new String[]{"%" + query + "%", "%" + query + "%"});
    }

    // Create User
    public long insertUser(String name, String phone, String email, String password, String bio, Integer avatar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_PHONE, phone);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_BIO, bio);
        values.put(COL_AVATAR, avatar);
        return db.insert(TABLE_USERS, null, values);
    }

    //Insert New Task
    public long createTask(Integer taskCreator, String goal, Integer isPrivate, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_CREATOR, taskCreator);
        values.put(COL_TASK_GOAL, goal);
        values.put(COL_IS_PRIVATE, isPrivate);
        values.put(COL_DUE_DATE, dueDate);
        return db.insert(TABLE_TASKS, null, values);
    }

    public Task createTask2(Integer taskCreator, String goal, Integer isPrivate, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TASK_CREATOR, taskCreator);
        values.put(COL_TASK_GOAL, goal);
        values.put(COL_IS_PRIVATE, isPrivate);
        values.put(COL_DUE_DATE, dueDate);

        long result = db.insert(TABLE_TASKS, null, values);

        if (result == -1) {
            System.out.println("Insertion Failed");
        } else {
            System.out.println("Insertion successful");
        }

        String query = "SELECT MAX(" + COL_TASK_ID + ") as max_id FROM " + TABLE_TASKS;

        Cursor c = db.rawQuery(query, null);

        int taskID = -1;

        if (c.moveToFirst()) {   // move cursor to first row
            int idIndex = c.getColumnIndexOrThrow("max_id");
            taskID = c.getInt(idIndex);
        }

        c.close();

        return new Task(taskID, taskCreator, goal, dueDate, false);
    }

    public boolean updateTaskDueDate(int taskID, String newDueDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_DUE_DATE, newDueDate);

        int result = db.update(
                TABLE_TASKS,
                values,
                COL_TASK_ID + " = ?",
                new String[]{String.valueOf(taskID)}
        );

        if (result == 0) {
            System.out.println("Update Due Date Failed");
            return false;
        } else {
            System.out.println("Update Due Date Successful");
            return true;
        }
    }

    public boolean updateTaskGoal(int taskID, String newGoal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TASK_GOAL, newGoal);

        int result = db.update(
                TABLE_TASKS,
                values,
                COL_TASK_ID + " = ?",
                new String[]{String.valueOf(taskID)}
        );

        if (result == 0) {
            System.out.println("Update Goal Failed");
            return false;
        } else {
            System.out.println("Update Goal Successful");
            return true;
        }
    }


    //Get Tasks For User
    public Cursor getUserTasks(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT * FROM " + TABLE_TASKS +
                " WHERE " + COL_TASK_CREATOR + " LIKE ?";
        return db.rawQuery(searchQuery, new String[]{"%" + query + "%"});
    }

    //List of the names of all users
    public Cursor getUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT UserName FROM " + TABLE_USERS;
        // " ORDER BY " + COL_FOLLOWERS_COUNT + " DESC LIMIT 20";
        return db.rawQuery(searchQuery, null);
    }

    //returns list of user objects for all users
    public List<User> getUsersList() {

        List<User> userList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String queryStr = "SELECT * FROM " + TABLE_USERS +
                " ORDER BY " + COL_USER_ID;

        Cursor cursor = db.rawQuery(queryStr, null);

        if (cursor != null) {

            int idIndex = cursor.getColumnIndexOrThrow(COL_USER_ID);
            int nameIndex = cursor.getColumnIndexOrThrow(COL_NAME);
            int moodIndex = cursor.getColumnIndexOrThrow(COL_MOOD);
            int avatarIndex = cursor.getColumnIndexOrThrow(COL_AVATAR);

            while (cursor.moveToNext()) {

                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String mood = cursor.getString(moodIndex);
                int avatar = cursor.getInt(avatarIndex);

                User user = new User(id, name, mood, avatar);
                userList.add(user);
            }

            cursor.close();
        }

        return userList;
    }

    //Gets user given name
    public Cursor getUserByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COL_NAME + "=?", new String[]{name}, null, null, null);
    }

    public Cursor getUserID(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COL_NAME + "=?", new String[]{userName}, null, null, null);
    }


    //returns int for userID
    public int getIntUserID(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1; // default if not found

        Cursor cursor = db.rawQuery(
                "SELECT UserID FROM users WHERE username = ?",
                new String[]{username}
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0); // first column (id)
            }
            cursor.close();
        }

        return userId;
    }


    // I need this to convert the ID into a username for add Friend use case
    public Cursor getUserByID(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                null,
                COL_USER_ID + "=?",
                new String[]{String.valueOf(userID)},
                null,
                null,
                null);
    }

    // Post Operations
    public long newFriend(int firstFriendID, int secondFriendID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FIRST_FRIEND_ID, firstFriendID);
        values.put(COL_SECOND_FRIEND_ID, secondFriendID);
        updateFriendCount(firstFriendID, 1);
        updateFriendCount(secondFriendID, 1);
        return db.insert(TABLE_FRIENDS, null, values);
    }

    //Internal function for updating friend count whenever a new friendship is established
    private void updateFriendCount(int userId, int friends) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. Get current friend count
        int currentCount = 0;

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_FRIEND_COUNT +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                currentCount = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COL_FRIEND_COUNT)
                );
            }
            cursor.close();
        }

        // 2. Increment
        int newCount = currentCount + 1;

        // 3. Update with new value
        db.execSQL(
                "UPDATE " + TABLE_USERS +
                        " SET " + COL_FRIEND_COUNT + " = ?" +
                        " WHERE " + COL_USER_ID + " = ?",
                new Object[]{newCount, userId}
        );
    }

    public Cursor getUserFriends(Integer userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT * FROM " + TABLE_FRIENDS +
                " WHERE " + COL_FIRST_FRIEND_ID + " = ? " +
                " OR " + COL_SECOND_FRIEND_ID + " = ?";

        return db.rawQuery(searchQuery, new String[]{
                String.valueOf(userID),
                String.valueOf(userID)
        });
    }

    //Returns a list of the users friends
    public List<Friend> getFriends(Integer userId) {

        List<Friend> friendsList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Get all friendships where the current user is involved
        String queryStr = "SELECT * FROM " + TABLE_FRIENDS +
                " WHERE " + COL_FIRST_FRIEND_ID + " = ? OR " + COL_SECOND_FRIEND_ID + " = ?";

        Cursor cursor = db.rawQuery(
                queryStr,
                new String[]{String.valueOf(userId), String.valueOf(userId)}
        );

        if (cursor != null) {

            int userOneIndex = cursor.getColumnIndexOrThrow(COL_FIRST_FRIEND_ID);
            int userTwoIndex = cursor.getColumnIndexOrThrow(COL_SECOND_FRIEND_ID);

            while (cursor.moveToNext()) {

                int userOneId = cursor.getInt(userOneIndex);
                int userTwoId = cursor.getInt(userTwoIndex);

                // Find instance of friendship
                int friendId = (userOneId == userId) ? userTwoId : userOneId;

                // Fetches identified friend's information from Users table
                String userQuery = "SELECT " + COL_USER_ID + ", " +
                        COL_NAME + ", " +
                        COL_EMAIL +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COL_USER_ID + " = ?";

                Cursor userCursor = db.rawQuery(
                        userQuery,
                        new String[]{String.valueOf(friendId)}
                );

                if (userCursor != null && userCursor.moveToFirst()) {

                    int idIndex = userCursor.getColumnIndexOrThrow(COL_USER_ID);
                    int nameIndex = userCursor.getColumnIndexOrThrow(COL_NAME);
                    int emailIndex = userCursor.getColumnIndexOrThrow(COL_EMAIL);

                    int id = userCursor.getInt(idIndex);
                    String name = userCursor.getString(nameIndex);
                    String email = userCursor.getString(emailIndex);

                    Friend friend = new Friend(id, name, email);
                    friendsList.add(friend);

                    userCursor.close();
                }
            }

            cursor.close();
        }

        return friendsList;
    }

    public List<Integer> getFriendUserIds(Integer userId) {

        List<Integer> friendIds = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String queryStr = "SELECT * FROM " + TABLE_FRIENDS +
                " WHERE " + COL_FIRST_FRIEND_ID + " = ? OR " + COL_SECOND_FRIEND_ID + " = ?";

        Cursor cursor = db.rawQuery(
                queryStr,
                new String[]{String.valueOf(userId), String.valueOf(userId)}
        );

        if (cursor != null) {

            int userOneIndex = cursor.getColumnIndexOrThrow(COL_FIRST_FRIEND_ID);
            int userTwoIndex = cursor.getColumnIndexOrThrow(COL_SECOND_FRIEND_ID);

            while (cursor.moveToNext()) {

                int userOneId = cursor.getInt(userOneIndex);
                int userTwoId = cursor.getInt(userTwoIndex);

                int friendId = (userOneId == userId) ? userTwoId : userOneId;

                friendIds.add(friendId);
            }

            cursor.close();
        }

        return friendIds;
    }


    //updates user bio
    public void updateUserBio(Integer userID, String newBio) {
        SQLiteDatabase db = this.getWritableDatabase();

        String updateQuery = "UPDATE " + TABLE_USERS +
                " SET " + COL_BIO + " = ? " +
                " WHERE " + COL_USER_ID + " = ?";

        db.execSQL(updateQuery, new Object[]{
                newBio,
                userID
        });
    }

    //marks task complete



    public void markTaskComplete(int taskID) {

        SQLiteDatabase db = this.getWritableDatabase();

        String updateQuery = "UPDATE " + TABLE_TASKS +
                " SET " + COL_IS_COMPLETE + " = 1 " +
                " WHERE " + COL_TASK_ID + " = ?";

        db.execSQL(updateQuery, new Object[]{taskID});
    }

    public void toggleTaskStatus(int taskID, boolean isNowComplete) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Convert boolean to 1 or 0 for SQLite
        values.put(COL_IS_COMPLETE, isNowComplete ? 1 : 0);

        db.update(TABLE_TASKS, values, COL_TASK_ID + " = ?", new String[]{String.valueOf(taskID)});
        db.close();
    }

    public void deleteTask(int taskID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TASKS + " WHERE " + COL_TASK_ID + " = ?", new Object[]{taskID});
    }

    //generic update profile function
    public void updateColumnValue(String columnToUpdate,
                                  Object newValue,
                                  String whereColumn,
                                  Object whereValue) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Validate column names against allowed constants
        if (!isValidColumn(columnToUpdate) || !isValidColumn(whereColumn)) {
            throw new IllegalArgumentException("Invalid column name");
        }

        String query = "UPDATE " + TABLE_USERS +
                " SET " + columnToUpdate + " = ? " +
                " WHERE " + whereColumn + " = ?";

        db.execSQL(query, new Object[]{newValue, whereValue});
    }

    //column validation check
    private boolean isValidColumn(String column) {
        return column.equals(COL_NAME) ||
                column.equals(COL_PASSWORD) ||
                column.equals(COL_PHONE) ||
                column.equals(COL_EMAIL) ||
                column.equals(COL_Premium) ||
                column.equals(COL_SCHEDULE) ||
                column.equals(COL_POINTS) ||
                column.equals(COL_AVATAR) ||
                column.equals(COL_MOOD);
    }

    //retrieves user schedule
//    public Cursor getUserSchedule(Integer userID) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        String searchQuery = "SELECT " + COL_SCHEDULE +
//                " FROM " + TABLE_USERS +
//                " WHERE " + COL_USER_ID + " = ?";
//
//        return db.rawQuery(searchQuery,
//                new String[]{String.valueOf(userID)});
//    }
    //returns schedule string from Users table
    public String getUserSchedule(Integer userID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String searchQuery = "SELECT " + COL_SCHEDULE +
                " FROM " + TABLE_USERS +
                " WHERE " + COL_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(searchQuery,
                new String[]{String.valueOf(userID)});

        String schedule = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                schedule = cursor.getString(0); // Only column selected
            }
            cursor.close();
        }

        return schedule;
    }


    //****MEMORIES DATABASE FUNCTIONS****///

    public long createMemory(int posterID,
                             String title,
                             String body,
                             String mediaType,
                             String mediaURL,
                             String timestamp) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_POSTER_ID, posterID);
        values.put(COL_MEMORY_TITLE, title);
        values.put(COL_MEMORY_BODY, body);
        values.put(COL_MEDIA_TYPE, mediaType);
        values.put(COL_MEDIA_URL, mediaURL);
        values.put(COL_TIME_STAMP, timestamp);
        values.put(COL_LIKES, 0);

        return db.insert(TABLE_MEMORIES, null, values);
    }

    public Cursor getMemoryByID(int memoryID) {

        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT * FROM " + TABLE_MEMORIES +
                        " WHERE " + COL_MEMORY_ID + " = ?";

        return db.rawQuery(query,
                new String[]{String.valueOf(memoryID)});
    }

    public void updateMemory(int memoryID,
                             String newTitle,
                             String newBody) {

        SQLiteDatabase db = this.getWritableDatabase();

        String query =
                "UPDATE " + TABLE_MEMORIES +
                        " SET " + COL_MEMORY_TITLE + " = ?, " +
                        COL_MEMORY_BODY + " = ? " +
                        " WHERE " + COL_MEMORY_ID + " = ?";

        db.execSQL(query,
                new Object[]{newTitle, newBody, memoryID});
    }

    public void deleteMemory(int memoryID) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(
                "DELETE FROM " + TABLE_MEMORIES +
                        " WHERE " + COL_MEMORY_ID + " = ?",
                new Object[]{memoryID}
        );

        db.execSQL(
                "DELETE FROM " + TABLE_COMMENTS +
                        " WHERE " + COL_COMMENT_MEMORY_ID + " = ?",
                new Object[]{memoryID}
        );
    }

    public Cursor getMemories() {
        SQLiteDatabase db = this.getReadableDatabase();

        String searchQuery =
                "SELECT * FROM " + TABLE_MEMORIES + " " +
                        "JOIN " + TABLE_USERS + " " +
                        "ON " + TABLE_MEMORIES + "." + COL_POSTER_ID +
                        " = " + TABLE_USERS + "." + COL_USER_ID + " " +
                        "ORDER BY " + COL_TIME_STAMP + " DESC";

        return db.rawQuery(searchQuery, null);
    }

    public Cursor getFriendsMemories(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Subquery to get friend IDs
        String friendIdsSubquery = "SELECT " + COL_FIRST_FRIEND_ID + " FROM " + TABLE_FRIENDS + " WHERE " + COL_SECOND_FRIEND_ID + " = " + userId +
                " UNION " +
                "SELECT " + COL_SECOND_FRIEND_ID + " FROM " + TABLE_FRIENDS + " WHERE " + COL_FIRST_FRIEND_ID + " = " + userId +
                " UNION SELECT " + userId; // Also include current user's memories

        String searchQuery =
                "SELECT * FROM " + TABLE_MEMORIES + " " +
                        "JOIN " + TABLE_USERS + " " +
                        "ON " + TABLE_MEMORIES + "." + COL_POSTER_ID +
                        " = " + TABLE_USERS + "." + COL_USER_ID + " " +
                        "WHERE " + COL_POSTER_ID + " IN (" + friendIdsSubquery + ") " +
                        "ORDER BY " + COL_TIME_STAMP + " DESC";

        return db.rawQuery(searchQuery, null);
    }


    //returns list of All memories
    public List<MemoriesItem> getMemorieList() {

        List<MemoriesItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT m.*, u." + COL_NAME + ", u." + COL_AVATAR +
                        " FROM " + TABLE_MEMORIES + " m" +
                        " JOIN " + TABLE_USERS + " u ON m." + COL_POSTER_ID + " = u." + COL_USER_ID +
                        " ORDER BY m." + COL_TIME_STAMP + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {

            int memoryIdIndex = cursor.getColumnIndexOrThrow(COL_MEMORY_ID);
            int posterIdIndex = cursor.getColumnIndexOrThrow(COL_POSTER_ID);
            int posterAvatarIndex = cursor.getColumnIndexOrThrow(COL_AVATAR);
            int usernameIndex = cursor.getColumnIndexOrThrow(COL_NAME);
            int titleIndex = cursor.getColumnIndexOrThrow(COL_MEMORY_TITLE);
            int bodyIndex = cursor.getColumnIndexOrThrow(COL_MEMORY_BODY);
            int mediaTypeIndex = cursor.getColumnIndexOrThrow(COL_MEDIA_TYPE);
            int mediaUrlIndex = cursor.getColumnIndexOrThrow(COL_MEDIA_URL);
            int timeStampIndex = cursor.getColumnIndexOrThrow(COL_TIME_STAMP);
            int likesIndex = cursor.getColumnIndexOrThrow(COL_LIKES);

            while (cursor.moveToNext()) {
                MemoriesItem memory = new MemoriesItem(
                        cursor.getInt(memoryIdIndex),
                        cursor.getInt(posterIdIndex),
                        cursor.getInt(posterAvatarIndex),
                        cursor.getString(usernameIndex), // Pass username here
                        cursor.getString(titleIndex),
                        cursor.getString(bodyIndex),
                        cursor.getString(mediaUrlIndex),
                        cursor.getString(timeStampIndex),
                        cursor.getInt(likesIndex)
                );
                list.add(memory);
            }
            cursor.close();
        }
        return list;
    }

//    public List<MemoriesItem> getFriendMemories(Integer userId) {
//
//        List<MemoriesItem> memoryList = new ArrayList<>();
//        List<Integer> friendIds = getFriendUserIds(userId);
//
//        if (friendIds.isEmpty()) {
//            return memoryList; // No friends → no memories
//        }
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        // Build placeholders (?, ?, ?, ...)
//        StringBuilder placeholders = new StringBuilder();
//        for (int i = 0; i < friendIds.size(); i++) {
//            placeholders.append("?");
//            if (i < friendIds.size() - 1) {
//                placeholders.append(",");
//            }
//        }
//
//        String queryStr = "SELECT * FROM " + TABLE_MEMORIES +
//                " WHERE " + COL_POSTER_ID + " IN (" + placeholders.toString() + ")" +
//                " ORDER BY " + COL_TIME_STAMP + " DESC";
//
//        // Convert Integer list to String array for query args
//        String[] args = new String[friendIds.size()];
//        for (int i = 0; i < friendIds.size(); i++) {
//            args[i] = String.valueOf(friendIds.get(i));
//        }
//
//        Cursor cursor = db.rawQuery(queryStr, args);
//
//        if (cursor != null) {
//
//            int memoryIdIndex = cursor.getColumnIndexOrThrow(COL_MEMORY_ID);
//            int posterIdIndex = cursor.getColumnIndexOrThrow(COL_POSTER_ID);
//            int titleIndex = cursor.getColumnIndexOrThrow(COL_MEMORY_TITLE);
//            int bodyIndex = cursor.getColumnIndexOrThrow(COL_MEMORY_BODY);
//            int mediaTypeIndex = cursor.getColumnIndexOrThrow(COL_MEDIA_TYPE);
//            int mediaUrlIndex = cursor.getColumnIndexOrThrow(COL_MEDIA_URL);
//            int timeStampIndex = cursor.getColumnIndexOrThrow(COL_TIME_STAMP);
//            int likesIndex = cursor.getColumnIndexOrThrow(COL_LIKES);
//
//            while (cursor.moveToNext()) {
//
//                int memoryID = cursor.getInt(memoryIdIndex);
//                int posterID = cursor.getInt(posterIdIndex);
//                String memoryTitle = cursor.getString(titleIndex);
//                String memoryBody = cursor.getString(bodyIndex);
//                String mediaType = cursor.getString(mediaTypeIndex);
//                String mediaURL = cursor.getString(mediaUrlIndex);
//                String timeStamp = cursor.getString(timestampIndex);
//                int likes = cursor.getInt(likesIndex);
//
//                MemoriesItem memory = new MemoriesItem(
//                        memoryID,
//                        posterID,
//
//                        memoryTitle,
//                        memoryBody,
//                        mediaURL,
//                        timeStamp,
//                        likes
//                );
//
//                memoryList.add(memory);
//            }
//
//            cursor.close();
//        }
//
//        return memoryList;
//    }

    public void updateMemoryLikes(int memoryID, int newLikes) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query =
                "UPDATE " + TABLE_MEMORIES +
                        " SET " + COL_LIKES + " = ?" +
                        " WHERE " + COL_MEMORY_ID + " = ?";

        db.execSQL(query, new Object[]{newLikes, memoryID});
    }

    public Cursor getPostComments(Integer postID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String searchQuery = "SELECT c.*, u." + COL_NAME + ", u." + COL_AVATAR +
                " FROM " + TABLE_COMMENTS + " AS c" +
                " JOIN " + TABLE_USERS + " AS u ON c." + COL_COMMENTOR_ID + " = u." + COL_USER_ID +
                " WHERE c." + COL_COMMENT_MEMORY_ID + " = ?";

        return db.rawQuery(searchQuery,
                new String[]{String.valueOf(postID)});
    }

    public List<Comment> getCommentsList(Integer memoryId) {

        List<Comment> commentList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String queryStr =
                "SELECT c.*, u." + COL_NAME + ", u." + COL_AVATAR +
                        " FROM " + TABLE_COMMENTS + " AS c" +
                        " JOIN " + TABLE_USERS + " AS u" +
                        " ON c." + COL_COMMENTOR_ID + " = u." + COL_USER_ID +
                        " WHERE c." + COL_COMMENT_MEMORY_ID + " = ?" +
                        " ORDER BY c." + COL_COMMENT_TIME_STAMP + " ASC";

        Cursor cursor = db.rawQuery(
                queryStr,
                new String[]{String.valueOf(memoryId)}
        );

        if (cursor != null) {

            int commentIdIndex = cursor.getColumnIndexOrThrow(COL_COMMENT_ID);
            int posterIdIndex = cursor.getColumnIndexOrThrow(COL_POSTER_ID);
            int posterNameIndex = cursor.getColumnIndexOrThrow(COL_NAME);
            int posterAvatarIndex = cursor.getColumnIndexOrThrow(COL_AVATAR);
            int memoryIdIndex = cursor.getColumnIndexOrThrow(COL_MEMORY_ID);
            int bodyIndex = cursor.getColumnIndexOrThrow(COL_COMMENT_BODY);
            int timeIndex = cursor.getColumnIndexOrThrow(COL_COMMENT_TIME_STAMP);

            while (cursor.moveToNext()) {

                int commentId = cursor.getInt(commentIdIndex);
                int posterId = cursor.getInt(posterIdIndex);
                String posterName = cursor.getString(posterNameIndex);
                int avatar = cursor.getInt(posterAvatarIndex);
                int memId = cursor.getInt(memoryIdIndex);
                String body = cursor.getString(bodyIndex);
                String timestamp = cursor.getString(timeIndex);

                Comment comment = new Comment(
                        commentId,
                        posterId,
                        posterName,
                        avatar,
                        memId,
                        body,
                        timestamp
                );

                commentList.add(comment);
            }

            cursor.close();
        }

        return commentList;
    }

    public long postComment(int memoryID, int commentorID, String commentBody) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_COMMENT_MEMORY_ID, memoryID);
        values.put(COL_COMMENTOR_ID, commentorID);
        values.put(COL_COMMENT_BODY, commentBody);
        values.put(COL_COMMENT_TIME_STAMP, System.currentTimeMillis());

        return db.insert(TABLE_COMMENTS, null, values);
    }

    public boolean login(int userID, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COL_PASSWORD};
        String selection = COL_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userID)};

        Cursor cursor = db.query(
                TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean isValid = false;

        if (cursor != null && cursor.moveToFirst()) {
            String storedPassword = cursor.getString(
                    cursor.getColumnIndexOrThrow(COL_PASSWORD)
            );

            isValid = password.equals(storedPassword);
            cursor.close();
        }

        return isValid;
    }

    //*** homepage methods ****

    // UPDATE USER MOOD
    public void updateUserMood(int userId, int mood) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_MOOD, mood);

        db.update(
                TABLE_USERS,
                values,
                COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)}
        );
    }

// INSERT DASHBOARD MESSAGE

    public long insertDashboardMessage(int senderId, String text) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_SENDER_ID, senderId);
        values.put(COL_POST_TEXT, text);
        values.put(COL_POST_TIME, System.currentTimeMillis());

        return db.insert(TABLE_DASHBOARD, null, values);
    }

    // GET DASHBOARD MESSAGES
    public List<DashboardMessage> getValidDashboardMessages(long expirationHours) {

        List<DashboardMessage> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        long currentTime = System.currentTimeMillis();
        long expirationMillis = expirationHours * 60 * 60 * 1000;

        String queryStr = "SELECT * FROM " + TABLE_DASHBOARD +
                " ORDER BY " + COL_POST_TIME + " DESC";
        System.out.println(queryStr);
        Cursor cursor = db.rawQuery(
                queryStr,
                null
        );

        while (cursor.moveToNext()) {

            long postTime = cursor.getLong(
                    cursor.getColumnIndexOrThrow(COL_POST_TIME)
            );

//            if (currentTime - postTime <= expirationMillis) {

            String text = cursor.getString(
                    cursor.getColumnIndexOrThrow(COL_POST_TEXT)
            );

            list.add(new DashboardMessage(text));
//            }
        }

        cursor.close();
        return list;
    }

    // -----------------------------
// GET TASKS FOR USER (List version)
// -----------------------------
    public List<Task> getTasksForUser(int userId) {

        List<Task> tasks = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_TASKS +
                        " WHERE " + COL_TASK_CREATOR + " = ?",
                new String[]{String.valueOf(userId)}
        );

        while (cursor.moveToNext()) {

            int taskId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(COL_TASK_ID));

            int taskCreator = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_CREATOR));

            String goal = cursor.getString(
                    cursor.getColumnIndexOrThrow(COL_TASK_GOAL));

            String dueDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(COL_DUE_DATE));

            int isComplete = cursor.getInt(
                    cursor.getColumnIndexOrThrow(COL_IS_COMPLETE));

            tasks.add(new Task(taskId, taskCreator, goal, dueDate, isComplete == 1));
        }

        cursor.close();
        return tasks;
    }

    // GET USER + FRIENDS
    public List<User> getUserAndFriends(int userId) {

        List<User> users = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Add current user first
        Cursor userCursor = getUserByID(userId);

        if (userCursor.moveToFirst()) {

            String name = userCursor.getString(
                    userCursor.getColumnIndexOrThrow(COL_NAME));

            String mood = userCursor.getString(
                    userCursor.getColumnIndexOrThrow(COL_MOOD));

            int avatar = userCursor.getInt(
                    userCursor.getColumnIndexOrThrow(COL_AVATAR));

            users.add(new User(userId, name, mood, avatar));
        }

        userCursor.close();

        // Get friends
        Cursor friendCursor = getUserFriends(userId);

        while (friendCursor.moveToNext()) {

            int friendId;

            int first = friendCursor.getInt(
                    friendCursor.getColumnIndexOrThrow(COL_FIRST_FRIEND_ID));

            int second = friendCursor.getInt(
                    friendCursor.getColumnIndexOrThrow(COL_SECOND_FRIEND_ID));

            friendId = (first == userId) ? second : first;

            Cursor friendData = getUserByID(friendId);

            if (friendData.moveToFirst()) {

                String name = friendData.getString(
                        friendData.getColumnIndexOrThrow(COL_NAME));

                String mood = friendData.getString(
                        friendData.getColumnIndexOrThrow(COL_MOOD));

                int avatar = friendData.getInt(
                        friendData.getColumnIndexOrThrow(COL_AVATAR));

                users.add(new User(friendId, name, mood, avatar));
            }

            friendData.close();
        }

        friendCursor.close();

        return users;
    }

    public long sendMessage(int senderId, int receiverId, String text) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_SENDER_ID, senderId);
        values.put(COL_RECEIVER_ID, receiverId);
        values.put(COL_MESSAGE_TEXT, text);

        String timestamp = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        values.put(COL_MESSAGE_TIME, timestamp);

        return db.insert(TABLE_MESSAGES, null, values);
    }

    public List<Message> getMessages(Integer userId) {

        List<Message> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String queryStr = "SELECT * FROM " + TABLE_MESSAGES +
                " WHERE (" + COL_SENDER_ID + " = ? AND " + COL_RECEIVER_ID + " = ?)" +
                " OR (" + COL_SENDER_ID + " = ? AND " + COL_RECEIVER_ID + " = ?)" +
                " ORDER BY " + COL_MESSAGE_TIME + " ASC";

        Cursor cursor = db.rawQuery(
                queryStr,
                new String[]{String.valueOf(userId), String.valueOf(userId)}
        );

        if (cursor != null) {

            int idIndex = cursor.getColumnIndexOrThrow(COL_MESSAGE_ID);
            int senderIndex = cursor.getColumnIndexOrThrow(COL_SENDER_ID);
            int receiverIndex = cursor.getColumnIndexOrThrow(COL_RECEIVER_ID);
            int contentIndex = cursor.getColumnIndexOrThrow(COL_MESSAGE_TEXT);
            int timeIndex = cursor.getColumnIndexOrThrow(COL_MESSAGE_TIME);

            while (cursor.moveToNext()) {

                int id = cursor.getInt(idIndex);
                int senderId = cursor.getInt(senderIndex);
                int receiverId = cursor.getInt(receiverIndex);
                String content = cursor.getString(contentIndex);
                String messageTime = cursor.getString(timeIndex);

                Message message = new Message(
                        id,
                        senderId,
                        receiverId,
                        content,
                        messageTime
                );

                list.add(message);
            }

            cursor.close();
        }

        return list;
    }

    public List<Message> getMessagesBetween(int user1, int user2) {
        List<Message> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String queryStr = "SELECT * FROM " + TABLE_MESSAGES +
                " WHERE (" + COL_SENDER_ID + " = ? AND " + COL_RECEIVER_ID + " = ?)" +
                " OR (" + COL_SENDER_ID + " = ? AND " + COL_RECEIVER_ID + " = ?)" +
                " ORDER BY " + COL_MESSAGE_TIME + " ASC";

        Cursor cursor = db.rawQuery(
                queryStr,
                new String[]{String.valueOf(user1), String.valueOf(user2), String.valueOf(user2), String.valueOf(user1)}
        );

        if (cursor != null) {
            int idIndex = cursor.getColumnIndexOrThrow(COL_MESSAGE_ID);
            int senderIndex = cursor.getColumnIndexOrThrow(COL_SENDER_ID);
            int receiverIndex = cursor.getColumnIndexOrThrow(COL_RECEIVER_ID);
            int contentIndex = cursor.getColumnIndexOrThrow(COL_MESSAGE_TEXT);
            int timeIndex = cursor.getColumnIndexOrThrow(COL_MESSAGE_TIME);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idIndex);
                int senderId = cursor.getInt(senderIndex);
                int receiverId = cursor.getInt(receiverIndex);
                String content = cursor.getString(contentIndex);
                String messageTime = cursor.getString(timeIndex);

                Message message = new Message(id, senderId, receiverId, content, messageTime);
                list.add(message);
            }
            cursor.close();
        }
        return list;
    }

    public long insertUserHistory(UserHistory history) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_USER_ID, history.getUserID());
        values.put(COL_RECORD_CATEGORY, history.getRecordCategory());
        values.put(COL_RECORD_VALUE, history.getRecordValue());
        values.put(COL_TIME_RECORDED, history.getTimeRecorded());

        return db.insert(TABLE_USER_HISTORY, null, values);
    }

    public List<UserHistory> getUserHistory(int userId) {

        List<UserHistory> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT * FROM " + TABLE_USER_HISTORY +
                        " WHERE " + COL_USER_ID + " = ? " +
                        " ORDER BY " + COL_TIME_RECORDED + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {

            int recordIdIndex = cursor.getColumnIndexOrThrow(COL_RECORD_ID);
            int userIdIndex = cursor.getColumnIndexOrThrow(COL_USER_ID);
            int categoryIndex = cursor.getColumnIndexOrThrow(COL_RECORD_CATEGORY);
            int valueIndex = cursor.getColumnIndexOrThrow(COL_RECORD_VALUE);
            int timeIndex = cursor.getColumnIndexOrThrow(COL_TIME_RECORDED);
            while (cursor.moveToNext()) {

                int recordID = cursor.getInt(recordIdIndex);
                int uID = cursor.getInt(userIdIndex);
                String category = cursor.getString(categoryIndex);
                int value = cursor.getInt(valueIndex);
                String timeRecorded = cursor.getString(timeIndex);

                UserHistory history = new UserHistory(
                        recordID,
                        uID,
                        category,
                        value,
                        timeRecorded
                );

                list.add(history);
            }

            cursor.close();
        }

        return list;
    }

    public List<String> getDueTasks(int UserID) {
        List<String> dueTasks = new ArrayList<>();

        String query = "SELECT TaskGoal FROM tasks " +
                "WHERE datetime(DueDate) BETWEEN datetime('now') AND datetime('now', '+1 day')";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                dueTasks.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return dueTasks;
    }

    public String getRandomSuggestion() {
        SQLiteDatabase db = this.getReadableDatabase();

        Random random = new Random();
        int randomNumber = random.nextInt(10) + 1; // 1–10 instead of 0–9

        String query = "SELECT " + COL_SUGGESTION +
                " FROM " + TABLE_SUGGESTIONS +
                " WHERE " + COL_SUGGESTION_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(randomNumber)});

        String randomTask = null;

        if (cursor.moveToFirst()) {
            randomTask = cursor.getString(0);
        }

        cursor.close();

        return randomTask;
    }

    //takes userID and returns ArrayList of integers corresponding to the user's owned avatars
    public ArrayList<Integer> getUserAvatars(int UserID) {
        ArrayList<Integer> ownedAvatars = new ArrayList<Integer>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_AVATAR_OWNED + " FROM " + TABLE_AVATAR_OWNERSHIP +
                " WHERE " + COL_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(UserID)});


        if (cursor.moveToFirst()) {
            do {
                ownedAvatars.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return ownedAvatars;
    }

    public boolean addAvatar(int UserID, int AvatarID) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT 1 FROM " + TABLE_AVATAR_OWNERSHIP +
                " WHERE " + COL_USER_ID + " = ? AND " + COL_AVATAR_OWNED + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(UserID),
                String.valueOf(AvatarID)
        });

        boolean exists = cursor.moveToFirst();
        cursor.close();

        if (exists) {
            return false; // already exists
        }

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, UserID);
        values.put(COL_AVATAR_OWNED, AvatarID);

        long result = db.insert(TABLE_AVATAR_OWNERSHIP, null, values);
        return result != -1;
    }

    //User premium status
    public static boolean isUserPremium(DatabaseHelper db, int userId) {

        SQLiteDatabase database = db.getReadableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT IsPremium FROM users WHERE UserID = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            int isPremium = cursor.getInt(0);
            cursor.close();
            return isPremium == 1;
        }

        cursor.close();
        return false;
    }

    public List<AvatarGroup> getAvatarGroups(int currentUserId) {

        SQLiteDatabase db = this.getReadableDatabase();

        List<AvatarGroup> groups = new ArrayList<>();

        // -----------------------------
        // GET CURRENT USER (CENTER)
        // -----------------------------
        Cursor userCursor = db.rawQuery(
                "SELECT UserName, Avatar, CurrentMood FROM users WHERE UserID = ?",
                new String[]{String.valueOf(currentUserId)}
        );

        String centerName = "";
        int centerAvatar = R.drawable.giraffe_neutral; // fallback

        if (userCursor != null && userCursor.moveToFirst()) {

            centerName = userCursor.getString(0);

            int avatarType = userCursor.getInt(1);
            int mood = userCursor.getInt(2);

            centerAvatar = getAvatarDrawable(avatarType, mood);
        }

        if (userCursor != null) userCursor.close();

        // -----------------------------
        // GET ALL FRIENDS
        // -----------------------------
        Cursor friendCursor = db.rawQuery(
                "SELECT UserName, Avatar, CurrentMood FROM users WHERE UserID IN (" +
                        "SELECT First_Friend_ID FROM friends WHERE Second_Friend_ID = ? " +
                        "UNION " +
                        "SELECT Second_Friend_ID FROM friends WHERE First_Friend_ID = ?" +
                        ") ORDER BY UserName",
                new String[]{String.valueOf(currentUserId), String.valueOf(currentUserId)}
        );

        List<User> friends = new ArrayList<>();

        if (friendCursor != null) {
            while (friendCursor.moveToNext()) {

                String name = friendCursor.getString(0);
                int avatarType = friendCursor.getInt(1);
                int mood = friendCursor.getInt(2);

                int avatarRes = getAvatarDrawable(avatarType, mood);

                // Use your existing User constructor
                friends.add(new User(0, name, "", avatarRes));
            }
            friendCursor.close();
        }

        // -----------------------------
        // SPLIT INTO GROUPS OF 2 FRIENDS
        // -----------------------------
        for (int i = 0; i < friends.size(); i += 2) {

            Integer leftAvatar = null;
            String leftName = null;

            Integer rightAvatar = null;
            String rightName = null;

            // LEFT friend
            if (i < friends.size()) {
                leftAvatar = friends.get(i).getAvatar();
                leftName = friends.get(i).getName();
            }

            // RIGHT friend
            if (i + 1 < friends.size()) {
                rightAvatar = friends.get(i + 1).getAvatar();
                rightName = friends.get(i + 1).getName();
            }

            groups.add(new AvatarGroup(
                    leftAvatar, leftName,
                    centerAvatar, centerName,
                    rightAvatar, rightName
            ));
        }

        // -----------------------------
        // NO FRIENDS CASE
        // -----------------------------
        if (groups.isEmpty()) {
            groups.add(new AvatarGroup(
                    null, null,
                    centerAvatar, centerName,
                    null, null
            ));
        }

        return groups;
    }


    private int getAvatarDrawable(int avatarType, int mood) {

        switch (avatarType) {


            case 1: // bunny
                switch (mood) {
                    case 0:
                        return R.drawable.bunny_happy;
                    case 1:
                        return R.drawable.bunny_sad;
                    case 2:
                        return R.drawable.bunny_excited;
                    case 3:
                        return R.drawable.bunny_neutral;
                    case 4:
                        return R.drawable.bunny_angry;
                }
                break;
            case 2: // giraffe
                switch (mood) {
                    case 0:
                        return R.drawable.giraffe_happy;
                    case 1:
                        return R.drawable.giraffe_sad;
                    case 2:
                        return R.drawable.giraffe_excited;
                    case 3:
                        return R.drawable.giraffe_neutral;
                    case 4:
                        return R.drawable.giraffe_angry;
                }
                break;

            case 3: // kitty
                switch (mood) {
                    case 0:
                        return R.drawable.kitty_happy;
                    case 1:
                        return R.drawable.kitty_sad;
                    case 2:
                        return R.drawable.kitty_excited;
                    case 3:
                        return R.drawable.kitty_neutral;
                    case 4:
                        return R.drawable.kitty_angry;
                }
                break;


            case 4: // puppy
                switch (mood) {
                    case 0:
                        return R.drawable.puppy_happy;
                    case 1:
                        return R.drawable.puppy_sad;
                    case 2:
                        return R.drawable.puppy_excited;
                    case 3:
                        return R.drawable.puppy_neutral;
                    case 4:
                        return R.drawable.puppy_angry;
                }
                break;
        }

        return R.drawable.bunny_happy; // fallback
    }

    public ArrayList<ShopItem> getAllShopItems() {

        ArrayList<ShopItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM shop", null);

        while (cursor.moveToNext()) {
            list.add(new ShopItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4)
            ));
        }

        cursor.close();
        return list;
    }

    public ArrayList<ShopItem> getShopItemsOwned(int UserID) {
        ArrayList<ShopItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT s.* FROM shop s " +
                        "INNER JOIN shopitemownership o " +
                        "ON s.itemID = o.ShopItemOwned " +
                        "WHERE o.UserID=?",
                new String[]{String.valueOf(currentUserId)}
        );

        while (cursor.moveToNext()) {
            list.add(new ShopItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4)
            ));
        }

        cursor.close();
        return list;
    }

    public boolean isItemOwned(int userId, int itemId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM shopitemownership WHERE UserID=? AND ShopItemOwned=?",
                new String[]{String.valueOf(userId), String.valueOf(itemId)}
        );

        boolean owned = cursor.moveToFirst();
        cursor.close();

        return owned;
    }

    public boolean buyItem(int userId, ShopItem item) {

        if (isItemOwned(userId, item.getItemID())) return false;

        int currentPoints = getUserPoints(userId);

        if (currentPoints < item.getItemPrice()) return false;

        SQLiteDatabase db = this.getWritableDatabase();

        // deduct points
        db.execSQL("UPDATE users SET Points = Points - ? WHERE UserID=?",
                new Object[]{item.getItemPrice(), userId});

        // add ownership
        db.execSQL("INSERT INTO shopitemownership (UserID, ShopItemOwned) VALUES (?,?)",
                new Object[]{userId, item.getItemID()});

        return true;
    }

    public boolean spendUserPoints(int UserID, int points) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. Get current points
        String query = "SELECT " + COL_POINTS +
                " FROM " + TABLE_USERS +
                " WHERE " + COL_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(UserID)
        });

        // If user not found
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        int currentPoints = cursor.getInt(cursor.getColumnIndexOrThrow(COL_POINTS));
        cursor.close();

        // 2. Check if enough points
        if (points > currentPoints) {
            return false; // would go negative
        }

        // 3. Update points
        int newPoints = currentPoints - points;

        ContentValues values = new ContentValues();
        values.put(COL_POINTS, newPoints);

        int result = db.update(
                TABLE_USERS,
                values,
                COL_USER_ID + " = ?",
                new String[]{String.valueOf(UserID)}
        );

        return result > 0;
    }

    public int getUserPoints(int UserID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COL_POINTS +
                " FROM " + TABLE_USERS +
                " WHERE " + COL_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(UserID)
        });

        int points = 0;

        if (cursor.moveToFirst()) {
            points = cursor.getInt(cursor.getColumnIndexOrThrow(COL_POINTS));
        }

        cursor.close();
        return points;
    }

    public void insertStudySession(int userId, String date, int minutes, int completed) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("UserID", userId);
        values.put("Date", date);
        values.put("DurationMinutes", minutes);
        values.put("Completed", completed);

        db.insert("study_sessions", null, values);
    }

    public void addPointsToUser(int userId, int points) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE users SET Points = Points + ? WHERE UserID = ?",
                new Object[]{points, userId});
    }

    //generate Pomodoro report

    public int getTotalMinutes(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(DurationMinutes) FROM study_sessions WHERE UserID = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        return 0;
    }

    public int getTodayMinutes(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(DurationMinutes)\n" +
                        "FROM study_sessions\n" +
                        "WHERE UserID = ?\n" +
                        "AND Date = DATE('now')",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        return 0;
    }

    public int getWeekMinutes(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(DurationMinutes)\n" +
                        "FROM study_sessions\n" +
                        "WHERE UserID = ?\n" +
                        "AND Date >= DATE('now', '-7 days')",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        return 0;
    }

    public int getMonthMinutes(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(DurationMinutes)\n" +
                        "FROM study_sessions\n" +
                        "WHERE UserID = ?\n" +
                        "AND Date >= DATE('now', '-30 days')",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        return 0;
    }

    public int getSuccessRate(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT \n" +
                        "    SUM(Completed) * 1.0 / COUNT(*)\n" +
                        "FROM study_sessions\n" +
                        "WHERE UserID = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        return 0;
    }

    public int getAverageStudyTime(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT AVG(DurationMinutes)\n" +
                        "FROM study_sessions\n" +
                        "WHERE UserID = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        return 0;
    }

    public int getMinutesForDate(int userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(DurationMinutes) FROM study_sessions WHERE UserID=? AND Date=?",
                new String[]{String.valueOf(userId), date}
        );
        int minutes = 0;
        if (cursor.moveToFirst()) {
            minutes = cursor.getInt(0);
        }
        cursor.close();
        return minutes;
    }


// UPDATE USER STATUS
//    public void updateUserStatus(int userId, String status) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(COL_STATUS, status);
//
//        db.update(
//                TABLE_USERS,
//                values,
//                COL_USER_ID + "=?",
//                new String[]{String.valueOf(userId)}
//        );
//    }

    public void insertSampleData() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM study_sessions"); // reset

        db.execSQL("INSERT INTO study_sessions (UserID, Date, DurationMinutes, Completed) VALUES (1, DATE('now'), 25, 1)");
        db.execSQL("INSERT INTO study_sessions (UserID, Date, DurationMinutes, Completed) VALUES (1, DATE('now'), 15, 0)");

        db.execSQL("INSERT INTO study_sessions VALUES (NULL,1,DATE('now','-1 day'),30,1)");
        db.execSQL("INSERT INTO study_sessions VALUES (NULL,1,DATE('now','-2 day'),20,1)");
        db.execSQL("INSERT INTO study_sessions VALUES (NULL,1,DATE('now','-3 day'),10,0)");
        db.execSQL("INSERT INTO study_sessions VALUES (NULL,1,DATE('now','-4 day'),45,1)");
        db.execSQL("INSERT INTO study_sessions VALUES (NULL,1,DATE('now','-6 day'),25,1)");

        db.execSQL("INSERT INTO study_sessions VALUES (NULL,1,DATE('now','-10 day'),60,1)");
        db.execSQL("INSERT INTO study_sessions VALUES (NULL,1,DATE('now','-15 day'),40,0)");
        db.execSQL("INSERT INTO study_sessions VALUES (NULL,1,DATE('now','-20 day'),35,1)");
    }

}
