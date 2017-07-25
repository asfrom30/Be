
### Summary

In my case, I realize User Status is important on the way. That is not enough which is using just `Condition`,
so I'm late but I separate User Status four, `NOT_YET_MATCHED`, `ON_FINDING`, `ON_MATCHING`, `ON_TOGEHTER`
 
the other focus is apply MVP Pattern, the advantage of MVP Pattern is I can change my view model easily.

and finally I try to data every data keeps always up-to-date

And also, using Interface and Abstract class I try to keep every class has recycleable and reuse.

I really hope this code helps even a little.

### Fetures
#### prevent memory leak

#### Keep all data recently.
When user changed profile Image, It does not mean only update current view. Espeacially in view pager case.
becase View Pager always create three views for Left and Right swiping. In order to solve this problem. I made Interface for UserProfileView.
and Add All views at Presenter and notify.


### Behind

#### Google API on Walking
Finding the path on warlking in google api is not working properyl. So if you consider any service has using walking path.
Consider another api..

### Util update

* Dialog manager
* Firebase `Get Model Path` and `Read` at Once in FirebaseDao class using FirebaseModel class
* String left and right padding

### Solved Problem
* RealtimeBlurView -> Rounded Blur view -> Top roundedBlurView -> Path

### Reference
#### Gradle
* [Realtime Blur](https://android-arsenal.com/details/1/4409)
#### API
* [Google Maps Android Api v2 Sample](https://github.com/googlemaps/android-samples)

#### Activity LifeCycle
* [About on Destroy](https://stackoverflow.com/questions/18361719/android-activity-ondestroy-is-not-always-called-and-if-called-only-part-of-the)

#### View
* [Set Color Image View](https://stackoverflow.com/questions/38653357/how-to-set-color-for-imageview-in-android)
* [remove ListView divider](https://stackoverflow.com/questions/5414902/how-to-remove-the-border-in-a-listview)
* [Claculate between two point latlng](https://stackoverflow.com/questions/14394366/find-distance-between-two-points-on-map-using-google-map-api-v2)
* [Fragment Transaction Slide in Slide out](https://stackoverflow.com/questions/21026409/fragment-transaction-animation-slide-in-and-slide-out)
* [Fragment animation back stack](https://stackoverflow.com/questions/10886669/how-to-reverse-fragment-animations-on-backstack)
###
-. 가장 어려웠던 점은... 시간이 걸리는 작업의 경우가 여러개 있는 경우
 User status를 반영하는 타이밍을 정하기 어려웠던 점입니다.