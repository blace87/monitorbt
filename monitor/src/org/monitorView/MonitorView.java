package org.monitorView;

import org.monitor.config.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MonitorView extends SurfaceView implements Callback {

	private Utils utils;
	private Paint ch1_color = new Paint();
	private Paint ch2_color = new Paint();
	private Paint grid_paint = new Paint();
	private Paint cross_paint = new Paint();
	private Paint outline_paint = new Paint();
	private MonitorThread thread;
	private Context mContext;
	private int latido = 0;
	
	//**********************/
	public MonitorView(Context context, AttributeSet attrs) {
		super(context, attrs);		
        mContext = context;
        
        // lo registro para se entere si hay cambios
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
        // solo creo el hilo, se inicia en surfaceCreated()
        createThread(holder);

        utils = new Utils();
        utils.iniData();
        
		ch1_color.setColor(Color.rgb(33, 33, 33));
		ch2_color.setColor(Color.rgb(33, 33, 33));
		ch1_color.setStrokeWidth(2);
		ch2_color.setStrokeWidth(2);
		//ch1_color.setAlpha(80);
		//ch2_color.setAlpha(80);
		ch1_color.setStyle(Paint.Style.FILL);
		ch2_color.setStyle(Paint.Style.FILL);
		
		grid_paint.setColor(Color.rgb(33, 33, 33));
		cross_paint.setColor(Color.rgb(33, 33, 33));
		cross_paint.setStyle(Paint.Style.FILL);
		cross_paint.setStrokeWidth(1);
		cross_paint.setAlpha(80);
		
		outline_paint.setColor(Color.rgb(33, 33, 33));
		outline_paint.setStyle(Paint.Style.FILL);
		outline_paint.setStrokeWidth(1);
		outline_paint.setAlpha(80);
        
        setFocusable(true);
	}

	private void createThread(SurfaceHolder holder) {
        thread = new MonitorThread(holder, mContext, new Handler() {
            @Override
            public void handleMessage(Message m) {
            	//
            }        	
        });        
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
    	//Inicio la ejecución del hilo
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        // finalizo la ejecución del hilo
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
	}
	
	public MonitorThread getThread() {
		return thread;
	}
	
	class MonitorThread extends Thread {
		
        private SurfaceHolder mSurfaceHolder;   
		private boolean mRun = true;

		public MonitorThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {

            mSurfaceHolder = surfaceHolder;
            mContext = context;            
            Resources res = context.getResources();
		}
		
		@Override
		public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {                        
                    	//updateDatos();
                    	doDraw(c);
                    }
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
		}
			
		//dibuja la pantalla
		private void doDraw(Canvas canvas) {        	
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
    		canvas.drawLine((Utils.WIDTH-1), 0, (Utils.WIDTH-1), (Utils.HEIGHT-1), outline_paint); //right
    		canvas.drawLine(0, (Utils.HEIGHT-1), (Utils.WIDTH-1), (Utils.HEIGHT-1), outline_paint); // bottom
    		canvas.drawLine(0, 0, 0, (Utils.HEIGHT+1), outline_paint); //left
    		
    		//Grafica la se�al
    		for(int x=0; x<(Utils.MAX_SAMPLES-1); x++){                 
    			canvas.drawLine(utils.getCh2_data()[x], utils.getCh1_data()[x], utils.getCh2_data()[x+1], utils.getCh1_data()[x+1], ch1_color);
            }
    	}

		public boolean ismRun() {
			return mRun;
		}

		public void setmRun(boolean mRun) {
			this.mRun = mRun;
		}
		
	}

	public Utils getUtils() {
		return utils;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	public int getLatido() {
		return latido;
	}

	public void setLatido(int latido) {
		this.latido = latido;
	}

	public void setThread(MonitorThread thread) {
		this.thread = thread;
	}
	
	
}
