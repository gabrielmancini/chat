package br.org.facul.chat.swing;

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
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;

import br.org.facul.chat.client.SecureChatClient;

public class Chat {

	private JFrame frmClient;
	private JTextField message;
	private JButton btnSend;
	private JTextField server;
	private JTextField nick;
	private JButton btnConnect;
	private SecureChatClient client;
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
					Chat window = new Chat();
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
	public Chat() {
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
	private class SwingAction extends AbstractAction {
		
		String messsage;
		Chat chat;

		public SwingAction(Chat chat) {
			putValue(NAME, "Envia");
			putValue(SHORT_DESCRIPTION, "Some short description");
			this.chat = chat;
		}
		public void actionPerformed(ActionEvent e) {
			chat.client.sendMessage(chat.message.getText());
			chat.message.setText("");
		}
	}
	private class SwingAction_1 extends AbstractAction {
		Chat chat;

		public SwingAction_1(Chat chat) {
			putValue(NAME, "Conecta");
			putValue(SHORT_DESCRIPTION, "Some short description");
			this.chat = chat;
		}
		
		public void actionPerformed(ActionEvent e) {
			try {
				this.chat.client = new SecureChatClient(chat.server.getText(), 8443, chat.txtChat);
				this.chat.client.run();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
