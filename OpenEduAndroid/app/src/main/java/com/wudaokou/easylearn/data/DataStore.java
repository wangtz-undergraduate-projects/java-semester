package com.wudaokou.easylearn.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.wudaokou.easylearn.MainActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DataStore extends PreferenceDataStore {

    public Context context;
    public String name;

    public final String TAG = "DataStore";

    public DataStore (Context context, String name) {
        this.context = context;
        this.name = name;
    }

    @Override
    public void putBoolean(String key, boolean value) {
        // Save the value somewhere
        // TODO!!录入个人名称后需要考虑对应用户是否存在

        MyDatabase.databaseWriteExecutor.submit(new Runnable() {
            @Override
            public void run() {
//                String sql = String.format("UPDATE user SET %s = %s WHERE name = '" + "%s" +"'",
//                        key, value, name);
//                SimpleSQLiteQuery query = new SimpleSQLiteQuery(sql);
//                MyDatabase.getDatabase(context).userDAO().updateBoolean(query);
//                MyDatabase.getDatabase(context).query(query);
                Log.w(TAG, String.format("putBoolean(%s, %s)", key, Boolean.toString(value)));
                User user = MyDatabase.getDatabase(context).userDAO().loadUserByName("default");
                switch (key) {
                    case "chineseChosen":
                        user.chineseChosen = !user.chineseChosen;
                        break;
                    case "mathChosen":
                        user.mathChosen = !user.mathChosen;
                        break;
                    case "englishChosen":
                        user.englishChosen = !user.englishChosen;
                        break;
                    case "physicsChosen":
                        user.physicsChosen = !user.physicsChosen;
                        break;
                    case "chemistryChosen":
                        user.chemistryChosen = !user.chemistryChosen;
                        break;
                    case "biologyChosen":
                        user.biologyChosen = !user.biologyChosen;
                        break;
                    case "historyChosen":
                        user.historyChosen = !user.historyChosen;
                        break;
                    case "geographyChosen":
                        user.geographyChosen = !user.geographyChosen;
                        break;
                    case "politicsChosen":
                        user.politicsChosen = !user.politicsChosen;
                        break;
                    default:
                        break;
                }
                MyDatabase.getDatabase(context).userDAO().updateUser(user);
            }
        });

    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        // Retrieve the value
        // TODO!!录入个人名称后需要考虑对应用户是否存在

        Log.w(TAG, String.format("enter getBoolean(%s)", key));
        Future<Boolean> future = MyDatabase.databaseWriteExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String sql = String.format("SELECT %s from user WHERE name = '" + "%s'", key, name);
                SimpleSQLiteQuery query = new SimpleSQLiteQuery(sql);
                boolean ret = MyDatabase.getDatabase(context).userDAO().loadBoolean(query);
                Log.w(TAG, String.format("getBoolean(%s, %s)", key, Boolean.toString(ret)));
                return ret;

//                return MyDatabase.getDatabase(context).userDAO().loadUserByName(name).chineseChosen;

//                User user = MyDatabase.getDatabase(context).userDAO().loadUserByName("default");
//                switch (key) {
//                    case "chineseChosen":
//                        return user.chineseChosen;
//                    case "mathChosen":
//                        return user.mathChosen;
//                    case "englishChosen":
//                        return user.englishChosen;
//                    case "physicsChosen":
//                        return user.physicsChosen;
//                    case "chemistryChosen":
//                        return user.chemistryChosen;
//                    case "biologyChosen":
//                        return user.biologyChosen;
//                    case "historyChosen":
//                        return user.historyChosen;
//                    case "geographyChosen":
//                        return user.geographyChosen;
//                    case "politicsChosen":
//                        return user.politicsChosen;
//                    default:
//                        return true;
//                }
            }
        });

        try {
            return future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}