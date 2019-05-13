package com.example.android.myapplication.Widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ListRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}