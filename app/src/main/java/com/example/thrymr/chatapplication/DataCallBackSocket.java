package com.example.thrymr.chatapplication;

import android.util.Log;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;

/**
 * Created by thrymr on 22/1/16.
 */
public class DataCallBackSocket implements DataCallback {
    @Override
    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
        Log.d("4444", "4444");
    }
}
