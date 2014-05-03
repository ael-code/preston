package org.simpleframework.util.lease;

import java.util.concurrent.TimeUnit;

public class ContractTest extends TimeTestCase {

   public void testContract()  throws Exception {
      Contract ten = new Entry(this, 10, TimeUnit.MILLISECONDS);
      Contract twenty = new Entry(this, 20, TimeUnit.MILLISECONDS); 
      Contract thirty= new Entry(this, 30, TimeUnit.MILLISECONDS);

      assertGreaterThanOrEqual(twenty.getDelay(TimeUnit.NANOSECONDS), ten.getDelay(TimeUnit.NANOSECONDS));
      assertGreaterThanOrEqual(thirty.getDelay(TimeUnit.NANOSECONDS), twenty.getDelay(TimeUnit.NANOSECONDS));
      
      assertGreaterThanOrEqual(twenty.getDelay(TimeUnit.MILLISECONDS), ten.getDelay(TimeUnit.MILLISECONDS));
      assertGreaterThanOrEqual(thirty.getDelay(TimeUnit.MILLISECONDS), twenty.getDelay(TimeUnit.MILLISECONDS));      
      
      ten.setDelay(0, TimeUnit.MILLISECONDS);
      twenty.setDelay(0, TimeUnit.MILLISECONDS);
      
      assertLessThanOrEqual(ten.getDelay(TimeUnit.MILLISECONDS), 0);
      assertLessThanOrEqual(twenty.getDelay(TimeUnit.MILLISECONDS), 0);
      
      ten.setDelay(10, TimeUnit.MILLISECONDS);
      twenty.setDelay(20, TimeUnit.MILLISECONDS); 
      thirty.setDelay(30, TimeUnit.MILLISECONDS);

      assertGreaterThanOrEqual(twenty.getDelay(TimeUnit.NANOSECONDS), ten.getDelay(TimeUnit.NANOSECONDS));
      assertGreaterThanOrEqual(thirty.getDelay(TimeUnit.NANOSECONDS), twenty.getDelay(TimeUnit.NANOSECONDS));
      
      assertGreaterThanOrEqual(twenty.getDelay(TimeUnit.MILLISECONDS), ten.getDelay(TimeUnit.MILLISECONDS));
      assertGreaterThanOrEqual(thirty.getDelay(TimeUnit.MILLISECONDS), twenty.getDelay(TimeUnit.MILLISECONDS));      
      
      ten.setDelay(0, TimeUnit.MILLISECONDS);
      twenty.setDelay(0, TimeUnit.MILLISECONDS);
   }
}
