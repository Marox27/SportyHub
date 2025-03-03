package com.example.sportyhub.PaypalPayment;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.OrderRequest;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PaypalCheckOut extends AppCompatActivity {
    PaymentButtonContainer paymentButtonContainer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        double cantidad = getIntent().getDoubleExtra("Precio", -1);
        // ...
        paymentButtonContainer.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.EUR)
                                                        .value(String.valueOf(cantidad))
                                                        .build()
                                        )
                                        .build()
                        );
                        OrderRequest order = new OrderRequest(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                            }
                        });
                    }
                }
        );

        /*
        * paymentButtonContainer.setup(
    new CreateOrder() {
        @Override
        public void create(@NotNull CreateOrderActions createOrderActions) {
            ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
            purchaseUnits.add(
                new PurchaseUnit.Builder()
                    .amount(
                        new Amount.Builder()
                            .currencyCode(CurrencyCode.EUR)
                            .value(String.valueOf(cantidad))
                            .build()
                    )
                    .build()
            );
            OrderRequest order = new OrderRequest(
                OrderIntent.CAPTURE,
                new AppContext.Builder()
                    .userAction(UserAction.PAY_NOW)
                    .build(),
                purchaseUnits
            );
            createOrderActions.create(order, null);
        }
    },
    new OnApprove() {
        @Override
        public void onApprove(@NotNull Approval approval) {
            approval.getOrderActions().captureOrder((orderResult) -> {
                if (orderResult != null) {
                    Log.i("CaptureOrder", "Order captured successfully: " + orderResult);
                } else {
                    Log.e("CaptureOrder", "Failed to capture the order");
                }
            });
        }
    }
);

        * */
    }

}

