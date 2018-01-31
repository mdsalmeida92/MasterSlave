package server;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import org.apache.commons.codec.binary.Base64;

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

public class ServerLogic {

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

	public ServerLogic(int port){
		jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", port);
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

	public MyEntry getEntry(String key) throws InterruptedException {

		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			Map<String, String> map = jedis.hgetAll(key);
			MyEntry entry = new MyEntry(map);
			getTime += getTime() - begin;
			return entry;
		}

	}


	public void putEntry(String key, MyEntry entry) throws InterruptedException { 
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) { 
			Map<String,String> map = entry.getAttributes(); 
			jedis.hmset(key, map); 
			jedis.sadd("keys", key);
			map.forEach((k, v) -> { 
				jedis.sadd(":"+k+":", key); 
				jedis.sadd(k+":"+v, key);}); 
		} 
		putTime += getTime() - begin;
	}


	public void removeEntry(String key) {
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


	public void updateEntry(String key, Element element) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			if ( ! jedis.exists(key))
				throw new WebApplicationException(  );
			else

				jedis.hset(key, element.getField(), element.getElement());

		}
		updateTime += getTime() - begin;
	}


	public String getElement(String key, @QueryParam("field") String field) throws InterruptedException {

		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			String elem = jedis.hget(key, field);
			getElemTime += getTime() - begin;
			return elem;
		}


	}




	public MyBoolean elementContainsSentence(String key, 
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


	public MyBoolean elementContainsSentenceEnc(String key, 
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



	public MyBoolean elementContainsSentenceEnEnc(String key, 
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


	public MyList searchEntryContainingWord(String field, 
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


	public MyList searchEntryContainingWordEnc(String field, 
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


	public MyList searchEntryContainingWordEnEnc(String field, 
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


	public void incr(String key, Element value) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			if ( ! jedis.exists(key))
				throw new WebApplicationException(  );
			else

				jedis.hincrBy(key, value.getField(), Integer.valueOf(value.getElement()));
		}
		incrTime += getTime() - begin;

	}


	public void incrEnc(String key, Element value) {
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


	public void incrEnEnc(String key, 
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



	public BigInteger sum(String key1,String field, String key2) {
		long begin = getTime();
		try {
			try (Jedis jedis = jedisPool.getResource()) {
				long value1 = Integer.valueOf(jedis.hget(key1, field));
				long value2 = Integer.valueOf(jedis.hget(key2, field));
				sumTime += getTime() - begin;
				return new BigInteger(Long.toString((value1+value2)));

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return new BigInteger("0");



	}


	public String sumEnc(String key1, 
			String field, 
			String nsquare, 
			String key2) {
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


	public String sumEnEnc(String key1, 
			String iv,
			String RandomKey,
			String field, 
			String nsquare, 
			String key2) {
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


	public BigInteger sumAll(String field) {
		long begin = getTime();
		try {
			try (Jedis jedis = jedisPool.getResource()) {
				long result = 0;
				Set<String> set = jedis.smembers(":"+field+":");
				for (String key : set){
					result += Long.valueOf(jedis.hget(key, field));
				}
				sumAllTime += getTime() - begin;
				return new BigInteger(Long.toString(result));

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return new BigInteger("0");



	}



	public String sumAllEnc(String field,
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


	public String sumAllEnEnc(String field,
			String iv,
			String RandomKey,
			String nsquare) {
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


	public BigInteger sumConst(String key, 
			String field, 
			int constant) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			long value = Integer.valueOf(jedis.hget(key, field));
			sumConstTime += getTime() - begin;
			return new BigInteger(Long.toString((value*constant)));


		}


	}


	public String sumConstEnc(String key, 
			String nsquare, 
			String field, 
			int constant) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {

			BigInteger result = HomoAdd.mult(new BigInteger(jedis.hget(key, field)), constant, new BigInteger(nsquare));

			sumConstTime += getTime() - begin;
			return result.toString();

		}



	}


	public String sumConst(String key, 
			String nsquare, 
			String iv,
			String RandomKey,
			String field, 
			int constant) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {

			BigInteger result = HomoAdd.mult(new BigInteger(RemoveEncryptLayer(RandomKey, iv,jedis.hget(key, field))), constant, new BigInteger(nsquare));

			sumConstTime += getTime() - begin;
			return  AddEncryptLayer(RandomKey, iv, result.toString());

		}



	}


	public long mult(String key1, 
			String field, 
			String key2) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			long value1 = Integer.valueOf(jedis.hget(key1, field));
			long value2 = Integer.valueOf(jedis.hget(key2, field));
			multTime += getTime() - begin;
			return value1*value2;
		}		



	}

	public String multEnc( String key1, 
			String field, 
			String publicKey, 
			String key2) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			BigInteger result = null;
			result = HomoMult.multiply(new BigInteger(jedis.hget(key1, field)), new BigInteger(jedis.hget(key2, field)), (RSAPublicKey)HelpSerial.fromString(publicKey));


			multTime += getTime() - begin;
			return result.toString();
		}		



	}


	public String multEnEnc(String key1, 
			String field, 
			String iv,
			String RandomKey,
			String publicKey, 
			String key2) {
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


	public long multAll(  String field) {
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


	public String multAllEnc(  String field,
			String publicKey) {
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


	public String multAllEnEnc(String field,
			String iv,
			String RandomKey,
			String publicKey) {
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


	public MyList searchElement( String field, 
			String value) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			List<String> l = jedis.smembers(field+":"+value).stream().collect(Collectors.toList());
			MyList list = new MyList(l);
			searchElemTime += getTime() - begin;
			return list;
		}


	}


	public MyList searchElementEnc(   String field, 
			String value) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			List<String> l = jedis.smembers(field+":"+value).stream().collect(Collectors.toList());
			MyList list = new MyList(l);
			searchElemTime += getTime() - begin;
			return list;
		}
	}


	public MyList searchElementEnEnc(  String field, 
			String iv,
			String RandomKey,
			String value) {
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


	public MyList searchEntrys(  List<String>  query) {
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


	public MyList searchEntrysEnc(  List<String>  query) {
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


	public MyList searchEntrysEnEnc(  List<String>  query,
			String iv,
			String RandomKey) {
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

	public MyList orderEntrys( String field) {
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


	public MyList orderEntrysEnc(  String field) {
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


	public MyList orderEntrysEnEnc(   String field,
			String iv,
			String RandomKey) {
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

	public MyList searchGreaterThan(  String field,  String value) {
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


	public MyList searchGreaterThanEnc(  String field,   String value) {
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



	public MyList searchGreaterThanEnEnc(   String field,   String value,
			String iv,
			String RandomKey) {
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


	public MyList searchLesserThan(  String field,  String value) {
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


	public MyList searchLesserThanEnc(   String field,   String value) {
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


	public MyList searchLesserThanEnEnc(  String field,   String value,
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


	public MyBoolean valuegreaterThan( String key1, 
			String field, 
			String key2) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			long value1 = Integer.valueOf(jedis.hget(key1, field));
			long value2 = Integer.valueOf(jedis.hget(key2, field));

			MyBoolean isGreater = new MyBoolean(value1>value2);
			valueGreaterTime+= getTime() - begin;
			return isGreater;

		}

	}


	public MyBoolean valuegreaterThanEnc( String key1, 
			String field, 
			String key2) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			long value1 = Long.valueOf(jedis.hget(key1, field));
			long value2 = Long.valueOf(jedis.hget(key2, field));

			MyBoolean isGreater = new MyBoolean(value1>value2);
			valueGreaterTime+= getTime() - begin;
			return isGreater;

		}

	}


	public MyBoolean valuegreaterThanEnEnc( String key1, 
			String field, 
			String key2,
			String iv,
			String RandomKey) {
		long begin = getTime();
		try (Jedis jedis = jedisPool.getResource()) {
			long value1 = Long.valueOf(RemoveEncryptLayer(RandomKey, iv, jedis.hget(key1, field)));
			long value2 = Long.valueOf(RemoveEncryptLayer(RandomKey, iv, jedis.hget(key2, field)));

			MyBoolean isGreater = new MyBoolean(value1>value2);
			valueGreaterTime+= getTime() - begin;
			return isGreater;

		}

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


	public long getTimes() {
		return getTime;
	}


	public long putTime() {
		return putTime;
	}




	public long removeTime() {
		return removeTime;
	}



	public long updateTime() {
		return updateTime;
	}



	public long incrTime() {
		return incrTime;
	}


	public long sumTime() {
		return sumTime;
	}


	public long sumConstTime() {
		return sumConstTime;
	}


	public long multTime() {
		return multTime;
	}



	public long searchElemTime() {
		return searchElemTime;
	}



	public long searchEntrysTime() {
		return searchEntrysTime;
	}


	public long orderEntrysTime() {
		return orderEntrysTime;
	}


	public long searchGreaterTime() {
		return searchGreaterTime;
	}



	public long searchLesserTime() {
		return searchLesserTime;
	}



	public long valueGreaterTime() {
		return valueGreaterTime;
	}


	public long getElemTime() {
		return getElemTime;
	}


	public long elementContainsSentenceTime() {
		return elementContainsSentenceTime;
	}


	public long searchEntryContainingSentenceTime() {
		return searchEntryContainingSentenceTime;
	}


	public long sumAllTime() {
		return sumAllTime;
	}


	public long multAllTime() {
		return multAllTime;
	}

	public void updateEntry(int element) {

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

	}

	public void flushAll() {

		try (Jedis jedis = jedisPool.getResource()) {
			jedis.flushAll();
		}
	}

	public Map<String, Map<String,String>> getstore() {

		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> list = jedis.smembers("keys");

			Map<String, Map<String,String>> map = new TreeMap<String, Map<String,String>>(); 
			for( String key : list) {
				Map<String,String> val = jedis.hgetAll(key);
				map.put(key,val);
			}
			
			return map;
		}
	}

}
