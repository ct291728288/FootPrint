package com.ctry.positionrecorder;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FileList extends Activity {

	ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dir_list_show);

		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		lv = (ListView) findViewById(R.id.List_View);
		lv.setAdapter(new File_Adter(this));
		lv.setOnItemClickListener( new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int positiong, long id) { 
				Intent tt = new Intent(FileList.this,MapShow.class);
				//获得选中项的HashMap对象   
				String Txt_Name=(String)lv.getItemAtPosition(positiong);    
				tt.putExtra("fileName", Txt_Name);
				//Toast.makeText(FileList.this, Txt_Name, 1).show();
				startActivity(tt);
				
				} 
		}); 
	}
	
	
	class File_Adter extends BaseAdapter { 
		public Activity activity; //创建View时必须要提供Context 
		public List<File> list=new LinkedList<File>(); //数据源（文件列表）  
		
		public int getCount() { 
			// TODO Auto-generated method stub 
			return list.size(); 
		} 
		public Object getItem(int arg0) { 
			
			File f=list.get(arg0); 
			return f.getName(); 
		} 
		public long getItemId(int position) { 
			// TODO Auto-generated method stub 
			return position; 
		} 
		public View getView(int position, View arg1, ViewGroup arg2) { 
			// TODO Auto-generated method stub 
			View v=View.inflate(activity,R.layout.item_show,null); 
			TextView Txt_Size=(TextView) v.findViewById(R.id.textView1); 
			File f=list.get(position); 
			Txt_Size.setText(f.getName()+"("+getFilesSize(f)+")"); 
			return v; 
		} 
		
		public File_Adter(Activity activity) 
		{ 
			this.activity=activity; 
			// 获取指定路径下的文件列表
			String SDCardRoot = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator;
			File fileDir = new File(SDCardRoot + "CTLocationInfo" + File.separator);
			File[] files = fileDir.listFiles();
			list.clear();
			if(files!=null) 
				for(File f:files) 
						list.add(f); 
			

		} 
		
		public  String getFilesSize(File f) 
		{ 
			int sub_index=0; 
			String show=""; 
			if(f.isFile()) 
			{ 
				long length=f.length(); 
				if(length>=1073741824) 
				{ 
					sub_index=String.valueOf((float)length/1073741824).indexOf("."); 
					show=((float)length/1073741824+"000").substring(0,sub_index+3)+"GB"; 
				} else if(length>=1048576) 
				{ 
					sub_index=(String.valueOf((float)length/1048576)).indexOf("."); 
					show=((float)length/1048576+"000").substring(0,sub_index+3)+"MB"; 
				}else if(length>=1024) 
				{ 
					sub_index=(String.valueOf((float)length/1024)).indexOf("."); 
					show=((float)length/1024+"000").substring(0,sub_index+3)+"KB"; 
				}else if(length<1024) 
				show=String.valueOf(length)+"B"; 
			} 
			return show; 
			} 
		} 

}
