package sleepchild.wireguard;

import android.widget.*;
import android.view.*;
import java.util.*;
import android.content.*;

public class LogAdapter extends BaseAdapter
{
    List<LogItem> itemList = new ArrayList<>();
    Context ctx;
    LayoutInflater inf;
    
    public LogAdapter(Context ctx){
        this.ctx = ctx;
        this.inf = LayoutInflater.from(ctx);
    }
    
    public void update(List<LogItem> nlist){
        this.itemList = nlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        // TODO: Implement this method
        return itemList.size();
    }

    @Override
    public Object getItem(int pos)
    {
        // TODO: Implement this method
        return itemList.get(pos);
    }

    @Override
    public long getItemId(int p1)
    {
        // TODO: Implement this method
        return p1;
    }

    @Override
    public View getView(int p1, View v, ViewGroup p3)
    {
        LogItem lit = itemList.get(p1);
        
        LinearLayout view  = (LinearLayout) inf.inflate(R.layout.logitem, null, false);
        TextView raw = (TextView) view.findViewById(R.id.logitem_raw);
        raw.setText(lit.raw);
        
        return view;
    }
    
}
