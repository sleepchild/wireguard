package sleepchild.wireguard;
import android.app.*;
import android.content.*;

public class Notif
{
    static String nTitle = "wireguard";
    
    public static Notification getOn(Context ctx){
        Notification.Builder b = new Notification.Builder(ctx);
        b.setSmallIcon(R.drawable.ic_launcher);
        b.setContentTitle(nTitle);
        b.setContentText("vpn is active");
        b.setOngoing(true);
        b.setStyle(new Notification.BigTextStyle());
        b.setContentIntent(getLaunchPe(ctx));
        b.addAction(R.drawable.outgoing,
            "stop",
            getStopPe(ctx)
        );
        return b.build();
    }
    
    private static PendingIntent getLaunchPe(Context ctx){
        return PendingIntent.getActivity(ctx,
                                         MainActivity.LAUNCH_CODE,
          new Intent(ctx, MainActivity.class),
          PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
    
    private static PendingIntent getStopPe(Context ctx){
        Intent stopi = new Intent(ctx, WireGuardService.class);
        stopi.setAction(WireGuardService.CMD_STOP);
        return PendingIntent.getService(ctx,
            WireGuardService.LAUNCH_CODE,
            stopi,
            PendingIntent.FLAG_CANCEL_CURRENT
        );
    }
    
    public static Notification getFailed(Context ctx){
        Notification.Builder b = new Notification.Builder(ctx);
        b.setSmallIcon(R.drawable.ic_launcher);
        b.setContentTitle(nTitle);
        b.setContentText("vpn service failed to start. see logs");
        b.setOngoing(true);
        b.setStyle(new Notification.BigTextStyle());
        return b.build();
    }
}
