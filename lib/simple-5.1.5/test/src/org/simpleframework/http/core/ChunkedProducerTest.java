package org.simpleframework.http.core;

import junit.framework.TestCase;

import org.simpleframework.http.message.ChunkedConsumer;
import org.simpleframework.transport.Cursor;
import org.simpleframework.util.buffer.ArrayAllocator;

public class ChunkedProducerTest extends TestCase {

   public void testChunk() throws Exception {
      testChunk(1024, 1);
      testChunk(1024, 2);
      testChunk(512, 20);
      testChunk(64, 64);
   }
   
   public void testChunk(int chunkSize, int count) throws Exception {
      MockSender sender = new MockSender((chunkSize * count) + 1024);
      MockMonitor monitor = new MockMonitor();
      ChunkedConsumer validator = new ChunkedConsumer(new ArrayAllocator());
      ChunkedProducer producer = new ChunkedProducer(sender, monitor);
      byte[] chunk = new byte[chunkSize];
      
      for(int i = 0; i < chunk.length; i++) {
         chunk[i] = (byte)String.valueOf(i).charAt(0);
      }
      for(int i = 0; i < count; i++) {
         producer.produce(chunk, 0, chunkSize);
      }
      producer.close();
      
      System.err.println(sender.getBuffer().encode("UTF-8"));
      
      Cursor cursor = sender.getCursor();
      
      while(!validator.isFinished()) {
         validator.consume(cursor);
      }
      assertEquals(cursor.ready(), -1);
      assertTrue(monitor.isReady());
   }
}
