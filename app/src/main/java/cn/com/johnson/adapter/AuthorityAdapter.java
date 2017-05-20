package cn.com.johnson.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.model.PhoneAuthonCountEntity;
import de.blinkt.openvpn.model.AuthorityEntity;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by Administrator on 2017/3/20.
 */

public class AuthorityAdapter extends RecyclerView.Adapter<AuthorityAdapter.AuthorityViewHolder> {


	private final Context context;
	private ArrayList<AuthorityEntity> data = null;

    public final static int FRIST=1;
    public final static int TWO=2;
    public final static int THREE=3;


	public AuthorityAdapter(Context context, ArrayList<AuthorityEntity> data) {
		this.context = context;
		this.data = new ArrayList<>();
		this.data.addAll(data);
	}

	@Override
	public AuthorityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		AuthorityViewHolder holder = new AuthorityViewHolder(LayoutInflater.from(context).inflate(R.layout.item_authority, parent, false));
		return holder;
	}

	@Override
	public void onBindViewHolder(AuthorityViewHolder holder, final int position) {
		final AuthorityEntity entity = data.get(position);
		holder.titleTextView.setText(entity.getTitle());
		holder.tipTextView.setText(entity.getTip());
		holder.tv_setting.setBackgroundResource(entity.isCanClick() ? R.drawable.authority_ret : R.drawable.authority_ret_unenable);
		final int number = position + 1;
		//根据位置设置不同序列号
		switch (number) {
			case FRIST:
				holder.iv_number.setBackgroundResource(R.drawable.number1);
				break;
			case TWO:
				holder.iv_number.setBackgroundResource(R.drawable.number2);
				break;
			case THREE:
				holder.iv_number.setBackgroundResource(R.drawable.number3);
				break;

		}

		holder.rootRelativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!entity.isCanClick()) return;
				try {
					context.startActivity(entity.getintentEntity().getAuthorityIntent());
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							//当前位置保存
							PhoneAuthonCountEntity.getInstance().setPosition(position + 1);
							context.startActivity(entity.getintentEntity().getShadeIntent());

						}
					}, 500);
					if (data.size() > number) {
						data.get(number).setCanClick(true);
						CommonTools.delayTime(1500);
						notifyDataSetChanged();
					}
				} catch (Exception e) {
					e.printStackTrace();
					CommonTools.showShortToast(context, "找不到该权限");
				}


			}
		});

	}


	@Override
	public int getItemCount() {
		return data.size();
	}

	class AuthorityViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.rootRelativeLayout)
		RelativeLayout rootRelativeLayout;
		@BindView(R.id.titleTextView)
		TextView titleTextView;
		@BindView(R.id.tipTextView)
		TextView tipTextView;
		@BindView(R.id.iv_number)
		ImageView iv_number;
		@BindView(R.id.tv_setting)
		TextView tv_setting;

		public AuthorityViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

	}
}
