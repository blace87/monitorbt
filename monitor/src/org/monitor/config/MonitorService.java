package org.monitor.config;

import org.monitorView.MonitorActivity;
import org.monitorView.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MonitorService extends Service {
	private static final String TAG = "MonitorService";
	private int NOTIFICATION = 0;
	private NotificationManager mNM;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		showNotification();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		mNM.cancel(NOTIFICATION);
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
	}
	
	/**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Notificacion";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.heart_red, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MonitorActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "MonitorService",
                       text, contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }


}
