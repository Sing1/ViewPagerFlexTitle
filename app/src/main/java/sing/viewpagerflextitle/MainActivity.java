package sing.viewpagerflextitle;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import sing.flextitle.ViewPagerTitle;

public class MainActivity extends AppCompatActivity {

    private ViewPager pager;
    private MyPagerAdapter adapter;
    private ArrayList<View> views;
    private ViewPagerTitle vpTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        views = new ArrayList<>();

        vpTitle = (ViewPagerTitle) findViewById(R.id.pager_title);
        pager = (ViewPager) findViewById(R.id.view_pager);

        vpTitle.initData(new String[]{"关注", "推荐", "视频"}, pager);

        MyOnPageChangeListener pageChangeListener = new MyOnPageChangeListener(this, pager, vpTitle.getDynamicLine(), vpTitle, vpTitle.getAllTextViewLength(), vpTitle.getMargin());
        vpTitle.addOnPageChangeListener(pageChangeListener);

        views = new ArrayList<>();
        TextView tv1 = (new TextView(this));
        TextView tv2 = (new TextView(this));
        TextView tv3 = (new TextView(this));
        tv1.setBackgroundColor(Color.parseColor("#EF111111"));
        tv2.setBackgroundColor(Color.parseColor("#EF222222"));
        tv3.setBackgroundColor(Color.parseColor("#EF333333"));

        views.add(tv1);
        views.add(tv2);
        views.add(tv3);

        adapter = new MyPagerAdapter(views);
        pager.setAdapter(adapter);
    }
}