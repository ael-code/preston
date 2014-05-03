package org.simpleframework.transport;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

class Console extends SocketChannel {
   
   private final WritableByteChannel out;
   private final ReadableByteChannel in;

   public Console() {
      super(null);
      this.out = Channels.newChannel(System.out);
      this.in = Channels.newChannel(System.in);
   }

   public void flush() throws IOException {
      System.out.flush();
   }

   @Override
   public int read(ByteBuffer buffer) throws IOException {
      return in.read(buffer);
   }

   @Override
   public int write(ByteBuffer buffer) throws IOException {
      return out.write(buffer);
   }

   @Override
   public boolean connect(SocketAddress remote) throws IOException {
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
   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      return 0;
   }

   @Override
   public Socket socket() {
      return null;
   }

   @Override
   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return 0;
   }

   @Override
   protected void implCloseSelectableChannel() throws IOException {
      in.close();
      out.close();      
   }

   @Override
   protected void implConfigureBlocking(boolean block) throws IOException {      
   }
}
