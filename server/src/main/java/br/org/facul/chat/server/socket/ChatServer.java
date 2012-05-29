package br.org.facul.chat.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer implements Runnable {
	
	private ChatServerThread clients[] = new ChatServerThread[10];
	private ServerSocket server = null;
	private Thread thread = null;
	private int clientCount = 0;

	public ChatServer(int porta) {
		try {
			System.out
					.println("Alocando a porta para a comunicação " + porta + ", aguarde  ...");
			server = new ServerSocket(porta);
			System.out.println("Servidor Iniciado: " + server);
			iniciar();
		} catch (IOException ioe) {
			System.out.println("Não foi possivel alocar a porta " + porta + ": "
					+ ioe.getMessage());
		}
	}

	/***
	 * Implementa o run do runnable
	 */
	public void run() {
		while (thread != null) {
			try {
				System.out.println("Aguardando um cliente ...");
				adicionarThread(server.accept());
			} catch (IOException ioe) {
				System.out.println("Erro ao aceitar um cliente: " + ioe);
				parar();
			}
		}
	}

	private int buscarCliente(int ID) {
		for (int i = 0; i < clientCount; i++)
			if (clients[i].getID() == ID)
				return i;
		return -1;
	}

	public synchronized void receber(int ID, String msg) {
		if (msg.equals(".sair")) {
			clients[buscarCliente(ID)].enviar(".sair");
			remover(ID);
		} else
			for (int i = 0; i < clientCount; i++)
				clients[i].enviar(msg);
	}

	public synchronized void remover(int ID) {
		int pos = buscarCliente(ID);
		if (pos >= 0) {
			ChatServerThread toTerminate = clients[pos];
			System.out.println("Removendo cliente [" + ID + "] na posição " + pos);
			if (pos < clientCount - 1)
				for (int i = pos + 1; i < clientCount; i++)
					clients[i - 1] = clients[i];
			clientCount--;
			try {
				toTerminate.fechar();
			} catch (IOException ioe) {
				System.out.println("Error ao fechar a thred: " + ioe);
			}
			toTerminate.stop();
		}
	}

	private void adicionarThread(Socket socket) {
		if (clientCount < clients.length) {
			System.out.println("Cliente aceito: " + socket);
			clients[clientCount] = new ChatServerThread(this, socket);
			try {
				clients[clientCount].abrir();
				clients[clientCount].start();
				clientCount++;
			} catch (IOException ioe) {
				System.out.println("Erro ao criar a thred: " + ioe);
			}
		} else
			System.out.println("Cliente recusado: maximo " + clients.length
					+ " atingido.");
	}

	public void iniciar() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void parar() {
		if (thread != null) {
			thread.stop();
			thread = null;
		}
	}

	public static void main(String args[]) {
		ChatServer server = null;
		if (args.length != 1)
			System.out.println("ERRO: Informe a porta de comunicação");
		else
			server = new ChatServer(Integer.parseInt(args[0]));
	}
}