package chat;

import java.rmi.*;
import java.util.*;

public interface ChatServerInterface extends Remote 
{
	public void login(String name,ChatClientInterface ref) throws RemoteException;
	public void takeMsg(String msg) throws RemoteException;
	public void logout(String name) throws RemoteException;
	public ChatClientInterface giveRef(String hisName) throws RemoteException;
}
