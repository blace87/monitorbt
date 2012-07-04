/***************************************
 * 
 * Android Bluetooth Oscilloscope
 * yus	-	projectproto.blogspot.com
 * September 2010
 *  
 ***************************************/

package org.monitorView;

import org.monitor.config.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.view.SurfaceHolder;

public class MonitorThread extends Thread {
	private SurfaceHolder mSurfaceHolder;   
	private boolean mRun = true;
	private Context mContext;
	private Utils utils;

	public MonitorThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {

        mSurfaceHolder = surfaceHolder;
        mContext = context;
        Resources res = context.getResources();
        utils = new Utils();
        utils.iniData();
	}
	
	@Override
	public void run() {
        while (mRun) {
            Canvas c = null;
            try {
                c = mSurfaceHolder.lockCanvas(null);
                synchronized (mSurfaceHolder) {                        
                	//updatePhysics();
                	//doDraw(c);
                }
            } finally {
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
	}
		
	//dibuja la pantalla
	/*private void doDraw(Canvas canvas) {        	
		PlotPoints(canvas);					
    }
	
    
    public void PlotPoints(Canvas canvas){
		
		// clear screen
		canvas.drawColor(Color.rgb(232,232,232));
	    // draw center cross
		canvas.drawLine(0, (Utils.HEIGHT/2)+1, Utils.WIDTH+1, (Utils.HEIGHT/2)+1, cross_paint);
		canvas.drawLine((Utils.WIDTH/2)+1, 0, (Utils.WIDTH/2)+1, Utils.HEIGHT+1, cross_paint);
		
		// draw outline
		canvas.drawLine(0, 0, (Utils.HEIGHT+1), 0, outline_paint);	// top
		canvas.drawLine((Utils.WIDTH-1), 0, (WIDTH-1), (HEIGHT-1), outline_paint); //right
		canvas.drawLine(0, (HEIGHT-1), (WIDTH-1), (HEIGHT-1), outline_paint); // bottom
		canvas.drawLine(0, 0, 0, (HEIGHT+1), outline_paint); //left
		
		//Grafica la señal
		for(int x=0; x<(MAX_SAMPLES-1); x++){                 
			canvas.drawLine(ch2_data[x], ch1_data[x], ch2_data[x+1], ch1_data[x+1], ch1_color);
        }
	}*/

	public boolean ismRun() {
		return mRun;
	}

	public void setmRun(boolean mRun) {
		this.mRun = mRun;
	}
	
}
