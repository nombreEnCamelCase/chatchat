package servidor_sala;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

// Este thread maneja la coneccion para cada cliente conectado
// asi el servidor puede manejar multiples clientes al mismo tiempo.

public class UserThread extends Thread {
	private Socket socket;
	private ChatServer server;
	private PrintWriter writer;
	private String userName;
	private List<Mensaje> mensajes = new LinkedList<Mensaje>();
	
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

			userName = reader.readLine();
			server.addUserName(userName);

			String serverMessage = "El usuario [ " + userName + " ] ha ingresado a la sala de chat.";
			server.broadcast(serverMessage, this);

			String clientMessage;

			do {
				clientMessage = reader.readLine();
				
				if (isValidMessage(clientMessage)) {
					
					if(!isCommandAction(clientMessage))
					{
						serverMessage = "[" + userName + "]: " + clientMessage;
						server.broadcast(serverMessage, this);
					}
					else {
						// Metodo para manejar responsabilidades.
						// Metodo magico tipo swtich
						attendAction(clientMessage);
						System.out.println("Se intento tirar un comando de sala.");
					}

				}

			} while (!isCommandActionQuit(clientMessage));
			
			

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
	
	
	/*
	 * El siguiente metodo sirve para analizar si es un commando de accion el que mando el usuario y no un mensaje.
	 * 
	 * 	= Comandos posibles dentro de una sala:
		=> -quit   // Desconexion de sala, vuelve al lobby.
	 	=> -wh(NOMBRE_USUARIO) MENSAJE // Comando para enviar un whisper.
	 	=> -download // Comando para descargar el array de mensajes persistidos del cliente
	 * 
	 * */
	private boolean isCommandAction(String message) {
		// Debo verificar que no sea un commando de accion del usuario.
		// Chain of responsability.
		if(message.equals("-quit")) {
			System.out.println("QUIERE SALIR");
			return true;
		}
		
		if(message.startsWith("-wh(")) {
			System.out.println("QUIERE WHISPEAR");
			return true;
		}
		
		
		if(message.startsWith("-download")) {
			System.out.println("QUIERE DESCARGAR HISTORIAL");
			return true;
		}
		
		return false;
	}
	
	
	
	private boolean isCommandActionQuit(String message) {
		return message.equals("-quit");
	}
	
	private void attendAction( String message ){
		if(message.startsWith("-wh(")) {

			String usuario = message.substring(message.indexOf("(")+1,message.indexOf(")"));

			String messageFinal = "[" + userName + "]: " + message.substring(message.indexOf(")")+1);

			server.privateBroadcast(messageFinal,usuario);
		}
		
		//if(message.equals("-download")) {
			//accion descargar 
		//}
	}

	public String getUserName() {
		return userName;
	}
	
//	public void addMessage(String msje, String receptPriv) {
//		this.mensajes.add(new Mensaje(msje,receptPriv));
//	}

	
}