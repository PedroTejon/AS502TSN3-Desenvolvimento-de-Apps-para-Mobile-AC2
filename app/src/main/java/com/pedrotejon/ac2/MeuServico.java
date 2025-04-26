package com.pedrotejon.ac2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MeuServico extends Service {
    private Handler handler = new Handler();
    private static final String CHANNEL_ID = "my_channel_id";
    private static final int NOTIFICATION_ID = 1;
    BancoHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification("Iniciando..."));

        databaseHelper = new BancoHelper(this);

        handler.postDelayed(() -> {
            try {
                var cursor = databaseHelper.listarTreinos();

                if (cursor.moveToFirst()) {
                    do {
                        String treino = cursor.getString(1);
                        int tempo = cursor.getInt(2);

                        for(int i = tempo; i > 0; i--) {
                            Notification updatedNotification = buildNotification("Treino: " + treino + " - Tempo: " + Integer.toString(i));
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(NOTIFICATION_ID, updatedNotification);
                            Thread.sleep(1000);
                        }
                    } while (cursor.moveToNext());

                    Notification updatedNotification = buildNotification("Treino finalizado!");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, updatedNotification);
                }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }, 0);
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private Notification buildNotification(String content) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Treino")
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
    }


    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Canal de Notificação",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
