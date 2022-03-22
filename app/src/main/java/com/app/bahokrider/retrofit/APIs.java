package com.app.bahokrider.retrofit;


import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class APIs {

  //  public static final String BASE_URL = "https://santaeatsapi.edigito.in/";
    public static final String BASE_URL = "https://api.foodbahok.com/";
    public static final String PATH = "";

    public static final String REQUEST_OTP = PATH + "delivery_boy_send_otp";
    public static final String VERIFY_OTP = PATH + "delivery_boy_verify_otp";
    public static final String GET_PROFILE = PATH + "delivery_boy_profile";
    public static final String UPDATE_PROFILE = PATH + "delivery_boy_update_profile";
    public static final String UPDATE_SELFI = PATH + "delivery_boy_selfie";
    public static final String REGISTER = PATH + "delivery_boy_request";
    public static final String REGISTER_RESTAURANT = PATH + "add_restaurant";

    public static final String CURRENT_ORDERS = PATH + "delivery_boy_accepted_orders";
    public static final String HOME_DATA = PATH + "delivery_boy_dashboard";
    public static final String GET_ORDERS = PATH + "delivery_boy_new_orders";
    public static final String GET_ORDER_HISTORY = PATH + "delivery_boy_orders_history";
    public static final String ACCEPT_ORDER = PATH + "delivery_boy_accept_order";
    public static final String REJECT_ORDER = PATH + "delivery_boy_reject_order";

    public static final String DELIVER_ORDER = PATH + "update-order-status";
    public static final String ADD_BALANCE = PATH + "delivery_boy_add_transaction";

    public static final String RIDER_ACTIVATION = PATH + "offline_online_status";
    public static final String RIDER_LOCATION = PATH + "delivery_boy_current_location";

    public static final String GET_ORDER_DETAILS = PATH + "delivery_boy_order_details";
    public static final String GET_MY_WALLER = PATH + "delivery_boy_get_transactions";
    public static final String NOTIFICATIONS = PATH + "notifications";

    public static final String GET_BANK_DETAILS = PATH + "delivery_boy_get_bank_details";
    public static final String POST_BANK_DETAILS = PATH + "delivery_boy_add_bank_details";




  public static OkHttpClient getUnsafeOkHttpClient() {

    try {
      // Create a trust manager that does not validate certificate chains
      final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] chain,
                String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] chain,
                String authType) throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return new java.security.cert.X509Certificate[0];
        }
      } };

      // Install the all-trusting trust manager
      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustAllCerts,
              new java.security.SecureRandom());
      // Create an ssl socket factory with our all-trusting manager
      final SSLSocketFactory sslSocketFactory = sslContext
              .getSocketFactory();

      OkHttpClient okHttpClient = new OkHttpClient();
      okHttpClient = okHttpClient.newBuilder()
              .sslSocketFactory(sslSocketFactory)
              .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();

      return okHttpClient;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}



