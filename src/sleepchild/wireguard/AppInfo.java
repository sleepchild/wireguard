package sleepchild.wireguard;
import java.util.*;
import android.content.pm.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.content.*;

public class AppInfo implements Comparable<AppInfo>
{
    ApplicationInfo rootinfo;
    String name="";
    String packageName="";
    String version="";
    boolean system=false;
    boolean hasInternet=false;
    boolean allowed=false;//
    Drawable icon=null;
    boolean ismorevisible=false;
    Intent launchIntent=null;
    
    
    AppInfo(){
        //
    }
    
    @Override
    public int compareTo(AppInfo other)
    {
        // TODO: Implement this method
        return name.toLowerCase().compareTo(other.name.toLowerCase());
    }
    
}
