package com.doyoon.android.bravenewworld.domain.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.dbStructureMap;
import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelAttribute;
import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelDir;
import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelPath;
import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.toMakeModelKey;

/**
 * Created by DOYOON on 7/9/2017.
 */

public class FirebaseDao {

    private static String TAG = FirebaseDao.class.getSimpleName();

    // todo 고민... 용어 통일 node, tag.. ref...
    public static <T extends FirebaseModel> String insert(T t, String... accessKeys) {

        String modelName = FirebaseHelper.getModelName(t);

        if (dbStructureMap == null) {
            throw new RuntimeException("DB Structure Map is null, Db structure is not executed yet.");
        }

        String modelDir = getModelDir(modelName, accessKeys);
        String modelKey = toMakeModelKey(modelName, t, accessKeys); //todo .... get Key First if not... create??
        t.setKey(modelKey);

        /* Build db path... */
        String modelPath = modelDir + modelKey;
        Log.e("Check model path", modelPath);
        FirebaseDatabase.getInstance().getReference(modelPath).setValue(t);

        // printInsertLog(modelName, isBundle, isAutoGenerateModelKey, modelDir, modelPath);

        return modelPath;
    }

    public static String insert(String modelName, boolean bool, String... accessKeys) {

        HashMap modelAttributeMap = getModelAttribute(modelName);
        String modelDir = getModelDir(modelName, accessKeys);
        String modelPath = modelDir + modelName;
        FirebaseDatabase.getInstance().getReference(modelPath).setValue(bool);

        return modelPath;
    }

    @Deprecated
    public static void delete(String modelName, String modelKey, String... accessKeys){
        String modelDir = FirebaseHelper.getModelPath(modelName, accessKeys);
        Log.e(TAG, modelDir + " Try to remove");
        FirebaseDatabase.getInstance().getReference(modelDir + modelKey).setValue(null);
    }

    public static <T extends FirebaseModel> void read (final Class<T> t, final FirebaseDao.ReadCallback<T> readCallback, String... accessKeys) {
        String modelName = t.getSimpleName().toLowerCase();
        Log.i(TAG, "model name은..." + modelName);

        String modelPath = getModelPath(modelName, accessKeys);

        if (modelPath == null) {
            Log.e(TAG, "Getting [" + modelName + "]'s path is null, you can not read Firebase Model Value, Model Name is incorrect or accesskeys null");
            return;
        }

        FirebaseDatabase.getInstance().getReference(modelPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                T model = dataSnapshot.getValue(t);
                readCallback.execute(model);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });
    }

    public interface ReadCallback<T extends FirebaseModel> {
        void execute(T t);
    }
}
