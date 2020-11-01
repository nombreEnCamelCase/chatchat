package servidor_lobby;

import servidor_sala.ChatServer;

public class RunMasterServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int port = 20000;

		try {
			LobbyServer server = new LobbyServer(port);
			server.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
