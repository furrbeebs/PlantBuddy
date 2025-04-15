package com.example.app0.utility;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class ModifiedObserver {
    // Helper Class to Run Observe ONLY ONCE
    public static <T> void observeOnce(LiveData<T> livedata, LifecycleOwner owner, Observer<T> observer) {
        livedata.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                livedata.removeObserver(this); // ✅ remove after first call
                observer.onChanged(t);         // ✅ pass the value through
            }
        });
    }
}
