package secondTry;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class Chat extends JFrame{

	private String name;
	private JTextArea jta;
	private JTextField jtf;
	private JButton jb;
	private JScrollPane jsp1,jsp2;
	private JPanel jp;
	private PrintWriter out =null;
	private BufferedReader br =null;
	private Socket s =null;
	private DefaultListModel model;
	private JList list;
	
	public Chat(String name)
	{
		this.name=name;
		jta = new JTextArea();
		jtf = new JTextField(20);
		jb = new JButton("发送");
		jb.addActionListener(new buttonClick());
		//文本框添加快捷键
		jtf.addKeyListener(new KeyClick());	
		
		this.addWindowListener(new WindowAdapter() {
				
			public void windowClosing(WindowEvent e)
			{
				out.println("disconnect:");
				System.exit(0);
			}
		});
		
		jsp1 = new JScrollPane(jta);
		jp = new JPanel();
		model = new DefaultListModel();
		list = new JList(model);
		list.setFixedCellWidth(150);
		jsp2 = new JScrollPane(list);
		
		jp.add(jtf);jp.add(jb);
		add(jsp1);
		add(jsp2,BorderLayout.WEST);
		add(jp,BorderLayout.SOUTH);
		connect();
		setBounds(100,100,500,500);
		setVisible(true);
		setTitle("当前用户是： "+name);
	}
	//发送消息
	public void send()
	{
		String msg = jtf.getText();
		if (msg==null||msg.trim().equals(""))
		{
			JOptionPane.showMessageDialog(null,"输入内容不能为空");
			return;
		}
		Object[] vals = list.getSelectedValues();
		String start = "to:";
		if (vals.length>=1)
		{
			for (Object user:vals)
			{
				start+=user.toString()+",";
			}
			out.println(start+":end"+msg);
		}
		else {
			for (Object user:vals)
			{
				start+=user.toString()+",";
			}
			out.println(start+"all"+":end"+msg);
		}
		jtf.setText("");
	}
	
	public void connect()
	{
		try {
			s= new Socket("localhost",1236);
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(s.getOutputStream(),true);
			
			out.println(name);
			
			new Thread(new ReceiveThread()).start();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class ReceiveThread implements Runnable
	{
		public void run() {
			
			while (true)
			{
				String str="test";
				try {
					while (true)
					{
						str = br.readLine();
						if (str.startsWith("connect:"))
						{
							String usergroup = str.substring("connect:".length());
							
							String[] userlist = usergroup.split(",");
							model.removeAllElements();
							model.addElement("所有人");
							for (String user:userlist)
							{
								model.addElement(user);
							}
						}else{
							jta.append(str+"\n");
						}
					}
				}
				catch (SocketException se){se.printStackTrace();}
				catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					try{
						if (s!=null) s.close();
						if (br!=null) br.close();
					}
					catch (IOException e){e.printStackTrace();}
				}
			}
		}
	}
	//文本框快捷
	private class KeyClick extends KeyAdapter
	{

		public void keyPressed(KeyEvent e) 
		{
			if (e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				send();
			}
		}
	}
//按钮快捷
	private class buttonClick implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if (e.getSource()==jb)
			{
				send();
			}
		}
		
	}
}
