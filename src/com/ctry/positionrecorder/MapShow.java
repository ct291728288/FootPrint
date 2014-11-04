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
		
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
		com.baidu.mapapi.SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.map_show);
		
		//获取地图控件引用  
        mMapView = (MapView) findViewById(R.id.bmapView); 
        mBaiduMap = mMapView.getMap();
        
        //获取选定位置文件中的位置信息
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
	        
	        //解析内容
	        String[] array = content.split("]");	        
	        for(int t=0;t<array.length;t++){
	        	array[t] = array[t].substring(1);
	        }
	        //Toast.makeText(MapShow.this, "读取文件成功:"+array[0], Toast.LENGTH_LONG).show();
	        
	        //定义折线位置
	        LatLng ptmp = null;
	        String[] stmp;
	        List<LatLng> pts = new ArrayList<LatLng>();
	        for(int t=0;t<array.length;t++){
	        	stmp = array[t].split(",");
	        	ptmp = null;
	        	ptmp = new LatLng(Double.valueOf(stmp[0]), Double.valueOf(stmp[1])); 
	        	pts.add(ptmp); 	        	
	        }
	        
	        	      
        	//绘制起点坐标点
	        //构建Marker图标 ,如果只有一个点，则绘制红点标示。否则，绘制起点、终点标示 
	        BitmapDescriptor bitmap;
	        if(array.length==1){
	        	bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);  
	        }else{
	        	bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
	        }
	        //构建MarkerOption，用于在地图上添加Marker  
	        OverlayOptions option = new MarkerOptions().position(pts.get(0)).icon(bitmap);  
	        //在地图上添加Marker，并显示  
	        mBaiduMap.addOverlay(option);	
            
	        if(array.length>1){
		        //地图绘制路线图
		        OverlayOptions ooPolyline = new PolylineOptions().width(7)
						.color(0xAAFF0000).points(pts);
				mBaiduMap.addOverlay(ooPolyline);
				
				//绘制终点图标
				bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
				option = new MarkerOptions().position(pts.get(array.length-1)).icon(bitmap);
				mBaiduMap.addOverlay(option);
	        }
	    
			
			// 移动到指定索引的坐标
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(pts.get(0)));
			
			// 设置地图缩放比例：17级100米  
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
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
        }  

}
