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
	//���ö�λģʽ
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	
	private Location lastLocation = null;
	
	RadioGroup group;
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Toast.makeText(getApplicationContext(), "�����˳���...", Toast.LENGTH_SHORT).show();
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
                        //��ȡ������ѡ�����ID
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
        
        //״̬��ȡ��ǰ��ͳһΪfalse�����ڸ��ߺ�̨service״̬��ʼ��
        //flag = isServiceRunning(Service_TAG);
        
        //���ݳ�ʼ״̬���¿���ͼƬ        
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
                
        //���SD���Ƿ����
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            //Toast.makeText(getApplicationContext(), "��SD��", Toast.LENGTH_SHORT).show(); 
        }else{
        	Toast.makeText(getApplicationContext(), "δ��⵽SD����APP�Զ��˳�������", Toast.LENGTH_SHORT).show();
        	finish();
        }
        
    }
    
    //���ݺ�̨�߳�״̬����ǰ̨��ʾ
    private void setImageViewByFlag(boolean flag)
    {
    	//��ʼ���ٶȶ�λ����
    	InitLocation();
    	
    	if(flag){
        	iv1.setImageResource(R.drawable.start);
        	iv2.setImageResource(R.drawable.on);
        	
        	String modeString = null;
        	if(tempMode == LocationMode.Hight_Accuracy){
        		modeString="�߾���ģʽ";
            }else if(tempMode == LocationMode.Battery_Saving){
            	modeString="ʡ��ģʽ";
            }else if(tempMode == LocationMode.Device_Sensors){
            	modeString="���豸";
            }
        		
        	
        	Toast.makeText(MainActivity.this, modeString+"�㼣��¼��ʼ...", Toast.LENGTH_SHORT).show();
        	mLocationClient.start();
        }else{
        	iv1.setImageResource(R.drawable.stop);
        	iv2.setImageResource(R.drawable.off);
        	
        	//���㱾�μ�¼��������
        	long i = (SystemClock.elapsedRealtime() - cm.getBase())/1000;
        	if(lastLocation!=null){
        		Toast.makeText(MainActivity.this, "�㼣��¼����,���μ�¼����"+i+"��", Toast.LENGTH_SHORT).show();
        	}
        	mLocationClient.stop();
        }
    }
    
       
    /**
	 * ʵ��ʵλ�ص�����
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			
			//��¼λ����Ϣ
			Location myLocation = new Location("");
			myLocation.setLatitude(location.getLatitude());
			myLocation.setLongitude(location.getLongitude());
			recordLocationInfo(myLocation);
			
			//Receive Location 
			StringBuffer sb = new StringBuffer(256);
			sb.append("��λʱ�� : ");
			sb.append(location.getTime());
			/*sb.append("\nerror code : ");
			sb.append(location.getLocType());*/
			sb.append("\nγ�� : ");
			sb.append(location.getLatitude());
			sb.append("\n���� : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nGPS�ٶ� : ");
				sb.append(location.getSpeed());
				sb.append("\nGPS������ : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nGPS��λ : ");
				sb.append(location.getDirection());
				sb.append("\n��ַ : ");
				sb.append(location.getAddrStr());
				
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\n��ַ : ");
				sb.append(location.getAddrStr());
				//��Ӫ����Ϣ
				sb.append("\n��Ӫ����Ϣ : ");
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
		
		//�ж�GPS�Ƿ���������
		lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);        
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS����δ����,���鿪����", Toast.LENGTH_SHORT).show();
            //tempMode = LocationMode.Battery_Saving;
            //���ؿ���GPS�������ý���
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
            startActivityForResult(intent,0); 
            return;
        }else{
        	//Toast.makeText(this, "GPS�����ѿ�������������ʹ��", Toast.LENGTH_SHORT).show();
        	//tempMode = LocationMode.Hight_Accuracy;
        }
		option.setLocationMode(tempMode);//���ö�λģʽ
		option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		option.setScanSpan(60000);//���÷���λ����ļ��ʱ��Ϊ60s
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	
	//��λ������Ϣд��
	private void recordLocationInfo(Location location) {
		// TODO Auto-generated method stub

		String SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;
		// ���SD���Ƿ����CTLocationInfo�ļ��ۣ�û���򴴽�һ��
		File fileDir = new File(SDCardRoot + "CTLocationInfo" + File.separator);
		if (!fileDir.exists())
			fileDir.mkdir(); // ����������򴴽�
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");
		String fileName = sDateFormat.format(new java.util.Date())+".txt";
		// ����Ƿ����/CTLocationInfo/���������������ļ������û�У��򴴽�һ��
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
			
			// ���ռ�¼�ļ�д��λ����Ϣ
			String writeContent = "[" + location.getLatitude() + ","
					+ location.getLongitude() + "]";
			
			// ��ָ���ļ����� RandomAccessFile����
			//ע������Ӧ����RandomAccessFile�������FileOutputStream��ÿ��д�ļ�����ȫ�������д��ȥ
			RandomAccessFile raf;
			try {
				raf = new RandomAccessFile(LocationInfoFile, "rw");
				// ���ļ���¼ָ���ƶ������
				raf.seek(LocationInfoFile.length());
				// ����ļ�����
				raf.write(writeContent.getBytes());
				raf.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	//��λ��ϸ��Ϣд��
	private void recordDetailInfo(String info) {
		// TODO Auto-generated method stub

		String SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;
		// ���SD���Ƿ����CTLocationInfo�ļ��ۣ�û���򴴽�һ��
		File fileDir = new File(SDCardRoot + "CTDetailInfo" + File.separator);
		if (!fileDir.exists())
			fileDir.mkdir(); // ����������򴴽�
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");
		String fileName = sDateFormat.format(new java.util.Date())+"��ϸ��Ϣ.txt";
		// ����Ƿ����/CTLocationInfo/���������������ļ������û�У��򴴽�һ��
		File LocationInfoFile = new File(SDCardRoot + "CTDetailInfo"
				+ File.separator + fileName);
		if (!LocationInfoFile.exists())
			try {
				LocationInfoFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			// ��ָ���ļ����� RandomAccessFile����
			//ע������Ӧ����RandomAccessFile�������FileOutputStream��ÿ��д�ļ�����ȫ�������д��ȥ
			RandomAccessFile raf;
			try {
				raf = new RandomAccessFile(LocationInfoFile, "rw");
				// ���ļ���¼ָ���ƶ������
				raf.seek(LocationInfoFile.length());
				// ����ļ�����
				raf.write(info.getBytes());
				raf.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
				
	}

	
    
    
}
