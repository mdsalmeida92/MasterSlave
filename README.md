
SearchableEncryptedRedis
====================================

Install redis, Java and Maven
--------------------------------


Build
--------------------------------

run:


	mvn clean package



Run Redis Server
--------------------------------

**$server_port**: port of the server about to be created
**$redis_port**: port of the redis key value store

example of variables:


	redis_port=6380
	server_port=8443

run  Server:

	redis-server --port $redis_port
	
	java -Djavax.net.ssl.keyStore=Server/server.jks -Djavax.net.ssl.keyStorePassword=changeme -jar target/AppServer-jar-with-dependencies.jar -port $server_port -p $redis_port



Run RedisMasterSlave Server
--------------------------------

**$server_port**: port of the server about to be created
**$redis_port**: port of the redis master, responsible for receiving requests
example of variables:

	
	redis_port=6380
	server_port=8443
    
run MasterSlave Server:

	linux/start-all.sh
	java -Djavax.net.ssl.keyStore=Server/server.jks -Djavax.net.ssl.keyStorePassword=changeme -jar target/AppServer-jar-with-dependencies.jar -port $server_port -p $redis_port


Run RedisBFT Server
--------------------------------


**$server_port**: port of the server about to be created
**$redis_portX**: port of the redis, responsible for receiving requests from that replica
**$replicaID**: number that identifies that replica (Must start at 0 and increment for the others e.g. 0,1,2,3)
**$config_path**: full path to the folder config
example of variables:

	redis_port1=6379
	redis_port2=6380
	redis_port3=6381
	redis_port4=6382
	server_port=8443
	replicaID1=0
	replicaID2=1
	replicaID3=2
	replicaID4=3

run redis servers, replicas and the BFTServer:

	redis-server linux/redis_bft.conf --port $redis_port1 
	redis-server linux/redis_bft.conf --port $redis_port2 
	redis-server linux/redis_bft.conf --port $redis_port3 
	redis-server linux/redis_bft.conf --port $redis_port4 

	java -jar target/BFTreplica-jar-with-dependencies.jar -port $redis_port1 -id $replicaID1 -path $config_path 
	java -jar target/BFTreplica-jar-with-dependencies.jar -port $redis_port2 -id $replicaID2 -path $config_path 
	java -jar target/BFTreplica-jar-with-dependencies.jar -port $redis_port3 -id $replicaID3 -path $config_path 
	java -jar target/BFTreplica-jar-with-dependencies.jar -port $redis_port4 -id $replicaID4 -path $config_path 


	java -Djavax.net.ssl.keyStore=Server/server.jks -Djavax.net.ssl.keyStorePassword=changeme -jar target/AppServer-jar-with-dependencies.jar -port $server_port -bft -id $replicaID


 
for changing the number of replicas, the file system.config and hosts.config in the folder config
