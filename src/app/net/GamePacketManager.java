
package app.net;

import java.io.IOException;

import java.awt.geom.Point2D;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class GamePacketManager {
	/**
	 * Size in bytes
	 */
	public static final int MAX_PACKET_PAYLOAD_SIZE = 128;
	
	public static final byte PACKET_TYPE_TO_SERVER_CONNECT = 0;
	public static final byte PACKET_TYPE_TO_SERVER_DISCONNECT = 1;
	public static final byte PACKET_TYPE_TO_SERVER_PLAYER_MOVE_INPUT = 2;
	
	public static final byte PACKET_TYPE_TO_CLIENT_CLIENT_CONNECT_RESPONSE = 3;
	public static final byte PACKET_TYPE_TO_CLIENT_OTHER_PLAYER_CONNECT = 4;
	public static final byte PACKET_TYPE_TO_CLIENT_OTHER_PLAYER_DISCONNECT = 5;
	public static final byte PACKET_TYPE_TO_CLIENT_OTHER_PLAYER_MOVE_INPUT = 6;
	
	public void sendData(byte[] dataToSend, DatagramSocket socket, InetAddress ipAddress, int port) {
		try {
			DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length, ipAddress, port);
			socket.send(packetToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public byte[] receiveData(DatagramSocket socket, DatagramPacket addressPacket) {
		byte[] dataToReceive = new byte[MAX_PACKET_PAYLOAD_SIZE];
		
		try {
			DatagramPacket packetToReceive = new DatagramPacket(dataToReceive, dataToReceive.length);
			socket.receive(packetToReceive);
				
			addressPacket.setAddress(packetToReceive.getAddress());
			addressPacket.setPort(packetToReceive.getPort());
				
			return packetToReceive.getData();		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int getPacketType(byte[] data) {
		return data[0];
	}
	
	public byte[] makeToClientConfirmationData(int newPlayerGameId, double spawnX, double spawnY, double spawnAngle) {
		byte[] data = new byte[2 + 3 * Double.BYTES];
		data[0] = PACKET_TYPE_TO_CLIENT_CLIENT_CONNECT_RESPONSE;
		data[1] = (byte) (newPlayerGameId);
		
		copyOrientationToData(data, spawnX, spawnY, spawnAngle);
		
		return data;
	}
	void parseToClientConfirmationData(byte[] data, int[] pGameId, double[] outOrientation) {
		pGameId[0] = data[1];
		
		ByteBuffer buffer = ByteBuffer.allocate(3 * Double.BYTES);
		buffer.put(data, 2, 3 * Double.BYTES);
		buffer.rewind();
		
		outOrientation[0] = buffer.getDouble();
		outOrientation[1] = buffer.getDouble();
		outOrientation[2] = buffer.getDouble();
	}
	
	
	public byte[] makeToClientOtherPlayerJoinData(int newPlayerGameId, double spawnX, double spawnY, double spawnAngle) {
		byte[] data = new byte[2 + 3 * Double.BYTES];
		data[0] = PACKET_TYPE_TO_CLIENT_OTHER_PLAYER_CONNECT;
		data[1] = (byte) (newPlayerGameId);
		
		copyOrientationToData(data, spawnX, spawnY, spawnAngle);
		
		return data;
	}
	public void parseToClientOtherPlayerJoinData(byte[] data, int[] pGameId, double[] outOrientation) {
		pGameId[0] = data[1];
		
		ByteBuffer buffer = ByteBuffer.allocate(3 * Double.BYTES);
		buffer.put(data, 2, 3 * Double.BYTES);
		buffer.rewind();
		
		outOrientation[0] = buffer.getDouble();
		outOrientation[1] = buffer.getDouble();
		outOrientation[2] = buffer.getDouble();
	}
	
	
	public byte[] makeToServerMoveInputData(boolean isTurningLeft, boolean isTurningRight, boolean isMovingForward, boolean isMovingBackward) {
		byte[] data = new byte[5];
		
		data[0] = GamePacketManager.PACKET_TYPE_TO_SERVER_PLAYER_MOVE_INPUT;
		data[1] = (byte) (isTurningLeft ? 1 : 0);
		data[2] = (byte) (isTurningRight ? 1 : 0);
		data[3] = (byte) (isMovingForward ? 1 : 0);
		data[4] = (byte) (isMovingBackward ? 1 : 0);
		
		return data;
	}
	public void parseToServerMoveInputData(byte[] data, boolean[] outInput) {
		outInput[0] = (data[1] > 0) ? true : false;
		outInput[1] = (data[2] > 0) ? true : false;
		outInput[2] = (data[3] > 0) ? true : false;
		outInput[3] = (data[4] > 0) ? true : false;
	}
	
	public byte[] makeToClientOtherPlayerMoveInputData(int otherPlayerGameId, 
			boolean isTurningLeft, boolean isTurningRight, boolean isMovingForward, boolean isMovingBackward) {
		byte[] data = new byte[6];
		
		data[0] = GamePacketManager.PACKET_TYPE_TO_CLIENT_OTHER_PLAYER_MOVE_INPUT;
		data[1] = (byte) (otherPlayerGameId);
		data[2] = (byte) (isTurningLeft ? 1 : 0);
		data[3] = (byte) (isTurningRight ? 1 : 0);
		data[4] = (byte) (isMovingForward ? 1 : 0);
		data[5] = (byte) (isMovingBackward ? 1 : 0);
		
		return data;
	}
	
	public int parseToClientOtherPlayerMoveInputData(byte[] data, boolean[] outInput) {
		outInput[0] = (data[2] > 0) ? true : false;
		outInput[1] = (data[3] > 0) ? true : false;
		outInput[2] = (data[4] > 0) ? true : false;
		outInput[3] = (data[5] > 0) ? true : false;
		
		return data[1];
	}
	
	private void copyOrientationToData(byte[] outDstData, double x, double y, double angle) {
		ByteBuffer buffer = ByteBuffer.allocate(3 * Double.BYTES);
		buffer.putDouble(x);
		buffer.putDouble(y);
		buffer.putDouble(angle);
		
		byte[] srcData = buffer.array();
		
		for (int i = 0; i < 3 * Double.BYTES; i++) {
			outDstData[i + 2] = srcData[i];
		}
	}
}
