package com.acg12.redenvelope.util;

import android.widget.Toast;

import com.acg12.redenvelope.app.BaseApplication;


/**
 * Toast统一管理类
 */
public class T {
	// Toast
	private static Toast toast;

	/** Hide the toast, if any. */
	public static void hideToast() {
		if (null != toast) {
			toast.cancel();
		}
	}

	/**
	 * 短时间显示Toast
	 *
	 * 优化使用Application 不会造成内存泄露
	 * @param message
	 */
	public static void showShort(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(BaseApplication.getApplication(), message, Toast.LENGTH_SHORT);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 短时间显示Toast
	 *
	 * @param message
	 */
	public static void showShort(int message) {
		if (null == toast) {
			toast = Toast.makeText(BaseApplication.getApplication(), message, Toast.LENGTH_SHORT);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 *
	 * @param message
	 */
	public static void showLong(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(BaseApplication.getApplication(), message, Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 *
	 * @param message
	 */
	public static void showLong(int message) {
		if (null == toast) {
			toast = Toast.makeText(BaseApplication.getApplication(), message, Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 自定义显示Toast时间
	 *
	 * @param message
	 * @param duration
	 */
	public static void show(CharSequence message, int duration) {
		if (null == toast) {
			toast = Toast.makeText(BaseApplication.getApplication(), message, duration);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 自定义显示Toast时间
	 *
	 * @param message
	 * @param duration
	 */
	public static void show(int message, int duration) {
		if (null == toast) {
			toast = Toast.makeText(BaseApplication.getApplication(), message, duration);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

}
