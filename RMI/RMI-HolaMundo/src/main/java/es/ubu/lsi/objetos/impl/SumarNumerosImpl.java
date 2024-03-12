package es.ubu.lsi.objetos.impl;

import es.ubu.lsi.objetos.SumarNumeros;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SumarNumerosImpl extends UnicastRemoteObject implements SumarNumeros {

    public SumarNumerosImpl() throws RemoteException {
        super();
    }
    @Override
    public int sumarNumeros(int a, int b) throws RemoteException {
        return a+b ;
    }
}
