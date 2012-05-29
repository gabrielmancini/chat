package br.org.facul.chat.client.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientThread extends Thread {
	private Socket socket = null;
	private ChatClient client = null;
	private DataInputStream streamIn = null;

	public ChatClientThread(ChatClient _client, Socket _socket) {
		client = _client;
		socket = _socket;
		abrir();
		start(); //Inicia a thred
	}

	public void abrir() {
		try {
			streamIn = new DataInputStream(socket.getInputStream());
		} catch (IOException ioe) {
			System.out.println("Error getting input stream: " + ioe);
			client.parar();
		}
	}

	public void fechar() {
		try {
			if (streamIn != null)
				streamIn.close();
		} catch (IOException ioe) {
			System.out.println("Error closing input stream: " + ioe);
		}
	}

	/***
	 * Implemetação do metodo run da thred
	 */
	public void run() {
		while (true) {
			try {
				client.receber(streamIn.readUTF());
			} catch (IOException ioe) {
				System.out.println("Listening error: " + ioe.getMessage());
				client.parar();
			}
		}
	}
}

