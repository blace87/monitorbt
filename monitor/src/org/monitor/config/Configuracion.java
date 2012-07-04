package org.monitor.config;

import org.monitorView.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class Configuracion extends Activity implements OnClickListener {

	/**
	 * Variables de Configuración
	 */
	private Button grabar;
	private Button cancelar;
	private Spinner spinner;
	private RadioGroup radioGroup;
    private String nombre_paciente;
    private String apellido_paciente;
    private String direccion_paciente;
    private String telefono_paciente;
    private String telefono_medico;
    private String email_paciente;
    private String email_medico;
    private String tipo;
    private int edad_paciente;
    private int tiempo_grabacion;
    private String usuario;
    private String password;
	
	
	/**
	 * Parametros de la aplicaion
	 */
	private ArrayAdapter<CharSequence> adapter;	
	private String minutos = ""; 
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.configuracion);
	    setResult(Activity.RESULT_OK);
	    //First Extract the bundle from intent
	    //Bundle bundle = getIntent().getExtras();
	    //Next extract the values using the key as
	    //app = bundle.getString("app");
	    
	    grabar = (Button) this.findViewById(R.id.bt_guardar_cfg);
	    cancelar = (Button) this.findViewById(R.id.bt_cancelar_cfg);
	    spinner = (Spinner) this.findViewById(R.id.txt_tiempo);
	    radioGroup = (RadioGroup)this.findViewById(R.id.rg_tipo);

	    grabar.setOnClickListener(this);
	    cancelar.setOnClickListener(this);
	    // TODO Auto-generated method stub
	    
	    adapter = ArrayAdapter.createFromResource(this, R.array.min_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
            	minutos = arg0.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> arg0) { }
       });
	    
	    adapter = ArrayAdapter.createFromResource(this, R.array.min_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
            	minutos = arg0.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> arg0) { }
       });
	    
	    cargarConfiguracion();
	}
	
	
	 public void onClick(View v) {
		 int buttonID;
			buttonID = v.getId();
			switch (buttonID)
			{
		    	case R.id.bt_guardar_cfg :
		    		guardarConfiguracion();
		    		finishActivity(3);
		    		finish();
		    	break;
		    	case R.id.bt_cancelar_cfg :
		    		finish();
		    	break;
			}
	}

	 //cargar configuración de la aplicacion
	 public void cargarConfiguracion()
	 {   
		 String currentMin ="";
		 SharedPreferences prefs = getSharedPreferences("monitorConfig", Context.MODE_PRIVATE);    
		 ((EditText) this.findViewById(R.id.txt_nombre_medico)).setText(prefs.getString("nombre_medico", ""));
		 ((EditText) this.findViewById(R.id.txt_email)).setText(prefs.getString("email", "@email.com"));
		 ((EditText) this.findViewById(R.id.txt_phone)).setText(prefs.getString("telefono_medico", "0"));
		 ((EditText) this.findViewById(R.id.txt_nombre_paciente)).setText(prefs.getString("nombre_paciente", ""));
		 ((EditText) this.findViewById(R.id.txt_apellido_paciente)).setText(prefs.getString("apellido_paciente", ""));
		 ((EditText) this.findViewById(R.id.txt_direccion_paciente)).setText(prefs.getString("direccion_paciente", ""));
		 ((EditText) this.findViewById(R.id.txt_telefono_paciente)).setText(prefs.getString("telefono_paciente", "0"));
		 ((EditText) this.findViewById(R.id.txt_email_paciente)).setText(prefs.getString("email_paciente", "@email.com"));
		 ((EditText) this.findViewById(R.id.txt_edad_paciente)).setText(prefs.getString("edad_paciente", "20"));
		 ((EditText) this.findViewById(R.id.txt_usuario)).setText(prefs.getString("user", ""));
		 ((EditText) this.findViewById(R.id.txt_pass)).setText(prefs.getString("pass", ""));
		 
		 currentMin = prefs.getString("minutos", "1");
		 
		 //Selecciona la opcio escogida.
		 for (int i=0; i < adapter.getCount();i++) {
			 if(adapter.getItem(i).toString().equals(currentMin))
			 {
				 spinner.setSelection(adapter.getPosition(currentMin));
				 minutos = currentMin;
			 }	
		 }
	       
		 //Selecciona la opcion escogida
		 String checkedRadioButton = prefs.getString("tipo", "Atleta").trim();
		 if (checkedRadioButton.equals("Atleta")) {
			 RadioButton id2 = (RadioButton) this.findViewById(R.id.radio1);
			 id2.setChecked(true);
		 }
		 else
		 {
			 RadioButton id1 = (RadioButton) this.findViewById(R.id.radio0);
			 id1.setChecked(true);
		 }	       
	 } 
	    
	 //guardar configuración Android SharedPreferences
	 public void guardarConfiguracion()
	 {
		 SharedPreferences prefs = getSharedPreferences("monitorConfig", Context.MODE_PRIVATE);       
		 SharedPreferences.Editor editor = prefs.edit();
		 editor.putString("nombre_medico", ((EditText) this.findViewById(R.id.txt_nombre_medico)).getText().toString());
		 editor.putString("email", ((EditText) this.findViewById(R.id.txt_email)).getText().toString());
		 editor.putString("telefono_medico", ((EditText) this.findViewById(R.id.txt_phone)).getText().toString());
		 editor.putString("nombre_paciente", ((EditText) this.findViewById(R.id.txt_nombre_paciente)).getText().toString());
		 editor.putString("apellido_paciente", ((EditText) this.findViewById(R.id.txt_apellido_paciente)).getText().toString());
		 editor.putString("direccion_paciente", ((EditText) this.findViewById(R.id.txt_direccion_paciente)).getText().toString());
		 editor.putString("telefono_paciente", ((EditText) this.findViewById(R.id.txt_telefono_paciente)).getText().toString());
		 editor.putString("email_paciente", ((EditText) this.findViewById(R.id.txt_email_paciente)).getText().toString());
		 editor.putString("edad_paciente", ((EditText) this.findViewById(R.id.txt_edad_paciente)).getText().toString());
		 editor.putString("pass", ((EditText) this.findViewById(R.id.txt_pass)).getText().toString());
		 editor.putString("user", ((EditText) this.findViewById(R.id.txt_usuario)).getText().toString());
		 editor.putString("minutos", minutos);
		 
		 int checkedRadioButton = radioGroup.getCheckedRadioButtonId();
		 switch (checkedRadioButton) {
		 	case R.id.radio0 : 
		 		editor.putString("tipo", "Paciente");
		 		break;
		 	case R.id.radio1 : 
		 		editor.putString("tipo", "Atleta");
		 		break;
		 }
	    	
		 editor.commit();
	 } 
	  
	 public void obtenerConfiguracion(SharedPreferences prefs)
	 {   
		 String currentMin ="";
		 //SharedPreferences prefs = getSharedPreferences("monitorConfig", modo);    
		 
		 nombre_paciente = prefs.getString("nombre_paciente", "");
		 apellido_paciente = prefs.getString("apellido_paciente", "");
		 direccion_paciente = prefs.getString("direccion_paciente", "");
		 telefono_paciente = prefs.getString("telefono_paciente", "0");
		 email_paciente = prefs.getString("email_paciente", "@email.com");
		 edad_paciente = new Integer(prefs.getString("edad_paciente", "20"));
		 telefono_medico = prefs.getString("telefono_medico", "0");
		 email_medico = prefs.getString("email", "");
		 tipo = prefs.getString("tipo", "Atleta");
		 currentMin = prefs.getString("minutos", "1");
		 tiempo_grabacion = new Integer(currentMin);
		 password = prefs.getString("pass", "1");
		 usuario = prefs.getString("user", "1");
	 }

	public String getApellido_paciente() {
		return apellido_paciente;
	}

	public void setApellido_paciente(String apellido_paciente) {
		this.apellido_paciente = apellido_paciente;
	}

	public String getDireccion_paciente() {
		return direccion_paciente;
	}

	public void setDireccion_paciente(String direccion_paciente) {
		this.direccion_paciente = direccion_paciente;
	}

	public String getTelefono_paciente() {
		return telefono_paciente;
	}

	public void setTelefono_paciente(String telefono_paciente) {
		this.telefono_paciente = telefono_paciente;
	}

	public String getEmail_paciente() {
		return email_paciente;
	}

	public void setEmail_paciente(String email_paciente) {
		this.email_paciente = email_paciente;
	}

	public int getEdad_paciente() {
		return edad_paciente;
	}

	public void setEdad_paciente(int edad_paciente) {
		this.edad_paciente = edad_paciente;
	}

	public int getTiempo_grabacion() {
		return tiempo_grabacion;
	}

	public void setTiempo_grabacion(int tiempo_grabacion) {
		this.tiempo_grabacion = tiempo_grabacion;
	}

	public String getNombre_paciente() {
		return nombre_paciente;
	}

	public void setNombre_paciente(String nombre_paciente) {
		this.nombre_paciente = nombre_paciente;
	}

	public String getTelefono_medico() {
		return telefono_medico;
	}

	public void setTelefono_medico(String telefono_medico) {
		this.telefono_medico = telefono_medico;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getEmail_medico() {
		return email_medico;
	}

	public void setEmail_medico(String email_medico) {
		this.email_medico = email_medico;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	 
	 
}
