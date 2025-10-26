
package app.net;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.HashMap;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.geom.Point2D;

import java.lang.Thread;

import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JPanel;

import app.GameRenderer;
import app.PlayerControls;
import app.SoundEngine;
import app.gameElements.GameMap;
import app.gameElements.MainCharacter;

public final class GameClient implements ActionListener {
	private static final int SERVER_PORT = 2534;
	
	private static final int TIMER_DELAY = 1;
	
	private static final int SERVER_MAX_CLIENT_COUNT = 20;
	
	private InetAddress serverIpAddress;
	private DatagramSocket serverSocket;
	private GamePacketManager packetManager;
	
	private int playerId;
	
	/**
	 * Key - player identifier
	 * Value - index of otherCharacters array
	 */
	private HashMap<Integer, Integer> otherClientToIndexLookup;
	private int otherClientCount = 0;
	private MainCharacter[] otherPlayers;
	private int[] otherPlayerGameIds;
	
	private Thread packetReceivingThread;
	
	// ----- client-side contents -----
	
	private GameMap m_currentMap;
	private MainCharacter m_mainCharacter;
	
	// ----- output contents -----
	
	private int frameWidth, frameHeight;
	
	private Color mainPanelBackgroundColor;
	
	private JFrame m_frame;
	private JPanel m_mainPanel;
	private Timer m_timer;
	
	private GameRenderer m_gameRenderer;
	
	private SoundEngine soundEngine;
	
	private PlayerControls playerInput;
	
	public GameClient() {
		try {
			serverIpAddress = InetAddress.getLocalHost();
			serverSocket = new DatagramSocket();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		packetManager = new GamePacketManager();
		
		packetReceivingThread = new Thread(new PacketReceiver());
		
		
		m_currentMap = new GameMap();
		m_mainCharacter = new MainCharacter();
        m_mainCharacter.setCurrentMap(m_currentMap);
        
        otherClientToIndexLookup = new HashMap<>();
        otherPlayers = new MainCharacter[SERVER_MAX_CLIENT_COUNT];
        otherPlayerGameIds = new int[SERVER_MAX_CLIENT_COUNT];
        for (int i = 0; i < SERVER_MAX_CLIENT_COUNT; i++) { otherPlayers[i] = new MainCharacter(); }
		
		m_timer = new Timer(TIMER_DELAY, this);
		
		initGUI();
		
		soundEngine = new SoundEngine();
	}
	public void startGame() {
		System.out.println("[CLIENT]: starting client");
		
		processServerConnection();
		
		packetReceivingThread.start();
		
		m_timer.start();
	}
	public void stopGame() {
		System.out.println("[CLIENT]: stopping client");
		
		byte[] leaveMessage = { GamePacketManager.PACKET_TYPE_TO_SERVER_DISCONNECT };
		packetManager.sendData(leaveMessage, serverSocket, serverIpAddress, SERVER_PORT);
		serverSocket.close();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		m_currentMap.tick(new Point2D.Double(0.0d, 0.0d), soundEngine);
		m_mainCharacter.periodic(soundEngine);
		for (int i = 0; i < otherClientCount; i++) {
			otherPlayers[i].periodic(soundEngine);
		}
		
		if (playerInput.inputIsChanged()) {
			byte[] sendData = packetManager.makeToServerMoveInputData(
					m_mainCharacter.isTurningLeft(),
					m_mainCharacter.isTurningRight(),
					m_mainCharacter.isMovingForward(),
					m_mainCharacter.isMovingBackward()
			);
			
			packetManager.sendData(sendData, serverSocket, serverIpAddress, SERVER_PORT);
		}
		
		m_gameRenderer.renderFrame();
		m_gameRenderer.drawPlayers(otherPlayers, otherClientCount);
	}
	
	private void addPlayer(int otherPlayerGameId, double spawnX, double spawnY, double spawnAngle) {
		spawnPlayer(otherClientCount, spawnX, spawnY, spawnAngle);
		
		otherClientToIndexLookup.put(otherPlayerGameId, otherClientCount);
		otherPlayerGameIds[otherClientCount] = otherPlayerGameId;
		
		otherClientCount++;
	}
	private void removePlayer(int otherPlayerGameId) {
		int index = otherClientToIndexLookup.get(otherPlayerGameId);
		
		int idOfLast = otherPlayerGameIds[otherClientCount - 1];
		
		otherClientToIndexLookup.remove(otherPlayerGameId);
		otherClientToIndexLookup.put(idOfLast, index);
		
		MainCharacter temp = otherPlayers[index];
		otherPlayers[index] = otherPlayers[otherClientCount - 1];
		otherPlayers[otherClientCount - 1] = temp;
		
		otherClientCount--;
	}
	
	private void spawnPlayer(int index, double spawnX, double spawnY, double spawnAngle) {
		otherPlayers[index].moveTo(new Point2D.Double(spawnX, spawnY));
		otherPlayers[index].setAngle(spawnAngle);
		otherPlayers[index].setCurrentMap(m_currentMap);
	}
	
	private void processServerConnection() {
		byte[] loginMessage = { GamePacketManager.PACKET_TYPE_TO_SERVER_CONNECT };
		packetManager.sendData(loginMessage, serverSocket, serverIpAddress, SERVER_PORT);
		
		DatagramPacket addressPacket = new DatagramPacket(new byte[1], 1);
		byte[] serverResponse = packetManager.receiveData(serverSocket, addressPacket);
		int[] pPlayerId = new int[1];
		double[] orientation = new double[3];
		packetManager.parseToClientConfirmationData(serverResponse, pPlayerId, orientation);
		playerId = pPlayerId[0];
		m_mainCharacter.moveTo(new Point2D.Double(orientation[0], orientation[1]));
		m_mainCharacter.setAngle(orientation[2]);
		
		m_frame.setVisible(true);
		
		System.out.println("[CLIENT]: successfully joined server! assigned id = " + playerId);
		
	}
	
	private void initGUI() {
		mainPanelBackgroundColor = new Color(0, 154, 23);
		
		m_frame = new JFrame("Tank Game - Multiplayer");
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
		m_frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stopGame();
				
				System.out.println("[CLIENT]: stopping client process");
				System.exit(0);
			}
		});
		
		m_gameRenderer = new GameRenderer();
		m_gameRenderer.setScene(m_mainCharacter, m_currentMap);
		
		m_mainPanel = new JPanel();
		m_mainPanel.setVisible(true);
		m_mainPanel.setFocusable(true);
		m_mainPanel.setFocusTraversalKeysEnabled(false);
		m_mainPanel.setBackground(mainPanelBackgroundColor);
		
		playerInput = new PlayerControls(m_mainCharacter, m_timer, m_gameRenderer, m_currentMap);
		
		m_mainPanel.addKeyListener(playerInput);
		m_mainPanel.addMouseListener(playerInput);
		m_mainPanel.add(m_gameRenderer);
		
		m_frame.add(m_mainPanel);
	}
	
	
	private class PacketReceiver implements Runnable {
		@Override
		public void run() {
			while(!serverSocket.isClosed()) {
				DatagramPacket addressPacket = new DatagramPacket(new byte[1], 1);
				byte[] receivedData = packetManager.receiveData(serverSocket, addressPacket);
				
				if (receivedData == null) {
					System.out.println("packet failed!");
					continue;
				}
				
				int type = packetManager.getPacketType(receivedData);
				
				if (type == GamePacketManager.PACKET_TYPE_TO_CLIENT_OTHER_PLAYER_CONNECT) {
					int[] pOtherPlayerGameId = new int[1];
					double[] otherPlayerOrientation = new double[3];
					packetManager.parseToClientOtherPlayerJoinData(receivedData, pOtherPlayerGameId, otherPlayerOrientation);
					
					addPlayer(pOtherPlayerGameId[0], otherPlayerOrientation[0], otherPlayerOrientation[1], otherPlayerOrientation[2]);
					System.out.println("[PLAYER::" + receivedData[1] + "] has joined! Orientation: (x: " + otherPlayerOrientation[0] + ", y: " + otherPlayerOrientation[1] + ", angle: " + otherPlayerOrientation[2]);
				}
				if (type == GamePacketManager.PACKET_TYPE_TO_CLIENT_OTHER_PLAYER_DISCONNECT) {
					System.out.println("[PLAYER::" + receivedData[1] + "] has left!");
					
					removePlayer(receivedData[1]);
				}
				if (type == GamePacketManager.PACKET_TYPE_TO_CLIENT_OTHER_PLAYER_MOVE_INPUT) {
					boolean[] outInput = new boolean[4];
					int otherPlayerGameId = packetManager.parseToClientOtherPlayerMoveInputData(receivedData, outInput);
					int index = otherClientToIndexLookup.get(otherPlayerGameId);
					MainCharacter updatedCharacter = otherPlayers[index];
					
					if (outInput[0]) updatedCharacter.turnLeft();
					else updatedCharacter.stopTurningLeft();
					
					if (outInput[1]) updatedCharacter.turnRight();
					else updatedCharacter.stopTurningRight();
					
					if (outInput[2]) updatedCharacter.moveForward();
					else updatedCharacter.stopMovingForward();
					
					if (outInput[3]) updatedCharacter.moveBackward();
					else updatedCharacter.stopMovingBackward();
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println("[CLIENT]: started client process");
		
		GameClient client = new GameClient();
		client.startGame();
	}
}
