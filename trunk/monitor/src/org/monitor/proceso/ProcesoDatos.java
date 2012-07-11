package org.monitor.proceso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.monitor.config.Compress;

import android.os.Environment;

public class ProcesoDatos {
	
	public void procesoDatosFrecuencia(int frecuencia)
	{
		if(frecuencia < 60)
		{
			
		}
		else if(frecuencia > 100)
		{
			
		}
		else
		{
			
		}
	}
	
	public String evaluaFrecuenciaActual(int frecuencia)
	{
		String diagnostico = "";
		if(frecuencia < 60)
		{
			diagnostico = "Bradicardia";
		}
		else if(frecuencia > 100)
		{
			diagnostico = "Taquicardia";
		}
		else
		{
			diagnostico = "Normal";
		}
		
		return diagnostico;
	}
	
	/**
     * Comienza el almacenamiento de la informacion receptada por el dispositivo.
     * 0 inicio de almacenamiento
     * 1 graba datos en archivo
     * 2 no hace nada
     */
    public void almacenarDatos(StringBuffer data)
    {
    	FileOutputStream fOut = null;
    	OutputStreamWriter osw = null;
			try{
				File dir = Environment.getExternalStorageDirectory();
				File txt = new File(dir, "data.txt");
				fOut = new FileOutputStream(txt);      
	            osw = new OutputStreamWriter(fOut);
	            osw.write(data.toString());
	            osw.flush();
	            data = new StringBuffer();
	            Compress compress = new Compress();
	            compress.zip();
	            txt.deleteOnExit();
	        }
	        catch (Exception e) 
	        {      
	        	e.printStackTrace();
	        }
	        finally {
	        	try {
	        		osw.close();
	        		fOut.close();
	            } catch (IOException e) {
	            	e.printStackTrace();
	            }
	        }
    }

}
