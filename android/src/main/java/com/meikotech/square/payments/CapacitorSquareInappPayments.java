package com.meikotech.square.payments;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import sqip.Card;
import sqip.CardDetails;
import sqip.CardEntry;
import sqip.GooglePay;


public class CapacitorSquareInappPayments extends AppCompatActivity {
    private static final String LOCATION_ID = "LPJRS8K5XW23F";
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 1;
    private PaymentsClient paymentsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getStringExtra("paymentMethod");

        if ("cardEntry".equals(action)) {
            initiateCardPayment();
        }

        if ("googlePay".equals(action)) {
            initiateGooglePayment();
        }
    }

    public void initiateCardPayment() {
        CardEntry.startCardEntryActivity(CapacitorSquareInappPayments.this);
    }

    public void initiateGooglePayment()  {
        paymentsClient = Wallet.getPaymentsClient(
                this,
                new Wallet.WalletOptions.Builder().setEnvironment(
                        WalletConstants.ENVIRONMENT_TEST
                ).build()
        );

        AutoResolveHelper.resolveTask(
            paymentsClient.loadPaymentData(
                    GooglePay.createPaymentDataRequest(
                            LOCATION_ID,
                            TransactionInfo.newBuilder().setTotalPriceStatus(
                                    WalletConstants.TOTAL_PRICE_STATUS_NOT_CURRENTLY_KNOWN
                            ).setCurrencyCode( "CAD" ).build()
                    )
            ),
                this, LOAD_PAYMENT_DATA_REQUEST_CODE
        );
        paymentsClient.isReadyToPay( GooglePay.createIsReadyToPayRequest() )
                .addOnCompleteListener(
                        this,
                        (task) -> Log.d("readyToPay", "true")
        );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("NONCE", String.valueOf(requestCode));
        Intent intent = getIntent();

        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentData paymentData = PaymentData.getFromIntent(data);
                if (paymentData != null && paymentData.getPaymentMethodToken() != null) {
                    String googlePayToken = paymentData.getPaymentMethodToken().getToken();

                    GooglePay.requestGooglePayNonce(googlePayToken).enqueue(result -> {
                        if (result.isSuccess()) {
                            String nonce = result.getSuccessValue().getNonce();
                            Card card = result.getSuccessValue().getCard();

                            intent.putExtra("paymentMethod", "googlePay");
                            intent.putExtra("cardNonce", nonce);
                            intent.putExtra("cardBrand",  card.getBrand());
                            intent.putExtra("cardLastFour", card.getLastFourDigits());
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            setResult(Activity.RESULT_CANCELED, intent);
                            finish();
                        }
                    });

                } else {
                    setResult(Activity.RESULT_CANCELED, intent);
                    finish();
                }
            } else {
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        }


        CardEntry.handleActivityResult(data, result -> {
            if (result.isSuccess()) {
                CardDetails cardResult = result.getSuccessValue();
                Card card = cardResult.getCard();
                String nonce = cardResult.getNonce();
                String brand = String.valueOf(card.getBrand());
                String lastFour = String.valueOf(card.getLastFourDigits());

                intent.putExtra("paymentMethod", "cardEntry");
                intent.putExtra("cardNonce", nonce);
                intent.putExtra("cardBrand", brand);
                intent.putExtra("cardLastFour", lastFour);
                setResult(Activity.RESULT_OK, intent);

                finish();
            } else {
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });
    }
}
