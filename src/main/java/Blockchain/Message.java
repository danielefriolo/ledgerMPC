package Blockchain;

import java.util.Arrays;

/**
 * Message is an internal object to manage a message.
 */
public class Message {

	private byte[] sid;
	private Integer sendingPid;
	private Integer receivingPid;
	private Integer msgNum;
	private Integer utility;
	private byte[] msg;
	
	public Message(byte[] sid, Integer sendingPid, Integer receivingPid, Integer msgNum, byte[] msg){
		this.msg = msg;
		this.msgNum = msgNum;
		this.sendingPid = sendingPid;
		this.sid = sid;
		this.receivingPid = receivingPid;
		this.utility = null;
	}

	public boolean isEqual(Message msg){
		return Arrays.equals(msg.sid, this.sid)
			&& msg.sendingPid == this.sendingPid
			&& msg.receivingPid == this.receivingPid
			&& msg.msgNum == this.msgNum
			&& Arrays.equals(msg.msg, this.msg);
	}
	
	public void setUtility(Integer utility) {
		this.utility = utility;
	}

	public Integer getUtility() {
		return utility;
	}
	public byte[] getSid() {
		return sid;
	}
	public Integer getSendingPid() {
		return sendingPid;
	}
	public Integer getReceivingPid() {
		return receivingPid;
	}
	public Integer getMsgNum() {
		return msgNum;
	}
	public byte[] getMsg() {
		return msg;
	}
}
