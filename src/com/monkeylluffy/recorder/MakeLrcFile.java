package com.monkeylluffy.recorder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.monkeylluffy.recorder.tools.GetSystemDateTime;
import com.monkeylluffy.recorder.tools.LrcContent;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 歌词制作,弹出对话框，确认是否保存歌词文件 制作歌词格式 点击插入之后光标自动插入下一行
 * */
public class MakeLrcFile extends Activity {

	EditText lrc;
	SeekBar seekBar;
	TextView nowTime;
	TextView endTime;
	ImageButton pause;
	Button addText;
	Button savefile;
	private boolean isPlaying;
	private MediaPlayer player;// 用于播放音乐
	AudioManager am;
	private String fileName;
	private String filePath;
	

	private TextView title_right;
	private TextView title_center;
	private TextView title_left;

	private int current;
	private int max;
	private int progress;
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
		setContentView(R.layout.makelrc);
		/*getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
		
		title_left = (TextView) findViewById(R.id.item_left);
		title_right = (TextView) findViewById(R.id.item_right);	
		title_center = (TextView) findViewById(R.id.item_center);
		
		title_left.setVisibility(View.GONE);
		title_right.setVisibility(View.GONE);
		title_center.setText("歌词制作");*/
		initData();
		initView();
		initEvent();
		play();

	}

	private void initData() {
		// TODO Auto-generated method stub
		Intent newIntent = getIntent();
		fileName = newIntent.getExtras().getString("fileAudioNameList");
		filePath = newIntent.getExtras().getString("urlString");
		Log.i("fileName", " " + fileName);

		player = new MediaPlayer();// 初始化播放器
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(filePath);
			player.prepare();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		pause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pausePlay();
				if (isPlaying) {
					isPlaying = false;
					pause.setBackgroundResource(R.drawable.pause_1);
				} else {
					isPlaying = true;
					pause.setBackgroundResource(R.drawable.play_6);
				}

			}
		});

		// 添加一句歌词，并将光标移动到下一行
		addText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addText();
			}
		});

		// 保存文件
		savefile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				save(lrc.getText().toString());
				Toast.makeText(MakeLrcFile.this, "保存成功", Toast.LENGTH_SHORT).show();
				pausePlay();
				if (isPlaying) {
					isPlaying = false;
					pause.setBackgroundResource(R.drawable.pause_1);
				} else {
					isPlaying = true;
					pause.setBackgroundResource(R.drawable.play_6);
				}
				Bundle bundle = new Bundle();
				bundle.putString("fileAudioNameList", fileName);  //文件名
				bundle.putString("urlString", filePath);  //文件路径
				Intent playIntent = new Intent(MakeLrcFile.this,PlayActivity.class);
				Intent intent = new Intent(MakeLrcFile.this,CountService.class);
				playIntent.putExtras(bundle);
	            Log.i("contextItem", "play");
				startService(intent);//开启一个服务
				startActivity(playIntent);
			}

		});
		
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
		
		
		

	}

	private void initView() {
		// TODO Auto-generated method stub
		lrc = (EditText) findViewById(R.id.makelrc_lrc);

		seekBar = (SeekBar) findViewById(R.id.makelrc_playSeekbar);
		nowTime = (TextView) findViewById(R.id.makelrc_nowTime);
		endTime = (TextView) findViewById(R.id.makelrc_endTime);
		pause = (ImageButton) findViewById(R.id.makelrc__pause);
		addText = (Button) findViewById(R.id.makelrc_addtext);
		savefile = (Button) findViewById(R.id.makelrc_savefile);
		
		String fileText = readFile();
		lrc.setText(fileText);
		if (fileText!=null) {
			lrc.setSelection(fileText.length());
		}

		pause.setBackgroundResource(R.drawable.pause_1);
		
	}
@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	player.stop();
	am.abandonAudioFocus(afChangeListener);
	super.onDestroy();
	
}

	private String readFile() {//从文件从读出文字设置
		// TODO Auto-generated method stub
		File f = new File(filePath.replace(".mp3", ".lrc"));// 读取同路径的同名不同后缀的文件
		String string = "";
		if (f.exists()) {
			try {
				//创建一个文件输入流对象
				FileInputStream fis = new FileInputStream(f);
				BufferedReader br = null;
				String s = "";
				
				//判断字符类型之后再决定字符 
				BufferedInputStream bis=new BufferedInputStream(fis);
				  bis.mark(4);
				   byte[] first3bytes=new byte[3];
				//   System.out.println("");
				   //找到文档的前三个字节并自动判断文档类型。
				   bis.read(first3bytes);
				   bis.reset();
				     if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
				                      && first3bytes[2] == (byte) 0xBF) {// utf-8

				              br = new BufferedReader(new InputStreamReader(bis, "utf-8"));

				      } else if (first3bytes[0] == (byte) 0xFF
				                      && first3bytes[1] == (byte) 0xFE) {

				              br = new BufferedReader(
				                              new InputStreamReader(bis, "unicode"));
				      } else if (first3bytes[0] == (byte) 0xFE
				                      && first3bytes[1] == (byte) 0xFF) {

				              br = new BufferedReader(new InputStreamReader(bis,
				                              "utf-16be"));
				      } else if (first3bytes[0] == (byte) 0xFF
				                      && first3bytes[1] == (byte) 0xFF) {

				              br = new BufferedReader(new InputStreamReader(bis,
				                              "utf-16le"));
				      } else {

				              br = new BufferedReader(new InputStreamReader(bis, "GBK"));
				      }
				     
				     while((s = br.readLine()) != null) {
				    	 string += (s+"\n");
					}
				     
				}catch(Exception e){
					
					e.printStackTrace();
				}
			return string;
			
			
			}
		else {//文件不存在

				return null;
			}
			
	}

	private void save(String write_str) {
		// TODO Auto-generated method stub
		File file = new File(filePath.replace(".mp3", ".lrc"));// 读取同路径的同名不同后缀的文件
		FileOutputStream fos;
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"GB2312")));
			out.print(write_str);
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/**
	 * 在页面中播放
	 * */
	public void play() {
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
	public void seekto(int position) {
		if (player != null) {
			player.seekTo(position);
		}

	}

	/**
	 * 暂停播放，改为在页面操作
	 * */
	public void pausePlay() {
		// TODO Auto-generated method stub
		if (player.isPlaying()) {
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
		if (player.isPlaying()) {
			isPlaying = false;
			player.pause();
			player.stop();
		}
	}
	
	
	
	
	
	
	//通过得到当前光标，然后得到他的行，然后对这一行的文字进行处理
	private void addText() {
		// TODO Auto-generated method stub
		String timeString = makeTimeString(player.getCurrentPosition());//得到时间的那个字符串
		String fileText = lrc.getText().toString();
		if (fileText.length() == 0) {
			return;
		}
		int index = this.lrc.getSelectionStart();// 获取光标位置
		int rows = this.lrc.getText().toString().substring(0, index).split("\n").length;// 从文件开始到光标位置找到分行的字符下标
		
		String[] arrayOfString = fileText.split("\n");//按行分割字符串，得到总行数
		int len = arrayOfString.length;
		String newFileString = "";
		if (rows == -1) {//在第一行
			arrayOfString[0] = timeString + lrc.getText().toString().substring(0, index);
			for (int i = 0; i < arrayOfString.length; i++) {
				newFileString += (arrayOfString[i]+"\n");
			}
			lrc.setText(newFileString);
			lrc.setSelection(newFileString.length());	
			
			return;
		}
		
		arrayOfString[rows-1] = timeString+arrayOfString[rows-1];//加到当前光标的那个行
		for (int i = 0; i < arrayOfString.length; i++) {
			newFileString += (arrayOfString[i]+"\n");
		}
		lrc.setText(newFileString);
		lrc.setSelection(newFileString.length());		
	}

	public String makeTimeString(int time) {

		int j = time / 1000;// totalS
		int i = j / 60;// total M
		j %= 60;// the s
		i %= 60;// the m
		time = time - i * 60 * 1000 - j * 1000;// 剩余的毫秒数
		String str1 = "";
		j -= 1;//试探是不是在1s以内
		if (j < 0) {
			time = i - 1;
			if (time < 0)
				str1 = "[00:00.00]";
			return String.format(str1, new Object[0]);
		}
		j += 1;//回复之前减的
		// 分钟数<10的时候在数字前+0
		if (i < 10) {
			str1 = "0" + i + ":";
		} else {
			str1 = i + ":";
		}

		if (j < 10) {
			str1 = str1 + "0" + j + ".";
		} else {
			str1 = str1 + j + ".";
		}
		if (time < 10) {
			str1 = str1 +"0" +time;
		}else {
			str1 = str1 +time;
		}
		
		return "[" + str1 + "]";
	}

}
