/*
 * This file is part of WhereYouGo.
 * 
 * WhereYouGo is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * WhereYouGo is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with WhereYouGo. If not,
 * see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
 */

package menion.android.whereyougo.utils;

import android.app.Application;

import menion.android.whereyougo.BuildConfig;
import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.audio.ManagerAudio;
import menion.android.whereyougo.geo.orientation.Orientation;
import menion.android.whereyougo.gui.extension.activity.CustomMainActivity;
import menion.android.whereyougo.guide.GuideContent;

/**
 * @author menion
 * @since 25.1.2010 2010
 */
public class A {

    private static CustomMainActivity main;
    private static final String TAG = "A";
    private static MainApplication app;
    private static GuideContent guidingContent;
    private static ManagerAudio managerAudio;

    public static void destroy() {
        guidingContent = null;
        managerAudio = null;
        main = null;
        // finally destroy app
        if (app != null)
            app.destroy();
        app = null;
    }

    public static Application getApp() {
        return app;
    }

    public static GuideContent getGuidingContent() {
        if (guidingContent == null) {
            guidingContent = new GuideContent();
        }
        return guidingContent;
    }

    public static CustomMainActivity getMain() {
        return main;
    }

    public static ManagerAudio getManagerAudio() {
        if (managerAudio == null) {
            managerAudio = new ManagerAudio();
        }
        return managerAudio;
    }

    public static void printState() {
        Logger.i(TAG, "printState() - STATIC VARIABLES");
        Logger.i(TAG, "app:" + app);
        Logger.i(TAG, "managerAudio:" + managerAudio);
        Logger.i(TAG, "main:" + main);
        Logger.i(TAG, "guidingContent:" + guidingContent);
    }

    public static void registerApp(MainApplication app) {
        A.app = app;
    }

    public static void registerMain(CustomMainActivity main) {
        A.main = main;
    }


}
