package br.org.facul.chat.client.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import br.org.facul.chat.client.socket.ChatClient;
import br.org.facul.chat.client.socket.ChatClientThread;

public class ChatClientImpl implements ChatClient {

	
	private Socket socket = null;
	private DataInputStream console = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client = null;
	
	private JFrame frmClient;
	private JTextField txtMessage;
	private JTextField txtHost;
	private JTextField txtNick;
	private JTextField txtPorta;
	private JTextPane txtChat;
	private JScrollBar scrollBar;
	private JButton btnConnect;
	private JButton btnSend;

	private final Action envia = new AEnvia(this);
	private final Action conecta = new AConecta(this);

	/**
	 * Inicia a aplicação
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
	 * Cria a Aplicação
	 */
	public ChatClientImpl() {
		initialize();
	}

	/**
	 * Inicializa os valores da janela swing
	 */
	private void initialize() {
		frmClient = new JFrame();
		frmClient.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				parar();
			}
		});
		frmClient.setTitle("client");
		frmClient.setBounds(100, 100, 600, 400);
		frmClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmClient.getContentPane().setLayout(null);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					enviar();
				}
			}
		});
		txtMessage.setBounds(79, 342, 417, 29);
		frmClient.getContentPane().add(txtMessage);
		txtMessage.setColumns(10);
		
		btnConnect = new JButton("conecta");
		btnConnect.setAction(conecta);
		btnConnect.setBounds(497, 9, 97, 29);
		frmClient.getContentPane().add(btnConnect);
		
		txtNick = new JTextField();
		txtNick.setBounds(362, 6, 134, 33);
		frmClient.getContentPane().add(txtNick);
		txtNick.setColumns(10);
		
		txtHost = new JTextField();
		txtHost.setBounds(75, 6, 78, 33);
		frmClient.getContentPane().add(txtHost);
		txtHost.setColumns(10);
		
		btnSend = new JButton("Envia");
		btnSend.setAction(envia);
		btnSend.setBounds(497, 343, 97, 29);
		frmClient.getContentPane().add(btnSend);
		
		txtChat = new JTextPane();
		txtChat.setBounds(6, 43, 574, 298);
		frmClient.getContentPane().add(txtChat);
		
		scrollBar = new JScrollBar();
		scrollBar.setBounds(579, 43, 15, 298);
		
		
		frmClient.getContentPane().add(scrollBar);
		
		JLabel lblNewLabel = new JLabel("Mensagem");
		lblNewLabel.setBounds(6, 348, 73, 16);
		frmClient.getContentPane().add(lblNewLabel);
		
		JLabel lblServidor = new JLabel("Servidor");
		lblServidor.setBounds(18, 14, 61, 16);
		frmClient.getContentPane().add(lblServidor);
		
		JLabel lblNick = new JLabel("Nick");
		lblNick.setBounds(328, 14, 37, 16);
		frmClient.getContentPane().add(lblNick);
		
		JLabel lblPorta = new JLabel("Porta");
		lblPorta.setBounds(186, 14, 37, 16);
		frmClient.getContentPane().add(lblPorta);
		
		txtPorta = new JTextField();
		txtPorta.setBounds(231, 8, 85, 28);
		frmClient.getContentPane().add(txtPorta);
		txtPorta.setColumns(10);

	}
	
	
	public void conectar(String host, int porta) {
		println("Estabelecendo conexão");
		try {
			socket = new Socket(host, porta);
			println("Conectado: " + socket);
			abrir();
			btnSend.enable();
			btnConnect.disable();

		} catch (UnknownHostException uhe) {
			println("Host desconhecido: " + uhe.getMessage());
		} catch (IOException ioe) {
			println("Exception desconhecida: " + ioe.getMessage());
		}
	}

	private void enviar() {
		try {
			streamOut.writeUTF("[" + txtNick.getText() + "]" + txtMessage.getText());
			streamOut.flush();
			txtMessage.setText("");
		} catch (IOException ioe) {
			println("Erro no envio da mensagem: " + ioe.getMessage());
			fechar();
		}
	}

	public void receber(String msg) {
		if (msg.equals(".sair")) {
			println("Saindo do chat ...");
			fechar();
		} else
			println(msg);
	}

	public void abrir() {
		try {
			streamOut = new DataOutputStream(socket.getOutputStream());
			client = new ChatClientThread(this, socket);
		} catch (IOException ioe) {
			println("Erro ao abrir canal de comunicação: " + ioe);
		}
	}

	public void fechar() {
		try {
			if (streamOut != null)
				streamOut.close();
			if (socket != null)
				socket.close();
		} catch (IOException ioe) {
			println("Erro ao sair ...");
		}
		client.fechar();
		client.stop();
	}

	private void println(String msg) {
		txtChat.setText(txtChat.getText() + msg + "\n");
	}

	
	@SuppressWarnings("serial")
	private class AEnvia extends AbstractAction {
		
		ChatClientImpl chat;

		public AEnvia(ChatClientImpl chat) {
			putValue(NAME, "Envia");
			this.chat = chat;
		}
		public void actionPerformed(ActionEvent e) {
			chat.enviar();
		}
	}
	@SuppressWarnings("serial")
	private class AConecta extends AbstractAction {
		ChatClientImpl chat;

		public AConecta(ChatClientImpl chat) {
			putValue(NAME, "Conecta");
			this.chat = chat;
		}
		
		public void actionPerformed(ActionEvent e) {
			chat.conectar(txtHost.getText(), Integer.parseInt(txtPorta.getText()));
		}
	}
	
	public void parar() {
		
		try {
			if (console != null)
				console.close();
			if (streamOut != null)
				streamOut.close();
			if (socket != null)
				socket.close();
		} catch (IOException ioe) {
			System.out.println("Erro ao parar a comunicação ...");
		}
		client.fechar();
		client.stop();
	}
}
