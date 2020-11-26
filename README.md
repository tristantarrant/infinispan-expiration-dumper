Expiration dumper
=================

This repository contains a simple Infinispan server-side task that scans the data container of a single node and creates a histogram of lifespans and maxidle times for all entries.
The histogram is then output in the server log in the EXPIRATIONS category.


Build
=====

`mvn clean package -Dversion.infinispan=<version>`

Once built, the target directory will contain two jars:

`expiration-dumper-task.jar` - the server task which should be deployed on the server (`standalone/deployments` for Infinispan 8 and 9 or `server/lib` for Infinispan 10 or newer)
`expiration-dumper.jar` - the client that invokes the task. Invoke it as follows: `java -jar expiration-dumper.jar <host> <port> <cachename>  com.redhat.datagrid.TaskExecutor`

