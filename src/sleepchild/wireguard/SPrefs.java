package sleepchild.wireguard;

import android.content.*;
import android.preference.*;

public class SPrefs
{
    Context ctx;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    
    
    public SPrefs(Context ctx){
        this.ctx=ctx;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        editor=prefs.edit();
    }
    
    public boolean getWGEnabled(){
        return prefs.getBoolean(KEY_ENABLED,false);
    }
    
    public void setWGEnabled(boolean enable){
        editor.putBoolean(KEY_ENABLED, enable).commit();
    }
    
    
    
    
    
    /////////
    /////////
    static final String KEY_ENABLED = "mrt.wg.sprefs.key.enabled";
}
