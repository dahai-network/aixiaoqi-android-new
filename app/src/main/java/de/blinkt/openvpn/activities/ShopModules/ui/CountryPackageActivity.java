package de.blinkt.openvpn.activities.ShopModules.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.ShopModules.presenter.CountryPackagePresenter;
import de.blinkt.openvpn.activities.ShopModules.view.CountryPackageView;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;

/**
 * 国家套餐界面
 */
public class CountryPackageActivity extends BaseActivity implements CountryPackageView {

    public static CountryPackageActivity activity = null;
    @BindView(R.id.packageImageView)
    ImageView packageImageView;
    @BindView(R.id.packageDetailRecyclerView)
    RecyclerView packageDetailRecyclerView;
    @BindView(R.id.retryTextView)
    TextView retryTextView;
    @BindView(R.id.NoNetRelativeLayout)
    RelativeLayout NoNetRelativeLayout;
    @BindView(R.id.nodataTextView)
    TextView nodataTextView;
    private CountryPackagePresenter countryPackagePresenter;

    public static void launch(Context context, String countryPic, String countryStr, String id) {
        Intent intent = new Intent(context, CountryPackageActivity.class);
        intent.putExtra("country", countryStr);
        intent.putExtra("id", id);
        intent.putExtra("countryPic", countryPic);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_detail);
        Log.i(TAG, "onCreate: CountryPackageActivity");
        ButterKnife.bind(this);
        initSet();
        countryPackagePresenter=new CountryPackagePresenter(this);
        addData();
    }

    String countryPic;

    private void initSet() {
        activity = this;
        Intent intent = getIntent();
        String countryStr = intent.getStringExtra("country");
        titleBar.setLeftBtnIcon(R.drawable.btn_top_back);
        if (TextUtils.isEmpty(countryStr)) {
            titleBar.getTitle().setText("");
        } else {
            titleBar.getTitle().setText(countryStr);
        }

        titleBar.getLeftText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        countryPic = getIntent().getStringExtra("countryPic");
        if (countryPic != null)
            Glide.with(ICSOpenVPNApplication.getContext()).load(countryPic).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    int imageWidth = resource.getWidth();
                    int imageHeight = resource.getHeight();
                    int height = CommonTools.getScreenWidth(CountryPackageActivity.this) * imageHeight / imageWidth;
                    ViewGroup.LayoutParams para = packageImageView.getLayoutParams();
                    para.height = height;
                    packageImageView.setLayoutParams(para);
                    packageImageView.setImageBitmap(resource);
                }
            });
        packageDetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    String countryId;
    private void addData() {
        countryId = getIntent().getStringExtra("id");
        countryPackagePresenter.addCountryPackageData();
    }

    @OnClick(R.id.retryTextView)
    public void onClick() {
        addData();
    }

    @Override
    public String getCountryId() {
        return countryId;
    }

    @Override
    public RelativeLayout getNoNetRelativeLayout() {
        return NoNetRelativeLayout;
    }

    @Override
    public RecyclerView getPackageDetailRecyclerView() {
        return packageDetailRecyclerView;
    }

    @Override
    public TextView getNodataTextView() {
        return nodataTextView;
    }

    @Override
    public String getCountryPic() {
        return countryPic;
    }

    @Override
    public ImageView getPackageImageView() {
        return packageImageView;
    }
}
