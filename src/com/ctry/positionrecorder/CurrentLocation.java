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
	//���ö�λģʽ
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
        option.setLocationMode(tempMode);//���ö�λģʽ
		option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		option.setScanSpan(3000);//���÷���λ����ļ��ʱ��Ϊ5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);	
		
		//��ȡ��ͼ�ؼ�����  
        mMapView = (MapView) findViewById(R.id.bmapView); 
        mBaiduMap = mMapView.getMap();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Toast.makeText(CurrentLocation.this, "ʵʱ��λ����", Toast.LENGTH_SHORT).show();
		mLocationClient.start();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Toast.makeText(CurrentLocation.this, "ʵʱ��λ����", Toast.LENGTH_SHORT).show();
		mLocationClient.stop();
	}
	
	
	/**
	 * ʵ��ʵλ�ص�����
	 */
	public class CurrentLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			
			//��¼λ����Ϣ
			LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
					
			//Receive Location 
			StringBuffer sb = new StringBuffer(256);
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("��ǰ��ַ : ");
				if(location.getAddrStr()==null){
					sb.append("����");
				}else{
					sb.append(location.getAddrStr());
				}
				
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("��ǰ��ַ : ");
				if(location.getAddrStr()==null){
					sb.append("����");
				}else{
					sb.append(location.getAddrStr());
				}
			}
			//Toast.makeText(CurrentLocation.this, sb.toString(), Toast.LENGTH_SHORT).show();
			
			//ɾ��֮ǰ�ĸ������
			mBaiduMap.clear();
			
			// �ƶ���ָ������������
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(myLocation));
			
			// ���õ�ͼ���ű�����17��100��  
            MapStatusUpdate ms = MapStatusUpdateFactory.zoomTo(18);   
            mBaiduMap.setMapStatus(ms); 
			
			//������ǰλ��  
	        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.mehere);  
	        //����MarkerOption�������ڵ�ͼ�����Marker  
	        OverlayOptions option = new MarkerOptions().position(myLocation).icon(bitmap);  
	        //�ڵ�ͼ�����Marker������ʾ  
	        mBaiduMap.addOverlay(option);
	        
	        // �������
			OverlayOptions ooText = new TextOptions().bgColor(0xAAF4F4F4)
					.fontSize(24).fontColor(0xFF5474B5).text(sb.toString())
					.position(myLocation);
			mBaiduMap.addOverlay(ooText);
			
			 
		}


	}
}
