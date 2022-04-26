package sleepchild.wireguard;
import android.os.*;

public class Debuging{
    
    WireGuardService ws;
    ParcelFileDescriptor vpn;
    
    public Debuging(WireGuardService ws){
        this.ws = ws;
    }
    
    public void start(ParcelFileDescriptor pfd){
        vpn = pfd;
    }
    
    public void stop(){
        //
        
        vpn = null;
    }
    
    //
    public void loop(){
        //
    }
    
    
}
