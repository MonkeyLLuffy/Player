package com.monkeylluffy.recorder.adpter;

import java.util.List;

import com.monkeylluffy.recorder.R;
import com.monkeylluffy.recorder.R.id;
import com.monkeylluffy.recorder.R.layout;
import com.monkeylluffy.recorder.tools.FileBean;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter{

	private List<FileBean> list;
	protected LayoutInflater mInflater;
	
	public ListAdapter(Context context,List<FileBean> list){
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		FileBean mFileBean = list.get(position);
		String path = mFileBean.getFilePath();
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item, null);
			viewHolder.mTextViewPath = (TextView) convertView.findViewById(R.id.filePath);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.fileTitle);		
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.mTextViewTitle.setText(mFileBean.getFileTitle());
		viewHolder.mTextViewPath.setText(mFileBean.getFilePath());
		//给ImageView设置路径Tag,这是异步加载图片的小技巧		
				
		return convertView;
		
	}
	//添加添加数据库
		public boolean add(String fileName,String filePath){
			
			
			return true;
		}
	
		//删除添加数据库
				public boolean remove(String path){
					
					
					return true;
				}
			
	
	
	
	
	
	public static class ViewHolder{
		public TextView mTextViewTitle;
		public TextView mTextViewPath;
	}
}
