package com.app.bahokrider.retrofit;

public interface IServiceCallbacks<T> {

    void onResponse(T response, String requestTag);

    void onError(String errorMessage);

}
