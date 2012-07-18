package org.monitor.config;

import org.monitor.proceso.ProcesoDatos;

import android.os.Handler;

public class Utils
{
	public final static int WIDTH = 300;
	public final static int HEIGHT = 234;
	public static final int MAX_SAMPLES = 2000;
	private static int[] ch1_data = new int[MAX_SAMPLES];
    private static float[] ch2_data = new float[MAX_SAMPLES];
    public static int ch1_pos = 117; //HEIGHT/2;
    public static float ch2_pos = 0f; //HEIGHT/2;
    public static float incremento_tiempo = 0.15f;
    private int dataIndex=0;
    private float tiempo = 0f;
    private float tiempoDato = 0f;
    private int secondlargest = 0;
    private int largest = 0;
    private double time1 = 0;
    private double time2 = 0;
    private int muestra1 = 0;
    private int muestra2 = 0;
    private boolean txt_latido= false;
    private int latido = 0;
  	boolean primerDato =  true;
  	private boolean almacenar = false;
	private int totalDatos = 1;
	private int cont = 0;
	private StringBuffer datosIngresados;
	private ProcesoDatos procesoDatos;
	private Handler handler;
	public static final int FINISH = 12;
	
  	private static int[] data_almacenamiento = new int[MAX_SAMPLES];
	
    public Utils()
    {
    	
    }
    
    public void iniData()
    {
    	datosIngresados = new StringBuffer();
    	procesoDatos = new ProcesoDatos();
    	
    	ch2_pos=0f;
    	for(int x=0; x<MAX_SAMPLES; x++){
    		ch1_data[x] = ch1_pos;
    		ch2_data[x] = ch2_pos;
    		ch2_pos+=incremento_tiempo;
    	}
    }
    
    /**
	 * Grafica los datos que se obtienen de los datos ramdomicos
	 * @param val
	 */
	public boolean validarDatos(int dato, boolean run_data)
	{
		 try {
	      	int decimal = dato;

	      	if(decimal==255)
	      	{	      		
	      		if(primerDato)
	      		{
	      			time1 = tiempo;
	      			primerDato = false;
	      		}
	      		else
	      		{
	      			time2 = tiempo;
	      			primerDato = true;
	      		}
	      	}
	      	else if(dataIndex>=MAX_SAMPLES)
	    	{
	      		txt_latido = true;
	      		double frecuencia = 0;
	      		double periodo = 0;
	      		if(time1>=time2)
	      		{
	      			//System.out.println("t1 ="+time1+" - "+time2+" = "+(time1-time2));
	      			//System.out.println("M1 ="+muestra1+" - "+muestra2+" = "+(muestra1-muestra2));
	      			periodo = (time1-time2)*2;
	      			periodo = periodo/300;
	      			frecuencia = 1/periodo;
	      			frecuencia = frecuencia*60;
	      			//System.out.println("P ="+periodo);
	      			//System.out.println("F ="+frecuencia);
	      		}
	      		else
	      		{
	      			//System.out.println("t2 ="+time2+" - "+time1+""+(time2-time1));
	      			//System.out.println("M2 ="+muestra2+" - "+muestra1+" = "+(muestra2-muestra1));
	      			periodo = (time2-time1)*2;
	      			periodo = periodo/300;
	      			frecuencia = 1/periodo;
	      			frecuencia = frecuencia*60;
	      			//System.out.println("P ="+periodo);
	      			
	      		}
	      		Double FxM = frecuencia;
	      		if(FxM.intValue() <= 129 && FxM.intValue() >= 41)
				{
	      			latido = FxM.intValue();
				}
	      		
	      		//latido = (largest+secondlargest)/2;
	      		System.out.println("Latido ="+latido);
	    		dataIndex = 0;
	    		tiempo = 0f;
	    		largest = 0;
	    		secondlargest = 0;
	    		muestra1 = 0;
	    		muestra2 = 0;
	    		
	    		return true;
	    	}
	    	else if( dataIndex<(MAX_SAMPLES) )
	    	{
	    		txt_latido = false;
	    		obtenerPicos(decimal, tiempo, dataIndex);
	    		tiempo= tiempo+0.15f;
	    		dataIndex++;
	    		
	    		if(!run_data)
	    		{
		    		ch1_data[dataIndex] = Utils.HEIGHT-decimal+1;
		    		ch2_data[dataIndex] = tiempo;
	    		}
	    		else
	    			updateDatos(decimal);
	    		
	    		return false;// odd data
	    	}
	      	
	    	return false;
	    }
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    	return false;
	    }
	}
	
	private void obtenerPicos(int dato, double t, int index)
	{       
		if(primerDato)
		{
			if (largest < dato) {
				largest = dato;
				//time1 = t;
				muestra1 = index;
			}
		}
		else{
			if (secondlargest < dato) {
				secondlargest = dato;
				//time2 = t;
				muestra2 = index;
			}
			
		}
        
	}
	
	public void updateDatos(int dato)
    {
    	if(almacenar && cont<totalDatos)
		{
    		String out = ""+new Float(tiempoDato).intValue()+","+dato+","+latido+"\n";
    		datosIngresados.append(out);
    		tiempoDato+=1;
    		cont++;
		}
		else if(almacenar && cont>=totalDatos)
		{
			procesoDatos.almacenarDatos(datosIngresados);
			almacenar = false;
			totalDatos = 0;
			tiempoDato=0;
			cont = 0;
			handler.sendEmptyMessage(FINISH);
		}
    }
	
    public static int[] getCh1_data() {
		return ch1_data;
	}
	public static void setCh1_data(int[] ch1_data) {
		Utils.ch1_data = ch1_data;
	}
	public static float[] getCh2_data() {
		return ch2_data;
	}
	public static void setCh2_data(float[] ch2_data) {
		Utils.ch2_data = ch2_data;
	}
	public static int getMaxSamples() {
		return MAX_SAMPLES;
	}

	public int getSecondlargest() {
		return secondlargest;
	}

	public void setSecondlargest(int secondlargest) {
		this.secondlargest = secondlargest;
	}

	public int getLargest() {
		return largest;
	}

	public void setLargest(int largest) {
		this.largest = largest;
	}
	

	public static int[] getData_almacenamiento() {
		return data_almacenamiento;
	}

	public static void setData_almacenamiento(int[] data_almacenamiento) {
		Utils.data_almacenamiento = data_almacenamiento;
	}

	public boolean isTxt_latido() {
		return txt_latido;
	}

	public void setTxt_latido(boolean txt_latido) {
		this.txt_latido = txt_latido;
	}

	public int getLatido() {
		return latido;
	}

	public void setLatido(int latido) {
		this.latido = latido;
	}
	
	public boolean isAlmacenar() {
		return almacenar;
	}

	public void setAlmacenar(boolean almacenar) {
		this.almacenar = almacenar;
	}
	
	public int getTotalDatos() {
		return totalDatos;
	}

	public void setTotalDatos(int totalDatos) {
		this.totalDatos = totalDatos*60*1000;
	}

	public int getCont() {
		return cont;
	}

	public void setCont(int cont) {
		this.cont = cont;
	}
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public float getTiempoDato() {
		return tiempoDato;
	}

	public void setTiempoDato(float tiempoDato) {
		this.tiempoDato = tiempoDato;
	}
	

}
