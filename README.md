# SwipeMenu
A handy view to reveal actions (API 8+)

![Alt text](https://github.com/NicolasSimon/SwipeMenu/blob/master/record/recording.gif "Example")




#How to use : 
1 ) Add the view to your xml file

<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/rootLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    tools:context=".activity.SwipeExample">

    <view.SwipeMenu
        android:id="@+id/revealLayout"
        android:layout_alignParentBottom="true"
        app:orientation="BottomToTop"
        app:minHeight="@dimen/reveal_layout_min_size"
        app:maxHeight="@dimen/reveal_layout_max_size"
        android:orientation="vertical"
        android:gravity="bottom"
        android:layout_width="match_parent"
        android:layout_height="@dimen/reveal_layout_min_size">
        <ImageView
            android:layout_marginStart="5dp"
            android:layout_marginEnd="5dp"
            android:background="@mipmap/material_flat"
            android:scaleType="centerCrop"
            android:layout_width="match_parent"
            android:layout_height="@dimen/reveal_layout_diff"/>
        <TextView
            android:layout_gravity="bottom"
            android:id="@+id/textField"
            android:textColor="@android:color/white"
            android:textSize="20sp"
            android:gravity="center"
            android:background="@color/colorPrimaryDark"
            android:layout_width="match_parent"
            android:layout_height="@dimen/reveal_layout_min_size"
            android:text="Pull me up!" />
    </view.SwipeMenu>
</RelativeLayout>

    
    
You can use app:orientation to specify the reveal orientation (default : top to bottom)
You can use app:minHeight and app:maxHeight to specify the bounds of the view
    
Additionaly, you have access to a callback which will be called each time the view is refreshed, with the percentage of the view being revealed :
    
    revealLayout.setCallback(new SwipeMenu.DropDownInterface() {
                @Override
                public void onUpdate(float percentage) {
                  //You can do things here !
                }
            });
