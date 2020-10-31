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

			printUsers();

			String userName = reader.readLine();
			server.addUserName(userName);

			String serverMessage = "El usuario [ " + userName + " ] ha ingresado a la sala de chat.";
			//server.broadcast(serverMessage, this);

			String clientMessage;

			do {
				clientMessage = reader.readLine();

				if (isValidMessage(clientMessage)) {
					serverMessage = "[" + userName + "]: " + clientMessage;
					//server.broadcast(serverMessage, this);
				}

			} while (!clientMessage.equals("-quit"));

			server.removeUser(userName, this);
			socket.close();

			serverMessage = userName + " se ha desconectado.";
			//server.broadcast(serverMessage, this);

		} catch (IOException ex) {
			System.out.println("Error en el  UserThread: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	// Envia una lista de usuarios online al usuario que recien entra.
	public void printUsers() {
		if (server.hasUsers()) {
			writer.println("Usuarios conectados: " + server.getUserNames());
		} else {
			writer.println("No hay usuarios conectados todavia.");
		}
	}

	// Envia un mensaje al cliente propiamente dicho.
	public void sendMessage(String message) {
		writer.println(message);
	}
	
	private boolean isValidMessage(String message) {
		return !message.equals("") && !message.equals("\n") && !message.equals("\r");
	}
}