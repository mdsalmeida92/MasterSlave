
package server;

import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import javax.inject.Inject;
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
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import utils.Element;
import utils.MyBoolean;
import utils.MyEntry;
import utils.MyList;
import utils.MyListEntry;


@Path("/server")
public class ServerResources {
	public JedisPool jedisPool ;
	private long putTime;
	private long getTime;
	private long removeTime;
	private long updateTime;
	private long incrTime;
	private long sumTime;
	private long sumConstTime;
	private long multTime;
	private long searchElemTime;
	private long searchEntrysTime;
	private long orderEntrysTime;
	private long searchGreaterTime;
	private long searchLesserTime;
	private long valueGreaterTime;

	public ServerResources(){
		jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
		Jedis jedis= jedisPool.getResource();
		jedis.connect();
		jedis.flushAll();
		putTime = 0;
		getTime= 0;
		removeTime= 0;
		updateTime= 0;
		incrTime= 0;
		sumTime= 0;
		sumConstTime= 0;
		multTime= 0;
		searchElemTime= 0;
		searchEntrysTime= 0;
		orderEntrysTime= 0;
		searchGreaterTime= 0;
		searchLesserTime= 0;
		valueGreaterTime= 0;

		System.out.println(jedis.ping());
	}


	@GET
	@Path("/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public MyEntry getEntry(@PathParam("key") String key) throws InterruptedException {

		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			Map<String, String> map = jedis.hgetAll(key);
			MyEntry entry = new MyEntry(map);
			getTime += getTime() - begin;
			return entry;
		}



	}

	@POST 
	@Path("/{key}") 
	@Consumes(MediaType.APPLICATION_JSON) 
	public void putEntry(@PathParam("key") String key, MyEntry entry) throws InterruptedException { 
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) { 
			Map<String,String> map = entry.getAttributes(); 
			jedis.hmset(key, map); 
			map.forEach((k, v) -> { 
				jedis.sadd(":"+k+":", key); 
				jedis.sadd(k+":"+v, key);}); 
		} 
		putTime += getTime() - begin;
	}

	@DELETE
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeEntry(@PathParam("key") String key) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			Map<String, String> map = jedis.hgetAll(key);
			map.forEach((k, v) ->{ 
				jedis.srem(k+":"+v, key);
				jedis.srem(":"+k+":", key);});
			jedis.del(key);
		}
		removeTime +=  getTime() - begin;


	}

	@PUT
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateEntry(@PathParam("key") String key, Element element) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			if ( ! jedis.exists(key))
				throw new WebApplicationException(  );
			else

				jedis.hset(key, element.getField(), element.getElement());

		}
		updateTime += getTime() - begin;
	}

	@PUT
	@Path("/incr/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void incr(@PathParam("key") String key, Element value) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			if ( ! jedis.exists(key))
				throw new WebApplicationException(  );
			else

				jedis.hincrBy(key, value.getField(), Integer.valueOf(value.getElement()));
		}
		incrTime += getTime() - begin;

	}

	@GET
	@Path("/sum")
	@Produces(MediaType.APPLICATION_JSON)
	public long sum(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {
		long begin = getTime();
		try {
			try (Jedis jedis = jedisPool.getResource()) {
				long value1 = Integer.valueOf(jedis.hget(key1, field));
				long value2 = Integer.valueOf(jedis.hget(key2, field));
				sumTime += getTime() - begin;
				return value1+value2;

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return 0;



	}

	@GET
	@Path("/multConst")
	@Produces(MediaType.APPLICATION_JSON)
	public long sumConst(@QueryParam("key") String key, 
			@QueryParam("field")  String field, 
			@QueryParam("const") int constant) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			long value = Integer.valueOf(jedis.hget(key, field));
			sumConstTime += getTime() - begin;
			return value*constant;

		}


	}

	@GET
	@Path("/mult")
	@Produces(MediaType.APPLICATION_JSON)
	public long mult(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			long value1 = Integer.valueOf(jedis.hget(key1, field));
			long value2 = Integer.valueOf(jedis.hget(key2, field));
			multTime += getTime() - begin;
			return value1*value2;
		}		



	}

	@GET
	@Path("/searchElement")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchElement( @QueryParam("field")  String field, 
			@QueryParam("value") String value) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			List<String> l = jedis.smembers(field+":"+value).stream().collect(Collectors.toList());
			MyList list = new MyList(l);
			searchElemTime += getTime() - begin;
			return list;
		}


	}

	@GET
	@Path("/searchEntrys")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntrys( @QueryParam("query")  List<String>  query) {
		long begin = getTime();

		try (Jedis jedis = jedisPool.getResource()) {
			String string = null;
			Iterator<String> iterator = query.iterator();
			if ( iterator.hasNext()) {
				string = (String) iterator.next();
			}

			Set<String> set = jedis.smembers(string);
			for (; iterator.hasNext();) {
				string = (String) iterator.next();
				set.retainAll(jedis.smembers(string));

			}
			List<String> l = set.stream().collect(Collectors.toList());
			MyList list = new MyList(l);
			searchEntrysTime += getTime() - begin;
			return list;
		}

	}

	@GET
	@Path("/orderEntrys")
	@Produces(MediaType.APPLICATION_JSON)
	public MyListEntry orderEntrys( @QueryParam("query")  String field) {
		long begin = getTime();

		try (Jedis jedis = jedisPool.getResource()) {
			Comparator<MyEntry> comparator = new Comparator<MyEntry>() {
				public int compare(MyEntry m1, MyEntry m2) {
					double a = Integer.valueOf(m1.getAttributes().get(field));
					double b = Integer.valueOf(m2.getAttributes().get(field));
					if (a > b)
						return 1;
					else return -1;
				}
			};

			Set<String> set = jedis.smembers(":"+field+":");
			SortedSet<MyEntry> s = new TreeSet<MyEntry>(comparator);

			set.forEach((key) -> { 
				s.add((new MyEntry(key,jedis.hgetAll(key))));
			});

			MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			orderEntrysTime += getTime() - begin;
			return list;
		}

	}

	@GET
	@Path("/searchGreaterThan")
	@Produces(MediaType.APPLICATION_JSON)
	public MyListEntry searchGreaterThan( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.smembers(":"+field+":");
			Set<MyEntry> s = new HashSet<MyEntry>();

			set.forEach((key) -> { 
				Map<String, String> map = jedis.hgetAll(key);
				if (Integer.valueOf(map.get(field)) > Integer.valueOf(value))
					s.add((new MyEntry(key,map)));
			});

			MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			searchGreaterTime+= getTime() - begin;
			return list;
		}


	}

	@GET
	@Path("/searchLesserThan")
	@Produces(MediaType.APPLICATION_JSON)
	public MyListEntry searchLesserThan( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		long begin = getTime();

		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.smembers(":"+field+":");
			Set<MyEntry> s = new HashSet<MyEntry>();

			set.forEach((key) -> { 
				Map<String, String> map = jedis.hgetAll(key);
				if (Integer.valueOf(map.get(field)) < Integer.valueOf(value))
					s.add((new MyEntry(key,map)));
			});

			MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			searchLesserTime+= getTime() - begin;
			return list;
		}

	}

	@GET
	@Path("/valuegreaterThan")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean valuegreaterThan(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			long value1 = Integer.valueOf(jedis.hget(key1, field));
			long value2 = Integer.valueOf(jedis.hget(key2, field));

			MyBoolean isGreater = new MyBoolean(value1>value2);
			valueGreaterTime+= getTime() - begin;
			return isGreater;

		}

	}
	
	@GET
	@Path("/getTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long getTimes() {
		return getTime;
	}
	
	@GET
	@Path("/putTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long putTime() {
		return putTime;
	}


	
	@GET
	@Path("/removeTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long removeTime() {
		return removeTime;
	}

	
	@GET
	@Path("/updateTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long updateTime() {
		return updateTime;
	}

	
	@GET
	@Path("/incrTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long incrTime() {
		return incrTime;
	}

	
	@GET
	@Path("/sumTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long sumTime() {
		return sumTime;
	}

	
	@GET
	@Path("/sumConstTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long sumConstTime() {
		return sumConstTime;
	}

	
	@GET
	@Path("/multTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long multTime() {
		return multTime;
	}

	
	@GET
	@Path("/searchElemTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long searchElemTime() {
		return searchElemTime;
	}

	
	@GET
	@Path("/searchEntrysTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long searchEntrysTime() {
		return searchEntrysTime;
	}

	
	@GET
	@Path("/orderEntrysTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long orderEntrysTime() {
		return orderEntrysTime;
	}

	
	@GET
	@Path("/searchGreaterTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long searchGreaterTime() {
		return searchGreaterTime;
	}

	
	@GET
	@Path("/searchLesserTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long searchLesserTime() {
		return searchLesserTime;
	}

	
	@GET
	@Path("/valueGreaterTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long valueGreaterTime() {
		return valueGreaterTime;
	}

	private long getTime() {
		return Calendar.getInstance().getTimeInMillis();
	}


}
