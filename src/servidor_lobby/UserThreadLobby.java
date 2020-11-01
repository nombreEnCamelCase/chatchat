package servidor_lobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import servidor_lobby.LobbyServer;

public class UserThreadLobby extends Thread {

	private Socket socket;
	private LobbyServer server;
	private PrintWriter writer;
	private String username;

	public UserThreadLobby(Socket socket, LobbyServer server) {
		this.socket = socket;
		this.server = server;
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);
			
			this.username = reader.readLine();
			server.addUserName(username);

			String serverMessage;
			// server.broadcast(serverMessage, this);

			String clientMessage;

			do {
				clientMessage = reader.readLine();

				if (isValidMessage(clientMessage))
					if (!isCommandAction(clientMessage)) {
						serverMessage = "[" + username + "]: " + clientMessage;
					// server.broadcast(serverMessage, this);
				}

			} while (!clientMessage.equals("-quit"));

			server.removeUser(username, this);
			socket.close();

			serverMessage = username + " se ha desconectado.";
			// server.broadcast(serverMessage, this);

		} catch (IOException ex) {
			System.out.println("Error en el  UserThread: " + ex.getMessage());
			ex.printStackTrace();
		}
	}


	// Envia un mensaje al cliente propiamente dicho.
	public void sendMessage(String message) {
		writer.println(message);
	}

	private boolean isValidMessage(String message) {
		return !message.equals("") && !message.equals("\n") && !message.equals("\r");
	}

	public String getUsername() {
		return this.username;
	}

	private boolean isCommandAction(String message) {
		// Debo verificar que no sea un commando de accion del usuario.
		// Chain of responsability.
		if(message.equals("-quit")) {
			System.out.println("QUIERE SALIR");
			return true;
		}
		
		if(message.startsWith("-create")) {
			System.out.println("QUIERE CREAR SALA");
			String roomName = message.split("-create ")[1];
			server.createNewRoom(this, roomName);
			return true;
		}
		
		if(message.startsWith("-chatrooms")) {
			System.out.println("QUIERE VER SALAS");
			server.showRoomList(this);
			return true;
		}
			
		if(message.startsWith("-goto")) {
			System.out.println("QUIERE IR A SALA");
			String roomName = message.split("-goto ")[1];
			server.goToRoom(this, roomName);
			return true;
		}
		
		return false;
	}
}