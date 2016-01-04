package com.monkeylluffy.recorder;
import java.io.File;
import com.monkeylluffy.recorder.cheapfile.CheapSoundFile;
import com.monkeylluffy.recorder.tools.CursorTool;
import com.monkeylluffy.recorder.tools.GetSystemDateTime;
import com.monkeylluffy.recorder.tools.SDcardTools;
import com.monkeylluffy.recorder.tools.StringTools;
import com.monkeylluffy.recorder.view.MarkerView;
import com.monkeylluffy.recorder.view.WaveformView;
import com.monkeylluffy.recorder.view.MarkerView.MarkerListener;
import com.monkeylluffy.recorder.view.WaveformView.WaveformListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class EditActivity extends Activity{

    private SeekBar seekBar;//音乐的进度条
    private ImageButton pause;//暂停播放音乐的按钮
 
    private TextView nowTime;//现在时间
    private TextView endTime;//现在时间
    private boolean isPlaying ;
    
    private Handler handler = new Handler();
	private int current;

    private String fileAudioNameList;
    private String urlString ; 
    
	private CursorTool cursorTool ;
	//用于剪辑
    private EditText start_text;//开始时间
    private EditText end_text;//结束时间
    private Button save_file;//结束时间
    private MarkerView start_maker;//开始时间的MakerView
    private MarkerView end_maker;//结束时间的MakerView
    private WaveformView waveform;//结束时间，mStartPos, mEndPos, mOffset
    
	private int mLastDisplayedStartPos;
	private int mLastDisplayedEndPos;
	private int mOffset;
	private int mOffsetGoal;
	private int mFlingVelocity;
	private int mPlayStartMsec;
	private int mPlayStartOffset;
	private int mPlayEndMsec;
	private int mWidth;//屏幕宽度
	private int mMaxPos;//waveform的
	private int mStartPos;
	private int mEndPos;
	private boolean mStartVisible;
	private boolean mEndVisible;
	private boolean mTouchDragging;
	private float mTouchStart;
	private int mTouchInitialOffset;
	private int mTouchInitialStartPos;
	private int mTouchInitialEndPos;
	private long mWaveformTouchStartMsec;
	private float mDensity;
	private int mMarkerLeftInset;
	private int mMarkerRightInset;
	private int mMarkerTopOffset;
	private int mMarkerBottomOffset;
	private CheapSoundFile mSoundFile;	
	private MarkerListener mMarkStartListener;	
	public WaveformListener mWaveformListener ;

	private TextView title_right;
	private TextView title_center;
	private TextView title_left;
	
	private MediaPlayer player;// 用于播放音乐
	AudioManager   am;
	private Handler handler_update_play = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				seekBar.setProgress(player.getCurrentPosition());
				seekBar.setMax(player.getDuration());
				handler_update_play.sendEmptyMessageDelayed(1, 1000);
				nowTime.setText(GetSystemDateTime.timeString(player.getCurrentPosition()));
				endTime.setText(GetSystemDateTime.timeString(player.getDuration()));
			}
		}		
		
	};
	 OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
		  public void onAudioFocusChange(int focusChange) {
		   if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {// 暂时获取焦点 
		    if(player.isPlaying()){
		     player.pause();
		    }
		    
		   } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
		    if(player==null){
		    play();
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	setContentView(R.layout.editsound);
	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局

	initData();
	initView();
	initEvent();
	handler.postDelayed(mTimerRunnable, 100);
	loadFromFile();
	
	}
	
	
	
	
	/**
	 * 从服务中播放改成在页面中播放
	 * */
	public void play(){
	int result = am.requestAudioFocus(afChangeListener,
				// Use the music stream.
						AudioManager.STREAM_MUSIC, // Request permanent focus.
						AudioManager.AUDIOFOCUS_GAIN);
				if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
					player.start();
					// Start playback. // 开始播放音乐文件
				}
		seekBar.setMax(player.getDuration());
		handler_update_play.sendEmptyMessage(1);
		
	}
	/**
	 * 拖动进度条
	 * */
	public void seekto(int position){
		if (player != null) {
			player.seekTo(position);
		}
		
	}	
	/**
	 * 初始化数据
	 * */
	private void initData(){
		Intent newIntent = getIntent();
		fileAudioNameList = newIntent.getExtras().getString("fileAudioNameList");  
	    urlString = newIntent.getExtras().getString("urlString");
		Log.i("fileAudioNameList", " "+fileAudioNameList);
		cursorTool = new CursorTool(EditActivity.this);
		
		player = new MediaPlayer();//初始化播放器
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(urlString);
			player.prepare();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		save_file.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSave();
			}
		});
		 mMarkStartListener  = new MarkerListener() {
			
			 public void markerDraw() {
				}
				/**
				 *  MarkerListener的方法,
				 *  设置为可拖拽的，然后初始的position为
				 * */
				public void markerTouchStart(MarkerView marker, float x) {
					mTouchDragging = true;
					mTouchStart = x;
					mTouchInitialStartPos = mStartPos;
					mTouchInitialEndPos = mEndPos;
				}
				/**
				 *  MarkerListener的方法
				 * */
				public void markerTouchMove(MarkerView marker, float x) {
					float delta = x - mTouchStart;

					if (marker == start_maker) {
						mStartPos = trap((int) (mTouchInitialStartPos + delta));
						mEndPos = trap((int) (mTouchInitialEndPos + delta));
					} else {
						mEndPos = trap((int) (mTouchInitialEndPos + delta));
						if (mEndPos < mStartPos)
							mEndPos = mStartPos;
					}

					updateDisplay();
				}
				/**
				 *  MarkerListener的方法
				 * */
				public void markerTouchEnd(MarkerView marker) {
					mTouchDragging = false;
					if (marker == start_maker) {
						setOffsetGoalStart();
					} else {
						setOffsetGoalEnd();
					}
				}
				/**
				 *  MarkerListener的方法
				 * */
				public void markerLeft(MarkerView marker, int velocity) {

					if (marker == start_maker) {
						int saveStart = mStartPos;
						mStartPos = trap(mStartPos - velocity);
						mEndPos = trap(mEndPos - (saveStart - mStartPos));
						setOffsetGoalStart();
					}

					if (marker == start_maker) {
						if (mEndPos == mStartPos) {
							mStartPos = trap(mStartPos - velocity);
							mEndPos = mStartPos;
						} else {
							mEndPos = trap(mEndPos - velocity);
						}

						setOffsetGoalEnd();
					}

					updateDisplay();
				}
				/**
				 *  MarkerListener的方法
				 * */
				public void markerRight(MarkerView marker, int velocity) {

					if (marker == start_maker) {
						int saveStart = mStartPos;
						mStartPos += velocity;
						if (mStartPos > mMaxPos)
							mStartPos = mMaxPos;
						mEndPos += (mStartPos - saveStart);
						if (mEndPos > mMaxPos)
							mEndPos = mMaxPos;
						setOffsetGoalStart();
					}

					if (marker == end_maker) {
						mEndPos += velocity;
						if (mEndPos > mMaxPos)
							mEndPos = mMaxPos;

						setOffsetGoalEnd();
					}

					updateDisplay();
				}
				/**
				 *  MarkerListener的方法
				 * */
				public void markerEnter(MarkerView marker) {
				}
				/**
				 *  MarkerListener的方法
				 * */
				public void markerKeyUp() {
					updateDisplay();
				}
				/**
				 *  MarkerListener的方法
				 * */
				public void markerFocus(MarkerView marker) {
					if (marker == start_maker) {
						setOffsetGoalStartNoUpdate();
					} else {
						setOffsetGoalEndNoUpdate();
					}

					// Delay updaing the display because if this focus was in
					// response to a touch event, we want to receive the touch
					// event too before updating the display.
					handler.postDelayed(new Runnable() {
						public void run() {
							updateDisplay();
						}
					}, 100);
				}
		};
		start_maker.setListener(mMarkStartListener);
		end_maker.setListener(mMarkStartListener);
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
		
		mWaveformListener = new WaveformListener(){
			// WaveformListener
			/**
			 * Every time we get a message that our waveform drew, see if we need to
			 * animate and trigger another redraw.
			 */
			public void waveformDraw() {
				mWidth = waveform.getMeasuredWidth();
				if (mOffsetGoal != mOffset)
					updateDisplay();
				else if (isPlaying) {
					updateDisplay();
				} else if (mFlingVelocity != 0) {
					updateDisplay();
				}
			}

			public void waveformTouchStart(float x) {
				mTouchDragging = true;
				mTouchStart = x;
				mTouchInitialOffset = mOffset;
				mFlingVelocity = 0;
				mWaveformTouchStartMsec = System.currentTimeMillis();
			}

			public void waveformTouchMove(float x) {
				mOffset = trap((int) (mTouchInitialOffset + (mTouchStart - x)));
				updateDisplay();
			}

			public void waveformTouchEnd() {
				mTouchDragging = false;
				mOffsetGoal = mOffset;

				long elapsedMsec = System.currentTimeMillis() - mWaveformTouchStartMsec;
				if (elapsedMsec < 300) {
					if (isPlaying) {
						int seekMsec = waveform
								.pixelsToMillisecs((int) (mTouchStart + mOffset));
						if (seekMsec >= mPlayStartMsec && seekMsec < mPlayEndMsec) {
							seekto(seekMsec - mPlayStartOffset);
						} else {
							pausePlay();
						}
					} else {
						onPlay((int) (mTouchStart + mOffset));
					}
				}
			}

			public void waveformFling(float vx) {
				mTouchDragging = false;
				mOffsetGoal = mOffset;
				mFlingVelocity = (int) (-vx);
				updateDisplay();
			}
			
			
			
		};
		waveform.setListener(mWaveformListener);
	}

	private long mLoadingStartTime;
	private long mLoadingLastUpdateTime;
	private boolean mLoadingKeepGoing;
	
	private void loadFromFile() {
		//SongMetadataReader主要是用来读取音频文件的信息
		SongMetadataReader metadataReader = new SongMetadataReader(this,
				urlString);// 读取文件的音频信息

		
		mLoadingStartTime = System.currentTimeMillis();// 当前的秒
		mLoadingLastUpdateTime = System.currentTimeMillis();// 当前的秒
		
		mLoadingKeepGoing = true;// 是否是正在加载
		mProgressDialog = new ProgressDialog(EditActivity.this);// 展示加载对话框，是带进度条的
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setTitle("载入中");
		mProgressDialog.setCancelable(true);
		mProgressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						mLoadingKeepGoing = false;// 设置为不是加载状态
					}
				});
		mProgressDialog.show();

		final CheapSoundFile.ProgressListener listener = new CheapSoundFile.ProgressListener() {
			// 返回真继续加载该文件，假就取消。
			public boolean reportProgress(double fractionComplete) {
				long now = System.currentTimeMillis();
				if (now - mLoadingLastUpdateTime > 100) {// 加载的时间大于100ms
					mProgressDialog
							.setProgress((int) (mProgressDialog.getMax() * fractionComplete));
					mLoadingLastUpdateTime = now;// 设置最近加载时间
				}
				return mLoadingKeepGoing;
			}
		};
		new Thread() {
			public void run() {
				play();
			}
		}.start();
		
		// Load the sound file in a background thread
		new Thread() {
			public void run() {
				try {
					// 通过路径创建文件，解析帧
					mSoundFile = CheapSoundFile.create(urlString,
							listener);
					if (mSoundFile == null) {//mSoundFile文件为空的时候
						mProgressDialog.dismiss();
						return;
					}
				} catch (final Exception e) {
					mProgressDialog.dismiss();
					e.printStackTrace();
					return;
				}
				mProgressDialog.dismiss();
				if (mLoadingKeepGoing) {
					Runnable runnable = new Runnable() {
						public void run() {
							finishOpeningSoundFile();
						}
					};
					handler.post(runnable);
				} else {
					EditActivity.this.finish();
				}
			}
		}.start();
	}
	/**
	 * Return extension including dot, like ".mp3" 返回扩展包括点，像“MP3”
	 */
	private String getExtensionFromFilename(String filename) {
		return filename.substring(filename.lastIndexOf('.'), filename.length());
	}
	private void finishOpeningSoundFile() {
		waveform.setSoundFile(mSoundFile);
		waveform.recomputeHeights(mDensity);

		mMaxPos = waveform.maxPos();
		mLastDisplayedStartPos = -1;
		mLastDisplayedEndPos = -1;

		mTouchDragging = false;

		mOffset = 0;
		mOffsetGoal = 0;
		mFlingVelocity = 0;
		resetPositions();
		if (mEndPos > mMaxPos)
			mEndPos = mMaxPos;
		
		updateDisplay();
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
	
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		/*开始的位置, int count被改变的旧内容数, int after改变后的内容数量
		 *这里的s表示改变之前的内容，通常start和count组合，可以在s中读取本次改变字段中被改变的内容。而after表示改变后新的内容的数量。
		 * */
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		//这里的s表示改变之后的内容，通常start和count组合，可以在s中读取本次改变字段中新的内容。而before表示被改变的内容的数量。
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
		//表示最终内容
		public void afterTextChanged(Editable s) {
			if (start_text.hasFocus()) {
				try {
					mStartPos = waveform.secondsToPixels(Double
							.parseDouble(start_text.getText().toString()));
					updateDisplay();
				} catch (NumberFormatException e) {
				}
			}
			if (end_text.hasFocus()) {
				try {
					mEndPos = waveform.secondsToPixels(Double
							.parseDouble(end_text.getText().toString()));
					updateDisplay();
				} catch (NumberFormatException e) {
				}
			}
		}
	};

	
	private void initView() {
		// TODO Auto-generated method stub
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mDensity = metrics.density;// 屏幕密度

		mMarkerLeftInset = (int) (46 * mDensity);
		mMarkerRightInset = (int) (48 * mDensity);
		mMarkerTopOffset = (int) (10 * mDensity);
		mMarkerBottomOffset = (int) (10 * mDensity);
		

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
		
		
		seekBar = (SeekBar) findViewById(R.id.edit_playSeekbar);
		pause = (ImageButton) findViewById(R.id.edit_pause);	
		
		nowTime = (TextView) findViewById(R.id.edit_nowTime);
		endTime = (TextView) findViewById(R.id.edit_endTime);	
		
		pause.setBackgroundResource(R.drawable.pause_1);
		
		mSoundFile = null;
		
		
		start_maker = (MarkerView) findViewById(R.id.edit_startmarker);
		end_maker = (MarkerView) findViewById(R.id.edit_endmarker);
		start_text = (EditText) findViewById(R.id.edit_start_text);
		end_text = (EditText) findViewById(R.id.edit_end_text);
		save_file = (Button) findViewById(R.id.edit_save_file);
		waveform = (WaveformView) findViewById(R.id.edit_waveform);
		waveform.setListener(mWaveformListener);
		mMaxPos = 0;
		mLastDisplayedStartPos = -1;
		mLastDisplayedEndPos = -1;
		if (mSoundFile != null) {
			waveform.setSoundFile(mSoundFile);
			waveform.recomputeHeights(mDensity);
			mMaxPos = waveform.maxPos();
		}

		
		end_text.addTextChangedListener(mTextWatcher);
		start_text.addTextChangedListener(mTextWatcher);
		
		start_maker.setListener(mMarkStartListener);
		start_maker.setAlpha(255);
		start_maker.setFocusable(true);
		start_maker.setFocusableInTouchMode(true);
		mStartVisible = true;

		end_maker.setListener(mMarkStartListener);
		end_maker.setAlpha(255);
		end_maker.setFocusable(true);
		end_maker.setFocusableInTouchMode(true);
		mEndVisible = true;
		
		updateDisplay();
	}

	@Override
	protected void onDestroy() {//改成停止播放音乐
		player.stop();
		am.abandonAudioFocus(afChangeListener);
		super.onDestroy();
	}
	
	/**
	 * 暂停播放，改为在页面操作
	 * */
	public void pausePlay() {
		// TODO Auto-generated method stub
		if(player.isPlaying()){
			isPlaying = false;
			player.pause();
		}else {
			player.start();
		}
	}
	
	/**
	 * 停止播放
	 * */
	public void stopPlay() {
		// TODO Auto-generated method stub
		if(player.isPlaying()){
			isPlaying = false;
			player.pause();
			player.stop();
		}
	}
	
	
	
	/**
	 * 重新定位，1.0,15.0
	 * */
	private void resetPositions() {
		mStartPos = waveform.secondsToPixels(0.0);
		mEndPos = waveform.secondsToPixels(15.0);
	}
	

	private void setOffsetGoalStart() {
		setOffsetGoal(mStartPos - mWidth / 2);
	}
	private void setOffsetGoalStartNoUpdate() {
		setOffsetGoalNoUpdate(mStartPos - mWidth / 2);
	}
	private void setOffsetGoalEnd() {
		setOffsetGoal(mEndPos - mWidth / 2);
	}

	private void setOffsetGoalEndNoUpdate() {
		setOffsetGoalNoUpdate(mEndPos - mWidth / 2);
	}

	private void setOffsetGoal(int offset) {
		setOffsetGoalNoUpdate(offset);
		updateDisplay();
	}
	
	private void setOffsetGoalNoUpdate(int offset) {
		if (mTouchDragging) {
			return;
		}

		mOffsetGoal = offset;
		if (mOffsetGoal + mWidth / 2 > mMaxPos)
			mOffsetGoal = mMaxPos - mWidth / 2;
		if (mOffsetGoal < 0)
			mOffsetGoal = 0;
	}

	private synchronized void updateDisplay() {
		if (isPlaying) {
			int now = current + mPlayStartOffset;//偏移量+当前的currenttime就跳到Player的某一段时间
			int frames = waveform.millisecsToPixels(now);
			waveform.setPlayback(frames);
			setOffsetGoalNoUpdate(frames - mWidth / 2);
			if (now >= mPlayEndMsec) {
				pausePlay();
			}
		}

		if (!mTouchDragging) {
			int offsetDelta;

			if (mFlingVelocity != 0) {
				float saveVel = mFlingVelocity;

				offsetDelta = mFlingVelocity / 30;
				if (mFlingVelocity > 80) {
					mFlingVelocity -= 80;
				} else if (mFlingVelocity < -80) {
					mFlingVelocity += 80;
				} else {
					mFlingVelocity = 0;
				}

				mOffset += offsetDelta;

				if (mOffset + mWidth / 2 > mMaxPos) {
					mOffset = mMaxPos - mWidth / 2;
					mFlingVelocity = 0;
				}
				if (mOffset < 0) {
					mOffset = 0;
					mFlingVelocity = 0;
				}
				mOffsetGoal = mOffset;
			} else {
				offsetDelta = mOffsetGoal - mOffset;

				if (offsetDelta > 10)
					offsetDelta = offsetDelta / 10;
				else if (offsetDelta > 0)
					offsetDelta = 1;
				else if (offsetDelta < -10)
					offsetDelta = offsetDelta / 10;
				else if (offsetDelta < 0)
					offsetDelta = -1;
				else
					offsetDelta = 0;

				mOffset += offsetDelta;
			}
		}

		waveform.setParameters(mStartPos, mEndPos, mOffset);
		waveform.invalidate();

		waveform.setContentDescription(getResources().getText(
				R.string.start_marker)
				+ " " + formatTime(mStartPos));
		end_maker.setContentDescription(getResources().getText(
				R.string.end_marker)
				+ " " + formatTime(mEndPos));

		int startX = mStartPos - mOffset - mMarkerLeftInset;
		if (startX + start_maker.getWidth() >= 0) {
			if (!mStartVisible) {
				// Delay this to avoid flicker，延迟避免闪烁
				handler.postDelayed(new Runnable() {
					public void run() {
						mStartVisible = true;
						start_maker.setAlpha(255);// 设置透明度
					}
				}, 0);
			}
		} else {
			if (mStartVisible) {
				start_maker.setAlpha(0);
				mStartVisible = false;
			}
			startX = 0;
		}

		int endX = mEndPos - mOffset - end_maker.getWidth()
				+ mMarkerRightInset;
		if (endX + end_maker.getWidth() >= 0) {
			if (!mEndVisible) {
				// Delay this to avoid flicker
				handler.postDelayed(new Runnable() {
					public void run() {
						mEndVisible = true;
						end_maker.setAlpha(255);
					}
				}, 0);
			}
		} else {
			if (mEndVisible) {
				end_maker.setAlpha(0);
				mEndVisible = false;
			}
			endX = 0;
		}

		start_maker.setLayoutParams(new AbsoluteLayout.LayoutParams(
				AbsoluteLayout.LayoutParams.WRAP_CONTENT,
				AbsoluteLayout.LayoutParams.WRAP_CONTENT, startX,
				mMarkerTopOffset));

		end_maker.setLayoutParams(new AbsoluteLayout.LayoutParams(
				AbsoluteLayout.LayoutParams.WRAP_CONTENT,
				AbsoluteLayout.LayoutParams.WRAP_CONTENT, endX, waveform
						.getMeasuredHeight()
						- end_maker.getHeight()
						- mMarkerBottomOffset));
	}

	private String formatTime(int pixels) {
		if (waveform != null && waveform.isInitialized()) {
			return formatDecimal(waveform.pixelsToSeconds(pixels));
		} else {
			return "";
		}
	}
	private String formatDecimal(double x) {
		int xWhole = (int) x;
		int xFrac = (int) (100 * (x - xWhole) + 0.5);

		if (xFrac >= 100) {
			xWhole++; // Round up
			xFrac -= 100; // Now we need the remainder after the round up
			if (xFrac < 10) {
				xFrac *= 10; // we need a fraction that is 2 digits long
			}
		}

		if (xFrac < 10)
			return xWhole + ".0" + xFrac;
		else
			return xWhole + "." + xFrac;
	}
	private int trap(int pos) {
		if (pos < 0)
			return 0;
		if (pos > mMaxPos)
			return mMaxPos;
		return pos;
	}

	/**
	 * 播放
	 * */
	private synchronized void onPlay(int startPosition) {
		if (isPlaying) {//在运行就暂停
			pausePlay();
			return;
		}

		try {
			mPlayStartMsec = waveform.pixelsToMillisecs(startPosition);
			if (startPosition < mStartPos) {
				mPlayEndMsec = waveform.pixelsToMillisecs(mStartPos);
			} else if (startPosition > mEndPos) {
				mPlayEndMsec = waveform.pixelsToMillisecs(mMaxPos);
			} else {
				mPlayEndMsec = waveform.pixelsToMillisecs(mEndPos);
			}

			mPlayStartOffset = 0;

			int startFrame = waveform
					.secondsToFrames(mPlayStartMsec * 0.001);
			int endFrame = waveform.secondsToFrames(mPlayEndMsec * 0.001);
			int startByte = mSoundFile.getSeekableFrameOffset(startFrame);// 开始帧
			int endByte = mSoundFile.getSeekableFrameOffset(endFrame);// 结束帧
			if (startByte >= 0 && endByte >= 0) {//如果开始，结束都大于0,打开播放
				mPlayStartOffset = 0;
				play();
			}

			isPlaying = true;

			updateDisplay();
			enableDisableButtons();
		} catch (Exception e) {
			
			return;
		}
	}
	
	/**
	 * 保存的
	 * */
	private void onSave() {
		if (isPlaying) {
			pausePlay();
		}
		saveRingtone();
	}
	
	
	
	
	/**
	 * 通过保存为铃声或者其他设置保存位置，通过Title设置文件名,extension设置文件后缀
	 * */
	private String makeRingtoneFilename() {
		String filePath = SDcardTools.getSDPath() + "/" + "myAudio";
		String fileAudioName = "audio" + GetSystemDateTime.now()
				+ StringTools.getRandomString(2) + ".mp3";

		File dir = new File(filePath);
		if (!dir.exists()) {
			// 如果文件夹不存在 则创建文件夹
			dir.mkdir();
		}
		
		return filePath+"/"+fileAudioName;
		
		}

/**
 * 更新starttime，endtime的Text
 * */
private Runnable mTimerRunnable = new Runnable() {
	public void run() {
		// Updating an EditText is slow on Android. Make sure
		// we only do the update if the text has actually changed.
		// 更新EditText慢在Android上。请确保我们只做更新，如果文本已经改变。
		if (mStartPos != mLastDisplayedStartPos && !start_text.hasFocus()) {
			start_text.setText(formatTime(mStartPos));
			mLastDisplayedStartPos = mStartPos;
		}

		if (mEndPos != mLastDisplayedEndPos && !end_text.hasFocus()) {
			end_text.setText(formatTime(mEndPos));
			mLastDisplayedEndPos = mEndPos;
		}

		handler.postDelayed(mTimerRunnable, 100);
	}
};

	private ProgressDialog mProgressDialog;
	private void saveRingtone() {
		
		String filePath = SDcardTools.getSDPath() + "/" + "myAudio";
		final String fileAudioName = "audio" + GetSystemDateTime.now()
				+ StringTools.getRandomString(2) + ".mp3";

		File dir = new File(filePath);
		if (!dir.exists()) {
			// 如果文件夹不存在 则创建文件夹
			dir.mkdir();
		}
		
		final String outPath = filePath+"/"+fileAudioName;

		double startTime = waveform.pixelsToSeconds(mStartPos);// position
		double endTime = waveform.pixelsToSeconds(mEndPos);
		final int startFrame = waveform.secondsToFrames(startTime);// 开始帧
		final int endFrame = waveform.secondsToFrames(endTime);// 结束帧
		final int duration = (int) (endTime - startTime + 0.5);// 时间

		// Create an indeterminate progress dialog
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setTitle("保存中");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		// Save the sound file in a background thread,后台进程来保存文件
		new Thread() {
			public void run() {
				final File outFile = new File(outPath);
				try {
					// Write the new file
					mSoundFile.WriteFile(outFile, startFrame, endFrame
							- startFrame);
					ContentValues values = new ContentValues();
			        values.put("filePath", outPath);
			        values.put("fileName", fileAudioName);
					cursorTool.insertValue(values);
					//保存文件之后跳转回主页面
					Intent intent = new Intent(EditActivity.this,LuYinActivity.class);
					startActivity(intent);					
				} catch (Exception e) {
					mProgressDialog.dismiss();
					return;
				}

				mProgressDialog.dismiss();
			}
		}.start();
		
		
		
		
	}
	
	
}
