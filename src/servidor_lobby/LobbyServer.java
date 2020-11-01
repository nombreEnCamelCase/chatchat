package servidor_lobby;

import java.io.*;
import java.net.*;
import java.util.*;

import cliente.ChatClient;
import servidor_sala.ChatServer;
import servidor_sala.UserThread;
import servidor_lobby.UserThreadLobby;

public class LobbyServer {
	private int port;
	// Usamos set para no dejar repetir usuarios.
	private Set<String> userNames = new HashSet<>();
	private Set<UserThreadLobby> usersInLobby = new HashSet<>();
	private HashMap<String, Integer> userMapConnection = new HashMap<String, Integer>();
	private HashMap<String, ChatServer> availableRooms = new HashMap<String, ChatServer>();

	public LobbyServer(int port) {
		this.port = port;
	}

	public void execute() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Servidor de LOBBY escuchando puerto: " + port);

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


	// Guardamos el nickname del nuevo usuario conectado.
	public void addUserName(String userName) {
		userNames.add(userName);
	}

	// Cuando el usuario se desconecta lo revomeos de la lista de nicknames.
	public void removeUser(String userName, UserThreadLobby aUser) {
		boolean removed = userNames.remove(userName);
		if (removed) {
			usersInLobby.remove(aUser);
			System.out.println("El usuario [" + userName + "] se ha desconectado.");
		}
	}

	public Set<String> getUserNames() {
		return this.userNames;
	}

	// Retornamos verdadero si existe al menso algun usuario.
	public boolean hasUsers() {
		return !this.userNames.isEmpty();
	}

	public void createNewRoom(UserThreadLobby user, String room) {
		int value = 0;
		boolean userNotExists = false;
		try {

			// Si la sala que quiero crear no existe.
			if (!this.availableRooms.containsKey(room)) {

				// Si no existe el usuario en mi mapa, o existe pero tiene menos de 3
				// conexiones, procedo a crear la sala y meterlo.

				userNotExists = !this.userMapConnection.containsKey(user.getUsername());
				if (userNotExists || (value = this.userMapConnection.get(user.getUsername())) > 3) {
					ChatServer servidorSala = new ChatServer(0, room);
					servidorSala.execute();

					// Agrego la sala a mi mapa de salas activas.
					this.availableRooms.put(room, servidorSala);

					// Creo la conexion del usuario a la sala que acaba de crear.
					try {
						ChatClient client = new ChatClient("localhost", servidorSala.getPort());
						client.setUserName(user.getUsername());
						client.execute();
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (userNotExists) {
						this.userMapConnection.put(user.getUsername(), 0);
					} else
						this.userMapConnection.computeIfPresent(user.getUsername(), (k, v) -> v + 1);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void goToRoom(UserThreadLobby user, String room) {
		int value = 0;
		boolean userNotExists = false;
		try {

			// Si la sala que quiero entrar no existe
			if (!this.availableRooms.containsKey(room)) {

				// Si no existe el usuario en mi mapa, o existe pero tiene menos de 3
				// conexiones, procedo a crear la sala y meterlo.

				userNotExists = !this.userMapConnection.containsKey(user.getUsername());
				if (userNotExists || (value = this.userMapConnection.get(user.getUsername())) > 3) {
					
					try {
						ChatClient client = new ChatClient("localhost", this.availableRooms.get(room).getPort());
						client.execute();
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (userNotExists) {
						this.userMapConnection.put(user.getUsername(), 0);
					} else
						this.userMapConnection.computeIfPresent(user.getUsername(), (k, v) -> v + 1);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void showRoomList(UserThreadLobby receptor) {
		String buffer="";
		 for (Map.Entry<String,ChatServer> entry : this.availableRooms.entrySet())  
	            buffer += "Sala: "+entry.getValue().getNombreSala() +" Users: "+entry.getValue().getConnectedUsers()+"\n";
		 for (UserThreadLobby aUser : this.usersInLobby) {
				if (aUser == receptor) {
					aUser.sendMessage(buffer);
				}
			}
	}
	
	
}
