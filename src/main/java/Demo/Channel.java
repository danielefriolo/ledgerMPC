package Demo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Channel {
	private Integer party1;
	private Integer party2;
	private Integer party1LastRead;
	private Integer party2LastRead;
	private Socket  party1socket;
	private Socket  party2socket;
	private DataInputStream disParty1;
	private DataInputStream disParty2;
	private DataOutputStream dosParty1;
	private DataOutputStream dosParty2;
	public boolean needsRestartParty1;
	public boolean needsRestartParty2;
	private byte[] sid;
	
	public Channel(Integer party1, Integer party2, Socket soc, DataInputStream dis, DataOutputStream dos, byte[] sid) {
		this.party1 = party1;
		this.party2 = party2;
		this.party1LastRead = 0;
		this.party2LastRead = 0;
		this.party1socket = soc;
		this.party2socket = null;
		this.disParty1 = dis;
		this.disParty2 = null;
		this.dosParty1 = dos;
		this.dosParty2 = null;
		this.sid = sid;
		this.needsRestartParty1 = false;
		this.needsRestartParty2 = false;
	}
	
	public boolean isUpdated() {
		return party2socket != null;
	}
	
	public void updateParty(Integer party, Socket soc, DataInputStream dis, DataOutputStream dos) throws IllegalArgumentException {
		if (party2 == party && party2socket == null) {
			party2socket = soc;
			disParty2 = dis;
			dosParty2 = dos;
		}
		else {
			throw new IllegalArgumentException("Wrong party");
		}
	}
	
	public void updateConuter(Integer party, Integer lastRead) throws IllegalArgumentException{
		if (party == party1) {
			party1LastRead = lastRead;
		}else if(party == party2) {
			party2LastRead = lastRead;
		}else {
			throw new IllegalArgumentException("Wrong party");
		}
	}

	public Integer getParty1() {
		return party1;
	}

	public Integer getParty2() {
		return party2;
	}
	
	public byte[] getSid() {
		return sid;
	}

	public Integer getParty1LastRead() {
		return party1LastRead;
	}

	public Integer getParty2LastRead() {
		return party2LastRead;
	}

	public Socket getParty1socket() {
		return party1socket;
	}

	public Socket getParty2socket() {
		return party2socket;
	}

	public DataInputStream getDisParty1() {
		return disParty1;
	}

	public DataInputStream getDisParty2() {
		return disParty2;
	}

	public DataOutputStream getDosParty1() {
		return dosParty1;
	}

	public DataOutputStream getDosParty2() {
		return dosParty2;
	}
	
	public boolean equals(Channel c) {
		Integer party1 = c.getParty1(); 
		Integer party2 = c.getParty2();
		byte[] sid = c.getSid();
		return (this.party1 == party1 && this.party2 == party2 && Arrays.equals(this.sid, sid)) ||
				(this.party1 == party2 && this.party2 == party1 && Arrays.equals(this.sid, sid));
	}
	
	
	public void breakConnection(Integer downParty) {
		if (downParty == party1) {
			this.party1socket = null;
			this.disParty1 = null;
			this.dosParty1 = null;
			this.party1LastRead = 0;
			this.needsRestartParty1 = true;
		} else if (downParty == party2) {
			this.party2socket = null;
			this.disParty2 = null;
			this.dosParty2 = null;
			this.needsRestartParty2 = true;
			this.party2LastRead = 0;
		}else {
			throw new IllegalArgumentException("Selected party does not exist");
		}
	}
	
	public void restartChannel(byte[] sid, Integer party1, Integer party2, 
			Socket soc, DataInputStream dis, DataOutputStream dos) {
		if(Arrays.equals(this.sid, sid)) {
			if(party1 == this.party1 && this.party1socket == null) {
				this.party1socket = soc;
				this.disParty1 = dis;
				this.dosParty1 = dos;
				this.needsRestartParty1 = false;
			}else if (party1 == this.party2 && this.party2socket == null) {
				this.party2socket = soc;
				this.disParty2 = dis;
				this.dosParty2 = dos;
				this.needsRestartParty2 = false;
			}else {
				throw new IllegalArgumentException("Selected party is wrong");
			}
		} else {
			throw new IllegalArgumentException("Selected sid is wrong");
		}
	}
	
}
