# SwipeMenu
A handy view to reveal actions

#How to use : 
Add your xml

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
    
    You can use app:orientation to specify the reveal orientation (default : top to bottom)
    You can use app:minHeight and app:maxHeight to specify the bounds of the view
    
    
    Additionaly, you have access to a callback which will be called each time the view is refreshed, with the percentage of the view being revealed :
    
    revealLayout.setCallback(new SwipeMenu.DropDownInterface() {
                @Override
                public void onUpdate(float percentage) {
                  //You can do things here !
                }
            });
