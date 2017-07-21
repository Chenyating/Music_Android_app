package jimihua.music_list;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jimihua.user.biz.MusicListBiz;
import jimihua.user.biz.impl.MusicListBizImpl;
import jimihua.user.po.MusicList;
import jimihua.work.R;

/**
 * Created by Me on 2017/2/27.
 */
public class musiclistadapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<HashMap<String, Object>> lstitems;
    private LayoutInflater inflater;
    private  MusicListBiz musiclistBiz;
    private MusicList musiclist;

    public musiclistadapter(Context mContext, ArrayList<HashMap<String, Object>> lstitems) {
        this.mContext = mContext;
        this.lstitems = lstitems;
        this.inflater=LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return this.lstitems.size();
    }

    @Override
    public Object getItem(int position) {
        return this.lstitems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       viewholder viewholder=null;

        if (convertView==null){

            viewholder =new viewholder();
            convertView=inflater.inflate(R.layout.activity_music_list_item,null);//拿布局
            viewholder.songname= (TextView) convertView.findViewById(R.id.music_list_songname);
            viewholder.singername= (TextView) convertView.findViewById(R.id.music_list_singername);
            viewholder.songduration= (TextView) convertView.findViewById(R.id.music_list_songduration);
            viewholder.xihuan= (ImageView) convertView.findViewById(R.id.xihuan);
            convertView.setTag(viewholder);
        }else {
            viewholder=(viewholder)convertView.getTag();
        }

        viewholder.songname.setText(this.lstitems.get(position).get("songname").toString());
        viewholder.singername.setText(this.lstitems.get(position).get("singername").toString());
        viewholder.songduration.setText(this.lstitems.get(position).get("songduration").toString());

        viewholder.xihuan.setImageDrawable(mContext.getResources().getDrawable(R.drawable.nolove));

        MusicListBiz  musiclistBiz1=new MusicListBizImpl();
        List<MusicList> lstMessage=musiclistBiz1.find(mContext);

        for(int i=0;i<lstMessage.size();i++){

            if (lstMessage.get(i).getPosition()==position) {
                viewholder.xihuan.setImageDrawable(mContext.getResources().getDrawable(R.drawable.love));
                break;
            }
        }

        viewholder.xihuan.setOnClickListener(new ViewOcl(position, viewholder));

        return convertView;
    }

    private  class ViewOcl implements View.OnClickListener{

         private int position;
         private viewholder viewhold;

         public ViewOcl(int position,viewholder viewhold) {
             this.position = position;
             this.viewhold=viewhold;

             musiclistBiz=new MusicListBizImpl();
         }

         @Override
         public void onClick(View v) {

             if (viewhold.xihuan.getDrawable().getCurrent().getConstantState().equals(mContext.getResources().getDrawable(R.drawable.nolove).getConstantState()))
             {
                 viewhold.xihuan.setImageDrawable(mContext.getResources().getDrawable(R.drawable.love));

                 musiclist=new MusicList();

                 musiclist.setPosition(position);
                 musiclist.setSongname(lstitems.get(position).get("songname").toString());
                 musiclistBiz.add(mContext, musiclist);


                 MusicListBiz  musiclistBiz1=new MusicListBizImpl();
                 List<MusicList> lstMessage=musiclistBiz1.find(mContext);

             }else{
                 viewhold.xihuan.setImageDrawable(mContext.getResources().getDrawable(R.drawable.nolove));

                 musiclist=new MusicList();

                 musiclist.setPosition(position);

                 musiclistBiz.remove(mContext, musiclist);

             }
         }
     }
}
