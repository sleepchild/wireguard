package sleepchild.wireguard;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import java.util.*;
import android.text.*;
import android.os.*;
import java.util.concurrent.*;

public class SearchPanel implements View.OnClickListener
{
    View root;
    EditText searchInput;
    ListView list3;
    MainActivity act;
    MAD adaptor;
    Handler handle = new Handler(Looper.getMainLooper());
    ExecutorService worker;
    
    public SearchPanel(MainActivity ctx){
        //
        this.act = ctx;
        worker = Executors.newSingleThreadExecutor();
        
        root = act.findViewById(R.id.activity_main_searchpanel);
        root.setOnClickListener(this);
        
        searchInput = (EditText) act.findViewById(R.id.activity_main_searchinput);
        searchInput.addTextChangedListener(new TextWatcher(){
                @Override
                public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
                {
                    // TODO: Implement this method
                }

                @Override
                public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
                {
                    String text = searchInput.getText().toString();
                    if(text!=null && !text.isEmpty()){
                        getSearchResult(text);
                    }else if(text!=null && text.isEmpty()){
                        adaptor.clear();
                    }
                }

                @Override
                public void afterTextChanged(Editable p1)
                {
                    // TODO: Implement this method
                } 
        });
        //
        adaptor = new MAD(act);
        list3 = (ListView) act.findViewById(R.id.activity_main_searchlist);
        list3.setAdapter(adaptor);
        //
    }
    
    public void show(){
        root.setVisibility(View.VISIBLE);
        act.showKeyboard(searchInput);
    }
    
    public void hide(){
        close();
    }
    
    public void close(){
        searchInput.clearFocus();
        searchInput.setText("");
        adaptor.clear();
        root.setVisibility(View.GONE);
    }
    
    public boolean isVisible(){
        return root.getVisibility()==View.VISIBLE;
    }

    @Override
    public void onClick(View v){
        if(v.getTag()!=null){
            //
        }
    }
    
    private void getSearchResult(final String searchtext){
        //
        worker.submit(new Runnable(){
            @Override
            public void run(){
                final String text = searchtext.toLowerCase();
                final List<AppInfo> listRes = new ArrayList<>();
                
                for(AppInfo i : AppInfoFactory.getAllApps(act)){
                    if(i.name.toLowerCase().contains(text)
                        || i.packageName.toLowerCase().contains(text)){
                        listRes.add(i);
                    }
                }
                
                // needs rewrite so that starsWith comes first then contans 2nd 
                Collections.sort(listRes, new Comparator<AppInfo>(){
                        @Override
                        public int compare(AppInfo p1, AppInfo p2)
                        {
                            if(p1.name.toLowerCase().contains(text)){
                               return -1; 
                            }
                            return 0;
                        }
                });
                handle.postDelayed(new Runnable(){
                    public void run(){
                        adaptor.update(listRes);
                    }
                },1);
                
            }
        });
    }
    
    private class MAD extends BaseAdapter{
        List<AppInfo> results = new ArrayList<>();
        LayoutInflater inf;
        Utils utils;
        MainActivity mCtx;
        
        MAD(MainActivity act){
            this.mCtx = act;
            this.inf = act.getLayoutInflater();
            utils = new Utils(act);
        }
        
        public void update(List<AppInfo> list){
            results = list;
            notifyDataSetChanged();
        }
        
        public void clear(){
            results.clear();
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int pos)
        {
            return results.get(pos);
        }

        @Override
        public int getCount()
        {
            return results.size();
        }

        @Override
        public long getItemId(int p1)
        {
            //
            return p1;
        }

        @Override
        public View getView(int pos, View v, ViewGroup p3)
        {
            final AppInfo info = results.get(pos);
            v = inf.inflate(R.layout.appitem, null, false);
            TextView title = (TextView) v.findViewById(R.id.appitem_appname);
            title.setText(info.name);
            
            TextView name2 = (TextView) v.findViewById(R.id.appitem_more_name);
            name2.setText(info.packageName);
            TextView pkgn = (TextView) v.findViewById(R.id.appitem_more_pkgname);
            pkgn.setText(info.packageName);
            
            final Switch cb = (Switch) v.findViewById(R.id.appitem_switch);
            cb.setChecked(info.allowed);
            cb.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(cb.isChecked()){
                        utils.addAllowed(info.packageName);
                    }else{
                        utils.removePkg(info.packageName);
                    }
                    WireGuardService.reload(mCtx);
                }
            });
            
            v.setTag(info);
            return v;
        }
    }
    
    
    
} 
