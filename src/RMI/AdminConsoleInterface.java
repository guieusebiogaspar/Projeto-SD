package RMI;

import java.io.IOException;
import java.rmi.*;

public interface AdminConsoleInterface extends Remote{
        //olaAdmin
        //no ola server chamar ola admin
        public void olaServidor() throws RemoteException;
        public void adeusServidor() throws RemoteException;
}
