package sleepchild.wireguard;

import android.content.Context;
import android.widget.*;
import java.io.*;
import java.util.*;

public class Utils
{
    Context ctx;
    String wdir = "/sdcard/.sleepchild/wireguard/";
    String wgal = wdir+"wgal.txt";
    
    public Utils(Context ctx){
        this.ctx=ctx;
        new File(wdir).mkdirs();
    }
    
    public void toast(String msg){
        Toast.makeText(ctx,msg,500).show();
    }
    
    public List<String> getAllowedList(){
        List<String> apl = new ArrayList<>();
        String dat = readText(wgal);
        String[] l = dat.split(",");
        for(String s: l){
            if(!s.equals(",")){
                if(!s.isEmpty()){
                    apl.add(s);
                }
            }
        }
        return apl;
    }
    
    public void addAllowed(String pkgname){
        try
        {
            RandomAccessFile rf = new RandomAccessFile(wgal, "rw");
            rf.seek(rf.length());
            String dat = pkgname+",";
            rf.write(dat.getBytes());
            rf.close();
        }
        catch (FileNotFoundException e)
        {}catch(IOException ioe){}
    }
    
    public void removePkg(String pkgname){
        List<String> plist = getAllowedList();
        plist.remove(pkgname);
        clearPKL();
        for(String pk : plist){
            addAllowed(pk);
        }
    }
    
    public boolean isAllowed(String pkgname){
        return getAllowedList().contains(pkgname);
    }
    
    void clearPKL(){
        File fl = new File(wgal);
        fl.delete();
    }
    
    String readText(String filepath){
        //
        
        if(new File(filepath).exists()){
            try
            {
                String dat="";
                String s ="";
                BufferedReader br = new BufferedReader(new FileReader(filepath));
                while(( s = br.readLine())!=null){
                    dat += s;
                }
                return dat;
            }
            catch (FileNotFoundException e)
            {}catch(IOException ioe){}
        }
        return "";
    }
    //
}
