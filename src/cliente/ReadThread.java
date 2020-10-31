package cliente;

import java.io.*;
import java.net.*;

/*
 * Este thread tiene la responsabilidad de leer el input del servidor e imprimirlo en la consola. 
 * Este demonio corre mientras el cliente no este desconectado.
 */
public class ReadThread extends Thread {
	private BufferedReader reader;
	private Socket socket;
	private ChatClient client;

	public ReadThread(Socket socket, ChatClient client) {
		this.socket = socket;
		this.client = client;

		try {
			InputStream input = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
		} catch (IOException ex) {
			System.out.println("Error al obtener el inputstream: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				String response = reader.readLine();
				System.out.println("\n" + response);

//				if (client.getUserName() != null) {
//					System.out.print("[" + client.getUserName() + "]: asd");
//				}
			} catch (IOException ex) {
				System.out.println("Error al leer desde el server: " + ex.getMessage());
				ex.printStackTrace();
				break;
			}
		}
	}
}