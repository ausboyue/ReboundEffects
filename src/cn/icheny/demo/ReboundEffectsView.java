package cn.icheny.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 回弹阻尼效果的简单实现,基于FrameLayout
 * 使用该ReboundEffectsView时应保证其只有且只有一个子View,唯一的独生子，我就叫它太子View
 * 
 * @author Cheny
 *
 */
public class ReboundEffectsView extends FrameLayout {

	private View mPrinceView;// 太子View
	private int mInitTop, mInitBottom;// 太子View初始时上下坐标位置(相对父View,即当前ReboundEffectsView)
	private boolean isEndwiseSlide;// 是否纵向滑动
	private float mVariableY;// 手指上下滑动Y坐标变化前的Y坐标值

	public ReboundEffectsView(Context context) {
		this(context, null);
	}

	public ReboundEffectsView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ReboundEffectsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.setClickable(true);
	}

	/**
	 * Touch事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (null != mPrinceView) {
			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				onActionDown(e);
				break;
			case MotionEvent.ACTION_MOVE:
				return onActionMove(e);
			case MotionEvent.ACTION_UP:
				onActionUp(e);
				break;
			case MotionEvent.ACTION_CANCEL:
				onActionUp(e);// 当ACTION_UP一样处理
				break;
			}
		}
		return super.onTouchEvent(e);
	}

	/**
	 * 手指按下事件
	 */
	private void onActionDown(MotionEvent e) {
		mVariableY = e.getY();
		/**
		 * 保存mPrinceView的初始上下高度位置
		 */
		mInitTop = mPrinceView.getTop();
		mInitBottom = mPrinceView.getBottom();
	}

	/**
	 * 手指滑动事件
	 */
	private boolean onActionMove(MotionEvent e) {
		float nowY = e.getY();
		float diff = (nowY - mVariableY) / 3;
		if (Math.abs(diff) > 0) {// 上下滑动
			// 移动太子View的上下位置
			mPrinceView.layout(mPrinceView.getLeft(), mPrinceView.getTop() + (int) diff, mPrinceView.getRight(),
					mPrinceView.getBottom() + (int) diff);
			mVariableY = nowY;
			isEndwiseSlide = true;
			return true;// 消费touch事件
		}
		return super.onTouchEvent(e);
	}

	/**
	 * 手指释放事件
	 */
	private void onActionUp(MotionEvent e) {
		if (isEndwiseSlide) {// 是否为纵向滑动事件
			// 是纵向滑动事件，需要给太子View重置位置
			resetPrinceView();
			isEndwiseSlide = false;
		}
	}

	/**
	 * 回弹，重置太子View初始的位置
	 */
	private void resetPrinceView() {
		mPrinceView.layout(mPrinceView.getLeft(), mInitTop, mPrinceView.getRight(), mInitBottom);
	}

	/**
	 * XML布局完成加载
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (getChildCount() > 0) {
			mPrinceView = getChildAt(0);// 获得子View，太子View
		}
	}
}
