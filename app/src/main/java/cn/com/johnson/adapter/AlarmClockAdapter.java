package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.model.AlarmClockEntity;
import de.blinkt.openvpn.views.SwipeMenuView;
import de.blinkt.openvpn.views.SwitchView;


/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class AlarmClockAdapter extends RecyclerBaseAdapter<AlarmClockAdapter.AlarmClockHolder, AlarmClockEntity> {

    private boolean isEditAlarmClock;
    private boolean isVisibility;
    public AlarmClockAdapter(Context context, List<AlarmClockEntity> list) {
        super(context, list);
    }

    public void isEditAlarmClock(boolean isEditAlarmClock){
        this.isEditAlarmClock=isEditAlarmClock;
    }
    public void isVisibility(boolean isVisibility){
        this.isVisibility=isVisibility;
    }
    @Override
    public void onBindViewHolder(final AlarmClockHolder holder,final int position) {
        if(!isVisibility&&(position==0||position==mList.size()-1)){
            holder.marginScreen.setVisibility(View.GONE);
            holder.matchScreen.setVisibility(View.VISIBLE);
        }else if(isVisibility&&position==0){
            holder.marginScreen.setVisibility(View.GONE);
            holder.matchScreen.setVisibility(View.VISIBLE);
        }else {
            holder.marginScreen.setVisibility(View.VISIBLE);
            holder.matchScreen.setVisibility(View.GONE);
        }
        if(isEditAlarmClock){
            holder.swipeContent.setBackground(mContext.getResources().getDrawable(R.drawable.call_phone_selector));
            ((SwipeMenuView) holder.itemView).setIos(false).setSwipeEnable(false);
            holder.alarmClockDeleteIv.setVisibility(View.VISIBLE);
            holder.canClickIv.setVisibility(View.VISIBLE);
            holder.alarmClockDeleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(SwipeMenuView.getViewCache()!=null){
                        if(SwipeMenuView.getViewCache() != holder.itemView)
                            SwipeMenuView.getViewCache().smoothClose();
                        SwipeMenuView.setViewCache(null);
                    }
                    ((SwipeMenuView) holder.itemView).setIos(false).setSwipeEnable(true);
                    ((SwipeMenuView) holder.itemView).noScroll(true);
                    ((SwipeMenuView) holder.itemView).setExpand(true);
                    ((SwipeMenuView) holder.itemView).smoothExpand();
                    SwipeMenuView.setViewCache((SwipeMenuView) holder.itemView);
                }
            });
            holder.llSmothClose.setEnabled(true);
            holder.llSmothClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!((SwipeMenuView) holder.itemView).getExpand()) {
                        mOnSwipeListener.onItemClick(position);
                    } else {
                        ((SwipeMenuView) holder.itemView).setExpand(false);
                    }
                }
            });
            holder.switchView.setVisibility(View.GONE);
        }else{
            holder.llSmothClose.setEnabled(false);
            ((SwipeMenuView) holder.itemView).noScroll(false);
            holder.swipeContent.setBackground(mContext.getResources().getDrawable(R.color.white));
            ((SwipeMenuView) holder.itemView).setIos(false).setSwipeEnable(true);
            ((SwipeMenuView) holder.itemView).setIos(false).setLeftSwipe(true);
            holder.alarmClockDeleteIv.setVisibility(View.GONE);
            holder.canClickIv.setVisibility(View.GONE);
            holder.switchView.setVisibility(View.VISIBLE);
            holder.switchView.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                @Override
                public void toggleToOn(SwitchView view) {
                    view.setOpened(true);
                    mOnSwipeListener.statueChange(1,position);
                }

                @Override
                public void toggleToOff(SwitchView view) {
                    view.setOpened(false);
                    mOnSwipeListener.statueChange(0,position);
                }
            });
        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnSwipeListener) {
                    //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                    //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                    //((CstSwipeDelMenu) holder.itemView).quickClose();
                    mOnSwipeListener.onDel(position);
                }
            }
        });
        holder.alarmClockDays.setText(mList.get(position).getTag()+getStringBuilder(TextUtils.isEmpty(mList.get(position).getRepeat())?null:Arrays.asList(mList.get(position).getRepeat().split(","))));
        holder.switchView.setOpened(mList.get(position).getStatus().equals("1")?true:false);
        holder.alarmClockTimeTv.setText(mList.get(position).getTime());
    }
    private String getStringBuilder(List<String> repeatDayList) {
        if(repeatDayList==null){
            return "";
        }
        StringBuilder weekStr = new StringBuilder(",");
        for (String iString : repeatDayList) {
            if(TextUtils.isEmpty(iString)){
                continue;
            }
            int jday=Integer.parseInt(iString);
            switch (jday) {
                case 0:
                    weekStr.append(" "+mContext.getResources().getString(R.string.Monday));
                    break;
                case 1:
                    weekStr.append(" "+mContext.getResources().getString(R.string.Tuesday));
                    break;
                case 2:
                    weekStr.append(" "+mContext.getResources().getString(R.string.Wednesday));
                    break;
                case 3:
                    weekStr.append(" "+mContext.getResources().getString(R.string.Thursday));

                    break;
                case 4:
                    weekStr.append(" "+mContext.getResources().getString(R.string.Friday));

                    break;
                case 5:
                    weekStr.append(" "+mContext.getResources().getString(R.string.Saturday));
                    break;
                case 6:
                    weekStr.append(" "+mContext.getResources().getString(R.string.Sunday));
                    break;
            }

        }
        return weekStr.toString();
    }
    @Override
    public AlarmClockHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlarmClockHolder(LayoutInflater.from(mContext).inflate(R.layout.item_alarm_clock, parent, false));
    }
    public interface onSwipeListener {
        void onDel(int pos);
        void onItemClick(int pos);
        void statueChange(int statue,int pos);
    }

    private onSwipeListener mOnSwipeListener;

    public void setOnDelListener(onSwipeListener mOnDelListener) {
        this.mOnSwipeListener = mOnDelListener;
    }
    public class AlarmClockHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.alarm_clock_delete_iv)
        ImageView alarmClockDeleteIv;
        @BindView(R.id.alarm_clock_time_tv)
        TextView alarmClockTimeTv;
        @BindView(R.id.swipe_content)
        LinearLayout swipeContent;
        @BindView(R.id.alarm_clock_days)
        TextView alarmClockDays;
        @BindView(R.id.switchView)
        SwitchView switchView;
        @BindView(R.id.can_click_iv)
        ImageView canClickIv;
        @BindView(R.id.btnDelete)
        Button btnDelete;
        @BindView(R.id.match_screen)
        View matchScreen;
        @BindView(R.id.margin_screen)
        View marginScreen;
        @BindView(R.id.ll_smoth_close)
        LinearLayout llSmothClose;

        public AlarmClockHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
