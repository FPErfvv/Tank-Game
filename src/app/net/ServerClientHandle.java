
package app.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class ServerClientHandle {
	private InetAddress ipAddress;
	private int port;
	
	private int gameId;
	
	public ServerClientHandle() {}
	
	public InetAddress getIpAddress() { return ipAddress; }
	public int getPort() { return port; }
	public int getGameId() { return gameId; }
	
	public void setIpAddress(InetAddress addr) {
		ipAddress = addr;
	}
	
	public void setPort(int newPort) {
		port = newPort;
	}
	
	public void setGameId(int newGameId) {
		gameId = newGameId;
	}
	
	@Override
	public boolean equals(Object other) {
		ServerClientHandle otherClient = (ServerClientHandle)other;
		
		return ipAddress.equals(otherClient.getIpAddress()) && port == otherClient.getPort();
	}
	
	@Override
	public int hashCode() {
		return ipAddress.hashCode() + port;
	}
}
