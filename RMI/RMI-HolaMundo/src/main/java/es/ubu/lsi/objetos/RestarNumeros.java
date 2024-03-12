package es.ubu.lsi.objetos;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RestarNumeros extends Remote {
    int restarNumeros(int a, int b) throws RemoteException;
}
