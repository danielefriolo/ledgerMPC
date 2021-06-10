package Demo;

import Blockchain.Message;
import Blockchain.Protocol;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class DemoConnection implements Runnable {
	private final Integer maxWorkers = 1000;

	private Integer inputPort;
	private String file;
	private Boolean running = true;
	private Protocol protocol;
	DemoWorker[] workers = new DemoWorker[maxWorkers];
	Integer workerIndex = -1;

	public DemoConnection(Integer inputPort, String file) throws IOException {
		this.inputPort = inputPort;
		this.file = file;
		protocol = new Protocol();
		updateRamBlockchain(file);
	}

	public void run() {
		ServerSocket serverSocket;
		writeOnFile();
		try {
			serverSocket = new ServerSocket(inputPort);
			serverSocket.setReuseAddress(true);
			while (running) {
				waitConnection(serverSocket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateRamBlockchain(String file) {
		DataInputStream inputStream;
		File f = new File(file);
		if(f.exists()) {
			try {
				inputStream = new DataInputStream(new FileInputStream(file));
				Integer msgNum;
				Message msg;
				
				do{
					msg = protocol.readSingleMessage2(inputStream);
					msgNum = msg.getMsgNum();
					if(msgNum != protocol.impossibleVal) {
						DemoWorker.messages.add(msg);
					}
				}while(msgNum != protocol.impossibleVal);
				
				inputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void writeOnFile() {
		Runnable runnable = () -> {	
			ArrayList<Message> stored = new ArrayList<Message>();
			ArrayList<Message> toStore = new ArrayList<Message>();
			FileOutputStream output;
				
			CopyOnWriteArrayList<Message> messages = DemoWorker.getMessages();
			while(running) {		
				for(Message message : messages) {
					if(!stored.contains(message)) {
						toStore.add(message);
					}
				}
				if(!toStore.isEmpty()) {
					try {
						output = new FileOutputStream(file, true);
						Iterator<Message> itr = toStore.iterator();
						while(itr.hasNext()) {
							Message message2 = itr.next();
							output.write(protocol.prepareMessage(message2));
							stored.add(message2);
							itr.remove();
						}
						
						output.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} 
			
		};
		
		Thread t = new Thread(runnable);
		t.start();
	}

	private void waitConnection(ServerSocket serverSocket) {
		try {
			Socket socket = serverSocket.accept();
			if (running) {
				DemoWorker worker = new DemoWorker(socket, file);
				workerIndex++;
				workers[workerIndex] = worker;

				new Thread(worker).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String argv[]) throws IOException {
		final Integer serverPort = 3000;
		final String file = "blockchain.dat";
		DemoConnection dummyServer = new DemoConnection(serverPort, file);

		new Thread(dummyServer).run();
	}
}
