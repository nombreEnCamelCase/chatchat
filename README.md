# chatchat
Repo destinado al proyecto de sala de chat

PASOS:
	-1 Run As JavaApplication "RunMasterServer"
	-2 Run As JavaApplication "RunNewClient"


	= Comandos posibles dentro de una sala:
		=> -quit   // Desconexion de sala, vuelve al lobby.
	 	=> -wh(NOMBRE_USUARIO) MENSAJE // Comando para enviar un whisper.
	 	=> -download // Comando para descargar el array de mensajes persistidos del cliente
.
	= Comandos posibles dentro del lobby:
		=> -create NOMBRE_SALA // Crea nueva sala si se puede y te ingresa directamente.
		=> -goto NOMBRE_SALA // Va a una sala existente.
		=> -chatrooms // Muestra salas publicas activas y sus conectados.
		=> -quit  // Desconexion total.