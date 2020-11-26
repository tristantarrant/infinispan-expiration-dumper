package com.redhat.datagrid;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * @author Tristan Tarrant &lt;tristan@infinispan.org&gt;
 * @since 12.0
 **/
public class Filler {
   public static void main(String args[]) {
      if (args.length != 3) {
         System.err.println(Filler.class.getName() + " host port cache");
         System.exit(0);
      }
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host(args[0]).port(Integer.parseInt(args[1]));
      RemoteCacheManager rcm = new RemoteCacheManager(builder.build());
      RemoteCache<Object, Object> cache = rcm.getCache(args[2]);
      for (int i = 0; i < 1000; i++) {
         cache.put(randomKey(), Integer.toString(i), 1, TimeUnit.HOURS);
      }
      System.out.println("Done.");
   }

   public static String randomKey() {
      int leftLimit = 97; // letter 'a'
      int rightLimit = 122; // letter 'z'
      int targetStringLength = 10;
      Random random = new Random();

      return random.ints(leftLimit, rightLimit + 1)
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
   }
}
