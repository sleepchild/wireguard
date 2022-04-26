package sleepchild.wireguard;

import android.app.Notification;
import android.content.Context;
import android.widget.*;
import android.app.*;
import android.content.*;

public class MNotification
{
    final static String TAG = "wireguard.MNotification";
    
    private MNotification(){}
    
    public static Notification get(Context ctx){
        Notification.Builder b = new Notification.Builder(ctx);
        b.setSmallIcon(R.drawable.ic_launcher);
        b.setVisibility(Notification.VISIBILITY_PUBLIC);
        b.setCategory("wireguard");
        b.setGroup("wireguard");
        b.setContent(rem(ctx));
        b.setContentIntent(PendingIntent.getActivity(
          ctx,
          123, 
          new Intent(ctx, MainActivity.class),
          PendingIntent.FLAG_UPDATE_CURRENT));
        
        return b.build();
    }
    
    // move up
    private static RemoteViews rem(Context ctx){
        RemoteViews r = new RemoteViews(ctx.getPackageName(), R.layout.notification);
        Intent i = new Intent(ctx, WireGuardService.class);
        i.setAction(WireGuardService.CMD_STOP);
        r.setOnClickPendingIntent(R.id.notification_btn_end, 
          PendingIntent.getService(ctx, 
            234, i, PendingIntent.FLAG_CANCEL_CURRENT));
            
        return r;
    }
    
}
