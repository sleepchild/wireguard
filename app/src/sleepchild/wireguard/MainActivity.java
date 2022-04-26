package sleepchild.wireguard;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.os.*;
import java.util.*;
import android.view.animation.*;
import android.content.*;
import android.net.*;
import android.view.inputmethod.*;

public class MainActivity extends Activity {
    //
    public static final String TAG = "";
    public static final int LAUNCH_CODE = 246;
    
    Utils util=new Utils(this);
    SPrefs prefs;
    Switch wgToggle;
    ListView list5;
    AppListAdapter alAdapter;
    ProgressBar spinner;
    Context ctx;
    
    App app;
    
    boolean enabled;
    int VPN_SERVICE_REQUEST = 566;
    
    SearchPanel searchPanel;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = getApplicationContext();
        //
        prefs=new SPrefs(this);
        //
        init();
    }
    
    private void init(){
        app = new App(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        
        searchPanel = new SearchPanel(this);
        
        enabled=prefs.getWGEnabled();
        //
        wgToggle=(Switch)findViewById(R.id.main_toggle);
        updateToggle();
        wgToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            //
            @Override
            public void onCheckedChanged(CompoundButton p1, boolean on)
            {
                if(on){
                    startVPN();
                }else{
                    WireGuardService.stop(ctx);
                }
            }
        });
        //
        spinner=(ProgressBar)findViewById(R.id.spinner);
        //
        list5=(ListView)findViewById(R.id.applist);
        alAdapter=new AppListAdapter(ctx, app);
        list5.setAdapter(alAdapter);
        //
        updateAppList();
        //
    }
    
    private void startVPN(){
        Intent prep = VpnService.prepare(MainActivity.this);
        if(prep==null){
            onActivityResult(VPN_SERVICE_REQUEST,RESULT_OK,null);
        }else{
            try{
                startActivityForResult(prep, VPN_SERVICE_REQUEST);
            }catch(Exception e){
                onActivityResult(VPN_SERVICE_REQUEST,RESULT_CANCELED,null);
            }
            
        }
    }
    
    private void updateToggle(){
        wgToggle.setChecked(enabled);
    }
    
    public void openSettings(View v){
        Intent logintent = new Intent(this, SettingsActivity.class);
        startActivity(logintent);
    }
    
    public void showKeyboard(View v){
        v.requestFocus();
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }
    
    public void showSearchPanel(View v){
        searchPanel.show();
    }
    
    public void updateAppList(){
        new AsyncTask<Object, Object, List<AppInfo>>(){
            @Override
            public void onPreExecute(){
                // notify ui. app list updating..; show the spinner
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<AppInfo> doInBackground(Object[] p1){
                List<AppInfo> list = AppInfoFactory.getAllApps(getApplicationContext());
                return list;
            }
            
            @Override 
            public void onPostExecute(List<AppInfo> result){
                // update the list
                alAdapter.updateList(result);
                // notify ui update complete; remove the spinner
                spinner.setVisibility(View.GONE);
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==VPN_SERVICE_REQUEST){
            if(resultCode==RESULT_OK){
                WireGuardService.start(ctx);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed()
    {
        if(searchPanel.isVisible()){
            searchPanel.close();
            updateAppList();
        }else{
            super.onBackPressed();
        }
    }
    
    
    
}
