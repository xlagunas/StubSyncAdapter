/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cat.xlagunas.stubsyncadapterlib.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cat.xlagunas.stubsyncadapterlib.BaseSyncAdapter;
import cat.xlagunas.stubsyncadapterlib.R;

/** Service to handle sync requests.
 *
 * <p>This cat.xlagunas.stubsyncadapterlib.service is invoked in response to Intents with action android.content.SyncAdapter, and
 * returns a Binder connection to SyncAdapter.
 *
 * <p>For performance, only one sync adapter will be initialized within this application's context.
 *
 * <p>Note: The SyncService itself is not notified when a new sync occurs. It's role is to
 * manage the lifecycle of our {@link cat.xlagunas.stubsyncadapterlib.service.SyncService} and provide a handle to said SyncAdapter to the
 * OS on request.
 */
public class SyncService extends Service {
    private static final String TAG = "SyncService";

    private static final Object sSyncAdapterLock = new Object();
    private static BaseSyncAdapter sSyncAdapter = null;

    /**
     * Thread-safe constructor, creates static {@link BaseSyncAdapter} instance.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = getBaseSyncAdapter();
            }
        }
    }

    @Override
    /**
     * Logging-only destructor.
     */
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    /**
     * Return Binder handle for IPC communication with {@link BaseSyncAdapter}.
     *
     * <p>New sync requests will be sent directly to the SyncAdapter using this channel.
     *
     * @param intent Calling intent
     * @return Binder handle for {@link BaseSyncAdapter}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }

    private BaseSyncAdapter getBaseSyncAdapter(){
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Bundle data=ai.metaData;

            String fqdnClass = data.getString(getString(R.string.sync_metadata));

            Class aClass = getClassLoader().loadClass(fqdnClass);
            Constructor<?> aClassConstructor = aClass.getConstructor(new Class[]{Context.class, boolean.class});

            return (BaseSyncAdapter) aClassConstructor.newInstance(new Object[] { getApplicationContext(), true });

        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            throw new RuntimeException("couldn't obtain meta-tag", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found", e);
        } catch (InvocationTargetException e) {
            Log.e("ERROR!!!", "Error: " + e);
        } catch (NoSuchMethodException e) {
            Log.e("ERROR!!!" , "Error: "+e);
        } catch (InstantiationException e) {
            Log.e("ERROR!!!", "Error: " + e);
        } catch (IllegalAccessException e) {
            Log.e("ERROR!!!", "Error: " + e);
        }

        return null;
    }
}
