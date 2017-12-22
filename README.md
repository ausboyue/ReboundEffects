# ReboundEffects
> 先简单说说回弹阻尼效果的思路，先自定义一个ViewGroup ----- ReboundEffectsView，通过手势的上下滑动距离差不断改变其子View（一般都是子ViewGroup）的相对于该ReboundEffectsView的位置（坐标），当手势为释放（action_up）或取消（action_cancel）时，重置子View最初始相对ReboundEffectsView的位置，最初始的位置值应在处理滑动事件前保存下来以用来重置。

![效果图](https://github.com/ausboyue/ReboundEffects/raw/master/demo.gif)

Demo项目下载地址：https://github.com/ausboyue/ReboundEffects

### 进入代码板块
1.在自定义ReboundEffectsView中重写onFinishInflate方法，XML布局完成加载后获取其第一个子View（使用该ReboundEffectsView时应保证其只有且只有一个子View,唯一的独生子，我就叫它太子View）:
``` java
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
```

2.onTouchEvent方法，分别对ACTION_DOWN，ACTION_MOVE，ACTION_UP，ACTION_CANCEL事件进行处理：
``` java
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
			case MotionEvent.ACTION_CANCEL:
				onActionUp(e);// 当ACTION_UP一样处理
				break;
			}
		}
		return super.onTouchEvent(e);
	}
```
3.onActionDown中保存子View的初始上下高度位置：
``` java
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
```
4.核心代码，onActionMove中，主要是判断手势移动事件是否为上下滑动事件，是的话根据上下滑动的绝对距离重绘子View的位置（这里的绝对距离除以2了，目的是放缓子View的移动速度），并在以后的Touch拿到直接的控制权，返回true:
``` java 
	/**
	 * 手指滑动事件
	 */
	private boolean onActionMove(MotionEvent e) {
		float nowY = e.getY();
		float diff = (nowY - mVariableY) / 2;
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
```
5.手指释放或手势取消时，则调用onActionUp方法恢复子View的位置：
``` java
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
		TranslateAnimation ta = new TranslateAnimation(0, 0, mPrinceView.getTop() - mInitTop, 0);
		ta.setDuration(600);
		mPrinceView.startAnimation(ta);
		mPrinceView.layout(mPrinceView.getLeft(), mInitTop, mPrinceView.getRight(), mInitBottom);
	}
```
---
OK，整个自定义View中重写原生的方法并不多，也不需要自行测量和绘制View，代码逻辑也足够精简，如果有什么bug或建议可以在下面评论，我会继续更新博客。
