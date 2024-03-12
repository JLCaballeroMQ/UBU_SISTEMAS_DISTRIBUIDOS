package es.ubu.lsi;

import es.ubu.lsi.objetos.DecirAdios;
import es.ubu.lsi.objetos.RestarNumeros;
import es.ubu.lsi.objetos.SumarNumeros;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Cliente remoto.
 */
public class Cliente {

	/**
	 * Constructor oculto,
	 */
    private Cliente() {}


	/**
	 * Método raíz.
	 *
	 * @param args host con el registro
	 */
    public static void main(String[] args) {

		String host = (args.length < 1) ? null : args[0];
		try {
		   Registry registry = LocateRegistry.getRegistry(host);
		   // Resuelve el objeto remoto (la referencia a...)
	 	   HolaMundo stub = (HolaMundo) registry.lookup("Hola");
	 	   String respuesta = stub.decirHola();
	       System.out.println("Respuesta del servidor remoto: " + respuesta);

		   // Aca consumimos los nuevos objetos

		   DecirAdios decirAdiosStub = (DecirAdios) registry.lookup("DecirAdios");
		   System.out.println(decirAdiosStub.decirAdios("Mundo"));

		   SumarNumeros sumarNumerosStub = (SumarNumeros) registry.lookup("SumarNumeros");
		   System.out.println(sumarNumerosStub.sumarNumeros(4,10));

		   RestarNumeros restarNumerosStub = (RestarNumeros) registry.lookup("RestarNumeros");
		   System.out.println(restarNumerosStub.restarNumeros(10,4));

		} 
		catch (Exception e) {
	    	System.err.println("Excepción en cliente: " + e.toString());
		} // try
		
    } // main
    
} // Cliente