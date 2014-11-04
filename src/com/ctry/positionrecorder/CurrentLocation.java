package com.ctry.positionrecorder;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

public class CurrentLocation extends Activity {
	
	private LocationClient mLocationClient;
	//设置定位模式
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	
	MapView mMapView = null; 
	BaiduMap mBaiduMap = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext()); 
		setContentView(R.layout.current_location);

		mLocationClient = new LocationClient(this.getApplicationContext());
        mLocationClient.registerLocationListener(new CurrentLocationListener());
        
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(3000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);	
		
		//获取地图控件引用  
        mMapView = (MapView) findViewById(R.id.bmapView); 
        mBaiduMap = mMapView.getMap();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Toast.makeText(CurrentLocation.this, "实时定位开启", Toast.LENGTH_SHORT).show();
		mLocationClient.start();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Toast.makeText(CurrentLocation.this, "实时定位结束", Toast.LENGTH_SHORT).show();
		mLocationClient.stop();
	}
	
	
	/**
	 * 实现实位回调监听
	 */
	public class CurrentLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			
			//记录位置信息
			LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
					
			//Receive Location 
			StringBuffer sb = new StringBuffer(256);
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("当前地址 : ");
				if(location.getAddrStr()==null){
					sb.append("不详");
				}else{
					sb.append(location.getAddrStr());
				}
				
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("当前地址 : ");
				if(location.getAddrStr()==null){
					sb.append("不详");
				}else{
					sb.append(location.getAddrStr());
				}
			}
			//Toast.makeText(CurrentLocation.this, sb.toString(), Toast.LENGTH_SHORT).show();
			
			//删除之前的覆盖物层
			mBaiduMap.clear();
			
			// 移动到指定索引的坐标
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(myLocation));
			
			// 设置地图缩放比例：17级100米  
            MapStatusUpdate ms = MapStatusUpdateFactory.zoomTo(18);   
            mBaiduMap.setMapStatus(ms); 
			
			//构建当前位置  
	        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.mehere);  
	        //构建MarkerOption，用于在地图上添加Marker  
	        OverlayOptions option = new MarkerOptions().position(myLocation).icon(bitmap);  
	        //在地图上添加Marker，并显示  
	        mBaiduMap.addOverlay(option);
	        
	        // 添加文字
			OverlayOptions ooText = new TextOptions().bgColor(0xAAF4F4F4)
					.fontSize(24).fontColor(0xFF5474B5).text(sb.toString())
					.position(myLocation);
			mBaiduMap.addOverlay(ooText);
			
			 
		}


	}
}
