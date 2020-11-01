package servidor_sala;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	private int port;
	// Usamos set para no dejar repetir usuarios.
	private Set<String> userNames = new HashSet<>();
	private Set<UserThread> userThreads = new HashSet<>();
	
	public ChatServer(int port) {
		this.port = port;
	}

	public void execute() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Servidor escuchando puerto: " + port);

			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("Nuevo usuario conectado.");

				UserThread newUser = new UserThread(socket, this);
				userThreads.add(newUser);
				newUser.start();
			}

		} catch (IOException ex) {
			//System.out.println("Error en el servidor: " + ex.getMessage());
			//ex.printStackTrace();
		}
	}

	// Envia mensaje desde un usuario hacia otros.
	public void broadcast(String message, UserThread excludeUser) {
		
		for (UserThread aUser : userThreads) {
			if (aUser != excludeUser) {
				aUser.sendMessage(message);
			}
		}
	}
	
	// Envia mensaje desde un usuario hacia otro privado.
	public void privateBroadcast(String message, String privateReceptor) {
		for (UserThread aUser : userThreads) {
			if (aUser.getName().equals(privateReceptor)) {
				aUser.sendMessage(message);
			}
		}
	}

	// Guardamos el nickname del nuevo usuario conectado.
	public void addUserName(String userName) {
		userNames.add(userName);
	}

	// Cuando el usuario se desconecta lo revomeos de la lista de nicknames.
	public void removeUser(String userName, UserThread aUser) {
		boolean removed = userNames.remove(userName);
		if (removed) {
			userThreads.remove(aUser);
			System.out.println("El usuario [" + userName + "] ha salido.");
		}
	}

	public Set<String> getUserNames() {
		return this.userNames;
	}
	
	// Retornamos verdadero si existe al menso algun usuario.
	public boolean hasUsers() {
		return !this.userNames.isEmpty();
	}
}