package ie.gmit.proskillsserver.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ie.gmit.proskillsserver.proceeses.Login;
import ie.gmit.proskillsserver.proceeses.Register;

public class EchoServer {
  public static void main(String[] args) throws Exception {
    ServerSocket m_ServerSocket = new ServerSocket(2004,10);
    int id = 0;
    while (true) {
      Socket clientSocket = m_ServerSocket.accept();
      ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);
      cliThread.start();
    }
  }
}

class ClientServiceThread extends Thread {
  Socket clientSocket;
  String message;
  int clientID = -1;
  boolean running = true;
  ObjectOutputStream out;
  ObjectInputStream in;

  ClientServiceThread(Socket s, int i) {
    clientSocket = s;
    clientID = i;
  }

  void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
  
  public void run() {
    System.out.println("> Accepted Client ID: " + clientID + " | Address - " + clientSocket.getInetAddress().getHostName());
   
	try {
		out = new ObjectOutputStream(clientSocket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(clientSocket.getInputStream());
		
		message = (String)in.readObject();
		
		String[] splited = message.split("\\s+");
		boolean loginStatus = false;
		
		if(splited[0].equals("login")){
			
			System.out.println("> Client ID: " + clientID + " | Login Attempt - " + splited[1]);
			
			loginStatus = Login.main(splited[1], splited[2], clientID);	
			
		}
		else if(splited[0].equals("register")){
			
			System.out.println("> Client ID: " + clientID + " | Register Attempt - " + splited[1]);
			
			loginStatus = Register.main(splited[1], splited[2], clientID);	
			
		}
		
		sendMessage("" + loginStatus);
		
		System.out.println("> Disconnecting Client ID: " + clientID + " | Address - " + clientSocket.getInetAddress().getHostName());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}			
  }			
}