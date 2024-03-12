package es.ubu.lsi.objetos;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DecirAdios extends Remote {
   String decirAdios(String nombre) throws RemoteException;
}
