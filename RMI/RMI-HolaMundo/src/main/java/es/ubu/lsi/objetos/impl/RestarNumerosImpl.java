package es.ubu.lsi.objetos.impl;

import es.ubu.lsi.objetos.RestarNumeros;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RestarNumerosImpl extends UnicastRemoteObject implements RestarNumeros {

    public RestarNumerosImpl() throws RemoteException {
        super();
    }
    @Override
    public int restarNumeros(int a, int b) throws RemoteException {
        return a-b ;
    }
}
