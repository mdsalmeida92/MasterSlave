
package server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ManagedAsync;

import client.MyEntry;
import redis.clients.jedis.Jedis;

@Path("/server")
public class ServerResources {
	public Jedis jedis;

	public ServerResources(){
		jedis = new Jedis();
		jedis.connect();
		jedis.flushAll();
		System.out.println(jedis.ping());
	}


	@GET
	@Path("getSet/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public MyEntry getMap( @PathParam("id") String id) {
		System.out.println("gege");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("somekey", "somevalue");
		MyEntry entry = new MyEntry(map);
		return entry;

	}




}
