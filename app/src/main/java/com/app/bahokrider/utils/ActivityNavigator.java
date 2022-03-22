package com.app.bahokrider.utils;

import android.content.Context;
import android.content.Intent;

import com.app.bahokrider.activities.LoginActivity;
import com.app.bahokrider.activities.MainScreenActivity;
import com.app.bahokrider.activities.OTPActivity;
import com.app.bahokrider.activities.SignupActivity;
import com.app.bahokrider.activities.WelcomeActivity;

/**
 * @author Date 19-11-2019
 */
public class ActivityNavigator {

  public static void launchLoginActivity(Context context, String email) {
    Intent intent = new Intent(context, LoginActivity.class);
    context.startActivity(intent);
  }

  public static void launchOTPActivity(Context context) {
    Intent intent = new Intent(context, OTPActivity.class);
    context.startActivity(intent);
  }

//  public static void launchForgetPasswordActivity(Context context) {
//    Intent intent = new Intent(context, ForgotPasswordActivity.class);
//    context.startActivity(intent);
//  }

  public static void launchSignupActivity(Context context) {
    Intent intent = new Intent(context, SignupActivity.class);
    context.startActivity(intent);
  }

  public static void launchMainScreenActivity(Context context) {
    Intent intent = new Intent(context, MainScreenActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    context.startActivity(intent);
  }

  public static void launchWelcomeActivity(Context context) {
    Intent intent = new Intent(context, WelcomeActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    context.startActivity(intent);
  }

//  public static void launchOrderDetailsActivity(Context context, OrderModel orderModel) {
//    Intent intent = new Intent(context, JobDetailsActivity.class);
//    intent.putExtra(DeliveyBoyConstants.ORDERDETAILS, orderModel);
//    context.startActivity(intent);
//  }

}
