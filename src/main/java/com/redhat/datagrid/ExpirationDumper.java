package com.redhat.datagrid;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.infinispan.Cache;
import org.infinispan.container.DataContainer;
import org.infinispan.container.entries.InternalCacheEntry;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;

/**
 * @author Tristan Tarrant &lt;tristan@infinispan.org&gt;
 * @since 12.0
 **/
public class ExpirationDumper implements ServerTask<Void> {

   public static final Long ZERO = Long.valueOf(0);
   private Cache<?, ?> cache;

   @Override
   public void setTaskContext(TaskContext taskContext) {
      cache = taskContext.getCache().get();
   }

   @Override
   public Void call() throws Exception {
      Logger logger = Logger.getLogger("EXPIRATIONS");
      logger.info("EXPIRATION STATS DUMP");
      DataContainer dc = cache.getAdvancedCache().getDataContainer();
      Map<Long, AtomicInteger> lifespanDistribution = initHistogram();
      Map<Long, AtomicInteger> maxIdleDistribution = initHistogram();
      Iterator<InternalCacheEntry<?, ?>> it1 = dc.iteratorIncludingExpired();
      while (it1.hasNext()) {
         InternalCacheEntry<?, ?> entry = it1.next();
         Long ls = entry.canExpire() ? entry.getLifespan() : ZERO;
         updateHistogram(ls, lifespanDistribution);
         Long mi = entry.canExpire() ? entry.getMaxIdle() : ZERO;
         updateHistogram(mi, maxIdleDistribution);
      }
      printHistogram("Lifespan", lifespanDistribution);
      printHistogram("MaxIdle", maxIdleDistribution);

      return null;
   }

   private Map<Long, AtomicInteger> initHistogram() {
      Map<Long, AtomicInteger> map = new HashMap<>();
      map.put(ZERO, new AtomicInteger());
      return map;
   }

   void updateHistogram(Long value, Map<Long, AtomicInteger> distribution) {
      AtomicInteger count = distribution.get(value);
      if (count == null) {
         count = new AtomicInteger(1);
         distribution.put(value, count);
      } else {
         count.incrementAndGet();
      }
   }

   void printHistogram(String kind, Map<Long, AtomicInteger> distribution) {
      Logger logger = Logger.getLogger("EXPIRATIONS");
      Iterator<Map.Entry<Long, AtomicInteger>> it2 = distribution.entrySet().iterator();
      while (it2.hasNext()) {
         Map.Entry<Long, AtomicInteger> next = it2.next();
         logger.info(kind + " = " + next.getKey() + " Count = " + next.getValue());
      }
   }

   @Override
   public String getName() {
      return "ExpirationDumper";
   }
}
