
package server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ManagedAsync;

import redis.clients.jedis.Jedis;
import utils.Element;
import utils.MyEntry;
import utils.MyList;
import utils.Utils;

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
	@Path("/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public MyEntry getEntry( @PathParam("key") String key) {

		Map<String, String> map = jedis.hgetAll(key);
		MyEntry entry = new MyEntry(map);
		return entry;

	}

	@POST
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void putEntry(@PathParam("key") String key, MyEntry entry) {

		Map<String,String> map = entry.getAttributes();
		jedis.hmset(key, map);
		map.forEach((k, v) -> {
			if(Utils.isNumeric(v))
				jedis.zadd(k, Double.parseDouble(v), key);
			else
				jedis.sadd(k+":"+v, key);});

	}

	@DELETE
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeEntry(@PathParam("key") String key) {

		Map<String, String> map = jedis.hgetAll(key);
		map.forEach((k, v) -> jedis.srem(k+":"+v, key));
		jedis.del(key);

	}

	@PUT
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateEntry(@PathParam("key") String key, Element element) {

		if ( ! jedis.exists(key))
			throw new WebApplicationException(  );
		else

			jedis.hset(key, element.getField(), element.getElement());

	}

	@PUT
	@Path("/incr/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void incr(@PathParam("key") String key, Element value) {

		if ( ! jedis.exists(key))
			throw new WebApplicationException(  );
		else

			jedis.hincrBy(key, value.getField(), Integer.valueOf(value.getElement()));
	}

	@GET
	@Path("/sum")
	@Produces(MediaType.APPLICATION_JSON)
	public long sum(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {

		long value1 = Integer.valueOf(jedis.hget(key1, field));
		long value2 = Integer.valueOf(jedis.hget(key2, field));
		return value1+value2;


	}

	@GET
	@Path("/multConst")
	@Produces(MediaType.APPLICATION_JSON)
	public long sumConst(@QueryParam("key") String key, 
			@QueryParam("field")  String field, 
			@QueryParam("const") int constant) {

		long value = Integer.valueOf(jedis.hget(key, field));
		return value*constant;


	}

	@GET
	@Path("/mult")
	@Produces(MediaType.APPLICATION_JSON)
	public long mult(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {

		long value1 = Integer.valueOf(jedis.hget(key1, field));
		long value2 = Integer.valueOf(jedis.hget(key2, field));
		return value1*value2;


	}

	@GET
	@Path("/searchElement")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchElement( @QueryParam("field")  String field, 
			@QueryParam("value") String value) {
		List<String> l = jedis.smembers(field+":"+value).stream().collect(Collectors.toList());
		MyList list = new MyList(l);
		return list;

	}


	@GET
	@Path("/searchEntrys")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntrys( @QueryParam("query")  List<String>  query) {


		List<String> l = jedis.sinter( query.toArray(new String[query.size()])).stream().collect(Collectors.toList());
		MyList list = new MyList(l);
		return list;




	}


}
