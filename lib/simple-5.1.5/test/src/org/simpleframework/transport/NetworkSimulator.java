package org.simpleframework.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NetworkSimulator extends SocketChannel {
   private SocketChannel channel;
   private Socket socket;
   public NetworkSimulator(final int port, final int buffer) throws Exception{
      super(null);
      final ServerSocket server = new ServerSocket(port);
      new Thread(new Runnable() {
         public void run() {
            try {
               socket = server.accept();
               socket.setReceiveBufferSize(buffer);
            }catch(Exception e) {
               e.printStackTrace();
            }
         }
      }).start();
      SocketAddress address = new InetSocketAddress("localhost", port);
      channel = SocketChannel.open();
      channel.configureBlocking(false); // underlying socket must be non-blocking
      channel.connect(address);

      while(!channel.finishConnect()) { // wait to finish connection
         Thread.sleep(10);
      };
      channel.socket().setSendBufferSize(buffer);
   }
   
   public int available() throws IOException {
      return socket.getInputStream().available();
   }
   public void drainTo(OutputStream out, int count) throws IOException {
      InputStream in = socket.getInputStream();
      for(int i = 0; i < count; i++) {
         int octet = in.read();
         if(octet == -1) {
            throw new IOException("Socket closed");
         }
         out.write(octet);
      }
   }
   @Override
   public boolean connect(SocketAddress arg0) throws IOException {
      return false;
   }
   @Override
   public boolean finishConnect() throws IOException {
      return false;
   }
   @Override
   public boolean isConnected() {
      return false;
   }
   @Override
   public boolean isConnectionPending() {
      return false;
   }
   @Override
   public int read(ByteBuffer arg0) throws IOException {
      return channel.read(arg0);
   }
   @Override
   public long read(ByteBuffer[] arg0, int arg1, int arg2) throws IOException {
      return channel.read(arg0, arg1, arg2);
   }
   @Override
   public Socket socket() {
      return channel.socket();
   }
   @Override
   public int write(ByteBuffer arg0) throws IOException {
      return channel.write(arg0);
   }
   @Override
   public long write(ByteBuffer[] arg0, int arg1, int arg2) throws IOException {
      return channel.write(arg0, arg1, arg2);
   }
   @Override
   protected void implCloseSelectableChannel() throws IOException {
      return;
   }
   @Override
   protected void implConfigureBlocking(boolean arg0) throws IOException {
      return;
   }
}
