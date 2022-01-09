package sleepchild.wireguard;

import java.util.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.drawable.*;

public class AppInfoFactory
{
    AppInfoFactory(){}
    
    public static List<AppInfo> getAllApps(Context ctx){
        //
        Utils util = new Utils(ctx);
        
        List<AppInfo> alist=new ArrayList<>();
        PackageManager pm = ctx.getPackageManager();
        //
        for(PackageInfo pi : pm.getInstalledPackages(0)){
            //
            AppInfo info = new AppInfo();
            info.rootinfo = pi.applicationInfo;
            info.name = pi.applicationInfo.loadLabel(pm).toString();
            info.packageName=pi.packageName;
            info.version=pi.versionName;
            info.system = ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            //info.icon = pi.applicationInfo.loadIcon(pm);
            info.allowed = util.isAllowed(info.packageName);
            info.launchIntent = pm.getLaunchIntentForPackage(pi.packageName);
            alist.add(info);
        }
        //
        Collections.sort(alist);
        //
        return alist;
    }
    
    public static Drawable getIcon(Context ctx,ApplicationInfo inf){
        return inf.loadIcon(ctx.getPackageManager());
    }
}
