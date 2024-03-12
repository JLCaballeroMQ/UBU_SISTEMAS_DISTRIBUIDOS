package es.ubu.lsi.objetos;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SumarNumeros extends Remote {
   int sumarNumeros(int a, int b) throws RemoteException;
}
