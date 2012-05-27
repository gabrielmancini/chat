package br.org.facul.chat.client.socket;

public interface ChatClient {

	void stop();

	void handle(String readUTF);

}
