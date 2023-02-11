package chat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
public class  RMIChatClient extends JFrame implements ActionListener,ChatClientInterface
{
    
	JTextArea   jta;
	JList       list;
	JTextField  jtf;
	JButton     jbtn;
	JScrollPane jsp1, jsp2;
	DefaultListModel model;
	ChatServerInterface csi;
	String myName;
	Hashtable<String,MiniWindow> ht;

	public RMIChatClient(String name)
	{
		super("ChatClient "+name);
		myName = name;
		ht = new Hashtable<String,MiniWindow>();
		setResizable(false);
		setSize(750,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		jta  = new JTextArea();
		jsp1 = new JScrollPane(jta);
		jsp1.setBounds(5,5,500,400);
		add(jsp1);
		
		model = new DefaultListModel();

		list  = new JList(model);
		jsp2 = new JScrollPane(list);
		jsp2.setBounds(510,5,200,400);
		add(jsp2);

		jtf = new JTextField();
		jtf.setBounds(5,410,500,25);
		add(jtf);

		jbtn = new JButton("Send");
		jbtn.setBounds(510,410,200,25);
		add(jbtn);
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				try
				{
					csi.logout(myName);
				}
				catch(Exception e1)
				{
					System.out.println(e1);
				}
			}
		});
		jtf.addActionListener(this);
		jbtn.addActionListener(this);
		list.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() ==2)
				{
					String hisName = (String)list.getSelectedValue();
					MiniWindow mw = ht.get(hisName);
					if(mw == null)
					{
						try
						{
							ChatClientInterface hisRef = csi.giveRef(hisName);
							mw = new MiniWindow(hisName,hisRef);
							ht.put(hisName,mw);
						}
						catch(Exception e1)
						{
							System.out.println(e1);
						}
					}
				}
			}
		});
		jta.setEditable(false);
		setVisible(true);
		try
		{
			csi = (ChatServerInterface)Naming.lookup("rmi://localhost:1099/Bhavesh");
			UnicastRemoteObject.exportObject(this);
			csi.login(myName, this);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	public void pm(String hisName, ChatClientInterface hisRef, String hisMsg)
	{
		MiniWindow mw = ht.get(hisName);
		if(mw == null)
		{
			mw = new MiniWindow(hisName,hisRef);
			ht.put(hisName,mw);
		}
		mw.jta.append(hisMsg+"\n");
	}
	public void actionPerformed(ActionEvent e)
	{
		String msg = jtf.getText();
		msg = myName + " : "+msg;
		try
		{
			csi.takeMsg(msg);
		}
		catch(Exception e1)
		{
			System.out.println(e1);
		}
		jtf.setText("");
	}
	public void takeMsg(String msg)
	{
		jta.append(msg + "\n");
	}
	public void takeClientList(Vector<String> clients)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				model.removeAllElements();
				Enumeration<String> en = clients.elements();
				while(en.hasMoreElements())
				{
					String s = en.nextElement();
					model.addElement(s);
				}
			}
		});
	}
	public class  MiniWindow extends JFrame implements ActionListener
	{
	   
		JTextArea   jta;	
		JTextField  jtf;
		JButton     jbtn;
		JScrollPane jsp1;
		String hisName;
		ChatClientInterface hisRef;
		public MiniWindow(String name, ChatClientInterface ref)
		{
			super("From "+myName+" to "+name);
			hisName = name;
			hisRef = ref;
			setResizable(false);
			setSize(340,280);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setLayout(null);

			jta  = new JTextArea();
			jsp1 = new JScrollPane(jta);
			jsp1.setBounds(5,5,325,200);
			add(jsp1);		

			jtf = new JTextField();
			jtf.setBounds(5,210,235,25);
			add(jtf);

			jbtn = new JButton("Send");
			jbtn.setBounds(245,210,85,25);
			add(jbtn);

			jtf.addActionListener(this);
			jbtn.addActionListener(this);
			addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					ht.remove(hisName);
				}
			});
		
			jta.setEditable(false);
			setVisible(true);
		}
		public void actionPerformed(ActionEvent e)
		{
			String myMsg = jtf.getText();
			myMsg = myName +" : "+myMsg;
			try
			{
				hisRef.pm(myName,RMIChatClient.this,myMsg);
			}
			catch(Exception e1)
			{
				System.out.println(e1);
			}
			jta.append(myMsg+"\n");
			jtf.setText("");
		}
	}
	public static void main(String[] args) 
	{
		RMIChatClient app = new RMIChatClient(args[0]);
	}
}