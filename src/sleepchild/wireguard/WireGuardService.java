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
                                //startDebug(vpn);
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
        LogTool.get().f(TAG, "preparing application list");
        
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
        
        //LogTool.get().f(TAG, "done.");
        //*/
        //*
        if(debug){
            //builder.setBlocking(true);
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
        debugThread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    FileInputStream in = new FileInputStream(pfd.getFileDescriptor());
                    FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());

                    //DatagramChannel tunnel = DatagramChannel.open();
                    
                    //tunnel.connect("",88);
                    
                  //  protect(tunnel.socket());
                    
                    ByteBuffer buffer = ByteBuffer.allocate(32767);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    LogTool.get().f(TAG, "Start receiving");
                    while (!Thread.currentThread().isInterrupted() &&
                           pfd.getFileDescriptor() != null &&
                           pfd.getFileDescriptor().valid())
                        try {
                            buffer.clear();
                            int length = in.read(buffer.array());
                            if (length > 0) {
                                buffer.limit(length);
                                
                                debugPacket(buffer);
                                
                                Thread.sleep(100);
                                
                                
                                /*
                                Packet pkt = new Packet(buffer);

                                if (pkt.IPv4.protocol == Packet.IPv4Header.TCP && pkt.TCP.SYN) {
                                    int uid = pkt.getUid4();
                                    if (uid < 0)
                                        LogTool.get().f(TAG, "uid not found");

                                    String[] pkg = getPackageManager().getPackagesForUid(uid);
                                    if (pkg == null)
                                        pkg = new String[]{uid == 0 ? "root" : "unknown"};

                                    LogTool.get().f(TAG, "Connect " + pkt.IPv4.destinationAddress + ":" + pkt.TCP.destinationPort + " uid=" + uid + " pkg=" + pkg[0]);

                                    // Send RST
                                    pkt.swapAddresses();
                                    pkt.TCP.clearFlags();
                                    pkt.TCP.RST = true;
                                    long ack = pkt.TCP.acknowledgementNumber;
                                    pkt.TCP.acknowledgementNumber = (pkt.TCP.sequenceNumber + 1) & 0xFFFFFFFFL;
                                    pkt.TCP.sequenceNumber = (ack + 1) & 0xFFFFFFFFL;
                                    pkt.send(out);
                                }
                                
                                //*/
                            }
                        } catch (Throwable ex) {
                            LogTool.get().f(TAG, ex.toString());
                        }
                        in.close();
                        out.close();
                        pfd.close();
                    LogTool.get().f(TAG, "End receiving");
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
        }
        debugThread=null;
    }
    
    private void debugPacket(ByteBuffer buffer)
    {
        byte[] v6Buffer = buffer.array();
        /*
        buffer.clear();
        for( byte b : v6Buffer){
            buffer.put(b);
        }
        //*/
        
        IPv4 pkt;
        String stats = "";
        boolean p = false;
        try
        {
            pkt = new IPv4(buffer);
           
            stats = "receive packet:: ";
            
            stats += "  version=" + pkt.getVersion();
            stats += "  Source addr= "
               + pkt.getSourceAddr().getHostAddress() 
               + ":"+ pkt.getTCP().getSourcePort();
               
            stats +="  Dest addr= "
                +pkt.getDestAddr().getHostAddress()
                +":"+ pkt.getTCP().getDestPort();
            
            int uid = pkt.getUid4();
            stats += "  uid=" + uid;
            
            String[] pkg = getPackageManager().getPackagesForUid(uid);
            if (pkg == null)
                pkg = new String[]{uid == 0 ? "root" : "unknown"};
                
            stats += "  pkg=" + pkg[0];
            p=true;
            //int tcp = packet.protocol;
        }
        catch (IPv4.NotVersion4Exception e){
            debugV6(ByteBuffer.wrap(v6Buffer));
        }catch(IOException ioe){
            LogTool.get().f(TAG, ioe.getMessage());
        }
        
        if(p){
            LogTool.get().f(TAG, stats);
        }
    }
    
    void debugV6(ByteBuffer buffer){
        String stats="";
        boolean p = false;
        try
        {
            IPv6 pkt6 = new IPv6(buffer);
            
            TCP tcp6=null;
            if(pkt6.getProtocol() == IPv6.PROTOCOL_TCP){
                tcp6 = pkt6.getTCP();
            }
            
            stats += "recieve packet.. "
              + "  protocol="+pkt6.getProtocol()
            
            + "  source address="
                + pkt6.getSourceAddress().getHostAddress();
            if(tcp6!=null){
                //tats += " src_port="+tcp6.getSourcePort();
            }
            
            stats += "  dest address="
                + pkt6.getDestAddress().getHostAddress();
            if(tcp6!=null){
                //stats += " dest_port="+tcp6.getDestPort();
            }
            
            
            stats += "  uuid=";
            
            stats += "  pkg=";
            
            
            p = true;
            
        }
        catch (IPv6.NotVersion6Exception e){
            LogTool.get().f(TAG, e.getMessage());
        }catch(UnknownHostException uhe){
            LogTool.get().f(TAG, uhe.getMessage());
        }
        
        if(p == true){
            LogTool.get().f(TAG, stats);
        }
    }
    
    
    private void showNotif(){
        startForeground(nid, Notif.getOn(ctx_));
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
