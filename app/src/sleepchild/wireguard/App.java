package sleepchild.wireguard;
import java.util.concurrent.*;
import android.content.*;
import android.os.*;

public class App
{
    ExecutorService worker;
    Handler handle;
     
    public App(Context ctx){
        worker = Executors.newFixedThreadPool(2);
        handle = new Handler(Looper.getMainLooper());
        
    }
    
    public void runInMainThread(Runnable task){
        handle.postDelayed(task,1);
    }
    
    public void queueTask(Runnable task){
        worker.submit(task);
    }
    
    
    
    
}
