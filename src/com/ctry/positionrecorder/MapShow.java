package com.ctry.positionrecorder;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

public class MapShow extends Activity {
	
	MapView mMapView = null; 
	BaiduMap mBaiduMap = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
		com.baidu.mapapi.SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.map_show);
		
		//��ȡ��ͼ�ؼ�����  
        mMapView = (MapView) findViewById(R.id.bmapView); 
        mBaiduMap = mMapView.getMap();
        
        //��ȡѡ��λ���ļ��е�λ����Ϣ
        Intent itt = getIntent();
        String Txt_Name = itt.getStringExtra("fileName");
        //Toast.makeText(MapShow.this, Txt_Name, 1).show();
        String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		File file = new File(SDCardRoot + "CTLocationInfo" + File.separator+Txt_Name);
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			byte[] b = new byte[inputStream.available()];
	        inputStream.read(b);
	        String content = new String(b);
	        content = content.trim();
	        
	        //��������
	        String[] array = content.split("]");	        
	        for(int t=0;t<array.length;t++){
	        	array[t] = array[t].substring(1);
	        }
	        //Toast.makeText(MapShow.this, "��ȡ�ļ��ɹ�:"+array[0], Toast.LENGTH_LONG).show();
	        
	        //��������λ��
	        LatLng ptmp = null;
	        String[] stmp;
	        List<LatLng> pts = new ArrayList<LatLng>();
	        for(int t=0;t<array.length;t++){
	        	stmp = array[t].split(",");
	        	ptmp = null;
	        	ptmp = new LatLng(Double.valueOf(stmp[0]), Double.valueOf(stmp[1])); 
	        	pts.add(ptmp); 	        	
	        }
	        
	        	      
        	//������������
	        //����Markerͼ�� ,���ֻ��һ���㣬����ƺ���ʾ�����򣬻�����㡢�յ��ʾ 
	        BitmapDescriptor bitmap;
	        if(array.length==1){
	        	bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);  
	        }else{
	        	bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
	        }
	        //����MarkerOption�������ڵ�ͼ�����Marker  
	        OverlayOptions option = new MarkerOptions().position(pts.get(0)).icon(bitmap);  
	        //�ڵ�ͼ�����Marker������ʾ  
	        mBaiduMap.addOverlay(option);	
            
	        if(array.length>1){
		        //��ͼ����·��ͼ
		        OverlayOptions ooPolyline = new PolylineOptions().width(7)
						.color(0xAAFF0000).points(pts);
				mBaiduMap.addOverlay(ooPolyline);
				
				//�����յ�ͼ��
				bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
				option = new MarkerOptions().position(pts.get(array.length-1)).icon(bitmap);
				mBaiduMap.addOverlay(option);
	        }
	    
			
			// �ƶ���ָ������������
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(pts.get(0)));
			
			// ���õ�ͼ���ű�����17��100��  
            MapStatusUpdate ms = MapStatusUpdateFactory.zoomTo(18);   
            mBaiduMap.setMapStatus(ms);  
			
	       
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(MapShow.this, "Exception:"+e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}      
        
	}
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
        }  

}
