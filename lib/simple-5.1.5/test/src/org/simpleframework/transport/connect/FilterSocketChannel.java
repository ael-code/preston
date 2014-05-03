package org.simpleframework.transport.connect;


import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

// Can this be selected with?????
public class FilterSocketChannel extends SocketChannel {

    protected SocketChannel channel;

    public FilterSocketChannel(SocketChannel channel) {    
        super(channel.provider());
        this.channel = channel;
    }

    @Override
    public Socket socket() {
        return channel.socket();
    }

    @Override
    public boolean isConnected() {
       return channel.isConnected();
    }

    @Override
    public boolean isConnectionPending() {
        return channel.isConnectionPending();
    }

    @Override
    public boolean connect(SocketAddress remote) throws IOException {
        return channel.connect(remote);
    }

    @Override
    public boolean finishConnect() throws IOException {
        return channel.finishConnect();
    }
    
    @Override
    public int read(ByteBuffer dst) throws IOException {
       return channel.read(dst);
    }    

    @Override
    public long read(ByteBuffer[] array, int offset, int length) throws IOException {
        long count = 0;
        
        for(int i = offset; i < length; i++) {
            if(array[i].hasRemaining()) {
                int done = read(array[i]);
                
                if (done > 0) {
                    count += done;
                    
                    if(!array[i].hasRemaining()) {
                        break;
                    }
                } else {
                    if(done < 0 && count == 0){
                        count = -1;
                    }
                    break;
                }
            }
        }
        return count;
    }


    @Override
    public int write(ByteBuffer src) throws IOException {     
       return channel.write(src);
    }

    @Override
    public long write(ByteBuffer[] array, int offset, int length) throws IOException {    
        long count = 0;
        
        for(int i = offset; i < length; i++) {
            if (array[i].hasRemaining()) {
                int done = write(array[i]);
                
                if (done > 0) {
                    count += done;
                    
                    if(!array[i].hasRemaining()) {                    
                        break;
                    }
                } else {                
                    break;
                }
            }
        }
        return count;
    }

   @Override
   protected void implCloseSelectableChannel() throws IOException {
      channel.close();
   }

   @Override
   protected void implConfigureBlocking(boolean block) throws IOException {
      channel.configureBlocking(block);
   }
}
