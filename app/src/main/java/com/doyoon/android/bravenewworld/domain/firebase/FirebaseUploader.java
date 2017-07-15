package com.doyoon.android.bravenewworld.domain.firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

/**
 * Created by DOYOON on 7/14/2017.
 */

public class FirebaseUploader {

    private static final String TAG = FirebaseUploader.class.getSimpleName();

    public static void execute(String fromFileUri, String toFileDir, String toFileName, final Callback callback){

        FirebaseStorage.getInstance().getReference(toFileDir).child(toFileName).putFile(Uri.parse(fromFileUri))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests")
                        Uri uploadedFileUri = taskSnapshot.getDownloadUrl();
                        callback.postExecute(uploadedFileUri);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FBStroage", "Upload Fail : " + e.getMessage());
                    }
                });
    }

    public interface Callback {
        void postExecute(Uri uploadedFileUri);
    }

}
