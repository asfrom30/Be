package com.doyoon.android.bravenewworld.presenter;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.doyoon.android.bravenewworld.domain.RemoteDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.geovalue.ActiveUser;
import com.doyoon.android.bravenewworld.domain.firebase.value.Chat;
import com.doyoon.android.bravenewworld.domain.firebase.value.ChatProfile;
import com.doyoon.android.bravenewworld.domain.firebase.value.LocationFinder;
import com.doyoon.android.bravenewworld.domain.firebase.value.MatchingComplete;
import com.doyoon.android.bravenewworld.domain.firebase.value.PickMeRequest;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.interfaces.ActiveUserListUIController;
import com.doyoon.android.bravenewworld.presenter.interfaces.ActiveUserMapController;
import com.doyoon.android.bravenewworld.presenter.interfaces.ChatUIController;
import com.doyoon.android.bravenewworld.presenter.interfaces.LocationUIController;
import com.doyoon.android.bravenewworld.presenter.interfaces.ViewPagerMover;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.view.dialog.PickmeRequestNoticeDialog;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelDir;

/**
 * Created by DOYOON on 7/16/2017.
 */

public class AppPresenter {

    private static final String TAG = AppPresenter.class.getSimpleName();

    public static AppPresenter instance;

    public static AppPresenter getInstance(){
        if (instance == null) {
            instance = new AppPresenter();
        }
        return instance;
    }

    /* member variable */
    private AppCompatActivity activity;
    private FusedLocationProviderClient mFusedLocationClient;

    /* Communicate with fragment */
    private ViewPagerMover viewPagerMover;
    private ActiveUserMapController activeUserMapController;
    private ActiveUserListUIController activeUserListUIController;

    /* Shared Preference */

    private int startIndexForFetchUser = 0;

    private AppPresenter() {

    }

    public void initialize(AppCompatActivity activity){
        this.activity = activity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void runWithOutLocation(){

    }

    public void runOnMatching(){

        if (UserStatusPresenter.getInstance().isOnFinding()) {
            Log.e(TAG, "User Already on the matching. It's weired..");
        }

        if (UserStatusPresenter.activeUserType != Const.ActiveUserType.NOT_YET_CHOOSED) {
            Log.e(TAG, "Current User Type is [" + UserStatusPresenter.activeUserType + "], User type is changed, check listener removing properly");
        }

        RemoteDao.ActiveUser.remove(Const.ActiveUserType.Giver);
        RemoteDao.ActiveUser.remove(Const.ActiveUserType.Taker);

        getLastLocation(new LocationCallback() {
            @Override
            public void execute(LatLng latLng) {
                /* Register Active User */
                UserStatusPresenter.userStatus = Const.UserStatus.ON_MATCHING;
                registerActiveUser(latLng);

                /* Add Geo Query Data using Last LatLng */
                activeUserMap.clear();
                activeUserList.clear();
                toEnableActiveUserListListener(UserStatusPresenter.activeUserType, latLng, Const.DEFAULT_SEARCH_DISTANCE_KM);

                Log.e(TAG, "Get Query Complete");
            }
        });

        this.addMatchingCompleteListener();

        if(UserStatusPresenter.activeUserType == Const.ActiveUserType.Giver) {
            this.addPickMeRequestListener();
        }

        Log.e(TAG, "Add Listener Complete");
    }

    public void stopOnMatching(){
        //fixme remove geo listener
        removeMatchingCompleteListener();
        removePickMeRequestListener();
    }

    /**
     *
     * @param latLng
     */
    private void registerActiveUser(LatLng latLng){
        RemoteDao.ActiveUser.insert(latLng);
    }

    @SuppressWarnings("MissingPermission")
    public void getLastLocation(final LocationCallback callback) {
        if (this.activity == null) {
            Log.e(TAG, "Not Initialized yet. Can't get Last Location");
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this.activity, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            callback.execute(latLng);
                            Log.i(TAG, "Last Location Update Complete, My Last Location is " + latLng.toString());

                        } else {
                            Log.e(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    public int getUserType() {
        return UserStatusPresenter.activeUserType;
    }

    public interface LocationCallback {
        void execute(LatLng latLng);
    }

    private void receiveMatchingComplete(MatchingComplete matchingComplete){

        /* Remove Matching Complete Value */
        // RemoteDao.MatchingComplete.remove();

        /* Remove PickMe Request */
        if (UserStatusPresenter.activeUserType == Const.ActiveUserType.Giver) {
            String modelDir = FirebaseHelper.getModelDir(Const.RefKey.PICK_ME_REQUEST, UserStatusPresenter.myUserAccessKey);
            FirebaseDatabase.getInstance().getReference(modelDir).removeValue();
        }

        /* remove relate to OnFinding Listener */
        RemoteDao.ActiveUser.remove(UserStatusPresenter.userStatus);
        removeMatchingCompleteListener();
        removePickMeRequestListener();

        /* Get Other User Access Key */
        String otherUserAccessKey = getOtherUserAccessKey(matchingComplete);
        UserStatusPresenter.getInstance().otherUserAccessKey = otherUserAccessKey;

        /* Get Another User Profile from remote */
        FirebaseDao.read(UserProfile.class, new FirebaseDao.ReadCallback<UserProfile>() {
            @Override
            public void execute(UserProfile userProfile) {
                UserStatusPresenter.otherUserProfile = userProfile;
                chatUIController.updateTitle();
                chatUIController.updateProfileView();
            }
        }, otherUserAccessKey);

        /* Stop Matching(Activie User) */
        this.stopOnMatching();


        /* Run Finding */
        String chatAccessKey = matchingComplete.getChatAccessKey();
        String locationAccessKey = matchingComplete.getLocationAccessKey();
        this.runOnFinding(locationAccessKey, chatAccessKey);

        /* Change View */
        this.viewPagerMover.moveViewPage(1);
    }

    public void runOnFinding(String locationAccessKey, String chatAccessKey){

        UserStatusPresenter.currentChatAccessKey = chatAccessKey;
        UserStatusPresenter.locationFinderAccessKey = locationAccessKey;

        addChatListener(chatAccessKey);
        addOtherLocationListener();

        UserStatusPresenter.userStatus = Const.UserStatus.ON_FINDING;

        Log.i(TAG, "Now User is Run on Finding, Listen Chat and Other User Location");
        Log.i(TAG, ", Other User is " + UserStatusPresenter.getInstance().otherUserAccessKey +
                ", ChatAccessKey is " + chatAccessKey +
                ", LocationAccessKey is " + locationAccessKey);
    }

    public void stopOnFinding(){
        removeChatListener();
        removeOtherLocationListener();
    }

    private String getOtherUserAccessKey(MatchingComplete matchingComplete){

        String otherUserAccessKey;
        if (UserStatusPresenter.activeUserType == Const.ActiveUserType.Giver) {
            otherUserAccessKey = matchingComplete.getTakerAccessKey();
        } else {
            otherUserAccessKey = matchingComplete.getGiverAccessKey();
        }

        return otherUserAccessKey;
    }

    private void receivePickMeRequest(PickMeRequest pickMeRequest){
        new PickmeRequestNoticeDialog(pickMeRequest, new PickmeRequestNoticeDialog.Callback(){

        }).show(this.activity.getSupportFragmentManager(), null);
        // Create Pick Me Response

        // and send
        // inviteDialog.showInvitedDialog(pickMeRequest);

        // todo readed pickMeRequest must be deleted...
        // dataSnapshot.getRef().removeValue();
    }

    /* Match Complete in Dialog when user accept pick me request notice */
    public void acceptPickmeRequestNotice(PickMeRequest pickMeRequest){
        String modelDir = FirebaseHelper.getModelDir(Const.RefKey.CHAT_ROOM);
        String chatAccessKey = FirebaseDatabase.getInstance().getReference(modelDir).push().getKey();

        /* ChatProfile Manual Input for Remember Auto Generate Key */
        String otherUserAccesskey = pickMeRequest.getFromUserAccessKey();
        ChatProfile chatProfile = new ChatProfile(chatAccessKey);
        chatProfile.setGiverKey(UserStatusPresenter.myUserAccessKey);
        chatProfile.setTakerKey(otherUserAccesskey);
        FirebaseDao.insert(chatProfile, chatAccessKey);
        // FirebaseDatabase.getInstance().getReference(modelDir + chatAccessKey \).setValue(chatProfile);

        /* Location Service를 개설하고 상대방에게 채팅방 키를 전달해줍니다. */
        String locationAccessKey = RemoteDao.LocationFinder.insertWithGetRandomKey(UserStatusPresenter.lastLatLng);

        /* 채팅방을 개설하고 상대방에게 채팅방 키를 전달해줍니다 */
        MatchingComplete matchingComplete = new MatchingComplete(UserStatusPresenter.myUserAccessKey, otherUserAccesskey, chatAccessKey, locationAccessKey);
        FirebaseDao.insert(matchingComplete, otherUserAccesskey);
        FirebaseDao.insert(matchingComplete, UserStatusPresenter.myUserAccessKey);
    }

    /* Firebase : Matching Complete Listener on Firebase */
    private ValueEventListener matchingCompleteListener;

    private void addMatchingCompleteListener(){
        if (matchingCompleteListener != null) {
            removeMatchingCompleteListener();
        }

        final String modelPath = FirebaseHelper.getModelPath(Const.RefKey.MATCHING_COMPLETE, UserStatusPresenter.myUserAccessKey);
        Log.e(TAG, "matching complete model path is " + modelPath);
        this.matchingCompleteListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MatchingComplete matchingComplete = dataSnapshot.getValue(MatchingComplete.class);
                // todo if value is empty but run first at once???
                if (matchingComplete == null) {
                    Log.e(TAG, "matching complete is null");
                    return;
                }
                Log.e(TAG, matchingComplete.toString());
                receiveMatchingComplete(matchingComplete);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseDatabase.getInstance().getReference(modelPath).addValueEventListener(this.matchingCompleteListener);
    }

    private void removeMatchingCompleteListener(){
        if (this.matchingCompleteListener == null) {
            Log.e(TAG, "matchingCompleteListener already null, can't remove complete listener");
            return;
        }
        String modelPath = FirebaseHelper.getModelPath(Const.RefKey.MATCHING_COMPLETE, UserStatusPresenter.myUserAccessKey);
        FirebaseDatabase.getInstance().getReference(modelPath).removeEventListener(this.matchingCompleteListener);
        Log.i(TAG, "removeMatchingCompleteListener Succesfully");
    }

    /* Firebase :  Add PickMerequestListener, refer key is "invite" */
    private ChildEventListener pickmeRequestListener;

    private void addPickMeRequestListener(){

        if (this.pickmeRequestListener != null) {
            removePickMeRequestListener();
        }

        String modelDir = getModelDir(Const.RefKey.PICK_ME_REQUEST, UserStatusPresenter.myUserAccessKey);
        FirebaseDatabase.getInstance().getReference(modelDir).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // todo  남은 메세지수 몇개... 방향키로 메세지 이동할 수 있게 할 것...
                // todo Message를 add만 하고 update ui하게끔 onChilAdded와 같은 Thread를 쓰면 안된다.
                // todo 이것은 그냥 데이터만 추가하고.... 다른 쓰레드에서 update를 갱신할 수 있도록
                // todo 여기에 다 추가하면... firebase firebase 업데이트 속도가 느려질 수 있겠지만 큰 상관은 없을듯...
                PickMeRequest pickMeRequest = dataSnapshot.getValue(PickMeRequest.class);
                receivePickMeRequest(pickMeRequest);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onChildRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "onCancelled");
            }
        });
    }

    private void removePickMeRequestListener(){
        if (this.pickmeRequestListener == null) {
            Log.e(TAG, "pickmeRequestListener already null, can't remove complete listener");
            return;
        }

        String modelDir = getModelDir(Const.RefKey.PICK_ME_REQUEST, UserStatusPresenter.myUserAccessKey);
        FirebaseDatabase.getInstance().getReference(modelDir).removeEventListener(pickmeRequestListener);
        Log.i(TAG, "removePickMeRequestListener Succesfully");
    }

    /* Firebase :   Add Firebase Listener for Geo Data */
    private GeoQuery geoQuery = null;

    private void toEnableActiveUserListListener(int userType, LatLng latLng, double distance_km) {
        /* Valid Query Check */
        if (latLng == null) {
            Log.i(TAG, "Latlng is null, can't be set last geo location qeury");
            return;
        }
        if (distance_km <= 0) {
            Log.i(TAG, "Distance Query can't be set under zero");
            return;
        }

        /* Release Geo Query Remove All Listener Before getQuery */
        this.detachGeoQuery(this.geoQuery);

        /* Prepare Query */
        GeoLocation lastGeoLocationQuery = new GeoLocation(latLng.latitude, latLng.longitude);
        String userTypeQuery = getQueryDependOnUserType(userType);
        double distanceQuery = distance_km;

        /* reset get Query */
        geoQuery = this.toBuildGeoQuery(lastGeoLocationQuery, userTypeQuery, distanceQuery);
        addGeoQueryListener(geoQuery);

        Log.i(TAG, "Add Geo Query Successfully");
    }

    private void detachGeoQuery(GeoQuery geoQuery) {
        if (this.geoQuery != null) {
            this.geoQuery.removeAllListeners();
            this.geoQuery = null;

            if (this.geoQuery == null) {
                Log.i(TAG, "Before Geo Query Is Detached Successful");
            }
        }
    }

    private GeoQuery toBuildGeoQuery(GeoLocation coordiGeoLocationQuery, String userTypeQuery, double distanceQuery){
        String modelDir = getModelDir(userTypeQuery);
        GeoQuery geoQuery = new GeoFire(FirebaseDatabase.getInstance().getReference(modelDir)).queryAtLocation(coordiGeoLocationQuery, distanceQuery);
        return geoQuery;
    }

    private List<String> activeUserList = new ArrayList<>();
    private Map<String, ActiveUser> activeUserMap = new HashMap<>();

    private void addGeoQueryListener(GeoQuery geoQuery) {
        if (geoQuery == null) {
            Log.e(TAG, "Geo Query is null, Can't not add Listener");
            return;
        }
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                LatLng latLng = new LatLng(location.latitude, location.longitude);
                ActiveUser activeUser = new ActiveUser(key, latLng);

                // 한번 등록되었다가 등록되지 않은 유저가 다시 active로 올경우 key가 중복될수 있으니 이경우 다시 active를 해줘야 한다.
                activeUserMap.put(key, activeUser);
                activeUserList.add(key);

                /* No need this method... validate distance */
                // float distance_m = LatLngUtil.distanceBetweenTwoLatLngUnitMeter(lastLatLng, activeUser.getLatLng());
                // Log.i(TAG, "ADD Active User Complete " + activeUser.getKey() + ", distance(m) is " + distance_m);
            }

            @Override
            public void onKeyExited(String key) {
                /* Don't remove key and object in activeUserMap and activeUserList */
                // todo remove or not??
                activeUserMap.get(key).setActive(false);
                activeUserMapController.resetMarker(activeUserMap);
                Log.i(TAG, "Remove Geo Query : Active user [" + key + "] is now deactive");
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                fetchNextPageUserProfiles();
                activeUserMapController.resetMarker(activeUserMap);
                Log.i(TAG, "On Geo Query Ready");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private String getQueryDependOnUserType(int userType){
        if (userType == Const.ActiveUserType.Giver) {
            return Const.QueryKey.TAKER;
        } else {
            return Const.QueryKey.GIVER;
        }
    }

    /* Firebase : run Location Listener */
    private LocationUIController locationUIController;
    private ValueEventListener locationListener;

    private void addOtherLocationListener(){
        String locationAccessKey = UserStatusPresenter.getInstance().locationFinderAccessKey;
        String otherUserAccessKey = UserStatusPresenter.getInstance().otherUserAccessKey;

        if(locationAccessKey == null) return;
        if(otherUserAccessKey == null) return;

        if(locationListener != null) removeOtherLocationListener();

        this.locationListener = RemoteDao.LocationFinder.addListener(locationAccessKey, otherUserAccessKey
                , new RemoteDao.LocationFinder.Callback(){
                    @Override
                    public void execute(LocationFinder locationFinder) {
                        if(locationFinder == null) return;
                        // todo latitude and longitude validate check
                        if(locationFinder.getLatitude() == 0 || locationFinder.getLongitude() == 0) return;
                        locationUIController.updateOtherMarker(locationFinder.getLatitude(), locationFinder.getLongitude());
                    }
                });

        Log.i(TAG, "Add Location Listener Successfull");
    }


    private void removeOtherLocationListener() {
        String locationAccessKey = UserStatusPresenter.getInstance().locationFinderAccessKey;
        String otherUserAccessKey = UserStatusPresenter.getInstance().otherUserAccessKey;

        if(locationAccessKey == null) return;
        if(otherUserAccessKey == null) return;

        RemoteDao.LocationFinder.removeListener(locationAccessKey, otherUserAccessKey, this.locationListener);
        locationListener = null;

        Log.i(TAG, "remove Location Listener Successfull");
    }

    /* Firebase : run Chat Listener */
    private ChatUIController chatUIController;
    private String currentChatAccessKey;
    private ChildEventListener chatListener;

    public void addChatListener(String chatAccessKey){
        if (chatAccessKey == null) {
            Log.i(TAG, "Access key is null, addChatListener can't start");
            return;
        }

        if (chatListener != null) {
            removeChatListener();
            Log.i(TAG, "Chat Listener is not null. Try to remove chat listener.");
        }

        this.currentChatAccessKey = chatAccessKey;

        String modelDir = FirebaseHelper.getModelDir(Const.RefKey.CHAT, chatAccessKey);
        FirebaseDatabase.getInstance().getReference(modelDir).addChildEventListener(this.chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (chat == null) {
                    Log.e(TAG, "Chat is null");
                    return;
                }
                chatUIController.addChat(chat);
                chatUIController.notifySetChanged();
                chatUIController.setFocusLastItem();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeChatListener(){

        if (currentChatAccessKey == null) {
            Log.e(TAG, "currentChatAccessKey is null, can't removeChatListener");
            return;
        }

        if (this.chatListener != null) {
            Log.e(TAG, "chatListener already null, can't removeChatListener");
            return;
        }

        String modelDir = FirebaseHelper.getModelDir(Const.RefKey.CHAT, currentChatAccessKey);
        FirebaseDatabase.getInstance().getReference(modelDir).removeEventListener(this.chatListener);
        this.currentChatAccessKey = null;
    }

    private List<UserProfile> activeUserProfileList = new ArrayList();

    public void fetchNextPageUserProfiles(){
        // start = 0 ~ 0
        if (startIndexForFetchUser >= activeUserList.size()) {
            Log.e(TAG, "end of page, can't get fetchNextPageUserProfiles");
            return;
        }

        int endIndex = startIndexForFetchUser + Const.PAGING_NUMBER_AT_ONCE;

        if (endIndex > activeUserList.size()) {
            endIndex = activeUserList.size()-1;
        }

        Log.i(TAG, startIndexForFetchUser + "부터 " + endIndex + "까지 데이터를 가져오려고 시도 합니다. ");
        fetchUserProfiles(startIndexForFetchUser, endIndex);
        startIndexForFetchUser = endIndex+1;    // next Index
    }

    private void fetchUserProfiles(int startIndex, int endIndex) {
        for (int i = startIndex; i <= endIndex; i++) {
            String userAccessKey = activeUserList.get(i);
            RemoteDao.FetchUserProfile.execute(userAccessKey, new RemoteDao.FetchUserProfile.Callback() {
                @Override
                public void postExecute(DataSnapshot dataSnapshot) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    /* Update UI */
                    activeUserProfileList.add(userProfile);
                    activeUserListUIController.update();

                    Log.i(TAG, "Fetch UserProfile Successful : " + userProfile.toString());
                }
            });
        }
    }

    /* Getter and Setter */
    public Map<String, ActiveUser> getActiveUserMap() {
        return this.activeUserMap == null ? null : this.activeUserMap;
    }

    public List<UserProfile> getActiveUserProfileList(){
        if (this.activeUserProfileList == null) {
            return null;
        }
        return activeUserProfileList;
    }

    public void setViewPagerMover(ViewPagerMover viewPagerMover) {
        this.viewPagerMover = viewPagerMover;
    }

    public void setActiveUserMapController(ActiveUserMapController activeUserMapController) {
        this.activeUserMapController = activeUserMapController;
    }

    public void setActiveUserListUIController(ActiveUserListUIController activeUserListUIController) {
        this.activeUserListUIController = activeUserListUIController;
    }

    public void setChatUIController(ChatUIController chatUIController) {
        this.chatUIController = chatUIController;
    }

    public void setLocationUIController(LocationUIController locationUIController) {
        this.locationUIController = locationUIController;
    }

    public String getCurrentChatAccessKey() {
        return currentChatAccessKey;
    }
}
