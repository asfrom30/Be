package com.doyoon.android.bravenewworld.presenter;

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
import com.doyoon.android.bravenewworld.presenter.fetch.MyLastLocationFetcher;
import com.doyoon.android.bravenewworld.presenter.interfaces.ActiveUserListView;
import com.doyoon.android.bravenewworld.presenter.interfaces.ActiveUserMapView;
import com.doyoon.android.bravenewworld.presenter.interfaces.ChatView;
import com.doyoon.android.bravenewworld.presenter.interfaces.FindingMapView;
import com.doyoon.android.bravenewworld.presenter.interfaces.OtherUserProfileUpdater;
import com.doyoon.android.bravenewworld.presenter.interfaces.ViewPagerMover;
import com.doyoon.android.bravenewworld.presenter.listener.ChatListener;
import com.doyoon.android.bravenewworld.presenter.listener.GeoQueryListener;
import com.doyoon.android.bravenewworld.presenter.listener.MatchingCompleteListener;
import com.doyoon.android.bravenewworld.presenter.listener.OtherUserLocationListener;
import com.doyoon.android.bravenewworld.presenter.listener.PickmeRequestListener;
import com.doyoon.android.bravenewworld.presenter.location.Locator;
import com.doyoon.android.bravenewworld.view.dialog.PickmeReceiveDialog;
import com.doyoon.android.bravenewworld.z.util.Const;
import com.doyoon.android.bravenewworld.z.util.LatLngUtil;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.key;
import static com.doyoon.android.bravenewworld.presenter.UserStatusPresenter.chatAccessKey;
import static com.doyoon.android.bravenewworld.presenter.UserStatusPresenter.myLatLng;
import static com.doyoon.android.bravenewworld.presenter.UserStatusPresenter.otherLatLng;

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

    private AppPresenter() {}

    public void initialize(AppCompatActivity activity){
        this.activity = activity;
    }

    public void runWithOutLocation(){

    }

    /**
     *
     * @param selectedType
     *
     * Add active user and get other users
     */
    public void runOnMatching(int selectedType){
        if (!UserStatusPresenter.getInstance().isNotYetChoosed()) Log.e(TAG, "User status is not 'not_yet_Choosed' 'already something choosed. It's weired..");
        if (UserStatusPresenter.activeUserType != Const.ActiveUserType.NOT_YET_CHOOSED) Log.e(TAG, "Current User Type is [" + UserStatusPresenter.activeUserType + "], User type is changed, check listener removing properly");

        /* clear all active user data in local */
        activeUserMap.clear();
        activeUserList.clear();

        /* Add Geo Query Data using Last LatLng */
        removeExistedActiveUserFromRemote();

        /* Get last location and add geo query */
        // last location callback
        // geo query callback
        // addActiveUserAndGetOtherActiveUsersFromMyLastLocation(selectedType, /* */, this.geoQueryCallbackForActiveUser);
        addActiveUserAndGetOtherActiveUsersFromMyLastLocation(selectedType);

        /* Add Matching Complete Listener */
        MatchingCompleteListener.getInstance().addMatchingCompleteListener(this.matchingCompleteListenerCallback);

        /* If Active user type is giver, add pickme receive listener */
        if(UserStatusPresenter.activeUserType == Const.ActiveUserType.Giver) {
            PickmeRequestListener.getInstance().addPickMeRequestListener(this.pickmeReceiveListener);
        }

        Log.i(TAG, "Now User is run on matching");
    }

    public void stopOnMatching(){
        //fixme remove geo listener
        MatchingCompleteListener.getInstance().removeMatchingCompleteListener();
        PickmeRequestListener.getInstance().removePickMeRequestListener();

        Log.i(TAG, "Now User is stop on matching");
    }


    /**
     * If receive the matchingcomplete.
     * stop on matching and run on finding.
     *
     * @param matchingComplete
     */
    private void receiveMatchingComplete(MatchingComplete matchingComplete){

        /* Remove PickMe Request */
        if (UserStatusPresenter.activeUserType == Const.ActiveUserType.Giver) {
            String modelDir = FirebaseHelper.getModelDir(Const.RefKey.PICK_ME_REQUEST, UserStatusPresenter.myUserAccessKey);
            FirebaseDatabase.getInstance().getReference(modelDir).removeValue();
        }

        /* remove relate to OnFinding Listener */
        RemoteDao.ActiveUser.remove(UserStatusPresenter.userStatus);
        MatchingCompleteListener.getInstance().removeMatchingCompleteListener();
        PickmeRequestListener.getInstance().removePickMeRequestListener();

        /* Get Other User Access Key */
        String otherUserAccessKey = getOtherUserAccessKey(matchingComplete);
        UserStatusPresenter.getInstance().otherUserAccessKey = otherUserAccessKey;

        /* Get Another User Profile from remote */
        FirebaseDao.read(UserProfile.class, new FirebaseDao.ReadCallback<UserProfile>() {
            @Override
            public void execute(UserProfile userProfile) {
                UserStatusPresenter.otherUserProfile = userProfile;

                /* View Update */
                notifyOtherUserProfileUpdate(userProfile);

                chatView.updateTitle();
                chatView.updateProfileView();
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

            UserStatusPresenter.chatAccessKey = chatAccessKey;
            UserStatusPresenter.locationFinderAccessKey = locationAccessKey;

            /* Update my location automatically to remote */
            Locator.getInstance().traceAndExecute(this.activity, new Locator.CustomLocationCallback() {
                @Override
                public void execute(LatLng currentLatLng, String lastUpdateTime) {
                    RemoteDao.LocationFinder.updateMyLocation(currentLatLng, UserStatusPresenter.locationFinderAccessKey, UserStatusPresenter.myUserAccessKey);
                    if(findingMapView != null) findingMapView.updateMyMarker(currentLatLng.latitude, currentLatLng.longitude);

                    UserStatusPresenter.myLatLng = new LatLng(currentLatLng.latitude, currentLatLng.longitude);
                    if (isNearEachOther()) {
                        RemoteDao.MatchingComplete.remove();
                        stopOnFinding();
                    }
                    // Log.i(TAG, currentLatLng.latitude + ", " + currentLatLng.longitude + ", " + lastUpdateTime);
                }
            });

            /* Get Other User location from remote */
            OtherUserLocationListener.getInstance().addOtherLocationListener(new OtherUserLocationListener.Callback() {
                @Override
                public void execute(LocationFinder locationFinder) {
                    if(findingMapView == null) return;
                    UserStatusPresenter.otherLatLng = new LatLng(locationFinder.getLatitude(), locationFinder.getLongitude());

                    findingMapView.updateOtherMarker(locationFinder.getLatitude(), locationFinder.getLongitude());
                    if (isNearEachOther()){
                        RemoteDao.MatchingComplete.remove();
                        stopOnFinding();
                    }
                }
            });

            /* Chat Listener */
            ChatListener.getInstance().addChatListener(new ChatListener.Callback() {
                @Override
                public void onChatAdded(Chat chat) {
                    chatView.addChat(chat);
                    chatView.notifySetChanged();
                    chatView.setFocusLastItem();
                }
            });

            UserStatusPresenter.userStatus = Const.UserStatus.ON_FINDING;



            Log.i(TAG, "Now User is Run on Finding, Listen Chat and Other User Location");
            Log.i(TAG, ", Other User is " + UserStatusPresenter.getInstance().otherUserAccessKey +
                    ", ChatAccessKey is " + chatAccessKey +
                    ", LocationAccessKey is " + locationAccessKey);
    }

    public void stopOnFinding(){
        Locator.getInstance().stopTraceAndExecute();
        OtherUserLocationListener.getInstance().removeOtherLocationListener();

        ChatListener.getInstance().removeChatListener();

        Log.i(TAG, "stop on finding");
    }

    public int getUserType() {
        return UserStatusPresenter.activeUserType;
    }

    private boolean isNearEachOther(){
        if(UserStatusPresenter.myLatLng == null || UserStatusPresenter.otherLatLng == null) return false;

        float distance = LatLngUtil.distanceBetweenTwoLatLngUnitMeter(myLatLng, otherLatLng);

        Log.i(TAG, "Distance is " + distance +"m");

        if (distance < Const.DEFAULT_FINDING_DISTANCE_M) return true;
        else return false;
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
        new PickmeReceiveDialog(pickMeRequest, new PickmeReceiveDialog.Callback(){

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

    public void fetchNextPageUserProfiles(){
        // start = 0 ~ 0
        if (activeUserProfileStartIndex >= activeUserList.size()) {
            Log.i(TAG, "end of page, can't get fetchNextPageUserProfiles");
            return;
        }

        int endIndex = activeUserProfileStartIndex + Const.PAGING_NUMBER_AT_ONCE;

        if (endIndex > activeUserList.size()) endIndex = activeUserList.size()-1;

        fetchUserProfiles(activeUserProfileStartIndex, endIndex);
        activeUserProfileStartIndex = endIndex+1;    // next Index
        Log.i(TAG, activeUserProfileStartIndex + "부터 " + endIndex + "까지 데이터를 가져오려고 시도 합니다. ");
    }


    /* View Updater */
    // Data relate to View
    private int activeUserProfileStartIndex = 0;

    private List<String> activeUserList = new ArrayList<>();
    private Map<String, ActiveUser> activeUserMap = new HashMap<>();
    private List<UserProfile> activeUserProfileList = new ArrayList();

    // View interface
    private ViewPagerMover viewPagerMover;

    private ActiveUserMapView activeUserMapView;
    private ActiveUserListView activeUserListView;

    private ChatView chatView;
    private FindingMapView findingMapView;

    private List<OtherUserProfileUpdater> otherUserProfileUpdaterList = new ArrayList<>();
    public void addOtherUserProfileUpdater(OtherUserProfileUpdater updater){
        otherUserProfileUpdaterList.add(updater);
    }

    public void notifyOtherUserProfileUpdate(UserProfile userProfile) {
        for (OtherUserProfileUpdater updater : otherUserProfileUpdaterList) {
            updater.otherUserProfileUpdate(userProfile);
        }
    }

    public void setViewPagerMover(ViewPagerMover viewPagerMover) {
        this.viewPagerMover = viewPagerMover;
    }

    public void setActiveUserMapView(ActiveUserMapView activeUserMapView) {
        this.activeUserMapView = activeUserMapView;
    }

    public void setActiveUserListView(ActiveUserListView activeUserListView) {
        this.activeUserListView = activeUserListView;
    }

    /* private method */
    private static void removeExistedActiveUserFromRemote() {
        RemoteDao.ActiveUser.remove(Const.ActiveUserType.Giver);
        RemoteDao.ActiveUser.remove(Const.ActiveUserType.Taker);
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
                    activeUserListView.update();

                    Log.i(TAG, "Fetch UserProfile Successful : " + userProfile.toString());
                }
            });
        }
    }

    /** Get Last Location,
     *  After that Add ActiveUser Depend on last location and ActiveUser type,
     *  and get other active users from Remote */
    private void addActiveUserAndGetOtherActiveUsersFromMyLastLocation(final int selectedType) {
        MyLastLocationFetcher.getInstance().fetch(this.activity, new MyLastLocationFetcher.Callback() {
            @Override
            public void execute(LatLng latLng) {
                Log.i(TAG, "Get User's last location Complete, Execute Callback");

                RemoteDao.ActiveUser.insert(latLng);
                GeoQueryListener.getInstance().listen(selectedType, latLng, geoQueryCallbackForActiveUser);
                postRegisterActiveUser();
            }
        });
    }

    private void postRegisterActiveUser(){
        UserStatusPresenter.userStatus = Const.UserStatus.ON_MATCHING;

        /* Update View */
    }

    /* Create Callback Method */
    private GeoQueryListener.Callback geoQueryCallbackForActiveUser = new GeoQueryListener.Callback() {
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
        public void onKeyExited() {
            /* Don't remove key and object in activeUserMap and activeUserList */
            // todo remove or not??
            activeUserMapView.resetMarker(activeUserMap);
            activeUserMap.get(key).setActive(false);
            Log.i(TAG, "Remove Geo Query : Active user [" + key + "] is now deactive");
        }

        @Override
        public void onGeoQueryReady() {
            fetchNextPageUserProfiles();
            activeUserMapView.resetMarker(activeUserMap);
            Log.i(TAG, "On Geo Query Ready");
        }
    };

    private MatchingCompleteListener.Callback matchingCompleteListenerCallback = new MatchingCompleteListener.Callback() {
        @Override
        public void execute(MatchingComplete matchingComplete) {
            receiveMatchingComplete(matchingComplete);
        }
    };

    private PickmeRequestListener.Callback pickmeReceiveListener = new PickmeRequestListener.Callback() {
        @Override
        public void execute(PickMeRequest pickMeRequest) {
            receivePickMeRequest(pickMeRequest);
        }
    };

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

    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
    }

    public void setFindingMapView(FindingMapView findingMapView) {
        this.findingMapView = findingMapView;
    }

    public String getCurrentChatAccessKey() {
        return chatAccessKey;
    }



}
