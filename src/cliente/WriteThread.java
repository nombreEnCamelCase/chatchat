package cliente;

import java.io.*;
import java.net.*;
import java.util.Scanner;

// Este thread tiene la responsabilidad de leer desde el input que realice el usuario y enviarlo al servidor.
// Esto corre infitamente en un demonio, hasta que el usuario ingrese '-quit' para desconectarse de la sala de chat y eliminar el thread.

public class WriteThread extends Thread {
	private PrintWriter writer;
	private Socket socket;
	private ChatClient client;
	private Scanner localScan;

	public WriteThread(Socket socket, ChatClient client) {
		this.socket = socket;
		this.client = client;
		this.localScan = new Scanner(System.in);

		try {
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);
		} catch (IOException ex) {
			System.out.println("Error obteniendo el output stream: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {

		String userName = getLineFromUserByInput("Ingrese nickname");

		client.setUserName(userName);
		writer.println(userName);

		System.out.println("Ha entrado al chat, puede comenzar a hablar.");

		String text, msje;
		
		do {
			text = getLineFromUserByInput("");
			if (isValidMessage(text)) {
				msje = "[yo]: " + text;
				System.out.println(msje);
				writer.println(text);
			}

		} while (!text.equals("-quit"));

		try {
			this.localScan.close();
			this.socket.close();
		} catch (IOException ex) {

			System.out.println("Error al escribir al server " + ex.getMessage());
		}
	}

	public String getLineFromUserByInput(String inputLabel) {
		String resultLine = "";
		try {

			if (!inputLabel.equals("") && !inputLabel.equals("\n"))
				System.out.print(inputLabel + ": ");

			resultLine = this.localScan.nextLine();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resultLine;
	}

	private boolean isValidMessage(String message) {
		return !message.equals("") && !message.equals("\n") && !message.equals("\r");
	}

}