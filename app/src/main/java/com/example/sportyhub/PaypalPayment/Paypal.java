package com.example.sportyhub.PaypalPayment;

import android.app.Application;

import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class Paypal extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        PayPalCheckout.setConfig(new CheckoutConfig(
            this,
            "ARuGMSfzKGwETPZgsWXISPAxXqKEh39LNsGn4PcFG-LnHfIWgsZp3S3JPu1odVHxXqM6yOgwvuE9GyEs",
            Environment.SANDBOX,
            CurrencyCode.EUR,
            UserAction.PAY_NOW,
            "nativexo://paypalpay"
        ));
    }
}
