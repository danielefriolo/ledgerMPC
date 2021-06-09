package Blockchain;

import java.net.InetAddress;

public class Connection {
	private Integer remotePort;
	private InetAddress remoteAddress;

	public Connection (Integer remotePort, InetAddress remoteAddress) {
		this.remotePort = remotePort;
		this.remoteAddress = remoteAddress;
	}

	public Integer getRemotePort() {
		return remotePort;
	}

	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}
}
