package com.meikotech.square.payments;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.android.gms.wallet.PaymentsClient;

import sqip.Card;
import sqip.CardDetails;
import sqip.CardEntry;



@CapacitorPlugin(name = "CapacitorSquareInappPayments")
public class CapacitorSquareInappPaymentsPlugin extends Plugin {

    private CapacitorSquareInappPayments implementation = new CapacitorSquareInappPayments();


    @PluginMethod
    public void startCardPayment(PluginCall call) {

        Intent intent = new Intent(getActivity().getApplicationContext(), CapacitorSquareInappPayments.class);
        intent.putExtra("paymentMethod", "cardEntry");
        startActivityForResult(call, intent, "handleOnResult");

        JSObject ret = new JSObject();
        ret.put("cardEntryStarted", "cardEntry" );
        call.resolve(ret);

    }

    @PluginMethod
    public void startGooglePay(PluginCall call) {
        Intent intent = new Intent(getActivity().getApplicationContext(), CapacitorSquareInappPayments.class);
        intent.putExtra("paymentMethod", "googlePay");
        startActivityForResult(call, intent, "handleOnResult");
        JSObject ret = new JSObject();
        ret.put("cardEntryStarted", "googlePay" );
        call.resolve(ret);
    }



    @ActivityCallback
    protected void handleOnResult(PluginCall call, ActivityResult result) {
        if (call == null) {
            Log.d("NONCE", "NULL");
            return;
        }
        String nonce = result.getData().getStringExtra("NONCE");
        JSObject response = new JSObject();
        response.put("cardNonce", result.getData().getStringExtra("cardNonce"));
        response.put("cardBrand", result.getData().getStringExtra("cardBrand"));
        response.put("cardLastFour", result.getData().getStringExtra("cardLastFour"));
        notifyListeners("cardDetailsSuccess", response);
        Log.d("NONCE2", nonce);
    }
}
