package sleepchild.wireguard;
import java.io.*;
import java.util.*;

public class LogTool
{
    public static volatile LogTool deftInstance;
    
    private LogTool(){
        //
    }
    
    public static LogTool get(){
        LogTool instance = deftInstance;
        if(instance==null){
            synchronized(LogTool.class){
                instance = LogTool.deftInstance;
                if(instance==null){
                    instance = LogTool.deftInstance = new LogTool();
                }
            }
        }
        return instance;
    }
    
    
    String filestr = "";;
    public void f(String tag, String msg){
        filestr += tag +":\n  "+ msg + "\n";
    }
    
    public void push(){
        if(filestr.isEmpty()){
            return;
        }
        try
        {
            long stamp = new Date().getTime();
            FileOutputStream fout = new FileOutputStream("/sdcard/.sleepchild/wire_"+stamp+".txt");
            fout.write(filestr.getBytes());
            fout.flush();
            fout.close();
            filestr="";
        }
        catch (FileNotFoundException e)
        {}catch(IOException ioe){
            
        }
    }
}
