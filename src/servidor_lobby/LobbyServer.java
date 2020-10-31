package servidor_lobby;

import java.io.*;
import java.net.*;
import java.util.*;

import servidor_sala.ChatServer;
import servidor_lobby.UserThreadLobby;

public class LobbyServer {
	private int port;
	// Usamos set para no dejar repetir usuarios.
	private Set<String> userNames = new HashSet<>();
	private Set<UserThreadLobby> usersInLobby = new HashSet<>();

	public LobbyServer(int port) {
		this.port = port;
	}

	public void execute() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Servidor escuchando puerto: " + port);

			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("Nuevo usuario conectado.");

				UserThreadLobby newUser = new UserThreadLobby(socket, this);
				usersInLobby.add(newUser);
				newUser.start();
			}

		} catch (IOException ex) {
			System.out.println("Error en el servidor: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {

		int port = 20000;

		try {
			ChatServer server = new ChatServer(port);
			server.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*	EL BROADCAST NO LO NECESITO PORQUE  NO TENGO QUE AVISARLE NADA A LOS OTROS USURAIOS.*/
//	// Envia mensaje desde un usuario hacia otros.
//	void broadcast(String message, UserThreadLobby excludeUser) {
//		for (UserThreadLobby aUser : usersInLobby) {
//			if (aUser != excludeUser) {
//				aUser.sendMessage(message);
//			}
//		}
//	}

	// Guardamos el nickname del nuevo usuario conectado.
	public void addUserName(String userName) {
		userNames.add(userName);
	}

	// Cuando el usuario se desconecta lo revomeos de la lista de nicknames.
	public void removeUser(String userName, UserThreadLobby aUser) {
		boolean removed = userNames.remove(userName);
		if (removed) {
			usersInLobby.remove(aUser);
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
