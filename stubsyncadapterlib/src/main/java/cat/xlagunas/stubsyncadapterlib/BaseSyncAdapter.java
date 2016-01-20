package cat.xlagunas.stubsyncadapterlib;

import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Context;

/**
 * Created by xlagunas on 20/1/16.
 */
public abstract class BaseSyncAdapter extends AbstractThreadedSyncAdapter{

    public BaseSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @TargetApi(11)
    public BaseSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

}
