package cliente;

public class RunNewClient {

	public static void main(String[] args) {
		// Conecta directamente al LOBBY que es el puerto conocido.
		String hostname = "localhost";
		int port = 20000;

		try {
			ChatClient client = new ChatClient(hostname, port);
			client.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
