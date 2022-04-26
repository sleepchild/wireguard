package sleepchild.wireguard;
import android.widget.*;
import android.view.*;
import android.os.*;
import java.util.*;

public class LogActivity extends BaseActivity
{
    ListView ilist;
    LogAdapter lad;

    @Override
    public void init()
    {
        // TODO: Implement this method
        super.init();
        setContentView(R.layout.logactivity);
        
        ilist = (ListView) findViewById(R.id.logactivity_ilist);
        lad = new LogAdapter(this);
        
        ilist.setAdapter(lad);
        
        getLOGDATA();
    }
    
    void getLOGDATA(){
        new AsyncTask<Void, Void, List<LogItem>>(){

            @Override
            public void onPreExecute(){
                //
            }
            
            @Override
            protected List<LogItem> doInBackground(Void[] p1)
            {
                List<LogItem> res = new ArrayList<>();
                String raw = LogTool.get().filestr;
                for(String str : raw.split(" ")){
                    res.add(new LogItem(str));
                }
                return res;
            }
            
            @Override
            public void onPostExecute(List<LogItem> res){
                
                lad.update(res);
            }
            
            
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add("refresh");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //
        getLOGDATA();
        return super.onOptionsItemSelected(item);
    }
}
