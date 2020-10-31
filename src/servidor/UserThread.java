package servidor;

import java.io.*;
import java.net.*;

// Este thread maneja la coneccion para cada cliente conectado, asi el servidor puede manejar multiples clientes al mismo tiempo.

public class UserThread extends Thread {
	private Socket socket;
	private ChatServer server;
	private PrintWriter writer;

	public UserThread(Socket socket, ChatServer server) {
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
			server.broadcast(serverMessage, this);

			String clientMessage;

			do {
				clientMessage = reader.readLine();

				if (!clientMessage.equals("") && !clientMessage.equals("\n") && !clientMessage.equals("\r")) {
					serverMessage = "[" + userName + "]: " + clientMessage;
					server.broadcast(serverMessage, this);
				}

			} while (!clientMessage.equals("-quit"));

			server.removeUser(userName, this);
			socket.close();

			serverMessage = userName + " ha salido del chat.";
			server.broadcast(serverMessage, this);

		} catch (IOException ex) {
			System.out.println("Error en el  UserThread: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	// Envia una lista de usuarios online al usuario que recien entra.
	void printUsers() {
		if (server.hasUsers()) {
			writer.println("Usuarios conectados: " + server.getUserNames());
		} else {
			writer.println("No hay usuarios conectados todavia.");
		}
	}

	// Envia un mensaje al cliente propiamente dicho.
	void sendMessage(String message) {
		writer.println(message);
	}
}