package com.ctry.positionrecorder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class MainActivity extends Activity {
	
	ImageView iv1;
	ImageView iv2;
	Chronometer cm;
	
	boolean flag = false;
	
	private LocationManager lm;
	
	private LocationClient mLocationClient;
	//设置定位模式
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	
	private Location lastLocation = null;
	
	RadioGroup group;
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Toast.makeText(getApplicationContext(), "程序退出中...", Toast.LENGTH_SHORT).show();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        iv2 = (ImageView) findViewById(R.id.imageView2);
        iv1 = (ImageView) findViewById(R.id.imageView1);
        cm  = (Chronometer)findViewById(R.id.chronometer1);
        
        mLocationClient = new LocationClient(this.getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        
        group = (RadioGroup)this.findViewById(R.id.radioGroup1);
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                  
                    @Override
                   public void onCheckedChanged(RadioGroup arg0, int arg1) {
                        // TODO Auto-generated method stub
                        //获取变更后的选中项的ID
                       int radioButtonId = arg0.getCheckedRadioButtonId();
                       if(radioButtonId == R.id.radio0){
                    	   tempMode = LocationMode.Hight_Accuracy;
                       }else if(radioButtonId == R.id.radio1){
                    	   tempMode = LocationMode.Battery_Saving;
                       }else if(radioButtonId == R.id.radio2){
                    	   tempMode = LocationMode.Device_Sensors;
                       }
                    }
               });
        
        //状态获取，前期统一为false，后期更具后台service状态初始化
        //flag = isServiceRunning(Service_TAG);
        
        //根据初始状态更新开关图片        
        setImageViewByFlag(flag);
        
        iv2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				flag = !flag;
				setImageViewByFlag(flag);
				if(flag){
					cm.setBase(SystemClock.elapsedRealtime());
					cm.start();
				}else{					
					cm.stop();
				}
				
			}
		});
                
        //检测SD卡是否存在
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            //Toast.makeText(getApplicationContext(), "有SD卡", Toast.LENGTH_SHORT).show(); 
        }else{
        	Toast.makeText(getApplicationContext(), "未检测到SD卡，APP自动退出！！！", Toast.LENGTH_SHORT).show();
        	finish();
        }
        
    }
    
    //根据后台线程状态设置前台显示
    private void setImageViewByFlag(boolean flag)
    {
    	//初始化百度定位参数
    	InitLocation();
    	
    	if(flag){
        	iv1.setImageResource(R.drawable.start);
        	iv2.setImageResource(R.drawable.on);
        	
        	String modeString = null;
        	if(tempMode == LocationMode.Hight_Accuracy){
        		modeString="高精度模式";
            }else if(tempMode == LocationMode.Battery_Saving){
            	modeString="省电模式";
            }else if(tempMode == LocationMode.Device_Sensors){
            	modeString="仅设备";
            }
        		
        	
        	Toast.makeText(MainActivity.this, modeString+"足迹记录开始...", Toast.LENGTH_SHORT).show();
        	mLocationClient.start();
        }else{
        	iv1.setImageResource(R.drawable.stop);
        	iv2.setImageResource(R.drawable.off);
        	
        	//计算本次记录共多少秒
        	long i = (SystemClock.elapsedRealtime() - cm.getBase())/1000;
        	if(lastLocation!=null){
        		Toast.makeText(MainActivity.this, "足迹记录结束,本次记录共计"+i+"秒", Toast.LENGTH_SHORT).show();
        	}
        	mLocationClient.stop();
        }
    }
    
       
    /**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			
			//记录位置信息
			Location myLocation = new Location("");
			myLocation.setLatitude(location.getLatitude());
			myLocation.setLongitude(location.getLongitude());
			recordLocationInfo(myLocation);
			
			//Receive Location 
			StringBuffer sb = new StringBuffer(256);
			sb.append("定位时间 : ");
			sb.append(location.getTime());
			/*sb.append("\nerror code : ");
			sb.append(location.getLocType());*/
			sb.append("\n纬度 : ");
			sb.append(location.getLatitude());
			sb.append("\n经度 : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nGPS速度 : ");
				sb.append(location.getSpeed());
				sb.append("\nGPS卫星数 : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nGPS方位 : ");
				sb.append(location.getDirection());
				sb.append("\n地址 : ");
				sb.append(location.getAddrStr());
				
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\n地址 : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\n运营商信息 : ");
				sb.append(location.getOperators()+"\n");
			}
			
			sb.append("======================\n");
			//Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
			//Log.v("----CTInfo", sb.toString());
			recordDetailInfo(sb.toString());
		}


	}
	
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		
		//判断GPS是否正常启动
		lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);        
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS导航未开启,建议开启！", Toast.LENGTH_SHORT).show();
            //tempMode = LocationMode.Battery_Saving;
            //返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
            startActivityForResult(intent,0); 
            return;
        }else{
        	//Toast.makeText(this, "GPS导航已开启，可以正常使用", Toast.LENGTH_SHORT).show();
        	//tempMode = LocationMode.Hight_Accuracy;
        }
		option.setLocationMode(tempMode);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(60000);//设置发起定位请求的间隔时间为60s
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	
	//定位粗略信息写入
	private void recordLocationInfo(Location location) {
		// TODO Auto-generated method stub

		String SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;
		// 检查SD卡是否存在CTLocationInfo文件价，没有则创建一个
		File fileDir = new File(SDCardRoot + "CTLocationInfo" + File.separator);
		if (!fileDir.exists())
			fileDir.mkdir(); // 如果不存在则创建
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");
		String fileName = sDateFormat.format(new java.util.Date())+".txt";
		// 检查是否存在/CTLocationInfo/当天日期命名的文件，如果没有，则创建一个
		File LocationInfoFile = new File(SDCardRoot + "CTLocationInfo"
				+ File.separator + fileName);
		if (!LocationInfoFile.exists())
			try {
				LocationInfoFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		
		if(lastLocation==null || (location != null && (lastLocation.getLatitude() != location.getLatitude() 
				                 || lastLocation.getLongitude()!=location.getLongitude())
	       ) ){
			
			lastLocation = location;
			
			// 向当日记录文件写入位置信息
			String writeContent = "[" + location.getLatitude() + ","
					+ location.getLongitude() + "]";
			
			// 以指定文件创建 RandomAccessFile对象
			//注意这里应该有RandomAccessFile，如果用FileOutputStream，每次写文件都会全部清空再写进去
			RandomAccessFile raf;
			try {
				raf = new RandomAccessFile(LocationInfoFile, "rw");
				// 将文件记录指针移动到最后
				raf.seek(LocationInfoFile.length());
				// 输出文件内容
				raf.write(writeContent.getBytes());
				raf.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	//定位详细信息写入
	private void recordDetailInfo(String info) {
		// TODO Auto-generated method stub

		String SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;
		// 检查SD卡是否存在CTLocationInfo文件价，没有则创建一个
		File fileDir = new File(SDCardRoot + "CTDetailInfo" + File.separator);
		if (!fileDir.exists())
			fileDir.mkdir(); // 如果不存在则创建
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");
		String fileName = sDateFormat.format(new java.util.Date())+"详细信息.txt";
		// 检查是否存在/CTLocationInfo/当天日期命名的文件，如果没有，则创建一个
		File LocationInfoFile = new File(SDCardRoot + "CTDetailInfo"
				+ File.separator + fileName);
		if (!LocationInfoFile.exists())
			try {
				LocationInfoFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			// 以指定文件创建 RandomAccessFile对象
			//注意这里应该有RandomAccessFile，如果用FileOutputStream，每次写文件都会全部清空再写进去
			RandomAccessFile raf;
			try {
				raf = new RandomAccessFile(LocationInfoFile, "rw");
				// 将文件记录指针移动到最后
				raf.seek(LocationInfoFile.length());
				// 输出文件内容
				raf.write(info.getBytes());
				raf.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
				
	}

	
    
    
}
