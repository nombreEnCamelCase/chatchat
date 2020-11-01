package runners;

import servidor_sala.ChatServer;

public class RunServer {

	public static void main(String[] args) {

		int port = 20000;

		try {
			ChatServer server = new ChatServer(port);
			server.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
