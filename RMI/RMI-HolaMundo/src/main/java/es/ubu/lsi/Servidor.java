package es.ubu.lsi;
	
import es.ubu.lsi.objetos.DecirAdios;
import es.ubu.lsi.objetos.RestarNumeros;
import es.ubu.lsi.objetos.SumarNumeros;
import es.ubu.lsi.objetos.impl.DecirAdiosImpl;
import es.ubu.lsi.objetos.impl.RestarNumerosImpl;
import es.ubu.lsi.objetos.impl.SumarNumerosImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Servidor remoto.
 *
 */	
public class Servidor implements HolaMundo {
	
	/**
	 * {@inheritDoc}.
	 *
	 * @return {@inheritDoc}
	 */
		public String decirHola() {
		return "Hola mundo!";
    }
	
	/**
	 * Método raíz.
	 *
	 * @param args argumentos
	 */
    public static void main(String args[]) {
	
		try {
		    Servidor obj = new Servidor();
		    
		    // si no hereda de UnicastRemoteObject es necesario exportar
	    	HolaMundo stub = (HolaMundo) UnicastRemoteObject.exportObject(obj, 0);

		    // Liga el resguardo de objeto remoto en el registro
	    	Registry registro = LocateRegistry.getRegistry();
	    	registro.bind("Hola", stub);

			//Aca van los nuevos objetos

			DecirAdios decirAdios = new DecirAdiosImpl();
			registro.bind("DecirAdios", decirAdios);

			SumarNumeros sumarNumeros = new SumarNumerosImpl();
			registro.bind("SumarNumeros", sumarNumeros);

			RestarNumeros restarNumeros = new RestarNumerosImpl();
			registro.bind("RestarNumeros", restarNumeros);
	
	    	System.out.println("Servidor preparado");
		}
		catch (Exception e) {
		    System.err.println("Excepción de servidor: " + e.toString());
		}
    } // main
    
} // Servidor