# AudioClips
音频剪辑工具,能自定义许多元素
使用
-----------------------------
dependencies {
    compile 'com.luowei.warmheart:audioclip:+'
}

配置
-------------
<com.luowei.audioclip.ClipsFrameLayout
        android:id="@+id/clipsFrameLayout"
        android:layout_width="match_parent"
        android:layout_height="210dp"
        app:clip_background="@drawable/bg_clips"
        app:clip_clipsMinSecond="10"
        app:clip_endId="@+id/cmtvEnd"
        app:clip_progressHeight="180dp"
        app:clip_point_width="1dp"
        app:clip_startId="@+id/cmtvStart">

        <ImageView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:scaleType="fitXY"
            android:src="@drawable/img_graduation" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:text="0:00"
            android:textColor="#eee"
            android:textSize="14sp" />

        <TextView
            android:id="@+id/tvDuration"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="right"
            android:layout_marginTop="10dp"
            android:text="3:00"
            android:textColor="#eee"
            android:textSize="14sp" />

        <com.luowei.audioclip.ClipsMarkerTextView
            android:id="@+id/cmtvStart"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:drawableTop="@drawable/ic_start_clips"
            android:gravity="bottom|center_horizontal"
            android:paddingLeft="5dp"
            android:paddingRight="5dp"
            android:paddingTop="170dp"
            android:textColor="#888"
            android:textSize="14sp" />

        <com.luowei.audioclip.ClipsMarkerTextView
            android:id="@+id/cmtvEnd"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:drawableTop="@drawable/ic_end_clips"
            android:gravity="bottom|center_horizontal"
            android:paddingLeft="5dp"
            android:paddingRight="5dp"
            android:paddingTop="170dp"
            android:textColor="#888"
            android:textSize="14sp" />
    </com.luowei.audioclip.ClipsFrameLayout>
