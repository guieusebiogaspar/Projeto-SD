package RMI;

import java.rmi.*;

public interface AdminConsoleInterface extends Remote{
        void olaServidor() throws RemoteException;
        void adeusServidor() throws RemoteException;
        void printOnAdmin(String departamento, int terminais) throws RemoteException;
}
