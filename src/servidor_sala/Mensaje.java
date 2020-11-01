package servidor_sala;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Mensaje {
	 private String mensaje;
	    private Date momento;
	    private String emisor;
	    private String fechaText;
	    private String receptorPrivado;
	    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    
	    public Mensaje(String mensaje,String emisor, String receptorPrivado){
	    	this.momento = new Date();
	    	this.fechaText= formatter.format(this.momento);
	    	this.mensaje = mensaje;
	    	this.emisor = emisor;
	    }
	    
	    public String getMensaje(){
	        return this.mensaje;
	    }
	    
	    public String getEmisor(){
	        return this.emisor;
	    }
	    
	    public String getFechaText(){
	        return this.fechaText;
	    }
}
