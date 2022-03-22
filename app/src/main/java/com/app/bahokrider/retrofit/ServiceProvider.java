package com.app.bahokrider.retrofit;

import androidx.annotation.NonNull;

import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.utils.AppConstants;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceProvider implements Callback {

    private final IServices services;
    private final IServiceCallbacks serviceCallbacks;
    private static ServiceProvider serviceProvider = null;

    public static synchronized ServiceProvider getInstance(IServiceCallbacks serviceCallbacks) {
        if(serviceProvider == null){
            serviceProvider = new ServiceProvider(serviceCallbacks);
        }
       return serviceProvider;
    }

    private ServiceProvider(IServiceCallbacks serviceCallbacks) {
        this.serviceCallbacks = serviceCallbacks;
        this.services = RetrofitClient.getRetrofit().create(IServices.class);
    }

    public void requestOTP(String mobile) {
        services.requestOTP(mobile).enqueue(this);
    }

    public void verifyOTP(String mobile, String deviceToken) {
        services.verifyOTP(mobile, deviceToken).enqueue(this);
    }

    public void getProfile(String userId) {
        services.getProfile(userId).enqueue(this);
    }


    public void register(MultipartBody.Part image,
                         MultipartBody.Part adharFront,
                         MultipartBody.Part adharBack,
                         MultipartBody.Part licenseFront,
                         MultipartBody.Part licenseBack,
                         RequestBody firstName,
                         RequestBody lastName,
                         RequestBody email,
                         RequestBody contactNo,
                         RequestBody adharNo,
                         RequestBody licenseNo) {
        services.register(image, adharFront, adharBack, licenseFront, licenseBack,
                firstName, lastName, email, contactNo, adharNo, licenseNo).enqueue(this);
    }


    public void registerRestaurant(MultipartBody.Part pic,
                                   RequestBody rbName,
                                   RequestBody rbEmail,
                                   RequestBody rbMobile,
                                   RequestBody rbLoc,
                                   RequestBody rbLat,
                                   RequestBody rbLong,
                                   RequestBody rbPass,
                                   RequestBody rbDesc) {
        services.registerRestaurant(pic, rbName, rbEmail, rbMobile, rbLoc,
                rbLat, rbLong, rbPass, rbDesc).enqueue(this);
    }


    public void updateProfile(MultipartBody.Part image,
                              RequestBody userId,
                              RequestBody firstName,
                              RequestBody lastName) {
        services.updateProfile(image, userId, firstName, lastName).enqueue(this);
    }

    public void uploadSelfi(MultipartBody.Part image,
                            RequestBody userId) {
        services.uploadSelfi(image, userId).enqueue(this);
    }


    public void getOrders(String userId) {
        services.getOrders(userId).enqueue(this);
    }

    public void getOrderHistory(String userId) {
        services.getOrderHistory(userId).enqueue(this);
    }

    public void getCurrentOrder(String userId) {
        services.getCurrentOrder(userId).enqueue(this);
    }

    public void getHomeData(String userId) {
        services.getHomeData(userId).enqueue(this);
    }


    public void acceptOrder(RequestBody rbOrderId, RequestBody rbRiderId, RequestBody rbStatus) {
        services.acceptOrder(rbOrderId, rbRiderId, rbStatus).enqueue(this);
    }

    public void rejectOrder(RequestBody rbOrderId, RequestBody rbRiderId, RequestBody rbStatus) {
        services.rejectOrder(rbOrderId, rbRiderId, rbStatus).enqueue(this);
    }

    public void deliverOrder(RequestBody rbOrderId, RequestBody rbStatus) {
        services.deliverOrder(rbOrderId, rbStatus).enqueue(this);
    }

    public void activeInactiveRider(RequestBody rbRiderId, RequestBody rbStatus) {
        services.activeInactiveRider(rbRiderId, rbStatus).enqueue(this);
    }

    public void addBalance(RequestBody rbRiderId, RequestBody rbTransId, RequestBody rbAmount) {
        services.addBalance(rbRiderId, rbTransId, rbAmount).enqueue(this);
    }

    public void getOrderDetails(String orderId, String userId) {
        services.getOrderDetails(orderId, userId).enqueue(this);
    }

    public void postCurrentLocation(RequestBody userId, RequestBody userLat, RequestBody userLong) {
        services.postCurrentLocation(userId, userLat, userLong).enqueue(this);
    }

    public void getMyWallet(String userId) {
        services.getMyWallet(userId).enqueue(this);
    }

    public void getNotifications(String userId) {
        services.getNotifications(userId).enqueue(this);
    }

    public void getBankDetails(String userId) {
        services.getBankDetails(userId).enqueue(this);
    }

    public void postBankDetails(RequestBody rbRiderId, RequestBody rbAName, RequestBody rbBankName,
                                RequestBody rbBName, RequestBody rbANumber, RequestBody rbIFSC, MultipartBody.Part image) {
        services.postBankDetails(rbRiderId, rbAName, rbBankName, rbBName, rbANumber, rbIFSC, image).enqueue(this);
    }

//    public void login(Map<String, Object> requestData) {
//        services.login(requestData).enqueue(this);
//    }
//
//    public void register(Map<String, Object> requestData) {
//        services.register(requestData).enqueue(this);
//    }
//
//    public void subscribe(Map<String, Object> requestData) {
//        services.subscribe(requestData).enqueue(this);
//    }
//
//    public void getMessages(Map<String, Object> requestData) {
//        services.getMessages(requestData).enqueue(this);
//    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) {

        if (response.code() == 200 || response.code() == 201) {

            BaseResponse result = (BaseResponse) response.body();
            if (result.getSuccess() == 200)
                serviceCallbacks.onResponse(response.body(), response.raw().request().url().toString());
            else
                serviceCallbacks.onError(result.getMessage());

        } else if (response.code() == 500)
            serviceCallbacks.onError(response.message());
        else {
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                String message = jObjError.getString("message");
                serviceCallbacks.onError(message);
            } catch (Exception e) {
                e.printStackTrace();
                serviceCallbacks.onError(response.message());
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull Throwable t) {
        serviceCallbacks.onError(AppConstants.SOMETHING_WENT_WRONG);
    }


}
