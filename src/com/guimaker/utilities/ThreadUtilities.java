package com.guimaker.utilities;

public class ThreadUtilities {

	public static void callOnOtherThread(Runnable r) {
		new Thread(r).start();
	}
}
