package secondTry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Server {
	//Map不允许key相同
	private Map<String,ServerThread> cs;
	
	public void startup()
	{
		try {
			ServerSocket ss = new ServerSocket(1236);
			cs = new HashMap<String, ServerThread>();
			while(true)
			{
				Socket s = ss.accept();
				
				new Thread(new ServerThread(s)).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server().startup();
	}
	
	
	class ServerThread implements Runnable
	{
		BufferedReader br = null;
		PrintWriter out = null;
		Socket s = null;
		String name;
		
		public ServerThread(Socket s) throws IOException
		{
			this.s = s;
			br= new BufferedReader(new InputStreamReader(s.getInputStream()));
			out= new PrintWriter(s.getOutputStream(),true);
			name = br.readLine();
			name+="["+s.getInetAddress()+"/"+"]";
			cs.put(name,this);
			send(name+"上线了");
			//获取用户List
			sendUserList();
		}
		//发送消息
		public void send(String msg)
		{
			Set<String> s = cs.keySet();
			for (String name:s)
			{
				cs.get(name).out.println(msg);
			}
		}
		
		public void sendUserList()
		{
			Set<String> s = cs.keySet();
			String user="connect:";
			for (String users:s)
			{
				user+=users+",";
			}
			//更新用户列表
			for (String name:s)
			{
				cs.get(name).out.println(user);
			}
		}
		//提取私聊用户
		public String userByMsg(String msg)
		{
			String str=null;
			try {
				str = msg.substring("to:".length(),msg.indexOf(":end"));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return str;
		}
		//私聊
		public void sendPrivateuser(String user,String msg)
		{
			String users[] = user.split(",");
			for (String u:users)
			{
				cs.get(u).out.println(name+": "+msg+"(私聊)");
			}
		}
		//截取聊天信息
		public String sendmsg(String msg)
		{
			String str = msg.substring(msg.indexOf(":end")+":end".length());
			return str;
		}
		public void run() 
		{
			try 
			{
				while(true)
				{
					String msg = br.readLine();
					if (msg.equals("disconnect:"))
					{
						//将下线的用户移除掉
						cs.remove(name);
						send(name+"下线了");
					}
					else if (msg.contains("to:"))
					{
						String us = userByMsg(msg);//用户
						String str = sendmsg(msg);//消息
						
						if (us.equals("all"))
						{
							send(name+str+"(群聊)");
						}
						else
						{
							sendPrivateuser(us,str);
						}
					}
					else 
					{
						send(name+msg+"(群聊)");
					}
					//获取用户List
					sendUserList();
				}
			} 
			catch (SocketException se){}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				try{
					if (s!=null) s.close();
					if (br!=null) br.close();
				}
				catch (IOException e) {e.printStackTrace();}
			}
		}
		
	}

}
