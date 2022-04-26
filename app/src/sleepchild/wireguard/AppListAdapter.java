package sleepchild.wireguard;
import android.widget.*;
import android.view.*;
import android.content.*;
import java.util.*;
import android.graphics.*;
import android.graphics.drawable.*;

public class AppListAdapter extends BaseAdapter
{
    Context ctx;
    LayoutInflater inflator;
    List<AppInfo> appList=new ArrayList<>();
    SPrefs prefs;
    Utils util;
    App app;
    
    public AppListAdapter(Context ctx, App ai){
        this.ctx=ctx;
        this.app= ai;
        inflator=LayoutInflater.from(ctx);
        prefs=new SPrefs(ctx);
        util = new Utils(ctx);
    }
    
    public void updateList(List<AppInfo> newlist){
        //
        appList = sort(newlist);//AppInfoFactory.getAllApps(ctx);
        
        notifyDataSetChanged();
    }
    
    List<AppInfo> sort(List<AppInfo> mList){
        List<AppInfo> allowed = new ArrayList<>();
        List<AppInfo> rest = new ArrayList<>();
        for(AppInfo info : mList){
            if(info.allowed){
                allowed.add(info);
            }else{
                rest.add(info);
            }
        }
        //appList.clear();
        mList.clear();
        for(AppInfo a : allowed){
            mList.add(a);
        }
        for(AppInfo r : rest){
            mList.add(r);
        }
        return mList;
    }

    @Override
    public int getCount(){
        return appList.size();
    }

    @Override
    public Object getItem(int pos){
        return appList.get(pos);
    }

    @Override
    public long getItemId(int p1){
        return p1;
    }

    @Override
    public View getView(int pos, View view, ViewGroup p3)
    {
        final AppInfo info = appList.get(pos);
        view = inflator.inflate(R.layout.appitem, null, false);
        TextView name = (TextView)view.findViewById(R.id.appitem_appname);
        name.setText(info.name);
        if(info.system){
            name.setTextColor(Color.parseColor("#aa0009"));
        }
       
        final ImageView ic = (ImageView) view.findViewById(R.id.appitem_appicon);
        //ic.setBackgroundDrawable(info.icon);
        app.queueTask(new Runnable(){
            public void run(){
                final Drawable d = AppInfoFactory.getIcon(ctx,info.rootinfo);
                if(d!=null){
                    app.runInMainThread(new Runnable(){
                        public void run(){
                            ic.setBackgroundDrawable(d);
                        }
                    });
                }
            }
        });
        
        Switch itemswitch = (Switch) view.findViewById(R.id.appitem_switch);
        itemswitch.setChecked(info.allowed);
        itemswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton p1, boolean checked)
            {
                if(checked){
                    util.addAllowed(info.packageName);
                }else{
                    util.removePkg(info.packageName);
                }
                WireGuardService.reload(ctx);
            }
        });
        
        if(info.launchIntent!=null){
            ic.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    ctx.startActivity(info.launchIntent);
                }
            });
            view.findViewById(R.id.appitem_launchableic).setVisibility(View.VISIBLE);
        }
        
        TextView tvappname = (TextView)view.findViewById(R.id.appitem_more_name);
        tvappname.setText(info.name+" [["+info.rootinfo.uid+"]]");
        TextView tvpkgname = (TextView)view.findViewById(R.id.appitem_more_pkgname);
        tvpkgname.setText(info.packageName);
        TextView tvVersion = (TextView)view.findViewById(R.id.appitem_more_version);
        tvVersion.setText(info.version);
        //
        final LinearLayout more = (LinearLayout)view.findViewById(R.id.appitem_more_root);
        if(info.ismorevisible){
            more.setVisibility(View.VISIBLE);
        }else{
            more.setVisibility(View.GONE);
        }
        //
        view.findViewById(R.id.appitem_main)
        .setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p1)
            {
                    if(info.ismorevisible){
                        more.setVisibility(View.GONE);
                    }else{
                        more.setVisibility(View.VISIBLE);
                    }
                    
                    info.ismorevisible=!info.ismorevisible;
            }
        });
        return view;
    }
    
}
