package cliente;

import java.net.*;
import java.io.*;

// Escribir '-quit' para cerrar atencion de cliente.
public class ChatClient {
	private String hostname;
	private int port = 20000;
	private String userName;

	public ChatClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public void execute() {
		try {
			Socket socket = new Socket(hostname, port);

			System.out.println("Se ha conectado a la sala de chat.");

			(new ReadThread(socket, this)).start();
			(new WriteThread(socket, this)).start();

		} catch (UnknownHostException ex) {
			System.out.println("Servidor no encontrado: " + ex.getMessage());
		} catch (IOException ex) {
			System.out.println("I/O Error: " + ex.getMessage());
		}

	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return this.userName;
	}

	public static void main(String[] args) {
		String hostname = "localhost";
		int port = 20000;

		try {
			ChatClient client = new ChatClient(hostname, port);
			// client.setUserName(userName);
			client.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}