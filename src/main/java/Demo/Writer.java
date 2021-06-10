package Demo;

import Blockchain.Message;
import Blockchain.Protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class Writer implements Runnable{
	//private Channel channel;
	private Protocol protocol;
	private Integer sender;
	private Integer receiver;
	private int lastRead;
	private byte[] sid;
	private DataOutputStream os;
	private boolean notReceived = true;
	private CopyOnWriteArrayList<Message> messages;
	
	public Writer(byte[] sid, int receiver, int sender, int lastRead, String file, DataOutputStream os, CopyOnWriteArrayList<Message> messages) {
		/*
		if(c.getParty2() != sender && c.getParty1() != sender ) {
			throw new IllegalArgumentException("Wrong sender");
		}
		
		this.channel = c;
		*/
		this.sender = sender;
		this.receiver = receiver;
		this.lastRead = lastRead; 
		this.sid = sid;
		this.os = os;
		this.messages = messages;
		protocol = new Protocol();
	}
	
	/*
	private void writeMsgOnChannel(DataOutputStream os, Message msg, 
			Integer lastRead, Integer sendingPid, Integer receivingPid) throws IOException {
		if(msg.getMsgNum() > lastRead) {
			byte[] data = protocol.prepareMessage(msg.getSid(), sendingPid, receivingPid, msg.getMsgNum(), msg.getUtility(), msg.getMsg());
			os.write(data);
			os.flush();
		}
	}
	*/
	
	private void sendSingleMessages(Message msg) throws IOException {
		boolean sameSid = Arrays.equals(msg.getSid(), sid);
		
		/*
		if (sameSid && msg.getSendingPid() == sender && msg.getReceivingPid() == EVERYONE && msg.getMsgNum() > lastRead) {
			byte[] data = protocol.prepareMessage(msg.getSid(), sender, receiver, msg.getMsgNum(), msg.getUtility(), msg.getMsg());
			os.write(data);
			notReceived = false;
		}
		*/
		
		if (sameSid && msg.getSendingPid() == sender && msg.getReceivingPid() == receiver && msg.getMsgNum() > lastRead) {
				byte[] data = protocol.prepareMessage(msg.getSid(), sender, receiver, msg.getMsgNum(), msg.getUtility(), msg.getMsg());
				os.write(data);
				notReceived = false;
		}
	}
	

	public void run() {
		try {
			while(notReceived) {
				for(Message message: messages) {
					sendSingleMessages(message);
				}
				if (!notReceived) {
					Thread.sleep(100);
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
