package com.monkeylluffy.recorder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.monkeylluffy.recorder.adpter.ListAdapter;
import com.monkeylluffy.recorder.tools.CursorTool;
import com.monkeylluffy.recorder.tools.FileBean;
import com.monkeylluffy.recorder.tools.GetSystemDateTime;
import com.monkeylluffy.recorder.tools.SDcardTools;
import com.monkeylluffy.recorder.tools.ShowDialog;
import com.monkeylluffy.recorder.tools.StringTools;
import com.monkeylluffy.recorder.view.MyListView;
import com.monkeylluffy.recorder.view.MyListView.OnItemDeleteListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData.Item;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LuYinActivity extends Activity{
	private Button buttonStart; // 开始按钮
	private Button buttonStop; // 停止按钮
	private Button seekfile; // 停止按钮
	
	private Spinner spinner;//下拉列表，时间
	private ImageView volume; // 表示声音大小的动画图片
	private Chronometer timedown;//显示倒计时
    
	private TextView textViewLuYinState; // 录音状态
	private MyListView listViewAudio; // 显示录音文件的list
//	private ArrayAdapter<String> adaperListAudio; // 列表

	private String fileAudioName; // 保存的音频文件的名字
	private MediaRecorder mediaRecorder; // 录音控制
	private String filePath; // 音频保存的文件路径
	private List<String> listAudioFileName; // 音频文件列表
	private boolean isLuYin; // 是否在录音 true 是 false否
	private File fileAudio; // 录音文件
	private File fileAudioList; //列表中的 录音文件
	File dir; //录音文件
	/** Called when the activity is first created. */

    private static final int POLL_INTERVAL = 300;
    private Handler mHandler = new Handler();
   
    private long timeTotal = 30;//录音的默认时间长度为60s
    private int item_position;

    private View rcChat_popup;//按住按钮弹出的

    private long timeTotalInS = 0;
    private long timeLeftInS = 0;
    private Bundle bundle;

	private ListAdapter adapter;
	private CursorTool cursorTool;
    
    private ArrayList<String> data_list = new ArrayList<String>();
	private List<FileBean> list = new ArrayList<FileBean>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.main);	
		cursorTool = new CursorTool(LuYinActivity.this);
		// 初始化组件
		initView();
		// 初始化数据
		initData();
		// 设置组件
		setView();
		// 设置事件
		setEvent();

	}

	/* **********************************************************************
	 * 
	 * 初始化组件
	 */
	private void initView() {
		// 开始
		buttonStart = (Button) findViewById(R.id.button_start);
		// 停止
		buttonStop = (Button) findViewById(R.id.button_stop);
		//时间
		spinner = (Spinner) findViewById(R.id.spinner);
		//从手机中添加声音文件
		seekfile = (Button) findViewById(R.id.button_seekfile);

		// 录音状态
		textViewLuYinState = (TextView) findViewById(R.id.text_luyin_state);
		// 显示录音文件的列表
		listViewAudio = (MyListView) findViewById(R.id.listViewAudioFile);
		//声音的图片
		volume = (ImageView) findViewById(R.id.volume);
		//弹出的声音大小的动画
		rcChat_popup = this.findViewById(R.id.rcChat_popup);
		//倒计时
        timedown=(Chronometer)findViewById(R.id.timedown);
        
        
	}

	/* ******************************************************************
	 * 
	 * 初始化数据
	 */
	private void initData() {
		if (!SDcardTools.isHaveSDcard()) {
			Toast.makeText(LuYinActivity.this, "请插入SD卡以便存储录音",
					Toast.LENGTH_LONG).show();
			return;
		}
	     bundle  = new Bundle();  
		
		// 要保存的文件的路径
		filePath = SDcardTools.getSDPath() + "/" + "myAudio";
		// 实例化文件夹
		dir = new File(filePath);
		if (!dir.exists()) {
			// 如果文件夹不存在 则创建文件夹
			dir.mkdir();
		}
		Log.i("test", "要保存的录音的文件名为" + fileAudioName + "路径为" + filePath);
		listAudioFileName = SDcardTools.getFileFormSDcard(dir, ".mp3");
		
		list = cursorTool.getOwnFileList();
	    adapter = new ListAdapter(this, list);
		
        data_list.add("30s");
        data_list.add("60s");
        data_list.add("180s");
		//适配器
        ArrayAdapter<String> arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
        
	}

	/* **************************************************************
	 * 
	 * 设置组件
	 */
	private void setView() {
		buttonStart.setEnabled(true);
		buttonStop.setEnabled(false);
		listViewAudio.setAdapter(adapter);
	}

	/* ***********************************************************************
	 * 
	 * 设置事件
	 */
	private void setEvent() {

		// 开始按钮
		buttonStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rcChat_popup.setVisibility(View.VISIBLE);
                timedown.setVisibility(View.VISIBLE);
				startAudio();
				initTimer(timeTotal);
                timedown.start();
		        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
			}
		});

		// 停止按钮
		buttonStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(LuYinActivity.this, "录音时间到", Toast.LENGTH_SHORT).show();
                timedown.stop();
				stopAudion();
				rcChat_popup.setVisibility(View.GONE);
                timedown.setVisibility(View.GONE);
			}
		});
		
		seekfile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LuYinActivity.this,SearchMP3.class);
				
				startActivity(intent);
			}
		});
		
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0) {
					timeTotal = 30;
				}else if (position == 1) {
					timeTotal = 60;
				}else {
					timeTotal = 180;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				timeTotal = 30;
			}
		}); 

		
		//录音文件列表点击事件

        registerForContextMenu(listViewAudio);
		listViewAudio.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				item_position = position;
				
				
				openContextMenu(view);
				
			}
		});
	
		  //添加自定义listview的按钮单击事件，处理删除结果，和普通listview使用的唯一不同之处，
	    listViewAudio.setOnItemDeleteListener(new OnItemDeleteListener() {  
	      @Override  
	      public void onItemDelete(int index) {  
	    	  Log.i("test", "滑动删除事件执行了");
				FileBean fileBean= list.get(index);
				String fileAudioNameList = fileBean.getFileTitle();
				fileAudioList = new File(filePath + "/" + fileAudioNameList);
			    
			    	if (fileAudioList != null) {
			    		fileAudio = fileAudioList;
			    		fileAudioName = fileAudioNameList;
						showDeleteAudioDialog("是否删除" + fileAudioName + "文件", "不删除",
								"删除",index, false);
					} else {
						ShowDialog.showTheAlertDialog(LuYinActivity.this, "该文件不存在");
					}
	        adapter.notifyDataSetChanged(); 
	      }  
	    }); 
		
		
	}
	
	
	
	/* ****************************************************************
	 * 
	 * 开始录音
	 */
	private void startAudio() {
		// 创建录音频文件
		// 这种创建方式生成的文件名是随机的
		fileAudioName = "audio" + GetSystemDateTime.now()
				+ StringTools.getRandomString(2) + ".mp3";
		mediaRecorder = new MediaRecorder();
		// 设置录音的来源为麦克风
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setOutputFile(filePath + "/" + fileAudioName);
		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
			textViewLuYinState.setText("录音中。。。");

			fileAudio = new File(filePath + "/" + fileAudioName);
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
			listViewAudio.setEnabled(false);
			isLuYin = true;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (null != adapter) {
			adapter.notifyDataSetChanged();
		}
	}

	/* ******************************************************
	 * 
	 * 停止录制
	 */
	private void stopAudion() {
		if (null != mediaRecorder) {
            mHandler.removeCallbacks(mPollTask);
			// 停止录音
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
			textViewLuYinState.setText("录音停止了");

			// 开始键能够按下
			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
			listViewAudio.setEnabled(true);
			String filePath_adapter = filePath + "/" + fileAudioName;
			addItem(fileAudioName,filePath_adapter);
			adapter.notifyDataSetChanged();
	        volume.setImageResource(R.drawable.amp1);
		}
	}

	private void addItem(String fileAudioName, String filePath_adapter) {
		// TODO Auto-generated method stub
		ContentValues values1 = new ContentValues();
        values1.put("filePath", filePath_adapter);
        values1.put("fileName", fileAudioName);
        FileBean fileBean = new FileBean();
        fileBean.setFilePath(filePath_adapter);
        fileBean.setFileTitle(fileAudioName);
        list.add(fileBean);//为了刷新当前页面用
        
        //参数依次是：表名，强行插入null值得数据列的列名，一行记录的数据
        cursorTool.insertValue(values1);
        Toast.makeText(LuYinActivity.this, "插入完毕~", Toast.LENGTH_SHORT).show();
	}

	/*******************************************************************************************************
	 * 
	 * 是否删除录音文件
	 * 
	 * @param messageString
	 *            //对话框标题
	 * @param button1Title
	 *            //第一个按钮的内容
	 * @param button2Title
	 *            //第二个按钮的内容
	 * @param isExit
	 *            //是否是退出程序
	 */
	@SuppressWarnings("deprecation")
	public void showDeleteAudioDialog(String messageString,
			String button1Title, String button2Title,final int index, final boolean isExit) {
		AlertDialog dialog = new AlertDialog.Builder(LuYinActivity.this)
				.create();
		dialog.setTitle("提示");
		dialog.setMessage(messageString);
		dialog.setButton(button1Title, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (isExit) {
					dialog.dismiss();
					System.exit(0);
				}
			}
		});
		dialog.setButton2(button2Title, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				fileAudio.delete();
				File fileLrc = new File(fileAudio.getAbsolutePath().replace(".mp3", ".lrc"));//同时删除歌词文件
				if (fileLrc.exists()) {
					fileLrc.delete();
				}
				if (index != -1) {
					list.remove(index);
					adapter.notifyDataSetChanged();
				}
				cursorTool.deleteValue(fileAudioName);
				fileAudio = null;
				
				if (isExit) {
					dialog.dismiss();
					System.exit(0);
				}
			}
		});

		dialog.show();
	}

	/**
	 * ********************************************************
	 * 
	 * 当程序停止的时候
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (null != mediaRecorder && isLuYin) {
			// 停止录音
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;

			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
			listViewAudio.setEnabled(true);
			
		}

		
		
		
		super.onStop();
	}

	/**
	 * 
	 * 
	 *********************************************************************** 
	 * 点击退出按钮时
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != mediaRecorder && isLuYin) {
				if (fileAudio != null) {
					showDeleteAudioDialog("是否保存" + fileAudioName + "文件", "保存",
							"不保存",-1, true);
				} else {
					ShowDialog.showTheAlertDialog(LuYinActivity.this, "该文件不存在");
				}
			}else{
				return super.onKeyDown(keyCode, event);
			}
		}
		return true;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
	    
	//以线程的方式来更新动画
	 private Runnable mPollTask = new Runnable(){
	        public void run() {
	            double amp = mediaRecorder.getMaxAmplitude()/2700.0;
	            updateDisplay(amp);
	            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	        }
	    };
	    
	    
	    //在声音强弱变化时的动画
	   private void updateDisplay(double signalEMA) {

	        switch ((int) signalEMA) {
	            case 0:
	            case 1:
	                volume.setImageResource(R.drawable.amp1);
	                break;
	            case 2:
	            case 3:
	                volume.setImageResource(R.drawable.amp2);
	                break;
	            case 4:
	            case 5:
	                volume.setImageResource(R.drawable.amp3);
	                break;
	            case 6:
	            case 7:
	                volume.setImageResource(R.drawable.amp4);
	                break;
	            case 8:
	            case 9:
	                volume.setImageResource(R.drawable.amp5);
	                break;
	            case 10:
	            case 11:
	                volume.setImageResource(R.drawable.amp6);
	                break;
	            default:
	                volume.setImageResource(R.drawable.amp7);
	                break;
	        }
	    }
	   private void initTimer(long total) {
	        this.timeTotalInS = total;
	        this.timeLeftInS = total;
	        timedown.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
	            @Override
	            public void onChronometerTick(Chronometer chronometer) {
	                if (timeLeftInS <= 0) {
	                    Toast.makeText(LuYinActivity.this, "录音时间到", Toast.LENGTH_SHORT).show();
	                    timedown.stop();
	                    //录音停止
	                    stopAudion();
	                    rcChat_popup.setVisibility(View.GONE);
	                    timedown.setVisibility(View.GONE);
	                    return;
	                }
	                timeLeftInS--;
	                refreshTimeLeft();
	            }
	        });
	    }
	    //更新录音剩余时间
	    private void refreshTimeLeft() {
	        this.timedown.setText("录音时间剩余：" + timeLeftInS);
	        //TODO 格式化字符串
	    }
	   
	    
	    @Override
	    public void onCreateContextMenu(ContextMenu menu, View view, 
	            ContextMenuInfo menuInfo) {
	        menu.setHeaderTitle("操作");
	        //添加菜单项
	        menu.add(0, item_play, 0, "播放");
	        menu.add(0, item_edit, 0, "编辑");
	        menu.add(0, item_make_lrc, 0, "歌词制作");
	    }
	    private final int item_play = 1;
	    private final int item_edit = 2;
	    private final int item_make_lrc = 3;
	    
	    //菜单单击响应
	    @Override
	    public boolean onContextItemSelected(MenuItem item){ 

            FileBean fileBean = list.get(item_position);
			String file_path = fileBean.getFilePath();
			String file_name = fileBean.getFileTitle();

			Intent playIntent = new Intent(LuYinActivity.this,PlayActivity.class);
			Intent editIntent = new Intent(LuYinActivity.this,EditActivity.class);
			Intent makefileIntent = new Intent(LuYinActivity.this,MakeLrcFile.class);
			Intent intent = new Intent(LuYinActivity.this,CountService.class);
			bundle = new Bundle(); 
		     bundle.putString("fileAudioNameList", file_name);  //文件名
		     bundle.putString("urlString", file_path);  //文件路径
	        switch(item.getItemId()){
	        case item_play:
	            //在这里添加处理代码
				playIntent.putExtras(bundle);
	            Log.i("contextItem", "play");
				startService(intent);//开启一个服务
				startActivity(playIntent);
	            break;
	        case item_edit:
	            //在这里添加处理代码
	            Log.i("contextItem", "edit");
	            
			     editIntent.putExtras(bundle);				
				startActivity(editIntent);
	            break; 
	        case item_make_lrc:    	
	            Log.i("contextItem", "make_file");
	        	makefileIntent.putExtras(bundle);
				startActivity(makefileIntent); break;
	        }
	        return true;
	    }
	    
	    
	    
}

