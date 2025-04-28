package com.example.app0.utility;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class ModifiedObserver {
    // Helper Class to Run Observe ONLY ONCE.
    // Created with the aid of AI - Marcus
    public static <T> void observeOnce(LiveData<T> livedata, LifecycleOwner owner, Observer<T> observer) {
        livedata.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                livedata.removeObserver(this);
                observer.onChanged(t);
            }
        });
    }
}
