
package server;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
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

import javax.crypto.SecretKey;
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

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.server.ManagedAsync;

import hlib.hj.mlib.HelpSerial;
import hlib.hj.mlib.HomoAdd;
import hlib.hj.mlib.HomoMult;
import hlib.hj.mlib.HomoRand;
import hlib.hj.mlib.HomoSearch;
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
	private long getElemTime;
	private long searchEntryContainingSentenceTime;
	private long elementContainsSentenceTime;
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
	private long sumAllTime;
	private long multAllTime;
	

	public ServerResources(){
		jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
		Jedis jedis= jedisPool.getResource();
		jedis.connect();
		jedis.flushAll();
		putTime = 0;
		getTime= 0;
		getElemTime = 0;
		removeTime= 0;
		searchEntryContainingSentenceTime =0;
		elementContainsSentenceTime =0;
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
		sumAllTime = 0;
		multAllTime =0;

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

	@GET
	@Path("/getElem")
	@Produces(MediaType.APPLICATION_JSON)
	public String getElement(@QueryParam("key") String key, @QueryParam("field") String field) throws InterruptedException {

			long begin = getTime();
			try (Jedis jedis = jedisPool.getResource()) {
				String elem = jedis.hget(key, field);
				getElemTime += getTime() - begin;
				return elem;
			}


	}


	
	@GET
	@Path("/elementContainsSentence")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean elementContainsSentence(@QueryParam("key") String key, 
			@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {

		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			String elem = jedis.hget(key, field);

			MyBoolean contains = new MyBoolean(elem.contains(sentence));
			elementContainsSentenceTime += getTime() - begin;
			return contains;
		}



	}
	
	@GET
	@Path("/elementContainsSentence/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean elementContainsSentenceEnc(@QueryParam("key") String key, 
			@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			String elem = jedis.hget(key, field);

			MyBoolean contains = new MyBoolean(HomoSearch.pesquisa(sentence, elem)|| HomoSearch.searchAll(sentence, elem));
			elementContainsSentenceTime += getTime() - begin;
			return contains;
		}



	}
	

	@GET
	@Path("/elementContainsSentence/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean elementContainsSentenceEnEnc(@QueryParam("key") String key, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("field") String field,
			@QueryParam("sentence") String sentence) throws InterruptedException {
		long begin = getTime();
		
		try (Jedis jedis = jedisPool.getResource()) {
			String elem = RemoveEncryptLayer(RandomKey, iv,jedis.hget(key, field));
			
			MyBoolean contains = new MyBoolean(HomoSearch.pesquisa(sentence, elem)|| HomoSearch.searchAll(sentence, elem));
			elementContainsSentenceTime += getTime() - begin;
			return contains;
		}



	}
	
	@GET
	@Path("/searchEntryContainingSentence")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntryContainingWord(@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {

		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.smembers(":"+field+":");
			List<String> l = new LinkedList<>();
			set.forEach((key) -> {
				if(jedis.hget(key, field).contains(sentence))
					l.add(key);
			});

			MyList list = new MyList(l);
			searchEntryContainingSentenceTime += getTime() - begin;
			return list;
		}



	}
	
	@GET
	@Path("/searchEntryContainingSentence/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntryContainingWordEnc(@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {

		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.smembers(":"+field+":");
			List<String> l = new LinkedList<>();
			set.forEach((key) -> {
				String elem = jedis.hget(key, field);
				if(HomoSearch.pesquisa(sentence,elem)|| HomoSearch.searchAll(sentence, elem))
					l.add(key);
			});

			MyList list = new MyList(l);
			searchEntryContainingSentenceTime += getTime() - begin;
			return list;
		}



	}
	
	@GET
	@Path("/searchEntryContainingSentence/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntryContainingWordEnEnc(@QueryParam("field") String field, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("sentence") String sentence) throws InterruptedException {

		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.smembers(":"+field+":");
			List<String> l = new LinkedList<>();
			set.forEach((key) -> {
				String elem = RemoveEncryptLayer(RandomKey, iv,jedis.hget(key, field));
				if(HomoSearch.pesquisa(sentence,elem)|| HomoSearch.searchAll(sentence, elem))
					l.add(key);
			});

			MyList list = new MyList(l);
			searchEntryContainingSentenceTime += getTime() - begin;
			return list;
		}



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
	
	@PUT
	@Path("/incr/Encrypted/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void incrEnc(@PathParam("key") String key, Element value) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			if ( ! jedis.exists(key))
				throw new WebApplicationException(  );
			else {
				String redisValue = jedis.hget(key, value.getField());
				BigInteger result = HomoAdd.sum(new BigInteger(redisValue), new BigInteger(value.getElement()), new BigInteger(value.getKey()));
				jedis.hset(key,  value.getField(), result.toString());
				//jedis.hincrBy(key, value.getField(), Integer.valueOf(value.getElement()));
			}
		}
		incrTime += getTime() - begin;

	}
	
	@PUT
	@Path("/incr/EnhancedEncrypted/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void incrEnEnc(@PathParam("key") String key, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey, Element value) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			if ( ! jedis.exists(key))
				throw new WebApplicationException(  );
			else {
		
				
				String redisValue = RemoveEncryptLayer(RandomKey, iv,jedis.hget(key, value.getField()));
				String incrBy = RemoveEncryptLayer(RandomKey, iv,value.getElement());
				BigInteger result = HomoAdd.sum(new BigInteger(redisValue), new BigInteger(incrBy), new BigInteger(value.getKey()));

				String vResult = AddEncryptLayer(RandomKey, iv, result.toString());
				jedis.hset(key,  value.getField(), vResult);
				//jedis.hincrBy(key, value.getField(), Integer.valueOf(value.getElement()));
			}
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
	@Path("/sum/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("nsquare")  String nsquare, 
			@QueryParam("key2") String key2) {
		try {

			long begin = getTime();

			try (Jedis jedis = jedisPool.getResource()) {

				BigInteger result = HomoAdd.sum(new BigInteger(jedis.hget(key1, field)), new BigInteger(jedis.hget(key2, field)), new BigInteger(nsquare));
				sumTime += getTime() - begin;
				return result.toString();

			}

		} catch (Exception e) {
			System.err.println("errorsdsd" + e.getMessage());
		}
		throw new WebApplicationException( );



	}
	
	@GET
	@Path("/sum/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumEnEnc(@QueryParam("key1") String key1, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("field")  String field, 
			@QueryParam("nsquare")  String nsquare, 
			@QueryParam("key2") String key2) {
		try {

			long begin = getTime();

			try (Jedis jedis = jedisPool.getResource()) {

				String v1 = RemoveEncryptLayer(RandomKey, iv,jedis.hget(key1, field));
				String v2 = RemoveEncryptLayer(RandomKey, iv,jedis.hget(key2, field));
				BigInteger result = HomoAdd.sum(new BigInteger(v1), new BigInteger(v2), new BigInteger(nsquare));
				sumTime += getTime() - begin;
				return AddEncryptLayer(RandomKey, iv, result.toString());

			}

		} catch (Exception e) {
			System.err.println("errorsdsd" + e.getMessage());
		}
		throw new WebApplicationException( );



	}
	
	@GET
	@Path("/sumAll")
	@Produces(MediaType.APPLICATION_JSON)
	public long sumAll(@QueryParam("field")  String field) {
		long begin = getTime();
		try {
			try (Jedis jedis = jedisPool.getResource()) {
				long result = 0;
				Set<String> set = jedis.smembers(":"+field+":");
		        for (String key : set){
		        	 result += Long.valueOf(jedis.hget(key, field));
		        }
				sumAllTime += getTime() - begin;
				return result;

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return 0;



	}
	
	
	@GET
	@Path("/sumAll/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumAllEnc(@QueryParam("field")  String field,
			@QueryParam("nsquare")  String nsquare) {
		long begin = getTime();
		try {
			try (Jedis jedis = jedisPool.getResource()) {

				BigInteger result = null;
				Set<String> set = jedis.smembers(":"+field+":");
				Iterator<String> it = set.iterator();
				String s = null;
				if (it.hasNext()) {
					s= it.next();
					result = new BigInteger(jedis.hget(s, field));
				}
				while (it.hasNext()){
					s= it.next();

					result = HomoAdd.sum(result, new BigInteger(jedis.hget(s, field)),  new BigInteger(nsquare));
				}
				sumAllTime += getTime() - begin;
				return result.toString();

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;



	}
	
	@GET
	@Path("/sumAll/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumAllEnEnc(@QueryParam("field")  String field,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("nsquare")  String nsquare) {
		long begin = getTime();
		try {
			try (Jedis jedis = jedisPool.getResource()) {

				BigInteger result = null;
				Set<String> set = jedis.smembers(":"+field+":");
				Iterator<String> it = set.iterator();
				String s = null;
				if (it.hasNext()) {
					s= it.next();
			
					result = new BigInteger(RemoveEncryptLayer(RandomKey, iv,jedis.hget(s, field)));
				}
				while (it.hasNext()){
					s= it.next();

					result = HomoAdd.sum(result, new BigInteger(RemoveEncryptLayer(RandomKey, iv,jedis.hget(s, field))),  new BigInteger(nsquare));

				}
				sumAllTime += getTime() - begin;
				return  AddEncryptLayer(RandomKey, iv, result.toString());

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;



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
	@Path("/multConst/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumConstEnc(@QueryParam("key") String key, 
			@QueryParam("nsquare")  String nsquare, 
			@QueryParam("field")  String field, 
			@QueryParam("const") int constant) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {

			BigInteger result = HomoAdd.mult(new BigInteger(jedis.hget(key, field)), constant, new BigInteger(nsquare));

			sumConstTime += getTime() - begin;
			return result.toString();

		}



	}
	
	@GET
	@Path("/multConst/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumConst(@QueryParam("key") String key, 
			@QueryParam("nsquare")  String nsquare, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("field")  String field, 
			@QueryParam("const") int constant) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {

			BigInteger result = HomoAdd.mult(new BigInteger(RemoveEncryptLayer(RandomKey, iv,jedis.hget(key, field))), constant, new BigInteger(nsquare));

			sumConstTime += getTime() - begin;
			return  AddEncryptLayer(RandomKey, iv, result.toString());

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
	@Path("/mult/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String multEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("publicKey")  String publicKey, 
			@QueryParam("key2") String key2) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			BigInteger result = null;
			result = HomoMult.multiply(new BigInteger(jedis.hget(key1, field)), new BigInteger(jedis.hget(key2, field)), (RSAPublicKey)HelpSerial.fromString(publicKey));


			multTime += getTime() - begin;
			return result.toString();
		}		



	}
	
	@GET
	@Path("/mult/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String multEnEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("publicKey")  String publicKey, 
			@QueryParam("key2") String key2) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			BigInteger result = null;
			String v1 = RemoveEncryptLayer(RandomKey, iv,jedis.hget(key1, field));
			String v2 = RemoveEncryptLayer(RandomKey, iv,jedis.hget(key2, field));
			result = HomoMult.multiply(new BigInteger(v1), new BigInteger(v2), (RSAPublicKey)HelpSerial.fromString(publicKey));


			multTime += getTime() - begin;
			return  AddEncryptLayer(RandomKey, iv, result.toString());
		}		



	}
	
	@GET
	@Path("/multAll")
	@Produces(MediaType.APPLICATION_JSON)
	public long multAll(@QueryParam("field")  String field) {
		long begin = getTime();
		try {
			try (Jedis jedis = jedisPool.getResource()) {
				long result = 1;
				Set<String> set = jedis.smembers(":"+field+":");
		        for (String key : set){
		        	 result *= Long.valueOf(jedis.hget(key, field));
		        }
		        multAllTime += getTime() - begin;
				return result;

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return 0;



	}
	
	@GET
	@Path("/multAll/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String multAllEnc(@QueryParam("field")  String field,
			@QueryParam("publicKey")  String publicKey) {
		long begin = getTime();
		try {
			try (Jedis jedis = jedisPool.getResource()) {

				BigInteger result = null;
				Set<String> set = jedis.smembers(":"+field+":");
				Iterator<String> it = set.iterator();
				if (it.hasNext())
					result = new BigInteger(jedis.hget(it.next(), field));
				while (it.hasNext()){
					result = HomoMult.multiply(result, new BigInteger(jedis.hget(it.next(), field)), (RSAPublicKey)HelpSerial.fromString(publicKey));		 
				}

				multAllTime += getTime() - begin;
				return result.toString();

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;



	}
	
	@GET
	@Path("/multAll/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String multAllEnEnc(@QueryParam("field")  String field,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("publicKey")  String publicKey) {
		long begin = getTime();
		try {
			try (Jedis jedis = jedisPool.getResource()) {

				BigInteger result = null;
				Set<String> set = jedis.smembers(":"+field+":");
				Iterator<String> it = set.iterator();
				String s = null;
				if (it.hasNext())
					s = it.next();
					result = new BigInteger(RemoveEncryptLayer(RandomKey, iv,jedis.hget(s, field)));
				while (it.hasNext()){
					s = it.next();
					result = HomoMult.multiply(result, new BigInteger(RemoveEncryptLayer(RandomKey, iv,jedis.hget(s, field))), (RSAPublicKey)HelpSerial.fromString(publicKey));		 
				}

				multAllTime += getTime() - begin;
				return AddEncryptLayer(RandomKey, iv, result.toString());

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;



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
	@Path("/searchElement/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchElementEnc( @QueryParam("field")  String field, 
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
	@Path("/searchElement/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchElementEnEnc( @QueryParam("field")  String field, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("value") String value) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
		
			Set<String> set = jedis.smembers(":"+field+":");
			Iterator<String> it = set.iterator();
			List<String> l = new LinkedList<String>();
			while(it.hasNext()) {
				String key = it.next();
				String redisValue = RemoveEncryptLayer(RandomKey, iv,jedis.hget(key, field));
				if (value.equals(redisValue)){
					l.add(key);
				}
			}
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
	@Path("/searchEntrys/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntrysEnc( @QueryParam("query")  List<String>  query) {
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
	@Path("/searchEntrys/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntrysEnEnc( @QueryParam("query")  List<String>  query,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		long begin = getTime();

		try (Jedis jedis = jedisPool.getResource()) {
			String string = null;
			Iterator<String> iterator = query.iterator();
			Set<String> finalset = new HashSet<String>();
			
			if ( iterator.hasNext()) {
				string = iterator.next();
				String field = string.substring(0, string.indexOf("/"));
				String value = string.substring(string.indexOf("/")+1);
				Set<String> set = jedis.smembers(field);
				for (String string2 : set) {
					String valor = RemoveEncryptLayer(RandomKey, iv, jedis.hget(string2, field.substring(1, field.length()-1)));
					if(valor.equals(value))
						finalset.add(string2);
				}
			}

			
			
			while(iterator.hasNext()) {
				String q = iterator.next();
				String field = q.substring(0, q.indexOf("/"));
				String value = q.substring(q.indexOf("/")+1);
				Set<String> set = jedis.smembers(field);
				Set<String> nset = new HashSet<String>();
				for (String string2 : set) {
					String valor = RemoveEncryptLayer(RandomKey, iv, jedis.hget(string2, field.substring(1, field.length()-1)));
					if(valor.equals(value))
						nset.add(string2);
				}
				finalset.retainAll(nset);
				
			}

			List<String> l = finalset.stream().collect(Collectors.toList());

			MyList list = new MyList(l);
			searchEntrysTime += getTime() - begin;
			return list;
		}

	}

	@GET
	@Path("/orderEntrys")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList orderEntrys( @QueryParam("query")  String field) {
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

			//MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			List<String> l = new LinkedList<>();
			s.forEach((entry) ->{ l.add(entry.getKey());} );
			MyList list = new MyList(l);
			orderEntrysTime += getTime() - begin;
			return list;
		}

	}
	
	@GET
	@Path("/orderEntrys/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList orderEntrysEnc( @QueryParam("query")  String field) {
		long begin = getTime();

		try (Jedis jedis = jedisPool.getResource()) {
			Comparator<MyEntry> comparator = new Comparator<MyEntry>() {
				public int compare(MyEntry m1, MyEntry m2) {
					long a = Long.valueOf(m1.getAttributes().get(field));
					long b = Long.valueOf(m2.getAttributes().get(field));

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

			//MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			List<String> l = new LinkedList<>();
			s.forEach((entry) ->{ l.add(entry.getKey());} );
			MyList list = new MyList(l);
			orderEntrysTime += getTime() - begin;
			return list;
		}

	}
	
	@GET
	@Path("/orderEntrys/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList orderEntrysEnEnc( @QueryParam("query")  String field,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		long begin = getTime();

		try (Jedis jedis = jedisPool.getResource()) {
			Comparator<MyEntry> comparator = new Comparator<MyEntry>() {
				public int compare(MyEntry m1, MyEntry m2) {
					long a = Long.valueOf(RemoveEncryptLayer(RandomKey, iv, m1.getAttributes().get(field)));
					long b = Long.valueOf(RemoveEncryptLayer(RandomKey, iv, m2.getAttributes().get(field)));

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

			//MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			List<String> l = new LinkedList<>();
			s.forEach((entry) ->{ l.add(entry.getKey());} );
			MyList list = new MyList(l);
			orderEntrysTime += getTime() - begin;
			return list;
		}

	}

	@GET
	@Path("/searchGreaterThan")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchGreaterThan( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.smembers(":"+field+":");
			Set<MyEntry> s = new HashSet<MyEntry>();

			set.forEach((key) -> { 
				Map<String, String> map = jedis.hgetAll(key);
				if (Integer.valueOf(map.get(field)) > Integer.valueOf(value))
					s.add((new MyEntry(key,map)));
			});

			//MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			List<String> l = new LinkedList<>();
			s.forEach((entry) ->{ l.add(entry.getKey());} );
			MyList list = new MyList(l);
			searchGreaterTime+= getTime() - begin;
			return list;
		}


	}
	
	@GET
	@Path("/searchGreaterThan/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchGreaterThanEnc( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		long begin = getTime();

		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.smembers(":"+field+":");
			Set<MyEntry> s = new HashSet<MyEntry>();

			set.forEach((key) -> { 
				Map<String, String> map = jedis.hgetAll(key);
				if (Long.valueOf(map.get(field)) > Long.valueOf(value))
					s.add((new MyEntry(key,map)));
			});

			//MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			List<String> l = new LinkedList<>();
			s.forEach((entry) ->{ l.add(entry.getKey());} );
			MyList list = new MyList(l);
			searchGreaterTime+= getTime() - begin;
			return list;
		}


	}
	

	@GET
	@Path("/searchGreaterThan/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchGreaterThanEnEnc( @QueryParam("field")  String field, @QueryParam("value")  String value,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		long begin = getTime();

		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.smembers(":"+field+":");
			Set<MyEntry> s = new HashSet<MyEntry>();

			set.forEach((key) -> { 
				Map<String, String> map = jedis.hgetAll(key);
				if (Long.valueOf(RemoveEncryptLayer(RandomKey, iv, map.get(field))) > Long.valueOf(value))
					s.add((new MyEntry(key,map)));
			});

			//MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			List<String> l = new LinkedList<>();
			s.forEach((entry) ->{ l.add(entry.getKey());} );
			MyList list = new MyList(l);
			searchGreaterTime+= getTime() - begin;
			return list;
		}


	}

	@GET
	@Path("/searchLesserThan")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchLesserThan( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		long begin = getTime();

		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.smembers(":"+field+":");
			Set<MyEntry> s = new HashSet<MyEntry>();

			set.forEach((key) -> { 
				Map<String, String> map = jedis.hgetAll(key);
				if (Integer.valueOf(map.get(field)) < Integer.valueOf(value))
					s.add((new MyEntry(key,map)));
			});

			//MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			List<String> l = new LinkedList<>();
			s.forEach((entry) ->{ l.add(entry.getKey());} );
			MyList list = new MyList(l);
			searchLesserTime+= getTime() - begin;
			return list;
		}

	}
	
	@GET
	@Path("/searchLesserThan/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchLesserThanEnc( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		long begin = getTime();

		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.smembers(":"+field+":");
			Set<MyEntry> s = new HashSet<MyEntry>();

			set.forEach((key) -> { 
				Map<String, String> map = jedis.hgetAll(key);
				if (Long.valueOf(map.get(field)) < Long.valueOf(value))
					s.add((new MyEntry(key,map)));
			});


			//MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			List<String> l = new LinkedList<>();
			s.forEach((entry) ->{ l.add(entry.getKey());} );
			MyList list = new MyList(l);
			searchLesserTime+= getTime() - begin;
			return list;
		}

	}
	
	@GET
	@Path("/searchLesserThan/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchLesserThanEnEnc( @QueryParam("field")  String field, @QueryParam("value")  String value,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		long begin = getTime();

		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.smembers(":"+field+":");
			Set<MyEntry> s = new HashSet<MyEntry>();

			set.forEach((key) -> { 
				Map<String, String> map = jedis.hgetAll(key);
				if (Long.valueOf(RemoveEncryptLayer(RandomKey, iv, map.get(field))) < Long.valueOf(value))
					s.add((new MyEntry(key,map)));
			});


			//MyListEntry list = new MyListEntry(s.stream().collect(Collectors.toList()));
			List<String> l = new LinkedList<>();
			s.forEach((entry) ->{ l.add(entry.getKey());} );
			MyList list = new MyList(l);
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
	@Path("/valuegreaterThan/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean valuegreaterThanEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			long value1 = Long.valueOf(jedis.hget(key1, field));
			long value2 = Long.valueOf(jedis.hget(key2, field));

			MyBoolean isGreater = new MyBoolean(value1>value2);
			valueGreaterTime+= getTime() - begin;
			return isGreater;

		}

	}
	
	@GET
	@Path("/valuegreaterThan/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean valuegreaterThanEnEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			long value1 = Long.valueOf(RemoveEncryptLayer(RandomKey, iv, jedis.hget(key1, field)));
			long value2 = Long.valueOf(RemoveEncryptLayer(RandomKey, iv, jedis.hget(key2, field)));

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

	
	private String RemoveEncryptLayer( String RandomKey, String iv, String elem) {
		
		byte[] initV = Base64.decodeBase64(iv);
		SecretKey RandKey = (SecretKey)HelpSerial.fromString(RandomKey);
		return  HomoRand.decrypt(RandKey, initV, elem);
	}
	
	private String AddEncryptLayer( String RandomKey, String iv, String elem) {
		
		byte[] initV = Base64.decodeBase64(iv);
		SecretKey RandKey = (SecretKey)HelpSerial.fromString(RandomKey);
		return  HomoRand.encrypt(RandKey, initV, elem);
	}
	
	private long getTime() {
		return Calendar.getInstance().getTimeInMillis();
	}


}
