PROJECT 2:
Implement a distributed application based on heart-beats in order to identify the cluster topology at run-time, as well as the
services that run on each cluster node.
A service will be described using a well defined structure: uid, name, version, input parameters, return parameters.
The application will detect the faulty nodes as well as any new nodes that join the cluster.
The heart-beats will be implemented by using UDP packets.
Develop a client application that will be able to connect to any cluster node in order to receive at runtime the cluster topology and
the cluster services. The application will let the user to select a service and a cluster node upon which the service will be executed.
For scalability reasons, a cluster node will execute the requested services by using java.util.concurrent.ThreadPoolExecutor.
