package org.simpleframework.transport.connect;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;

/**
 * A secure socket channel implementation adding SSL engine based cryprography
 * to an adapted non-secure concrete <code>SocketChannel</code>
 * implementation.
 * 
 * <p>
 * This implementation extends abstract <code>SocketChannel</code> and
 * forwards applicable calls to methods of the adapted concrete implementation.
 * It also implements <code>AdaptableChannel</code> as selectors typically
 * don't accept channel implementations from other vendors, so the selector
 * registration must be done with the adaptee channel.
 * </p>
 * 
 * <p>
 * The additional <code>SecureChannel</code> methods help handshake and
 * shutdown even though they can also be handled by the read, write and close
 * methods. Note that the handshake method performs only one such channel read
 * or write operation during each call that is enabled by the ready set
 * parameter. Any other way of action seems to cause brosers to block
 * occasionally.
 * </p>
 * 
 * @author Ilkka Priha
 */
public class SecureSocketChannel extends FilterSocketChannel{

    /**
     * The unsecure socket channel.
     */
    private SocketChannel channel;

    /**
     * The SSL engine to apply.
     */
    private SSLEngine engine;

    /**
     * The active SSL session.
     */
    private SSLSession sslSession;

    /**
     * The minimum cache size.
     */
    private int minCacheSize;

    /**
     * The decrypted input cache.
     */
    private ByteBuffer[] inputCache;

    /**
     * The minimum buffer size.
     */
    private int minBufferSize;

    /**
     * The encrypted input buffer.
     */
    private ByteBuffer[] inputBuffer;

    /**
     * The encrypted output buffer.
     */
    private ByteBuffer[] outputBuffer;

    /**
     * An empty buffer for handshaking.
     */
    private ByteBuffer emptyBuffer;

    /**
     * The engine handshake status.
     */
    private HandshakeStatus handshake;

    /**
     * The initial handshake ops.
     */
    private int initialized = -1;

    /**
     * The engine shutdown flag.
     */
    private boolean shutdown;

    /**
     * Construct a new channel.
     * 
     * @param channel the unsecure socket channel.
     * @param engine the SSL engine.
     */
    public SecureSocketChannel(SocketChannel channel, SSLEngine engine) {    
        super(channel);
        this.engine = engine;
    }

    public synchronized int read(ByteBuffer dst) throws IOException
    {
        if (channel.socket().isInputShutdown())
        {
            throw new ClosedChannelException();
        }
        else if (initialized != 0)
        {
            handshake(SelectionKey.OP_READ);
            return 0;
        }
        else if (shutdown)
        {
            shutdown();
            return 0;
        }
        else if (engine.isInboundDone())
        {
            return -1;
        }
        else if ((fill(inputBuffer[0]) < 0) && (inputBuffer[0].position() == 0))
        {
            return -1;
        }

        SSLEngineResult result;
        Status status;
        do
        {
            if (!prepare(inputCache, minCacheSize))
            {
                // Overflow!
                break;
            }

            inputBuffer[0].flip();
            try
            {
                result = engine.unwrap(inputBuffer[0], inputCache[0]);
            }
            finally
            {
                inputBuffer[0].compact();
                inputCache[0].flip();
            }

            status = result.getStatus();
            if ((status == Status.OK) || (status == Status.BUFFER_UNDERFLOW))
            {
                if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK)
                {
                    runTasks();
                }
            }
            else
            {
                if (status == Status.CLOSED)
                {
                    shutdown();
                }

                throw new IOException("Read error '" + result.getStatus()
                    + '\'');
            }
        } while ((inputBuffer[0].position() != 0)
            && (status != Status.BUFFER_UNDERFLOW));

        int n = inputCache[0].remaining();
        if (n > 0)
        {
            if (n > dst.remaining())
            {
                n = dst.remaining();
            }
            for (int i = 0; i < n; i++)
            {
                dst.put(inputCache[0].get());
            }
        }
        return n;
    }

    public synchronized int write(ByteBuffer src) throws IOException
    {
        if (channel.socket().isOutputShutdown())
        {
            throw new ClosedChannelException();
        }
        else if (initialized != 0)
        {
            handshake(SelectionKey.OP_WRITE);
            return 0;
        }
        else if (shutdown)
        {
            shutdown();
            return 0;
        }

        // Check how much to write.
        int t = src.remaining();
        int n = 0;

        // Write as much as we can.
        SSLEngineResult result;
        Status status;
        do
        {
            if (!prepare(outputBuffer, minBufferSize))
            {
                // Overflow!
                break;
            }

            inputBuffer[0].flip();
            try
            {
                result = engine.wrap(src, outputBuffer[0]);
            }
            finally
            {
                outputBuffer[0].flip();
            }
            n += result.bytesConsumed();
            status = result.getStatus();
            if (status == Status.OK)
            {
                if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK)
                {
                    runTasks();
                }
            }
            else
            {
                if (status == Status.CLOSED)
                {
                    shutdown();
                }

                throw new IOException("Write error '" + result.getStatus()
                    + '\'');
            }
        } while (n < t);

        // Try to flush what we got.
        flush();

        return n;
    }

    public Channel getAdapteeChannel()
    {
        return channel;
    }

    public boolean finished()
    {
        return initialized == 0;
    }

    public int encrypted()
    {
        return outputBuffer[0].remaining();
    }

    public int decrypted()
    {
        return inputCache[0].remaining();
    }

    public synchronized int handshake(int ops) throws IOException
    {
        if (initialized != 0)
        {
            if (handshake == null)
            {
                engine.beginHandshake();
                handshake = engine.getHandshakeStatus();
            }

            if (outputBuffer[0].hasRemaining())
            {
                if ((ops & SelectionKey.OP_WRITE) != 0)
                {
                    flush(outputBuffer[0]);
                    if (outputBuffer[0].hasRemaining())
                    {
                        initialized = SelectionKey.OP_WRITE;
                    }
                    else
                    {
                        initialized = SelectionKey.OP_READ;
                    }
                    ops = 0;
                }
                else
                {
                    initialized = SelectionKey.OP_WRITE;
                }
            }
            else
            {
                initialized = SelectionKey.OP_READ;
            }

            while (initialized != 0)
            {
                if (handshake == HandshakeStatus.FINISHED)
                {
                    initialized = 0;
                }
                else if (handshake == HandshakeStatus.NEED_TASK)
                {
                    handshake = runTasks();
                }
                else if (handshake == HandshakeStatus.NEED_UNWRAP)
                {
                    ops = unwrap(ops);
                    if (ops != 0)
                    {
                        initialized = ops;
                        return initialized;
                    }
                }
                else if (handshake == HandshakeStatus.NEED_WRAP)
                {
                    ops = wrap(ops);
                    if (ops != 0)
                    {
                        initialized = ops;
                        return initialized;
                    }
                }
                else
                {
                    // NOT_HANDSHAKING
                    throw new IllegalStateException(
                        "Unexpected handshake status '" + handshake + '\'');
                }
            }
        }
        return initialized;
    }

    public synchronized boolean shutdown() throws IOException
    {
        shutdown = true;

        if (!engine.isOutboundDone())
        {
            engine.closeOutbound();
        }

        // Try to "fire-and-forget" the closed notification (RFC2616).
        SSLEngineResult result;
        if (prepare(outputBuffer, minBufferSize))
        {
            result = engine.wrap(emptyBuffer, outputBuffer[0]);
            if (result.getStatus() != Status.CLOSED)
            {
                throw new SSLException("Unexpected shutdown status '"
                    + result.getStatus() + '\'');
            }
            outputBuffer[0].flip();
        }
        else
        {
            result = null;
        }
        flush(outputBuffer[0]);
        return !outputBuffer[0].hasRemaining() && (result != null)
            && (result.getHandshakeStatus() != HandshakeStatus.NEED_WRAP);
    }

    public synchronized void flush() throws IOException
    {
        flush(outputBuffer[0]);
    }

    public String toString()
    {
        return "SSLSocketChannel[" + socket().toString() + "]";
    }
    
    /**
     * Gets the SSL session.
     * 
     * @return the session.
     */
    public SSLSession getSession()
    {
        return sslSession;
    }

    protected synchronized void implCloseSelectableChannel() throws IOException
    {
        try
        {
            shutdown();
        }
        catch (Exception x)
        {
        }

        channel.close();
        notifyAll();
    }

    protected void implConfigureBlocking(boolean block) throws IOException
    {
        channel.configureBlocking(block);
    }

    /**
     * Handshake unwrap.
     * 
     * @param ops the current ready operations set.
     * @return the interest set to continue or 0 if finished.
     * @throws IOException on I/O errors.
     */
    private synchronized int unwrap(int ops) throws IOException
    {
        // Fill the buffer, if applicable.
        if ((ops & SelectionKey.OP_READ) != 0)
        {
            fill(inputBuffer[0]);
        }

        // Unwrap the buffer.
        SSLEngineResult result;
        Status status;
        do
        {
            // Prepare the input cache, although no app
            // data should be produced during handshake.
            prepare(inputCache, minCacheSize);
            inputBuffer[0].flip();
            try
            {
                result = engine.unwrap(inputBuffer[0], inputCache[0]);
            }
            finally
            {
                inputBuffer[0].compact();
                inputCache[0].flip();
            }
            handshake = result.getHandshakeStatus();

            status = result.getStatus();
            if (status == Status.OK)
            {
                if (handshake == HandshakeStatus.NEED_TASK)
                {
                    handshake = runTasks();
                }
            }
            else if (status == Status.BUFFER_UNDERFLOW)
            {
                return SelectionKey.OP_READ;
            }
            else
            {
                // BUFFER_OVERFLOW/CLOSED
                throw new IOException("Handshake failed '" + status + '\'');
            }
        } while (handshake == HandshakeStatus.NEED_UNWRAP);

        return 0;
    }

    /**
     * Handshake wrap.
     * 
     * @param ops the current ready operations set.
     * @return the interest set to continue or 0 if finished.
     * @throws IOException on I/O errors.
     */
    private synchronized int wrap(int ops) throws IOException
    {
        // Prepare the buffer.
        if (prepare(outputBuffer, minBufferSize))
        {
            // Wrap the buffer.
            SSLEngineResult result;
            Status status;
            try
            {
                result = engine.wrap(emptyBuffer, outputBuffer[0]);
            }
            finally
            {
                outputBuffer[0].flip();
            }
            handshake = result.getHandshakeStatus();

            status = result.getStatus();
            if (status == Status.OK)
            {
                if (handshake == HandshakeStatus.NEED_TASK)
                {
                    handshake = runTasks();
                }
            }
            else
            {
                // BUFFER_OVERFLOW/BUFFER_UNDERFLOW/CLOSED
                throw new IOException("Handshake failed '" + status + '\'');
            }
        }

        // Flush the buffer, if applicable.
        if ((ops & SelectionKey.OP_WRITE) != 0)
        {
            flush(outputBuffer[0]);
        }

        return outputBuffer[0].hasRemaining() ? SelectionKey.OP_WRITE : 0;
    }

    /**
     * Fills the specified buffer.
     * 
     * @param in the buffer.
     * @return the number of read bytes.
     * @throws IOException on I/O errors.
     */
    private synchronized long fill(ByteBuffer in) throws IOException
    {
        try
        {
            long n = channel.read(in);
            if (n < 0)
            {
                // EOF reached.
                engine.closeInbound();
            }
            return n;
        }
        catch (IOException x)
        {
            // Can't read more bytes...
            engine.closeInbound();
            throw x;
        }
    }

    /**
     * Flushes the specified buffer.
     * 
     * @param out the buffer.
     * @return the number of written bytes.
     * @throws IOException on I/O errors.
     */
    private synchronized long flush(ByteBuffer out) throws IOException
    {
        try
        {
            // Flush only if bytes available.
            return out.hasRemaining() ? channel.write(out) : 0;
        }
        catch (IOException x)
        {
            // Can't write more bytes...
            engine.closeOutbound();
            shutdown = true;
            throw x;
        }
    }

    /**
     * Runs delegated handshaking tasks.
     * 
     * @return the handshake status.
     */
    private SSLEngineResult.HandshakeStatus runTasks()
    {
        Runnable runnable;
        while ((runnable = engine.getDelegatedTask()) != null)
        {
            runnable.run();
        }
        return engine.getHandshakeStatus();
    }

    /**
     * Prepares the specified buffer for the remaining number of bytes.
     * 
     * @param src the source buffer.
     * @param remaining the number of bytes.
     * @return true if prepared, false otherwise.
     */
    private boolean prepare(ByteBuffer[] src, int remaining)
    {
        ByteBuffer bb = src[0];
        if (bb.compact().remaining() < remaining)
        {
            int position = bb.position();
            int capacity = position + remaining;
            if (capacity <= 2 * remaining)
            {
                bb = ByteBuffer.allocate(capacity);
                if (position > 0)
                {
                    src[0].flip();
                    bb.put(src[0]);
                    src[0] = bb;
                }
            }
            else
            {
                bb.flip();
                bb = null;
            }
        }
        return bb != null;
    }
}
