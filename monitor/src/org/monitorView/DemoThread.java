/***************************************
 * 
 * Android Bluetooth Oscilloscope
 * yus	-	projectproto.blogspot.com
 * September 2010
 *  
 ***************************************/

package org.monitorView;

import java.util.Random;

import org.monitor.config.Utils;

import android.os.Handler;
import android.widget.TextView;

public class DemoThread extends Thread {
	private boolean _run = false;
	private MonitorView mWaveform = null;
	private Utils utils;
	private final Handler mHandler;
	
	public DemoThread(MonitorView view, Handler mHandler){
		this.mWaveform = view;
		this.mHandler = mHandler;
		utils = new Utils();
		utils.iniData();
	}
	
	@Override
	public void run(){
		while(true){
			Random rm= new Random();
			int dato = rm.nextInt(254); 
			if(_run)
			{
				utils.validarDatos(dato, false);
			}
			mWaveform.setUtils(utils);
			
			if(utils.isTxt_latido())
			{
				mHandler.obtainMessage(MonitorActivity.UPDATE_LCD, utils.getLargest()).sendToTarget();
				utils.setLargest(0);
				utils.setSecondlargest(0);
			}
		}
	}

	
	public Utils getUtils() {
		return utils;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	public void setRunning(boolean run){
		_run = run;
	}
	public boolean is_run() {
		return _run;
	}
	
}
