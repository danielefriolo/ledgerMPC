package Demo;

import Blockchain.Message;
import Blockchain.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;

public class DemoWorker implements Runnable {
	private boolean running = true;
	
	DataInputStream dis;
	DataOutputStream dos;
	String file;
	Protocol protocol;
	
	static CopyOnWriteArrayList<Message> messages = new CopyOnWriteArrayList<Message>();
	private Socket socket;

	public DemoWorker (Socket socket, String file) throws IOException{
		protocol = new Protocol();
		this.socket = socket;
		this.file = file;
		this.dis = new DataInputStream(this.socket.getInputStream());
		this.dos = new DataOutputStream(this.socket.getOutputStream());
	}
	
	static public CopyOnWriteArrayList<Message> getMessages() {
		return messages;
	}

	public void run() {
		try {
			socket.setTcpNoDelay(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		receiveMessage();
	}

	
	private void receiveMessage() {
		DataInputStream inFromClient;
		Message message = null;

		try {
			inFromClient = new DataInputStream(socket.getInputStream());
			
			while (running) {
				message = protocol.readSingleMessage(inFromClient);
				doAction (message);
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	
	private void store(Message message) {
		boolean toStore = true;
		for(Message msg : messages) {
			if (msg.isEqual(message)) {
				toStore = false;
			}
		}
		if (toStore) {
			messages.add(message);
		}
	}

	private void doAction(Message message) throws FileNotFoundException, IOException{
		Integer sender =  message.getSendingPid();
		Integer receiver = message.getReceivingPid();
		Integer cmd = message.getUtility();
		byte[] sid = message.getSid();
		
		if (cmd == protocol.write) {
			//System.out.println("Received message. Sender: " + sender.toString() + "; Receiver: " + receiver.toString() + "; sid: " + new String(sid) + "; message: " + new String(message.getMsg()));
			store(message);
		} else {
			Writer w = new Writer(sid, sender, receiver, message.getMsgNum(), file, new DataOutputStream(socket.getOutputStream()), messages);
			Thread th = new Thread(w);
			th.start();
		}
		
	} 
}
