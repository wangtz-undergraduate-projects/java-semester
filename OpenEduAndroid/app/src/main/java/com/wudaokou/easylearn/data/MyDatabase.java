package com.wudaokou.easylearn.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.wudaokou.easylearn.constant.Constant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, SearchRecord.class, Property.class,
        Content.class, Question.class, SearchResult.class},
        version = 11, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    public abstract SearchRecordDAO searchRecordDAO();
    public abstract SearchResultDAO searchResultDAO();
    public abstract ContentDAO contentDAO();
    public abstract PropertyDAO propertyDAO();
    public abstract QuestionDAO questionDAO();

    private static volatile MyDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MyDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyDatabase.class, "my_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.

                UserDAO dao = INSTANCE.userDAO();

                // 暂定用户名为"default"，后续再完善
                if (dao.loadUserByName("default1") == null) {
                    User user = new User("default1");
                    dao.insertUser(user);
                }
            });
        }
    };
}
