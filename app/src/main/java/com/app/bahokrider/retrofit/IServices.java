package com.app.bahokrider.retrofit;

import com.app.bahokrider.pojos.BaseResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IServices {

    @POST(APIs.REQUEST_OTP)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> requestOTP(@Query("mobileno") String mobile);

    @GET(APIs.VERIFY_OTP)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> verifyOTP(@Query("mobileno") String mobile,
                                 @Query("device_id") String deviceToken);

    @GET(APIs.GET_PROFILE)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> getProfile(@Query("delivery_boy_id") String userId);

    @Multipart
    @POST(APIs.UPDATE_PROFILE)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> updateProfile(@Part MultipartBody.Part file,
                                     @Part("delivery_boy_id") RequestBody userId,
                                     @Part("fname") RequestBody firstName,
                                     @Part("lname") RequestBody lastName);

    @Multipart
    @POST(APIs.UPDATE_SELFI)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> uploadSelfi(@Part MultipartBody.Part file,
                                   @Part("delivery_boy_id") RequestBody userId);

    @Multipart
    @POST(APIs.REGISTER)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> register(@Part MultipartBody.Part file,
                                @Part MultipartBody.Part adharFront,
                                @Part MultipartBody.Part adharBack,
                                @Part MultipartBody.Part licenseFront,
                                @Part MultipartBody.Part licenseBack,
                                @Part("fname") RequestBody firstName,
                                @Part("lname") RequestBody lastName,
                                @Part("email") RequestBody email,
                                @Part("mobileno") RequestBody contactNo,
                                @Part("aadharno") RequestBody adharNo,
                                @Part("licenseno") RequestBody licenseNo);

    @Multipart
    @POST(APIs.REGISTER_RESTAURANT)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> registerRestaurant(@Part MultipartBody.Part file,
                                          @Part("restaurant_name") RequestBody rbName,
                                          @Part("restaurant_email") RequestBody rbEmail,
                                          @Part("restaurant_contact") RequestBody rbMobile,
                                          @Part("restaurant_address") RequestBody rbLoc,
                                          @Part("latitude") RequestBody rbLat,
                                          @Part("longitude") RequestBody rbLong,
                                          @Part("restaurant_password") RequestBody rbPass,
                                          @Part("restaurant_details") RequestBody rbDesc);


    @GET(APIs.GET_ORDERS)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> getOrders(@Query("delivery_boy_id") String userId);

    @GET(APIs.GET_ORDER_HISTORY)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> getOrderHistory(@Query("delivery_boy_id") String userId);

    @GET(APIs.CURRENT_ORDERS)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> getCurrentOrder(@Query("delivery_boy_id") String userId);

    @GET(APIs.HOME_DATA)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> getHomeData(@Query("delivery_boy_id") String userId);

    @Multipart
    @POST(APIs.ACCEPT_ORDER)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> acceptOrder(@Part("order_id") RequestBody rbOrderId,
                                   @Part("delivery_boy_id") RequestBody rbRiderId,
                                   @Part("delivery_boy_order_status") RequestBody rbStatus);

    @Multipart
    @POST(APIs.REJECT_ORDER)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> rejectOrder(@Part("order_id") RequestBody rbOrderId,
                                   @Part("delivery_boy_id") RequestBody rbRiderId,
                                   @Part("delivery_boy_order_status") RequestBody rbStatus);

    @Multipart
    @POST(APIs.DELIVER_ORDER)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> deliverOrder(@Part("order_id") RequestBody rbOrderId,
                                    @Part("status") RequestBody rbStatus);

    @Multipart
    @POST(APIs.RIDER_ACTIVATION)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> activeInactiveRider(@Part("delivery_boy_id") RequestBody rbOrderId,
                                           @Part("status_val") RequestBody rbStatus);

    @Multipart
    @POST(APIs.RIDER_LOCATION)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> postCurrentLocation(@Part("delivery_boy_id") RequestBody rbRiderId,
                                           @Part("latitude") RequestBody rbLat,
                                           @Part("longitude") RequestBody rbLong);

    @GET(APIs.GET_ORDER_DETAILS)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> getOrderDetails(@Query("order_id") String orderId, @Query("delivery_boy_id") String userId);

    @GET(APIs.GET_MY_WALLER)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> getMyWallet(@Query("delivery_boy_id") String userId);

    @GET(APIs.NOTIFICATIONS)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> getNotifications(@Query("delivery_boy_id") String userId);

    @GET(APIs.GET_BANK_DETAILS)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> getBankDetails(@Query("delivery_boy_id") String userId);

    @Multipart
    @POST(APIs.POST_BANK_DETAILS)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> postBankDetails(@Part("delivery_boy_id") RequestBody rbRiderId,
                                       @Part("name") RequestBody rbAName,
                                       @Part("bank_name") RequestBody rbBankName,
                                       @Part("branch_name") RequestBody rbBName,
                                       @Part("acc_no") RequestBody rbANumber,
                                       @Part("ifsc") RequestBody rbIFSC,
                                       @Part MultipartBody.Part image);

    @Multipart
    @POST(APIs.ADD_BALANCE)
    @Headers("Cache-Control: no-cache")
    Call<BaseResponse> addBalance(@Part("delivery_boy_id") RequestBody rbRiderId,
                                  @Part("transaction_id") RequestBody rbTransId,
                                  @Part("amount") RequestBody rbAmount);


}
