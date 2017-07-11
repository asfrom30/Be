package com.doyoon.android.bravenewworld.domain.firebase;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.dbStructureMap;
import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelAttribute;
import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelDir;
import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.toMakeModelKey;

/**
 * Created by DOYOON on 7/9/2017.
 */

public class FirebaseDao {

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
}
