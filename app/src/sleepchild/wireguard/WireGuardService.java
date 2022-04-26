package sleepchild.wireguard;

import android.net.VpnService;
import android.content.Intent;
import android.content.Context;
import android.os.*;
import java.io.*;
import android.widget.*;
import android.content.pm.PackageManager.*;
import android.content.pm.*;
import java.nio.*;
import java.net.*;
import sleepchild.wireguard.packet.*;
import sleepchild.wireguard.packet.IPv6.*;
import java.nio.channels.*;

public class WireGuardService extends VpnService
{

    public static final String TAG = "WireGuardService";
    
    public static final String CMD_START ="wgs_cmd_start_vpn";
    public static final String CMD_RELOAD="wrgs_cmd_vpn_reload";
    public static final String CMD_STOP="sleep_wgs_stop_cmd_vpnstop";
    public static final int LAUNCH_CODE= 655;
    
    int nid = 547;
    //boolean vpnEnabled=false;
    String cmd;
    boolean debug= true;//false;
    Thread debugThread = null;
    SPrefs prefs;
    ParcelFileDescriptor vpn = null;
    Context ctx_;
    Handler handle;
    
    Debuging debuging;

    public static void start(Context ctx){
        Intent ssi = new Intent(ctx, WireGuardService.class);
        ssi.setAction(CMD_START);
        ctx.startService(ssi);
    }

    public static void reload(Context ctx){
        Intent ssi = new Intent(ctx, WireGuardService.class);
        ssi.setAction(CMD_RELOAD);
        ctx.startService(ssi);
    }

    public static void stop(Context ctx){
        Intent ssi = new Intent(ctx,WireGuardService.class);
        ssi.setAction(CMD_STOP);
        ctx.startService(ssi);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        ctx_ = WireGuardService.this;
        prefs = new SPrefs(ctx_);
        handle = new Handler();
        debuging = new Debuging(this);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //
        //vpnEnabled = prefs.getWGEnabled();
        cmd = intent.getAction();
        //*
        new Thread(new Runnable(){
            @Override
            public void run(){
                synchronized(WireGuardService.this){
                    switch(cmd){
                        case CMD_START:
                            if(vpn==null){
                                vpn = startVpn();
                                if(vpn!=null){
                                    showNotif();
                                }
                                prefs.setWGEnabled(true);//
                                startDebug(vpn);
                            }
                            break;
                        case CMD_RELOAD:
                            //*
                            //seamless handover
                            ParcelFileDescriptor prev = vpn;
                            
                            if(prefs.getWGEnabled()){
                                vpn = startVpn();
                            }
                            
                            if(prev!=null){
                                stopVpn(prev);
                            }
                            //*/
                            break;
                        case CMD_STOP:
                            stopVpn(vpn);
                            stopDebug();
                            removeNotif();
                            vpn = null;// why is this here?
                            prefs.setWGEnabled(false);//
                            break;
                    }
                }
            }
        }).start();
        //*/
        return START_NOT_STICKY;
    }
    
    private ParcelFileDescriptor startVpn(){
        Builder builder = new Builder();
        builder.setSession(getApplicationInfo().name);
        //*
        builder.addAddress("10.1.10.1", 32);
        //builder.addAddress("192.168.0.1",24);
        builder.addDnsServer("8.8.8.8");
        //builder.addAddress("fd00:1:fd00:1:fd00:1:fd00:1", 64);
        builder.addRoute("0.0.0.0", 0);
        //builder.addRoute("0:0:0:0:0:0:0:0", 0);
        //*/
        //*
        
        //*
        LogTool.get().f(TAG, "preparing allowed applications list");
        
        for(AppInfo info : AppInfoFactory.getAllApps(ctx_)){
            if(info.allowed){
                try{
                    LogTool.get().f(TAG,"adding: "+info.packageName);
                    builder.addDisallowedApplication(info.packageName);
                }
                catch (PackageManager.NameNotFoundException e){
                    // log nnfe
                }
            }
            
        }
        //*/
        
        //*/
        
        //LogTool.get().f(TAG, "done.");
        //*/
        //*
        if(debug){
           // builder.setBlocking(true);
        }
        //*/
        //builder.setBlocking(debug);
        
        try{
            return builder.establish();
        }catch(Throwable ex){
            stopVpn(vpn);
            return null;
        }
    }
    
    public void stopVpn(ParcelFileDescriptor spfd){
        stopDebug();
        if(spfd!=null){
            try
            {
                spfd.close();
            }
            catch (IOException e)
            {}
        }
        //prefs.setWGEnabled(false);
        //LogTool.get().push();
    }
    
    public void startDebug(final ParcelFileDescriptor pfd){
        if(pfd==null){
            return;
        }
        debuging.start(pfd);
        debugThread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    //FileInputStream in = new FileInputStream(pfd.getFileDescriptor());
                    //FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
                    
                    ByteBuffer buffer = ByteBuffer.allocate(32767);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    LogTool.get().f(TAG, "Start debugging");
                    while (!Thread.currentThread().isInterrupted() &&
                           pfd.getFileDescriptor() != null &&
                           pfd.getFileDescriptor().valid()){
                             debuging.loop();
                             Thread.sleep(1000);
                           }
                        pfd.close();
                    LogTool.get().f(TAG, "End debugging");
                    LogTool.get().push();
                } catch (Throwable ex) {
                    LogTool.get().f(TAG, ex.toString() + "\n" + android.util.Log.getStackTraceString(ex));
                }
            }
        });
        debugThread.start();
        //
    }
    
    public void stopDebug(){
        if(debugThread!=null){
            debugThread.interrupt();
            debuging.stop();
        }
        debugThread=null;
    }
    
    
    
    
    
    
    private void showNotif(){
        startForeground(nid, MNotification.get(ctx_));
    }
    
    private void removeNotif(){
        stopForeground(true);
    }

    @Override
    public void onRevoke(){
        // lost vpn priveledge
        stopVpn(vpn);
        removeNotif();
        //
        super.onRevoke();
    }
     
}
