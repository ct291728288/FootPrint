package com.ctry.positionrecorder;

import java.lang.reflect.Field;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

	@SuppressWarnings("deprecation")
	public class DeskMenu extends TabActivity {
		private TabHost tabHost;
		private TabWidget tabWidget;
		Field mBottomLeftStrip;
		Field mBottomRightStrip;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.desk_menu);

			makeTab();
		}

		public void makeTab() {
			if (this.tabHost == null) {
				tabHost = getTabHost();
				tabWidget = getTabWidget();
				tabHost.setup();
				tabHost.bringToFront();

				TabSpec firsttab = tabHost.newTabSpec("firsttab");
				TabSpec sencondtab = tabHost.newTabSpec("sencondtab");
				TabSpec thirdtab = tabHost.newTabSpec("thirdtab");
				TabSpec fourthtab = tabHost.newTabSpec("fourthtab");

				firsttab.setIndicator("开始记录",
						getResources().getDrawable(R.drawable.first)).setContent(
						new Intent(this, MainActivity.class));
				sencondtab.setIndicator("当前定位",
						getResources().getDrawable(R.drawable.second)).setContent(
						new Intent(this, CurrentLocation.class));
				thirdtab.setIndicator("足迹历史",
						getResources().getDrawable(R.drawable.third)).setContent(
						new Intent(this, FileList.class));
				fourthtab.setIndicator("软件介绍").setContent(
						new Intent(this, AboutMe.class));

				tabHost.addTab(firsttab);
				tabHost.addTab(sencondtab);
				tabHost.addTab(thirdtab);
				tabHost.addTab(fourthtab);

				
				try {
					mBottomLeftStrip = tabWidget.getClass().getDeclaredField(
							"mLeftStrip");
					mBottomRightStrip = tabWidget.getClass().getDeclaredField(
							"mRightStrip");
					if (!mBottomLeftStrip.isAccessible()) {
						mBottomLeftStrip.setAccessible(true);
					}
					if (!mBottomRightStrip.isAccessible()) {
						mBottomRightStrip.setAccessible(true);
					}
					mBottomLeftStrip.set(tabWidget,
							getResources().getDrawable(R.drawable.linee));
					mBottomRightStrip.set(tabWidget, getResources()
							.getDrawable(R.drawable.linee));
				} catch (Exception e) {
					e.printStackTrace();
				}
				

				for (int i = 0; i < tabWidget.getChildCount(); i++) {

					View view = tabWidget.getChildAt(i);
					if (tabHost.getCurrentTab() == i) {
						view.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.focus));
					} else {
						view.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.unfocus));
					}
				}

				tabHost.setOnTabChangedListener(new OnTabChangeListener() {

					@Override
					public void onTabChanged(String tabId) {
						for (int i = 0; i < tabWidget.getChildCount(); i++) {
							View view = tabWidget.getChildAt(i);
							if (tabHost.getCurrentTab() == i) {
								view.setBackgroundDrawable(getResources()
										.getDrawable(R.drawable.focus));
							} else {
								view.setBackgroundDrawable(getResources()
										.getDrawable(R.drawable.unfocus));
							}
						}
					}
				});
			}
		}
	}