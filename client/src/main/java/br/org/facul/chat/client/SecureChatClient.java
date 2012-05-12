/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package br.org.facul.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.swing.JTextPane;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/**
 * Simple SSL chat client modified from {@link TelnetClient}.
 */
public class SecureChatClient {

    private final String host;
    private final int port;
    private String recivedMessage;
	private ChannelFuture lastWriteFuture;
	private Channel channel;
	private Bootstrap bootstrap;
	private JTextPane txtChat;
    
    public SecureChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public SecureChatClient(String host, int port, JTextPane txtChat) {
        this.host = host;
        this.port = port;
		this.txtChat = txtChat;
	}

	public void run() throws IOException {
        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new SecureChatClientPipelineFactory(this));

        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

        // Wait until the connection attempt succeeds or fails.
        channel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return;
        }

        // Read commands from the stdin.
        this.lastWriteFuture = null;
        
    }

    public void closeConnection() {
    	// Wait until all messages are flushed before closing the channel.
        if (lastWriteFuture != null) {
            lastWriteFuture.awaitUninterruptibly();
        }

        // Close the connection.  Make sure the close operation ends because
        // all I/O operations are asynchronous in Netty.
        channel.close().awaitUninterruptibly();

        // Shut down all thread pools to exit.
        bootstrap.releaseExternalResources();
    }
    
	public void sendMessage(String message) {
		lastWriteFuture = channel.write(message + "\r\n");
	}
	
	public void recivedMessage(String message) {
		this.recivedMessage = message;
		if (null != txtChat)
			txtChat.setText(txtChat.getText() + "\r\n" + message);
	}

    public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + SecureChatClient.class.getSimpleName() +
                    " <host> <port>");
            return;
        }

        // Parse options.
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        new SecureChatClient(host, port).run();
    }
}
