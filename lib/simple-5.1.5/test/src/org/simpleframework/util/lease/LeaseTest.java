package org.simpleframework.util.lease;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class LeaseTest extends TimeTestCase {

   private static int ITERATIONS = 10000;

   static {
      String value = System.getProperty("iterations");

      if (value != null) {
         ITERATIONS = Integer.parseInt(value);
      }
   }

   public void testLease() throws Exception {
      final BlockingQueue<Integer> clean = new LinkedBlockingQueue<Integer>();

      Cleaner<Integer> cleaner = new Cleaner<Integer>() {
         public void clean(Integer key) {
            clean.offer(key);
         }
      };
      Map<Integer, Contract> table = new ConcurrentHashMap<Integer, Contract>();
      List<Lease> list = new ArrayList<Lease>();
      Controller controller = new Maintainer(cleaner);

      for (int i = 0; i < ITERATIONS; i++) {
         long random = (long) (Math.random() * 1000) + 1000L;
         Contract<Integer> contract = new Entry(i, random, TimeUnit.MILLISECONDS);
         Lease lease = new ContractLease(controller, contract);

         table.put(i, contract);
         list.add(lease);
         controller.issue(contract);
      }
      for (int i = 0; i < ITERATIONS; i++) {
         long random = (long) (Math.random() * 1000);

         try {
            list.get(i).renew(random, TimeUnit.MILLISECONDS);
         } catch (Exception e) {
            continue;
            // e.printStackTrace();
         }
      }
      for (int i = 0; i < ITERATIONS; i++) {
         try {
            System.err.println("delay: "
                  + list.get(i).getExpiry(TimeUnit.MILLISECONDS));
         } catch (Exception e) {
            continue;
            // e.printStackTrace();
         }
      }
      System.err.println("clean: " + clean.size());

      for (int i = 0; i < ITERATIONS; i++) {
         Integer index = clean.take();
         Contract contract = table.get(index);

         // assertLessThanOrEqual(-4000,
         // contract.getDelay(TimeUnit.MILLISECONDS));
         System.err.println(String.format("index=[%s] delay=[%s]", index,
               contract.getDelay(TimeUnit.MILLISECONDS)));
      }
   }

   public static void main(String[] list) throws Exception {
      new LeaseTest().testLease();
   }
}
