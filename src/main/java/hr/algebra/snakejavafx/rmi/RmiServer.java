package hr.algebra.snakejavafx.rmi;

import hr.algebra.snakejavafx.jndi.ConfigurationKey;
import hr.algebra.snakejavafx.jndi.ConfigurationReader;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer {
    private static final int RANDOM_PORT_HINT = 0;

    public static void main(String[] args){
        try {
            Registry registry = LocateRegistry.createRegistry(ConfigurationReader.getIntegerValueForKey(
                    ConfigurationKey.RMI_PORT));
            ChatRemoteService chatRemoteService = new ChatRemoteServiceImpl();
            ChatRemoteService skeleton = (ChatRemoteService) UnicastRemoteObject.exportObject(chatRemoteService,
                    RANDOM_PORT_HINT );
            registry.rebind(ChatRemoteService.REMOTE_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

}
