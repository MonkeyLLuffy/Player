package com.monkeylluffy.recorder.view;

import com.monkeylluffy.recorder.R;
import com.monkeylluffy.recorder.R.anim;
import com.monkeylluffy.recorder.R.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * 项目名称：viewTest 
 * 实现功能： 自定义ListView，增加滑动删除功能 类名称：MyListView 
 * 类描述：(该类的主要功能) E-mail:
 * 
 * @version
 */
public class MyListView extends ListView implements OnTouchListener,
		OnGestureListener {

	/**
	 * 手势识别类
	 */
	private GestureDetector gestureDetector;

	/**
	 * 滑动时出现的按钮
	 */
	private View btnDelete;

	/**
	 * listview的每一个item的布局
	 */
	private ViewGroup viewGroup;
	/**
	 * 选中的项
	 */
	private int selectedItem;

	/**
	 * 是否已经显示删除按钮
	 */
	private boolean isDeleteShow;

	/**
	 * 点击删除按钮时删除每一行的事件监听器
	 */
	private OnItemDeleteListener onItemDeleteListener;

	/**
	 * 构造函数，初始化手势监听器等
	 * 
	 * @param context
	 * @param attrs
	 */
	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		gestureDetector = new GestureDetector(getContext(), this);
		setOnTouchListener(this);
	}

	public void setOnItemDeleteListener(
			OnItemDeleteListener onItemDeleteListener) {
		this.onItemDeleteListener = onItemDeleteListener;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// 得到当前触摸的条目
		selectedItem = pointToPosition((int) event.getX(), (int) event.getY());
		// 如果删除按钮已经显示，那么隐藏按钮，异常按钮在当前位置的绘制
		if (isDeleteShow) {
			btnHide(btnDelete);
			viewGroup.removeView(btnDelete);
			btnDelete = null;
			isDeleteShow = false;
			return false;
		} else {
			// 如果按钮没显示，则触发手势事件
			// 由此去触发GestureDetector的事件，可以查看其源码得知，onTouchEvent中进行了手势判断，调用onFling
			return gestureDetector.onTouchEvent(event);
		}

	}

	@Override
	public boolean onDown(MotionEvent e) {
		// 得到当前触摸的条目
		if (!isDeleteShow) {
			selectedItem = pointToPosition((int) e.getX(), (int) e.getY());
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	@Override
	public boolean postDelayed(Runnable action, long delayMillis) {
		// TODO Auto-generated method stub
		return super.postDelayed(action, delayMillis);
	}
	
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	/**
	 * 滑动删除的主要响应方法。 E1第一下运动事件，开始onfling。
	 *  E2移动运动事件，引发了当前的onfling。 velocityX
	 * 这一秒的速度测量的像素每秒沿轴。 velocityY 这一秒的速度测量的像素每秒沿轴。
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// 如果删除按钮没有显示，并且手势滑动符合我们的条件
		// 此处可以根据需要进行手势滑动的判断，如限制左滑还是右滑，我这里是左滑右滑都可以
		// velocityX<0 只能左划删除，velocityX>0只能右划删除
		/*	*/
		try {

			Log.i("------------velocityX1------------", velocityX + "");
			if (!isDeleteShow && Math.abs(velocityX) > Math.abs(velocityY)
					&& velocityX > -6000 && velocityX < 0) {
				Log.i("------------velocityX2------------", velocityX + "");
				// 在当前布局上，动态添加我们的删除按钮，设置按钮的各种参数、事件，按钮的点击事件响应我们的删除项监听器
				btnDelete = LayoutInflater.from(getContext()).inflate(
						R.layout.button, null);
				btnDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// btnHide(btnDelete);
						viewGroup.removeView(btnDelete);
						btnDelete = null;
						isDeleteShow = false;
						onItemDeleteListener.onItemDelete(selectedItem);
					}
				});
				viewGroup = (ViewGroup) getChildAt(selectedItem
						- getFirstVisiblePosition());
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
				btnDelete.setLayoutParams(layoutParams);
				viewGroup.addView(btnDelete);
				btnShow(btnDelete);
				isDeleteShow = true;
			} else if (isDeleteShow && Math.abs(velocityX) > Math.abs(velocityY)
					&& velocityX > 0) {// 向左滑动并且按钮还在的时候，隐藏按钮
				btnHide(btnDelete);
			} else {
				setOnTouchListener(this);
			}
		}
		 catch (NullPointerException e) {
				// TODO: handle exception
			 try {
				Thread.sleep(100);
				Log.e("-----------------滑动过快----------------","------------------null----------------");
			} catch (InterruptedException e3) {
				// TODO Auto-generated catch block
				
				e3.printStackTrace();
			}
			}
		return false;
	}

	/**
	 * @类名称: OnItemDeleteListener
	 * @描述: 删除按钮监听器
	 * @throws
	 */
	public interface OnItemDeleteListener {
		public void onItemDelete(int selectedItem);
	}

	/**
	 * @方法名称: btnShow
	 * @描述: 按钮显示时的动画
	 * @param @param v
	 * @return void
	 */
	private void btnShow(View v) {
		v.startAnimation(AnimationUtils.loadAnimation(getContext(),
				R.anim.btn_show));
		v.clearAnimation();
	}

	/**
	 * @方法名称: btnHide
	 * @描述: 按钮隐藏时的动画
	 * @param @param v
	 * @return void
	 */
	private void btnHide(View v) {
		v.startAnimation(AnimationUtils.loadAnimation(getContext(),
				R.anim.btn_hide));
		v.clearAnimation();
	}

}
