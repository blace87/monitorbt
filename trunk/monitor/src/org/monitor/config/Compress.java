package org.monitor.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.os.Environment;

public class Compress {

	private static final int BUFFER = 2048; 
	 
	private String _file; 
	private String _zipFile; 
	String ruta= Environment.getExternalStorageDirectory().getAbsolutePath(); 
	  
	public Compress() { 
		_file = ruta+"/data.txt"; 
	    _zipFile = ruta+"/data.zip"; 
	} 
	 
	public void zip() 
	{ 
		  try  { 
			  
			  BufferedInputStream origin = null; 
			  FileOutputStream dest = new FileOutputStream(_zipFile); 
	 
			  ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 
	 
			  byte data[] = new byte[BUFFER]; 
			  FileInputStream fi = new FileInputStream(_file); 
			  origin = new BufferedInputStream(fi, BUFFER); 
			  ZipEntry entry = new ZipEntry(_file.substring(_file.lastIndexOf("/") + 1)); 
			  out.putNextEntry(entry); 
			  int count; 
			  while ((count = origin.read(data, 0, BUFFER)) != -1) { 
				  out.write(data, 0, count); 
			  } 
			  origin.close(); 
			  out.close(); 
		  } catch(Exception e) { 
			  e.printStackTrace(); 
		  } 
	 
	} 
}
