package com.app.bahokrider.interfaces;

import com.google.android.gms.maps.model.PolylineOptions;

public interface IPathFinderListener {
    void onPath(PolylineOptions polyLine);
    void onError();
}
