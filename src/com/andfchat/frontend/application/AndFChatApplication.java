/*******************************************************************************
 *     This file is part of AndFChat.
 * 
 *     AndFChat is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     AndFChat is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with AndFChat.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/


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

    public final static String DEBUG_CHANNEL_NAME = "Console";

    public final static int LED_NOTIFICATION_ID = 1337;

    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
