package sleepchild.wireguard;
import android.app.*;
import android.os.*;
import android.content.*;

public class BaseActivity extends Activity
{
    SPrefs prefs;
    Context ctx;
    

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState)
    {
        super.onCreate(savedInstanceState, persistentState);
        this.prefs = new SPrefs(this);
        this.ctx = this;
        init();
    }
    
    public void init(){
        //
    }
}
