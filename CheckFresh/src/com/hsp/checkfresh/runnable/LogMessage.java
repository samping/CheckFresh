package com.hsp.checkfresh.runnable;

import android.util.Log;

public class LogMessage {

	private static boolean Debug = true;
	
	public static void printMsg(String msg){
		if(Debug){
			Log.v("hsp",msg);
		}
	}
}
