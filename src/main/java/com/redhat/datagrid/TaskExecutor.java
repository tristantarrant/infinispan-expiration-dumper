package com.redhat.datagrid;

import java.util.Collections;

import org.infinispan.client.hotrod.ProtocolVersion;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * @author Tristan Tarrant &lt;tristan@infinispan.org&gt;
 * @since 12.0
 **/
public class TaskExecutor {
   public static void main(String args[]) {
      if (args.length != 4) {
         System.err.println(TaskExecutor.class.getName() + " host port cachename taskname");
         System.exit(0);
      }
      System.out.printf("Running task %s on cache %s on server %s:%s", args[3], args[2], args[0], args[1]);
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host(args[0]).port(Integer.parseInt(args[1])).version(ProtocolVersion.PROTOCOL_VERSION_28);
      RemoteCacheManager rcm = new RemoteCacheManager(builder.build());
      rcm.getCache(args[2]).execute(args[3], Collections.emptyMap());
      System.out.println("Done.");
   }
}
