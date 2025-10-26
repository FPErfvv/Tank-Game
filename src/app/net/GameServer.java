
package app.net;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketAddress;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.HashMap;

import app.GameRenderer;
import app.gameElements.GameMap;
import app.gameElements.MainCharacter;

public final class GameServer extends TimerTask {
	private static final int PORT = 2534;
	private static final int MAX_CLIENT_COUNT = 20;
	
	private static final int TIMER_DELAY = 1;
	
	private DatagramSocket clientSocket;
	private GamePacketManager packetManager;
	
	private int idAssignmentCounter = 0;
	
	private HashMap<ServerClientHandle, Integer> clientHandleToIndexLookup;
	private int clientCount;
	private ServerClientHandle[] clientHandles;
	
	private Thread packetReceivingThread;
	private Thread commandListeningThread;
	
	
	private Timer m_timer;
	
	private GameMap m_currentMap;
	private MainCharacter[] players;
	
	
	private int frameWidth, frameHeight;
	private JFrame m_frame;
	private GameRenderer m_gameRenderer;
	
	public GameServer() {
		try {
			clientSocket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		packetManager = new GamePacketManager();
		
		clientCount = 0;
		clientHandles = new ServerClientHandle[MAX_CLIENT_COUNT];
		for (int i = 0; i < MAX_CLIENT_COUNT; i++) { clientHandles[i] = new ServerClientHandle(); }
		clientHandleToIndexLookup = new HashMap<>();
		
		packetReceivingThread = new Thread(new PacketReceiver());
		commandListeningThread = new Thread(new CommandListener());
		
		m_timer = new Timer();
		
		
		m_currentMap = new GameMap();
		//m_currentMap.addTrees();
		
		players = new MainCharacter[MAX_CLIENT_COUNT];
		for (int i = 0; i < MAX_CLIENT_COUNT; i++) { players[i] = new MainCharacter(); }
		
		initGUI();
	}
	
	public void startGame() {
		System.out.println("[SERVER]: starting server");
		
		packetReceivingThread.start();
		commandListeningThread.start();
		
		m_timer.schedule(this, 0, TIMER_DELAY);
		
		m_frame.setVisible(true);
		
		try {
			packetReceivingThread.join();
			commandListeningThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void stopGame() {
		m_timer.cancel();
		m_timer.purge();
		
		System.out.println("[SERVER]: stopping server");
		clientSocket.close();
	}
	
	@Override
	public void run() {
		m_currentMap.tick(new Point2D.Double(0, 0), null);
		for (int i = 0; i < clientCount; i++)
			players[i].periodic(null);
		
		m_gameRenderer.drawPlayers(players, clientCount);
		m_gameRenderer.renderFrame();
	}
	
	private void addClient(InetAddress clientIpAddress, int clientPort, int gameId, double spawnX, double spawnY, double spawnAngle) {
		clientHandles[clientCount].setIpAddress(clientIpAddress);
		clientHandles[clientCount].setPort(clientPort);
		clientHandles[clientCount].setGameId(gameId);
		
		clientHandleToIndexLookup.put(clientHandles[clientCount], clientCount);
		
		spawnPlayer(clientHandles[clientCount], spawnX, spawnY, spawnAngle);
		
		clientCount++;
	}
	private void removeClient(InetAddress clientIpAddress, int clientPort) {
		ServerClientHandle equivilantHandle = new ServerClientHandle();
		equivilantHandle.setIpAddress(clientIpAddress);
		equivilantHandle.setPort(clientPort);
		
		int index = clientHandleToIndexLookup.get(equivilantHandle);
		
		// update the client lookup
		clientHandleToIndexLookup.put(clientHandles[clientCount - 1], index);
		clientHandleToIndexLookup.remove(clientHandles[index]);
		
		// perform the actual swap
		ServerClientHandle temp = clientHandles[index];
		clientHandles[index] = clientHandles[clientCount - 1];
		clientHandles[clientCount - 1] = temp;
		
		clientCount--;
	}
	
	private void sendDataToAllClients(byte[] dataToSend) {
		for (int i = 0; i < clientCount; i++)
			packetManager.sendData(dataToSend, clientSocket, clientHandles[i].getIpAddress(), clientHandles[i].getPort());
	}
	private void sendDataToAllClientsExceptFor(byte[] dataToSend, ServerClientHandle clientHandle) {
		for (int i = 0; i < clientCount; i++)
			if (!clientHandles[i].equals(clientHandle)) {
				packetManager.sendData(dataToSend, clientSocket, clientHandles[i].getIpAddress(), clientHandles[i].getPort());
			}
	}
	
	private void processClientConnection(InetAddress ipAddress, int port) {
		int index = clientCount;
		int playerGameId = idAssignmentCounter++;
		
		double maxDistanceFromSpawn = 1000.0d;
		//double spawnX = maxDistanceFromSpawn * Math.random();
		//double spawnY = maxDistanceFromSpawn * Math.random();
		//double spawnAngle = 360.0d * Math.random();
		double spawnX = 100.0d;
		double spawnY = 200.0d;
		double spawnAngle = Math.toRadians(180.0d);
		
		// let the new client know that the server has accepted connection
		byte[] confirmMessage = packetManager.makeToClientConfirmationData(playerGameId, spawnX, spawnY, spawnAngle);
		packetManager.sendData(confirmMessage, clientSocket, ipAddress, port);
		
		// notify the new client of other players that are in the server
		for (int i = 0; i < clientCount; i++) {
			int otherPlayerGameId = clientHandles[i].getGameId();
			Point2D.Double otherPlayerCoor = players[i].getMapCoodinate();
			double otherPlayerX = otherPlayerCoor.x;
			double otherPlayerY = otherPlayerCoor.y;
			double otherPlayerAngle = players[i].getAngle();
			byte[] joinMessage = packetManager.makeToClientOtherPlayerJoinData(otherPlayerGameId, otherPlayerX, otherPlayerY, otherPlayerAngle);
			packetManager.sendData(joinMessage, clientSocket, ipAddress, port);
		}
		
		addClient(ipAddress, port, playerGameId, spawnX, spawnY, spawnAngle);
		
		byte[] letPlayersKnow = packetManager.makeToClientOtherPlayerJoinData(playerGameId, spawnX, spawnY, spawnAngle);
		sendDataToAllClientsExceptFor(letPlayersKnow, clientHandles[index]);
		
		System.out.println("[" + ipAddress + "::" + port + "]: with gameId = " + playerGameId + " has joined! orientation: (x: " + spawnX + ", y: " + spawnY + ", angle: " + spawnAngle + ")");
	}
	private void processClientDisconnection(InetAddress ipAddress, int port) {
		ServerClientHandle equivilantHandle = new ServerClientHandle();
		equivilantHandle.setIpAddress(ipAddress);
		equivilantHandle.setPort(port);
		
		int index = clientHandleToIndexLookup.get(equivilantHandle);
		int playerGameId = clientHandles[index].getGameId();
		
		byte[] letPlayersKnow = { 
				GamePacketManager.PACKET_TYPE_TO_CLIENT_OTHER_PLAYER_DISCONNECT, 
				(byte) playerGameId
		};
		sendDataToAllClientsExceptFor(letPlayersKnow, clientHandles[index]);
		
		removeClient(ipAddress, port);
		
		System.out.println("[" + ipAddress + "::" + port + "]: with gameId = " + playerGameId + " has left!");
	}
	
	private void processClientMovement(InetAddress ipAddress, int port, byte[] receivedData) {
		ServerClientHandle equivilantHandle = new ServerClientHandle();
		equivilantHandle.setIpAddress(ipAddress);
		equivilantHandle.setPort(port);
		
		int index = clientHandleToIndexLookup.get(equivilantHandle);
		int playerGameId = clientHandles[index].getGameId();
		
		if (receivedData[1] > 0) players[index].turnLeft();
		else players[index].stopTurningLeft();
		
		if (receivedData[2] > 0) players[index].turnRight();
		else players[index].stopTurningRight();
		
		if (receivedData[3] > 0) players[index].moveForward();
		else players[index].stopMovingForward();
		
		if (receivedData[4] > 0) players[index].moveBackward();
		else players[index].stopMovingBackward();
		
		byte[] letPlayersKnow = packetManager.makeToClientOtherPlayerMoveInputData(playerGameId, 
				players[index].isTurningLeft(), players[index].isTurningRight(), 
				players[index].isMovingForward(), players[index].isMovingBackward());
		sendDataToAllClientsExceptFor(letPlayersKnow, clientHandles[index]);
		
		System.out.println("[" + ipAddress + "::" + port + "]: with gameId = " + playerGameId + " has moved!");
	}
	
	
	private void spawnPlayer(ServerClientHandle handle, double spawnX, double spawnY, double spawnAngle) {
		int index = clientHandleToIndexLookup.get(handle);
		
		players[index].moveTo(new Point2D.Double(spawnX, spawnY));
		players[index].setAngle(spawnAngle);
		players[index].setCurrentMap(m_currentMap);
	}
	
	private void initGUI() {
		m_frame = new JFrame("Tank Game - SERVER");
		m_frame.setMinimumSize(new Dimension(800, 600));
		m_frame.addComponentListener(new ComponentAdapter() {  
				@Override
                public void componentResized(ComponentEvent evt) {
                    Component c = (Component)evt.getSource();

                    frameWidth = m_frame.getWidth();
                    frameHeight = m_frame.getHeight();
                    
                    m_gameRenderer.setBounds(new Dimension(frameWidth, frameHeight));
                }
        });
		m_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		m_gameRenderer = new GameRenderer();
		m_gameRenderer.setScene(null, m_currentMap);
		
		m_frame.add(m_gameRenderer);
	}
	
	
	
	private class PacketReceiver implements Runnable {
		@Override
		public void run() {
			DatagramPacket addressPacket = new DatagramPacket(new byte[1], 1);
			
			while (!clientSocket.isClosed()) {
				byte[] receivedData = packetManager.receiveData(clientSocket, addressPacket);
				
				if (receivedData == null) {
					System.out.println("Packet failed!");
					continue;
				}
				
				int type = packetManager.getPacketType(receivedData);
				
				if (type == GamePacketManager.PACKET_TYPE_TO_SERVER_CONNECT) {
					processClientConnection(addressPacket.getAddress(), addressPacket.getPort());
				}
				if (type == GamePacketManager.PACKET_TYPE_TO_SERVER_DISCONNECT) {
					processClientDisconnection(addressPacket.getAddress(), addressPacket.getPort());
				}
				if (type == GamePacketManager.PACKET_TYPE_TO_SERVER_PLAYER_MOVE_INPUT) {
					processClientMovement(addressPacket.getAddress(), addressPacket.getPort(), receivedData);
				}
			}
		}
	}
	
	private class CommandListener implements Runnable {
		@Override
		public void run() {
			Scanner scan = new Scanner(System.in);
			scan.next();
			scan.close();
			
			stopGame();
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println("[SERVER]: started server process");
		
		GameServer server = new GameServer();
		server.startGame();
		
		System.out.println("[SERVER]: stopping server process");
	}
}
