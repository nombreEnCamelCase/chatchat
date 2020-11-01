package runners;

import cliente.ChatClient;

public class RunNewClient {
	public static void main(String[] args) {
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
