package com.monkeylluffy.recorder.adpter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Text;

import com.monkeylluffy.recorder.R;
import com.monkeylluffy.recorder.R.id;
import com.monkeylluffy.recorder.R.layout;
import com.monkeylluffy.recorder.tools.FileBean;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class AddFileAdapter extends BaseAdapter{

	private List<FileBean> list;
	protected LayoutInflater mInflater;
	private TextView textView;
	
	public AddFileAdapter(Context context,List<FileBean> list,TextView textView){
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
		this.textView = textView;
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		FileBean mFileBean = list.get(position);
		String path = mFileBean.getFilePath();
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.additem, null);
			viewHolder.mTextViewPath = (TextView) convertView.findViewById(R.id.filePath);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.fileTitle);
			viewHolder.mChoose = (CheckBox) convertView.findViewById(R.id.filechoose);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.mTextViewTitle.setText(mFileBean.getFileTitle());
		viewHolder.mTextViewPath.setText(mFileBean.getFilePath());
		viewHolder.mChoose.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
					addAnimation(viewHolder.mChoose);
				}
				mSelectMap.put(position, isChecked);
				textView.setText("确定("+getSelectItems().size()+")");
				
			}
		});	
		viewHolder.mChoose.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);
		
				
		return convertView;
		
	}
	
	/**
	 * 获取选中的Item的position
	 * @return
	 */
	public List<Integer> getSelectItems(){
		List<Integer> list = new ArrayList<Integer>();
		for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
			Map.Entry<Integer, Boolean> entry = it.next();
			if(entry.getValue()){
				list.add(entry.getKey());
			}
		}
		
		return list;
	}
	
	/**
	 * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画 
	 * @param view
	 */
	private void addAnimation(View view){
		float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules), 
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
				set.setDuration(150);
		set.start();
	}
	
	
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	
	public static class ViewHolder{
		public TextView mTextViewTitle;
		public TextView mTextViewPath;
		public CheckBox mChoose;
	}
}
