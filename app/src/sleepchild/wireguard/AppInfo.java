package sleepchild.wireguard;
import java.util.*;
import android.content.pm.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.content.*;

public class AppInfo implements Comparable<AppInfo>
{
    public ApplicationInfo rootinfo;
    public String name="";
    public String packageName="";
    public String version="";
    public boolean system=false;
    public boolean hasInternet=false;
    public boolean allowed=false;//
    public boolean ismorevisible=false;
    public Intent launchIntent=null;
    
    public AppInfo(){
        //
    } 
    
    @Override
    public int compareTo(AppInfo other)
    {
        return name.toLowerCase().compareTo(other.name.toLowerCase());
    }
    
}
