package es.ubu.lsi.objetos.impl;

import es.ubu.lsi.objetos.DecirAdios;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DecirAdiosImpl extends UnicastRemoteObject implements DecirAdios {

    public DecirAdiosImpl() throws RemoteException {
        super();
    }
    @Override
    public String decirAdios(String nombre) throws RemoteException {
        return "Adios " + nombre + "!";
    }
}
