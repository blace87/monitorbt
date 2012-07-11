package org.monitorView;
import java.util.List;

import org.monitor.config.Utils;
import org.monitor.proceso.ProcesoDatos;
import org.monitorView.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MonitorActivity extends Activity implements  Button.OnClickListener {

	private MonitorView monitorView;
	// Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the RFCOMM services
    private BluetoothRfcommClient mRfcommClient = null;

    // Message types sent from the BluetoothRfcommClient Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int UPDATE_LCD = 6;
    
    // Key names received from the BluetoothRfcommClient Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    //Name of the connected device
    private String mConnectedDeviceName = null;
    //Varibale de estaticas
   
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_DISABLE_BT = 4;
    private static final int REQUEST_CONFIGURACION = 3;
    
    private TextView mBTStatus;
    private ToggleButton run_buton;
    private TextView lcd;
    private Typeface lcdFont;
	
    // variabl de calculo de datos
    private Utils utils;
    private ProcesoDatos procesoDatos;
    //Hilo de demostracion
    private DemoThread demoThread;
    private boolean bReady = false;
    private boolean conected = false;
    
    private MenuItem item_connect;
    private MenuItem item_disconnect;
    private MenuItem item_email;
    private MenuItem item_sms;
    private MenuItem item_grabar;
    
    private org.monitor.config.Configuracion configuracion;
    private SharedPreferences prefs;
    
    private ProgressDialog progressDialog = null;
	private int progressBarStatus = 0;
    
	/** Se ejecuta cuando la actividad se crea. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        this.monitorView = (MonitorView) findViewById(R.id.ejemploSV);
        mBTStatus = (TextView) findViewById(R.id.txt_btstatus);
        lcd = (TextView) findViewById(R.id.txt_lcd);
        // Get local Bluetooth adapter
	    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    
	    //run_buton = (ToggleButton) findViewById(R.id.tbtn_runtoggle);
        //run_buton.setOnClickListener(this);
	    
        init();
	    
	    // If the adapter is null, then Bluetooth is not supported
	    if (mBluetoothAdapter == null) {
	    	Toast.makeText(this, "Bluetooth no esta habiltado.", Toast.LENGTH_LONG).show();
	        finish();
	        return;
	    }
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mRfcommClient != null)
        {
        	mRfcommClient.stop();
        }
		
		try {
        	bReady = false;
        	if(demoThread!=null)
        	{
        		demoThread.setRunning(false);
        		demoThread.join();
        	}
        	
        	if(monitorView!=null)
        	{
        		monitorView.getThread().setmRun(false);
        		monitorView.getThread().join();
        	}
        	
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}    
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {    	
    	super.onConfigurationChanged(newConfig);
    }

	@Override
	public void onClick(View v) {
		int buttonID;
		buttonID = v.getId();
		switch (buttonID)
		{
	    	/*case R.id.tbtn_runtoggle:
	    		if(run_buton.isChecked())
	    		{
	    			bReady = true;
	    			this.monitorView.setAlmacenar(true);
	    			this.monitorView.setTotalDatos(configuracion.getTiempo_grabacion());
	    			this.monitorView.setTiempo(0);
	    			this.monitorView.setCont(0);
	    		}else{
	    			bReady = false;
	    			this.monitorView.setAlmacenar(false);
	    			this.monitorView.setTotalDatos(0);
	    			this.monitorView.setTiempo(0);
	    			this.monitorView.setCont(0);
	    	    }
            break;*/
		}
		
	}	
	
	public void onStart() 
    {
    	super.onStart();
	    // If BT is not on, request that it be enabled.
	    // setupOscilloscope() will then be called during onActivityResult
	    if (!mBluetoothAdapter.isEnabled()) {
	    	Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	        // Otherwise, setup the Oscillosope session
	    } else 
	    {
	        if (mRfcommClient == null) 
	        {
	        	utils = new Utils();
	        	utils.iniData();
	        	mRfcommClient = new BluetoothRfcommClient(this, mHandler);
	        }
	    }
	}
    	   		
	private void BTConnect(){
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}
	
	private void BTDisconnect(){

		if (mRfcommClient != null) {
			try {
				mRfcommClient.stop();
				utils.iniData();
			} 
			catch (Exception e) {}
		}

	}
		
	// The Handler that gets information back from the BluetoothRfcommClient
    private final Handler mHandler = new Handler(){
      @Override
        public void handleMessage(Message msg){
        switch (msg.what){
        case MESSAGE_STATE_CHANGE:
          switch (msg.arg1){
          case BluetoothRfcommClient.STATE_CONNECTED:
        	  mBTStatus.setText(R.string.title_connected_to);
        	  mBTStatus.append("\n" + mConnectedDeviceName);
        	  conected = true;
            break;
          case BluetoothRfcommClient.STATE_CONNECTING:
        	  mBTStatus.setText(R.string.title_connecting);
            break;
          case BluetoothRfcommClient.STATE_NONE:
        	  mBTStatus.setText(R.string.title_not_connected);
        	  conected = false;
        	  item_connect.setVisible(true);
        	  item_disconnect.setVisible(false);
        	  showDialog(REQUEST_DISABLE_BT);
            break;
          }
          break;
        case MESSAGE_READ: // todo: implement receive data buffering
          byte[] readBuf = (byte[]) msg.obj;
          int data_length = msg.arg1;
          for(int x=0; x<data_length; x++){
        	  int raw = UByte(readBuf[x]);
        	  datos(raw);
          }
          break;
        case MESSAGE_DEVICE_NAME:
          // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Conectado a "
                        + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
          break;
        case MESSAGE_TOAST:
          Toast.makeText(getApplicationContext(), "",
                        Toast.LENGTH_SHORT).show();
          break;
        case SendEmail.STATE_NONE:
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Error al enviar Email.",
                    Toast.LENGTH_SHORT).show();
            break;
        case SendEmail.STATE_SEND:
            progressDialog.dismiss();
            break;
        case UPDATE_LCD:
           	  	lcd.setText(msg.obj.toString());
           	  	utils.setLargest(0);
				utils.setSecondlargest(0);
            break;
        }
      }
      // signed to unsigned
      private int UByte(byte b){
          if(b<0) // if negative
            return (int)( (b&0x7F) + 128 );
          else
            return (int)b;
        }
    };

		 
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	   	switch (requestCode) {
	   		case REQUEST_CONNECT_DEVICE:
	        	// When DeviceListActivity returns with a device to connect
	            if (resultCode == Activity.RESULT_OK) {
	                // Get the device MAC address
	                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
	                // Get the BLuetoothDevice object
	                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
	                // Attempt to connect to the device
	                mRfcommClient.connect(device);
	            }
	            break;
	        case REQUEST_ENABLE_BT:
	            // When the request to enable Bluetooth returns
	            if (resultCode == Activity.RESULT_OK) {
	            	// Bluetooth is now enabled, so set up the oscilloscope
	            } else {
		                // User did not enable Bluetooth or an error occured
	            	Toast.makeText(this, "No habilitado", Toast.LENGTH_SHORT).show();
	            	finish();
	            	//showDialog(REQUEST_DISABLE_BT);
	            }
	            break;
	        case REQUEST_CONFIGURACION:
	        	cargarOpciones();
	            break;
	   	}
	}
	
	/**
	 * Creacion del menu dependiendo del tipo de opcion seleccionada
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_deportista, menu);
        item_connect = menu.findItem(R.id.it_conectar);
        item_disconnect =  menu.findItem(R.id.it_desconectar);
        item_email = menu.findItem(R.id.it_email);
        item_sms = menu.findItem(R.id.it_sms);
        item_grabar = menu.findItem(R.id.it_grabar);
        cargarOpciones();
        return true;
    }

	/**
	 * Seleccion de opcion del menu
	 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_config:     
            	launchActividadConfiguracion();
                break;
            case R.id.it_conectar:
            	item.setVisible(false);
            	item_disconnect.setVisible(true);
            	BTConnect();
                break;
            case R.id.it_desconectar:
            	item.setVisible(false);
            	BTDisconnect();
            	item_connect.setVisible(true);
                break;
            case R.id.it_email:
            	if(!conected)
            	{
            		enviarEmail();
            	}
            	else
            	{
            		Toast.makeText(this, "El sistema no debe estar en funcionamiento para enviar el email.", Toast.LENGTH_SHORT).show();
            	}
            	//finish();
                break;
            case R.id.it_demostracion:
            	demostracion();
            	break;
            case R.id.it_sms:
            	if(conected)
            	{
            		sendSMS("Atleta");
            	}
            	else
            	{
            		Toast.makeText(this, "El sistema no esta en funcionamiento. Conectar para enviar SMS", Toast.LENGTH_SHORT).show();
            	}
            	break;
            case R.id.it_about:     
            	Toast.makeText(this, "You pressed the Acerca De!", Toast.LENGTH_LONG).show();
                break;
            case R.id.it_exit: 
            	Toast.makeText(this, "Exit!", Toast.LENGTH_LONG).show();
            	finish();
                break;
            case R.id.it_grabar: 
            	if(!bReady)
	    		{
	    			bReady = true;
	    			this.monitorView.setAlmacenar(true);
	    			this.monitorView.setTotalDatos(configuracion.getTiempo_grabacion());
	    			this.monitorView.setTiempo(0);
	    			this.monitorView.setCont(0);
	    		}else{
	    			bReady = false;
	    			this.monitorView.setAlmacenar(false);
	    			this.monitorView.setTotalDatos(0);
	    			this.monitorView.setTiempo(0);
	    			this.monitorView.setCont(0);
	    	    }
                break;
        }
        return true;
    }
    
    
	
    public void init()
	 {
    	procesoDatos = new ProcesoDatos();
    	configuracion = new org.monitor.config.Configuracion();
    	prefs = getSharedPreferences("monitorConfig", Context.MODE_PRIVATE);
    	configuracion.obtenerConfiguracion(prefs);
	    lcdFont = Typeface.createFromAsset(getAssets(), "font/lcd.ttf");
	    lcd.setTypeface(this.lcdFont);
	    lcd.setText("000");
	    ((TextView) findViewById(R.id.lb_cardiaco)).setTypeface(this.lcdFont);
	  }
    
    private void demostracion()
	{
		if(!bReady)
		{
			bReady = true;
			if(demoThread==null){
				demoThread = new DemoThread(monitorView, mHandler);
				demoThread.setRunning(true);
				demoThread.start();
			}
			else
			{
				demoThread.setRunning(true);
			}
		}
		else
		{
			bReady = false;
			demoThread.setRunning(false);
			utils.iniData();
			demoThread.setUtils(utils);
		}
	}
    
    /**
	 * Grafica los datos que se obtienen de los datos ramdomicos
	 * @param val
	 */
	public void datos(int dato)
	{
		 if(utils.validarDatos(dato, bReady))
		 {
			 if(utils.getLatido() <= 129 && utils.getLatido() >= 41)
			 {
				 lcd.setText(""+utils.getLatido());
				 this.monitorView.setLatido(utils.getLatido());
			 }
			 utils.setLargest(0);
			 utils.setSecondlargest(0);
		 }
	}
	
	/**
     * Lanza la actividad de Configuracion
     */
    protected void launchActividadConfiguracion() {
        Intent i = new Intent(this, org.monitor.config.Configuracion.class);
        Bundle bundle = new Bundle();
    	//Add the parameters to bundle as
    	//bundle.putString("app",app);    	
    	//Add this bundle to the intent
    	i.putExtras(bundle);
        startActivityForResult(i,REQUEST_CONFIGURACION);
    }
   
    /**
     * Metodos que envia el sms, en caso de un taquicardia.
     */
    public static final String ACTION_SMS_SENT = "com.example.android.apis.os.SMS_SENT_ACTION";

    public void sendSMS(String tipo)
    {
    	
    	prefs = getSharedPreferences("monitorConfig", Context.MODE_PRIVATE);
    	configuracion.obtenerConfiguracion(prefs);
        String phoneNumber = configuracion.getTelefono_medico();
        String diagnostico ="";
        String msg = configuracion.getNombre_paciente() +" "+configuracion.getApellido_paciente()+
        		"\n Edad: " + configuracion.getEdad_paciente();
        		
        if(tipo.equals("Atleta"))
        {
        	msg+="\n FC = " + utils.getLatido();
        	diagnostico = procesoDatos.evaluaFrecuenciaActual(utils.getLatido());
        }
        else
        {
        	diagnostico = "";
        	msg+="\n FC = " + utils.getLatido();
        }
        		
        msg+="\n FCM = " + (220 - configuracion.getEdad_paciente())+"\n "+diagnostico;
        
        if (phoneNumber.length() == 9 && msg.length()>0)
        {
            SmsManager sms = SmsManager.getDefault();

            List<String> messages = sms.divideMessage(msg);

            for (String message : messages) {
                sms.sendTextMessage(phoneNumber, null, message, PendingIntent.getBroadcast(
                        MonitorActivity.this, 0, new Intent(ACTION_SMS_SENT), 0), null);
            }
            
         // Register broadcast receivers for SMS sent and delivered intents
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String message = null;
                    boolean error = true;
                    switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        message = "Message sent!";
                        Toast.makeText(getBaseContext(),message,Toast.LENGTH_SHORT).show();
                        error = false;
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        message = "Error.";
                        Toast.makeText(getBaseContext(),message,Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        message = "Error: No service.";
                        Toast.makeText(getBaseContext(),message,Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        message = "Error: Null PDU.";
                        Toast.makeText(getBaseContext(),message,Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        message = "Error: Radio off.";
                        Toast.makeText(getBaseContext(),message,Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }, new IntentFilter(ACTION_SMS_SENT));

        }
        else
        {
            Toast.makeText(getBaseContext(),"Todos los campos son requeridos",Toast.LENGTH_SHORT).show();
        }

    }
    
    public void enviarEmail()
    {
    	progressDialog = ProgressDialog.show(MonitorActivity.this,
                "Email", "Enviando Email...");
    	prefs = getSharedPreferences("monitorConfig", Context.MODE_PRIVATE);
    	configuracion.obtenerConfiguracion(prefs);
    	String ruta= Environment.getExternalStorageDirectory().getAbsolutePath()+"/data.zip";
    	final SendEmail sendEmail = new SendEmail(configuracion.getUsuario(), configuracion.getPassword(), mHandler);
    	String [] email = {configuracion.getEmail_medico()};
    	try {
    		sendEmail.set_body("\n\n Nombres: "+configuracion.getNombre_paciente()+" "+configuracion.getApellido_paciente()+"\n Telèfono: "+configuracion.getTelefono_paciente()+"\n Direcciòn: "+configuracion.getDireccion_paciente());
    		sendEmail.set_from(configuracion.getEmail_paciente());
    		sendEmail.set_subject("Datos");
    		sendEmail.set_to(email);
    		sendEmail.addAttachment(ruta);
    		new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
						sendEmail.send();
					} catch (Exception e) {
						mHandler.sendEmptyMessage(SendEmail.STATE_NONE);
						e.printStackTrace();
					}
                }
            }).start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			progressDialog.dismiss();
			e.printStackTrace();
			Toast.makeText(getBaseContext(),"Error al enviar Email.",Toast.LENGTH_SHORT).show();
		}
    }
    
    private void cargarOpciones()
    {
    	configuracion.obtenerConfiguracion(prefs);
    	if(conected)
        {
        	item_disconnect.setVisible(true);
        }
        else
        {
        	item_disconnect.setVisible(false);
        }
        
        if(!configuracion.getTipo().equals("Atleta"))
        {
        	item_email.setVisible(false);
        	item_sms.setVisible(false);
        	item_grabar.setVisible(false);
        }
        else
        {
        	item_email.setVisible(true);
        	item_sms.setVisible(true);
        	item_grabar.setVisible(true);
        }
    }

	public boolean isbReady() {
		return bReady;
	}

	public void setbReady(boolean bReady) {
		this.bReady = bReady;
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        AlertDialog.Builder builder;
        switch(id) {
        case REQUEST_DISABLE_BT:
        	playSound();
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Dispositvo Bluetooth, inhabilitado!!")
            .setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                   ///Aqui el proceso
                }
            });
            dialog = builder.create();
            break;
        default:
            dialog = null;
        }
        return dialog;

    }
	
	@Override
    public void onBackPressed() {

    }
	
    private void playSound()
    {
    	Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    	Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
    	ringtone.play();
    }
    
}