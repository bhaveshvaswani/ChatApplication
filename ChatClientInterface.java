package chat;

import java.rmi.*;
import java.util.*;

public interface ChatClientInterface extends Remote 
{
	public void takeMsg(String msg) throws RemoteException;
	public void takeClientList(Vector<String>client) throws RemoteException;
	public void pm(String hisName, ChatClientInterface hisRef, String hisMsg) throws RemoteException;
}
