package com.delta.p15;

import com.squareup.otto.Subscribe;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final int CENTRAL_PAGE_INDEX = 1;

	private VerticalPager mVerticalPager;
	private ListView mListView;
    private ListViewAdapter mAdapter;
    private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		findViews();
		mListView = (ListView)findViewById(R.id.listview);
		LinearLayout container = (LinearLayout) findViewById(R.id.svg_container);
        LayoutInflater inflater = getLayoutInflater();
        addSvgView(inflater, container);

	if (savedInstanceState == null) {
        getFragmentManager().beginTransaction()
                .add(R.id.container, new ParallaxFragment(R.drawable.starfield2,0))
                .commit();
    }
	if (savedInstanceState == null) {
        getFragmentManager().beginTransaction()
                .add(R.id.container2, new ParallaxFragment(R.drawable.starfield1,1))
                .commit();
    }
	if (savedInstanceState == null) {
        getFragmentManager().beginTransaction()
                .add(R.id.container3, new ParallaxFragment(R.drawable.starfield1,1))
                .commit();
    }
	 mAdapter = new ListViewAdapter(this);
     mListView.setAdapter(mAdapter);
     mAdapter.setMode(SwipeItemMangerImpl.Mode.Single);

}
	private void findViews() {
		mVerticalPager = (VerticalPager) findViewById(R.id.activity_main_vertical_pager);
		initViews();
	}

	private void initViews() {
		snapPageWhenLayoutIsReady(mVerticalPager, CENTRAL_PAGE_INDEX);
	}

	private void snapPageWhenLayoutIsReady(final View pageView, final int page) {
		/*
		 * VerticalPager is not fully initialized at the moment, so we want to snap to the central page only when it
		 * layout and measure all its pages.
		 */
		pageView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				mVerticalPager.snapToPage(page, VerticalPager.PAGE_SNAP_DURATION_INSTANT);

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
					// recommended removeOnGlobalLayoutListener method is available since API 16 only
					pageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				else
					removeGlobalOnLayoutListenerForJellyBean(pageView);
			}

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			private void removeGlobalOnLayoutListenerForJellyBean(final View pageView) {
				pageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getInstance().register(this);
	}

	@Override
	protected void onPause() {
		EventBus.getInstance().unregister(this);
		super.onPause();
	}

	@Subscribe
	public void onLocationChanged(PageChangedEvent event) {
		mVerticalPager.setPagingEnabled(event.hasVerticalNeighbors());
	}
	private void addSvgView(LayoutInflater inflater, LinearLayout container)
    {
        final View view = inflater.inflate(R.layout.item_svg, container, false);
        final SvgView svgView = (SvgView) view.findViewById(R.id.svg);

        svgView.setSvgResource(R.raw.map_pr);
        //view.setBackgroundResource(R.color.accent);
        svgView.setmCallback(new SvgCompletedCallBack() {
			ProgressView p = (ProgressView) findViewById(R.id.progress);
			TextView t =(TextView) findViewById(R.id.textView2);
			@Override
			public void onSvgCompleted() {
				p.setVisibility(view.INVISIBLE);
				t.setVisibility(view.VISIBLE);
				
			}
		});
        
        container.addView(view);
       

        Handler handlerDelay = new Handler();
        handlerDelay.postDelayed(new Runnable(){
            public void run() {
                svgView.startAnimation();
            }}, 2000);
    }

}