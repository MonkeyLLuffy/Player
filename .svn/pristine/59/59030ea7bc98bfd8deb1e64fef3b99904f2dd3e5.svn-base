package com.monkeylluffy.recorder;
import java.io.File;

import com.monkeylluffy.recorder.cheapfile.CheapSoundFile;
import com.monkeylluffy.recorder.tools.CursorTool;
import com.monkeylluffy.recorder.tools.GetSystemDateTime;
import com.monkeylluffy.recorder.tools.SDcardTools;
import com.monkeylluffy.recorder.tools.StringTools;
import com.monkeylluffy.recorder.view.LrcView;
import com.monkeylluffy.recorder.view.MarkerView;
import com.monkeylluffy.recorder.view.WaveformView;
import com.monkeylluffy.recorder.view.MarkerView.MarkerListener;
import com.monkeylluffy.recorder.view.WaveformView.WaveformListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/***
 * 打开一个页面，并且oncreat之后Openfile,open 之后开启服务，把Media 传递
 * @author Administrator
 *存在的问题：
 */
public class PlayActivity extends Activity{

    private Bundle bundle;
    private Intent intent;
    private SeekBar seekBar;//音乐的进度条

    private ImageButton pause;//暂停播放音乐的按钮
       
    private TextView nowTime;//现在时间
    private TextView endTime;//现在时间
    private boolean isPlaying ;
    
    private Handler handler = new Handler();
	private int current;
	private int max;
	private int progress;

    private String fileAudioNameList;
    private String urlString ;
    private MyUpdateReceiver updateReceiver;

	private String BROAD_SERVICE_PLAY = "com.monkeyLLuffy.service.play";//控制播放
	private String BROAD_SERVICE_PAUSE = "com.monkeyLLuffy.service.pause";//控制暂停
	private String BROAD_SERVICE_SEEKTO = "com.monkeyLLuffy.activity.seekto";//跳到固定时间
	
	private String BROAD_ACTIVITY_CURRENT = "com.monkeyLLuffy.activity.current";//当前时间
	private String BROAD_ACTIVITY_DURING = "com.monkeyLLuffy.activity.during";//当前时间
	private String BROAD_ACTIVITY_ISPLAY = "com.monkeyLLuffy.activity.isplaying";//查看当前时间

	private TextView title_right;
	private TextView title_center;
	private TextView title_left;
	
	public static LrcView lrcView; // 自定义歌词视图
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	setContentView(R.layout.playsound);
	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
	Intent newIntent = getIntent();
	fileAudioNameList = newIntent.getExtras().getString("fileAudioNameList");  
    urlString = newIntent.getExtras().getString("urlString");
	Log.i("fileAudioNameList", " "+fileAudioNameList);
	
	initView();
	initUpdateReceiver();
	initEvent();	
	Intent intent = new Intent(PlayActivity.this,CountService.class);
	startService(intent);//开启一个服务
	play();	
	}
	
	public void play(){
		intent = new Intent();
		intent.setAction(BROAD_SERVICE_PLAY);
		intent.putExtra("fileName", fileAudioNameList);
		intent.putExtra("filePath", urlString);
		sendBroadcast(intent);
	}

	
	
	public void seekto(int position){
		intent = new Intent();
		intent.setAction(BROAD_SERVICE_SEEKTO);
		intent.putExtra("position", position);
		sendBroadcast(intent);
	}	
	private void initEvent() {
		// TODO Auto-generated method stub
		seekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						//更新音乐进度，更是更新
					}					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						// TODO Auto-generated method stub
						//一定要设置是来自用户，更新音乐进度
						if (fromUser){
							seekto(progress);
							
						}
					}
				}
		);
		pause.setOnClickListener(new OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			pausePlay();
    			if (isPlaying) {
    				isPlaying = false;
    				pause.setBackgroundResource(R.drawable.pause_1);
				}
    			else{
    				isPlaying = true;
    				pause.setBackgroundResource(R.drawable.play_6);
				}
    			
    		}
    	});
		
	}

	
	/**
	 * 更新playButton设置为暂停或者终止
	 * */
	private void enableDisableButtons() {
		if (isPlaying) {
			isPlaying = false;
			pause.setBackgroundResource(R.drawable.pause_1);
		}
		else{
			isPlaying = true;
			pause.setBackgroundResource(R.drawable.play_6);
		}
	}
	
	
	
	private void initView() {
		// TODO Auto-generated method stub

		title_left = (TextView) findViewById(R.id.item_left);
		title_right = (TextView) findViewById(R.id.item_right);	
		title_center = (TextView) findViewById(R.id.item_center);
		title_right.setVisibility(View.GONE);
		
		title_center.setText(fileAudioNameList);
		title_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		nowTime = (TextView) findViewById(R.id.play_nowTime);
		endTime = (TextView) findViewById(R.id.play_endTime);
		seekBar = (SeekBar) findViewById(R.id.play_playSeekbar);
		pause = (ImageButton) findViewById(R.id.play_pause);
		pause.setBackgroundResource(R.drawable.pause_1);
		
		lrcView = (LrcView) findViewById(R.id.lrcShowView);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (updateReceiver != null) {
			unregisterReceiver(updateReceiver);
		}
		super.onDestroy();
	}
	

	public void initUpdateReceiver(){
		updateReceiver  = new MyUpdateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BROAD_ACTIVITY_CURRENT);
		filter.addAction(BROAD_ACTIVITY_DURING);
		filter.addAction(BROAD_ACTIVITY_ISPLAY);
		registerReceiver(updateReceiver, filter);
	}

	public void pausePlay() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setAction(BROAD_SERVICE_PAUSE);
		sendBroadcast(intent);
	}
	

	//通过两边都注册广播的形式互相通信
	
	public class MyUpdateReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(BROAD_ACTIVITY_CURRENT)) {				
				current = intent.getIntExtra("current", 0);
				seekBar.setProgress(current);				
				Log.i("MyUpdateReceiver  current", current+"");
				Log.i("MyUpdateReceiver  max", max+"");
				
				if ((current/1000) >= (max/1000)) {
					//不知道为啥，声音完了也达不到那个Max的时间
					progress = 100;
					seekBar.setProgress(max);
					pause.setBackgroundResource(R.drawable.stop);
					pause.setEnabled(false);
					nowTime.setText(GetSystemDateTime.timeString(max));
			        
				}else {
					progress = (int)((((float)current)/max)*100);			
					nowTime.setText(GetSystemDateTime.timeString(current));
				}
			}
			if (intent.getAction().equals(BROAD_ACTIVITY_DURING)) {
				max = intent.getIntExtra("during", 0);
				seekBar.setMax(max);
				endTime.setText(GetSystemDateTime.timeString(max));
			}
			
			if (intent.getAction().equals(BROAD_ACTIVITY_ISPLAY)) {
				isPlaying = intent.getBooleanExtra("isPlaying", false);
				enableDisableButtons();
			}
			
			
			
		}
		
		
		
	}

	
}
