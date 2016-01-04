package com.monkeylluffy.recorder;

import java.util.ArrayList;
import java.util.List;

import com.monkeylluffy.recorder.tools.GetSystemDateTime;
import com.monkeylluffy.recorder.tools.LrcContent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;// 服务的类
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

/** 计数的服务 */
public class CountService extends Service {

	/** 创建参数 */
	private MediaPlayer player;// 用于播放音乐

	private int currentTime; // 当前播放进度
	private int duration; // 播放长度
	
	private Notification notification;
	private NotificationManager notificationManager ;  
	public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";//按钮中传递    
	public final static String INTENT_BUTTONID_TAG = "ButtonId";
	/** 播放/暂停 按钮点击 ID */
	public final static int BUTTON_PALY_ID = 1;

	private String BROAD_SERVICE_PLAY = "com.monkeyLLuffy.service.play";// 控制播放
	private String BROAD_SERVICE_PAUSE = "com.monkeyLLuffy.service.pause";// 控制暂停
	private String BROAD_SERVICE_SEEKTO = "com.monkeyLLuffy.activity.seekto";//跳到固定时间

	private String BROAD_ACTIVITY_CURRENT = "com.monkeyLLuffy.activity.current";// 当前时间
	private String BROAD_ACTIVITY_DURING = "com.monkeyLLuffy.activity.during";// 当前时间
	private String BROAD_ACTIVITY_ISPLAY = "com.monkeyLLuffy.activity.isplaying";//查看当前时间

	AudioManager   am;
	private String filePath;
	private String fileName;
	
	private LrcProcess mLrcProcess;	//歌词处理
	private List<LrcContent> lrcList = new ArrayList<LrcContent>(); //存放歌词列表对象
	private int index = 0;			//歌词检索值

	
	Handler handler = new Handler();
	private boolean isPlaying;
    private BroadcastReceiver bReceiver;
	
	/**
	 * handler用来接收消息，来发送广播更新播放时间
	 */
	private Handler handler_update_play = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (player != null) {
					if (player.isPlaying()) {
						currentTime = player.getCurrentPosition(); // 获取当前音乐播放的位置,发送广播，并且更新Notifycation
						Intent intent = new Intent();
						intent.setAction(BROAD_ACTIVITY_CURRENT);
						intent.putExtra("current", player.getCurrentPosition());
						sendBroadcast(intent); // 给PlayerActivity发送广播
						
						Log.i("handleMessage", player.getCurrentPosition() + "");
						handler_update_play.sendEmptyMessageDelayed(1, 1000);
					}
					duration = player.getDuration();
					if ((currentTime/1000) >= (duration/1000)) {
						notification.contentView.setProgressBar(R.id.noti_pd, 1,
		                    duration, false);
						notification.contentView.setTextViewText(R.id.noti_tv, 100 + "%");
						notificationManager.notify(0, notification);
						notification.contentView.setImageViewResource(R.id.noti_pause, R.drawable.stop);
					}else {
						int progress = (int)((((float)currentTime)/duration)*100);
						if(player.isPlaying()){
							 notification.contentView.setImageViewResource(R.id.noti_pause, R.drawable.pause_1);
						}else{
							//换图片
							notification.contentView.setImageViewResource(R.id.noti_pause, R.drawable.play_6);
						}
						notification.contentView.setProgressBar(R.id.noti_pd, duration,
			                    currentTime, false);
						notification.contentView.setTextViewText(R.id.noti_tv, progress + "%");
				        notificationManager.notify(0, notification);
					}

					
					Intent intent = new Intent(BROAD_ACTIVITY_ISPLAY);
					intent.putExtra("isPlaying", player.isPlaying());
					sendBroadcast(intent);
					
				}

			}
		};
	};
	

	 OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
		  public void onAudioFocusChange(int focusChange) {
		   if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {// 暂时获取焦点 
		    if(player.isPlaying()){
		     player.pause();
		    }
		    
		   } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
		    if(player==null){
		     openFile(filePath, fileName);
		    }else if(!player.isPlaying()){		     
		     player.start();
		     
		    }
		    // Resume playback
		   } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
		    if(player.isPlaying()){
		     
		     player.stop();
		    }
		    am.abandonAudioFocus(afChangeListener);
		    // Stop playback
		   }else if(focusChange==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){//永久失去焦点
		    if (player.isPlaying()) {
		     player.stop();
		    }
		    
		   }else if(focusChange==AudioManager.AUDIOFOCUS_REQUEST_FAILED){
		    if(player.isPlaying()){
		     player.stop();
		    }
		    
		   }
		  }
		 };
		 
	public void onCreate() {
		super.onCreate();
		Log.d("service", "service created");
		player = new MediaPlayer();
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		initNotification();
		initButtonReceiver();
		MyReceiver myReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BROAD_SERVICE_PLAY);
		filter.addAction(BROAD_SERVICE_SEEKTO);
		filter.addAction(BROAD_SERVICE_PAUSE);
		registerReceiver(myReceiver, filter);
		
		
		

	}
	/** 带按钮的通知栏点击广播接收 */
	public void initButtonReceiver(){
		bReceiver = new ButtonBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		registerReceiver(bReceiver, intentFilter);
	}
	
	
	public void initNotification(){
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification(R.drawable.ico, "下载", System.currentTimeMillis());

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.notification);
        notification.contentView = view;

        PendingIntent contentIntent = PendingIntent.getActivity(this,
                R.string.app_name, new Intent(CountService.this,LuYinActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        notification.contentIntent = contentIntent;
        Intent buttonIntent = new Intent(ACTION_BUTTON); 
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);  
        notification.contentView.setOnClickPendingIntent(R.id.noti_pause, intent_prev);
        notification.contentView.setImageViewResource(R.id.noti_pause, R.drawable.pause_1);
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		handler_update_play.sendEmptyMessage(1);// 更新进度的
	}

	/**
	 * 停止音乐
	 */
	private void stop() {
		if (player != null) {
			player.stop();
			try {
				player.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 打开播放录音文件的程序
	 * 
	 * @param f
	 */
	private void openFile(String filePath, String fileName) {
		if (player == null) {
			player = new MediaPlayer();
		} else {
			player.stop();
			player = new MediaPlayer();
		}
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			initLrc();
			player.setDataSource(filePath);
			player.prepare();
			//player.start();
			// Request audio focus for playback
			int result = am.requestAudioFocus(afChangeListener,
			// Use the music stream.
					AudioManager.STREAM_MUSIC, // Request permanent focus.
					AudioManager.AUDIOFOCUS_GAIN);
			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				player.start();
				// Start playback. // 开始播放音乐文件
			}
			Intent intent = new Intent(BROAD_ACTIVITY_DURING);
			intent.putExtra("during", player.getDuration());
			sendBroadcast(intent);
			Log.i("开始了", "------------");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void pausePlay() { // 关于音乐暂停
		if (!player.isPlaying()) { // 如果没有播放音乐（要在 playe！=null的情况下）
			player.start(); // 播放音乐
			handler_update_play.sendEmptyMessage(1);
		} else { // 正在播放
			player.pause(); // 音乐暂停
		}
	}

	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(bReceiver != null){
			unregisterReceiver(bReceiver);
		}
		am.abandonAudioFocus(afChangeListener);
	}
	
	/**
	 * 初始化歌词配置
	 */
	public void initLrc(){
		mLrcProcess = new LrcProcess();
		//读取歌词文件
		mLrcProcess.readLRC(filePath);
		//传回处理后的歌词文件
		lrcList = mLrcProcess.getLrcList();
		PlayActivity.lrcView.setmLrcList(lrcList);
		//切换带动画显示歌词
		PlayActivity.lrcView.setAnimation(AnimationUtils.loadAnimation(CountService.this,R.anim.alpha_z));
		handler.post(mRunnable);
	}
	Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			PlayActivity.lrcView.setIndex(lrcIndex());
			PlayActivity.lrcView.invalidate();
			handler.postDelayed(mRunnable, 100);
		}
	};
	/**
	 * 根据时间获取歌词显示的索引值
	 * @return
	 */
	public int lrcIndex() {
		if(player.isPlaying()) {
			currentTime = player.getCurrentPosition();
			duration = player.getDuration();
		}
		if(currentTime < duration) {
			for (int i = 0; i < lrcList.size(); i++) {
				if (i < lrcList.size() - 1) {
					if (currentTime < lrcList.get(i).getLrcTime() && i == 0) {
						index = i;
					}
					if (currentTime > lrcList.get(i).getLrcTime()
							&& currentTime < lrcList.get(i + 1).getLrcTime()) {
						index = i;
					}
				}
				if (i == lrcList.size() - 1
						&& currentTime > lrcList.get(i).getLrcTime()) {
					index = i;
				}
			}
		}
		return index;
	}
	
	
	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().endsWith(BROAD_SERVICE_PLAY)) {// 进行播放
				Log.i("MyReceiver", "进行播放");
				filePath = intent.getStringExtra("filePath");
				fileName = intent.getStringExtra("fileName");
				openFile(filePath, fileName);
			} else if (intent.getAction().equals(BROAD_SERVICE_PAUSE)) {// 进行暂停
				pausePlay();
			}else if (intent.getAction().equals(BROAD_SERVICE_SEEKTO)){
				int position = intent.getIntExtra("position", 0);
				player.seekTo(position);
			}

		}

	}
	public class ButtonBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(ACTION_BUTTON)){
				//通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
				int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
				switch (buttonId) {
				case BUTTON_PALY_ID:
					pausePlay();
					if(player.isPlaying()){
						 notification.contentView.setImageViewResource(R.id.noti_pause, R.drawable.pause_1);
					}else{
						//换图片
						notification.contentView.setImageViewResource(R.id.noti_pause, R.drawable.play_6);
					}
					notificationManager.notify(0, notification);
					Toast.makeText(CountService.this, "onclick", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		}
	}
	
	
	
}
