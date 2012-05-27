package br.org.facul.chat.client.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JScrollBar;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.Action;

import br.org.facul.chat.client.netty.SecureChatClient;
import br.org.facul.chat.client.socket.ChatClient;
import br.org.facul.chat.client.socket.ChatClientThread;

public class ChatClientImpl implements ChatClient {

	
	private Socket socket = null;
	private DataInputStream console = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client = null;
	
	private JFrame frmClient;
	private JTextField message;
	private JButton btnSend;
	private JTextField server;
	private JTextField nick;
	private JButton btnConnect;

	private final Action envia = new SwingAction(this);
	private JTextPane txtChat;
	private final Action conecta = new SwingAction_1(this);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatClientImpl window = new ChatClientImpl();
					window.frmClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChatClientImpl() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmClient = new JFrame();
		frmClient.setTitle("client");
		frmClient.setBounds(100, 100, 450, 300);
		frmClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmClient.getContentPane().setLayout(null);
		
		message = new JTextField();
		message.setBounds(79, 243, 267, 29);
		frmClient.getContentPane().add(message);
		message.setColumns(10);
		
		btnConnect = new JButton("conecta");
		btnConnect.setAction(conecta);
		btnConnect.setBounds(347, 9, 97, 29);
		frmClient.getContentPane().add(btnConnect);
		
		nick = new JTextField();
		nick.setBounds(212, 6, 134, 33);
		frmClient.getContentPane().add(nick);
		nick.setColumns(10);
		
		server = new JTextField();
		server.setBounds(75, 6, 78, 33);
		frmClient.getContentPane().add(server);
		server.setColumns(10);
		
		btnSend = new JButton("Envia");
		btnSend.setAction(envia);
		btnSend.setBounds(347, 244, 97, 29);
		frmClient.getContentPane().add(btnSend);
		
		txtChat = new JTextPane();
		txtChat.setBounds(6, 43, 422, 188);
		frmClient.getContentPane().add(txtChat);
		
		JScrollBar scrollBar = new JScrollBar();
		scrollBar.setBounds(429, 43, 15, 189);
		frmClient.getContentPane().add(scrollBar);
		
		JLabel lblNewLabel = new JLabel("Mensagem");
		lblNewLabel.setBounds(6, 249, 73, 16);
		frmClient.getContentPane().add(lblNewLabel);
		
		JLabel lblServidor = new JLabel("Servidor");
		lblServidor.setBounds(18, 14, 61, 16);
		frmClient.getContentPane().add(lblServidor);
		
		JLabel lblNick = new JLabel("Nick");
		lblNick.setBounds(178, 14, 37, 16);
		frmClient.getContentPane().add(lblNick);

	}
	
	
	public void connect(String serverName, int serverPort) {
		println("Establishing connection. Please wait ...");
		try {
			socket = new Socket(serverName, serverPort);
			println("Connected: " + socket);
			open();
			btnSend.enable();
			btnConnect.disable();

		} catch (UnknownHostException uhe) {
			println("Host unknown: " + uhe.getMessage());
		} catch (IOException ioe) {
			println("Unexpected exception: " + ioe.getMessage());
		}
	}

	private void send() {
		try {
			streamOut.writeUTF(message.getText());
			streamOut.flush();
			message.setText("");
		} catch (IOException ioe) {
			println("Sending error: " + ioe.getMessage());
			close();
		}
	}

	public void handle(String msg) {
		if (msg.equals(".bye")) {
			println("Good bye. Press RETURN to exit ...");
			close();
		} else
			println(msg);
	}

	public void open() {
		try {
			streamOut = new DataOutputStream(socket.getOutputStream());
			client = new ChatClientThread(this, socket);
		} catch (IOException ioe) {
			println("Error opening output stream: " + ioe);
		}
	}

	public void close() {
		try {
			if (streamOut != null)
				streamOut.close();
			if (socket != null)
				socket.close();
		} catch (IOException ioe) {
			println("Error closing ...");
		}
		client.close();
		client.stop();
	}

	private void println(String msg) {
		txtChat.setText(txtChat.getText() + msg + "\n");
	}

	
	private class SwingAction extends AbstractAction {
		
		String messsage;
		ChatClientImpl chat;

		public SwingAction(ChatClientImpl chat) {
			putValue(NAME, "Envia");
			putValue(SHORT_DESCRIPTION, "Some short description");
			this.chat = chat;
		}
		public void actionPerformed(ActionEvent e) {
			chat.send();
		}
	}
	private class SwingAction_1 extends AbstractAction {
		ChatClientImpl chat;

		public SwingAction_1(ChatClientImpl chat) {
			putValue(NAME, "Conecta");
			putValue(SHORT_DESCRIPTION, "Some short description");
			this.chat = chat;
		}
		
		public void actionPerformed(ActionEvent e) {
			chat.connect(server.getText(), 7000);
		}
	}
	
	public void stop() {
		
		try {
			if (console != null)
				console.close();
			if (streamOut != null)
				streamOut.close();
			if (socket != null)
				socket.close();
		} catch (IOException ioe) {
			System.out.println("Error closing ...");
		}
		client.close();
		client.stop();
	}
}
