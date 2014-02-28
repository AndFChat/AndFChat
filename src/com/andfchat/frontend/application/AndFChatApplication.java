package com.andfchat.frontend.application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.andfchat.R;

@ReportsCrashes(formKey = "", // will not be used
                mailTo = "githublimon@gmail.com",
                mode = ReportingInteractionMode.TOAST,
                resToastText = R.string.crash_toast_text)
public class AndFChatApplication extends Application{

    public final static boolean DEBUGGING_MODE = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
