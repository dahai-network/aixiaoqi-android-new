package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.model.AuthorityEntity;

/**
 * Created by Administrator on 2017/3/20.
 */

public class AuthorityAdapter extends RecyclerView.Adapter<AuthorityAdapter.AuthorityViewHolder> {

	private final Context context;
	private ArrayList<AuthorityEntity> data = null;


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
	public void onBindViewHolder(AuthorityViewHolder holder, int position) {
		final AuthorityEntity entity = data.get(position);
		holder.titleTextView.setText(entity.getTitle());
		holder.tipTextView.setText(entity.getTip());
		holder.rootRelativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				CommonTools.showShortToast(context, "产品信息: " + android.os.Build.MODEL + ","
//						+ android.os.Build.VERSION.SDK + ","
//						+ android.os.Build.VERSION.RELEASE);
				new Thread(new Runnable() {
					@Override
					public void run() {
						context.startActivity(entity.getintentEntity().getAuthorityIntent());
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						context.startActivity(entity.getintentEntity().getShadeIntent());
					}
				}).start();
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

		public AuthorityViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

	}
}
