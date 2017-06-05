package com.tomar.udacity.popularmovies.recievers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tomar.udacity.popularmovies.utilities.NetworkUtils;

public class NetworkChangeBroadcastReceiver extends BroadcastReceiver{

    OnNetworkConnectedListener mOnNetworkConnectedListener;
    public interface OnNetworkConnectedListener{
        public void onNetworkConnected();
    }

    public NetworkChangeBroadcastReceiver(OnNetworkConnectedListener onNetworkConnectedListener){
        mOnNetworkConnectedListener = onNetworkConnectedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            mOnNetworkConnectedListener.onNetworkConnected();
        }
    }
}
