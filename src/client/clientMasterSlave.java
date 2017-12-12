package client;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.crypto.SecretKey;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.codec.binary.Base64;

import API.clientAPI;
import hlib.hj.mlib.HelpSerial;
import hlib.hj.mlib.HomoAdd;
import hlib.hj.mlib.HomoDet;
import hlib.hj.mlib.HomoMult;
import hlib.hj.mlib.HomoOpeInt;
import hlib.hj.mlib.HomoRand;
import hlib.hj.mlib.HomoSearch;
import hlib.hj.mlib.PaillierKey;
import utils.Cipher;
import utils.Element;
import utils.InsecureHostnameVerifier;
import utils.MyBoolean;
import utils.MyEntry;
import utils.MyList;
import utils.MyListEntry;
import utils.SecurityType;

public class clientMasterSlave implements clientAPI{

	public static final String ENCRYPTED_URL = "Encrypted/";
	public static final String ENHANCED_ENCRYPTED_URL = "EnhancedEncrypted/";
	private WebTarget target;
	private Client client;

	private Map<String,Cipher>  mapping;
	private SecurityType securityType;

	private SecretKey RandomKey;
	private SecretKey SearchKey;
	private SecretKey DetKey;
	private HomoOpeInt opeObject;
	private KeyPair MultkeyPair;
	private PaillierKey SumKey;
	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;
	private byte[] IV;

	private long PrivacyputTime;
	private long PrivacygetTime;
	private long PrivacyremoveTime;
	private long PrivacyupdateTime;
	private long PrivacyincrTime;
	private long PrivacysumTime;
	private long PrivacysumConstTime;
	private long PrivacymultTime;
	private long PrivacysearchElemTime;
	private long PrivacysearchEntrysTime;
	private long PrivacyorderEntrysTime;
	private long PrivacysearchGreaterTime;
	private long PrivacysearchLesserTime;
	private long PrivacyvalueGreaterTime;



	public clientMasterSlave(String targelUrl, SecurityType securityType, String key, Map<String,Cipher>  map) {
		//ClientConfig config = new ClientConfig().register(JacksonFeature.class);
		//client = ClientBuilder.newClient(config);
		client = ClientBuilder.newBuilder().hostnameVerifier(new InsecureHostnameVerifier()).build();
		URI baseURI = UriBuilder.fromUri(targelUrl).build();
		target = client.target(baseURI);

		DetKey = HomoDet.generateKey();
		SearchKey = HomoSearch.generateKey();
		RandomKey  = HomoRand.generateKey();
		IV = HomoRand.generateIV();
		SumKey = HomoAdd.generateKey();
		opeObject = new HomoOpeInt(key);
		MultkeyPair = HomoMult.generateKey();
		publicKey = (RSAPublicKey) MultkeyPair.getPublic();
		privateKey = (RSAPrivateKey) MultkeyPair.getPrivate();		

		mapping = map;
		this.securityType = securityType;

		initTimes();

	}

	private void initTimes() {
		PrivacyputTime = 0;
		PrivacygetTime= 0;
		PrivacyremoveTime= 0;
		PrivacyupdateTime= 0;
		PrivacyincrTime= 0;
		PrivacysumTime= 0;
		PrivacysumConstTime= 0;
		PrivacymultTime= 0;
		PrivacysearchElemTime= 0;
		PrivacysearchEntrysTime= 0;
		PrivacyorderEntrysTime= 0;
		PrivacysearchGreaterTime= 0;
		PrivacysearchLesserTime= 0;
		PrivacyvalueGreaterTime= 0;
	}

	public clientMasterSlave(String targelUrl) {
		//ClientConfig config = new ClientConfig().register(JacksonFeature.class);
		//client = ClientBuilder.newClient(config);
		client = ClientBuilder.newBuilder().hostnameVerifier(new InsecureHostnameVerifier()).build();
		URI baseURI = UriBuilder.fromUri(targelUrl).build();
		target = client.target(baseURI);

		this.securityType = SecurityType.NORMAL;
		initTimes();

	}

	public void Close() {
		client.close();
	}

	@Override
	public Future<Map<String,String>> getSet(String key) throws InterruptedException, ExecutionException {
		Future<Map<String,String>> future = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);


		switch (securityType) {
		case NORMAL:
			Future<MyEntry> entry = target.path("server/"+key)
			.request()
			.accept(MediaType.APPLICATION_JSON)
			.async()
			.get(MyEntry.class);


			future = executor.submit(new Callable<Map<String,String>>() {
				public Map<String,String> call() throws InterruptedException, ExecutionException {
					return entry.get().getAttributes();
				}});


			break;
		case ENCRYPTED: case ENHANCED_ENCRYPTED:

			long begin = getTime();
			String EncKey = HomoDet.encrypt(DetKey, key);
			PrivacygetTime += getTime() - begin;
			String url = getUrl();

			Future<MyEntry> entryEncrypted = target.path("server/"+url+EncKey)
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyEntry.class);

			future = executor.submit(new Callable<Map<String,String>>() {
				public Map<String,String> call() throws InterruptedException, ExecutionException {
					Map<String,String> m = entryEncrypted.get().getAttributes();
					long begin = getTime();
					Map<String,String> map = decryptMap(m);
					PrivacygetTime += getTime() - begin;
					return map;
				}});


			break;

		}

		executor.shutdown();
		return future;
	}


	@Override
	public String addSet(String key, Map<String, String> set) throws InterruptedException, ExecutionException {

		switch (securityType) {
		case NORMAL:

			MyEntry entry = new MyEntry(set);
			target.path("server/"+key)
			.request()
			.post( Entity.entity( entry, MediaType.APPLICATION_JSON));

			break;
		case ENCRYPTED:	case ENHANCED_ENCRYPTED:
			long begin = getTime();
			String EncKey = HomoDet.encrypt(DetKey, key);
			MyEntry EncEntry = new MyEntry(encryptMap(set));
			PrivacyputTime += getTime() - begin;

			String url = getUrl();

			target.path("server/" + url +EncKey)
			.request()
			.post( Entity.entity( EncEntry, MediaType.APPLICATION_JSON));
			break;

		}

		return null;
	}




	@Override
	public boolean removeSet(String key) throws InterruptedException, ExecutionException {

		switch (securityType) {
		case NORMAL:

			target.path("server/"+key)
			.request()
			.delete();

			break;
		case ENCRYPTED: case ENHANCED_ENCRYPTED:
			long begin = getTime();
			String EncKey = HomoDet.encrypt(DetKey, key);
			PrivacyremoveTime += getTime() - begin;
			String url = getUrl();
			target.path("server/"+ url +EncKey)
			.request()
			.delete();
			break;
		}

		return true;
	}

	@Override
	public String addElement(String key, String field, String element) {

		switch (securityType) {
		case NORMAL:
			Element elem = new Element(field, element);
			target.path("server/"+key)
			.request()
			.put(Entity.entity( elem, MediaType.APPLICATION_JSON));
			break;
		case ENCRYPTED:
		case ENHANCED_ENCRYPTED:
			long begin = getTime();
			String EncKey = HomoDet.encrypt(DetKey, key);
			Element EncElem = encryptElement(field, element);
			PrivacyupdateTime += getTime() - begin;
			String url = getUrl();
			Response response = target.path("server/"+url+EncKey)
					.request()
					.put(Entity.entity( EncElem, MediaType.APPLICATION_JSON));
			response.getStatus();

			break;

		}


		return null;
	}

	@Override
	public Future<String> getElement(String key, String field) {
		Future<String> future = null;
		switch (securityType) {
		case NORMAL:
			future = target.queryParam("key", key).queryParam("field", field)
			.path("server/getElem")
			.request()
			.async()
			.get(String.class);

			break;
		case ENCRYPTED: case ENHANCED_ENCRYPTED:

			String EncKey = HomoDet.encrypt(DetKey, key);
			String Encfield = HomoDet.encrypt(DetKey, field);
			String url = getUrl();
			Future<String> response = target.queryParam("key", EncKey).queryParam("field", Encfield)
					.path("server/getElem"+url)
					.request()
					.async()
					.get(String.class);

			ExecutorService executor = Executors.newFixedThreadPool(1);
			future = executor.submit(new Callable<String>() {
				public String call() throws InterruptedException, ExecutionException {
					String cipherKey = response.get();
					String DecKey = HomoDet.decrypt(DetKey, cipherKey);
					return DecKey;
				}});
			executor.shutdown();
			break;

		}

		return future;
	}



	@Override
	public Future<Boolean> elementContainsSentence(String key, String field, String sentence) {
		Future<Boolean> future = null;
		String EncKey = null;
		String Encfield = null;
		String Encsentence = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		switch (securityType) {
		case NORMAL:
			Future<MyBoolean> contains = target.queryParam("key", key).queryParam("field", field).queryParam("sentence", sentence).path("server/elementContainsSentence/")
			.request()
			.accept(MediaType.APPLICATION_JSON)
			.async()
			.get(MyBoolean.class);

			future = executor.submit(new Callable<Boolean>() {
				public Boolean call() throws InterruptedException, ExecutionException {
					return contains.get().isMyboolean();
				}});



			break;
		case ENCRYPTED:

			EncKey = HomoDet.encrypt(DetKey, key);
			Encfield = HomoDet.encrypt(DetKey, field);
			Encsentence = HomoSearch.encrypt(SearchKey, sentence);

			Future<MyBoolean> Enccontains = target.queryParam("key", EncKey).queryParam("field", Encfield).queryParam("sentence", Encsentence).path("server/elementContainsSentence/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyBoolean.class);
			future = executor.submit(new Callable<Boolean>() {
				public Boolean call() throws InterruptedException, ExecutionException {
					return Enccontains.get().isMyboolean();
				}});

			break;
		case ENHANCED_ENCRYPTED:

			EncKey = HomoDet.encrypt(DetKey, key);
			Encfield = HomoDet.encrypt(DetKey, field);
			Encsentence = HomoSearch.encrypt(SearchKey, sentence);
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);


			Future<MyBoolean> EnEnccontains = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("key", EncKey).queryParam("field", Encfield).queryParam("sentence", Encsentence).path("server/elementContainsSentence/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyBoolean.class);
			future = executor.submit(new Callable<Boolean>() {
				public Boolean call() throws InterruptedException, ExecutionException {
					return EnEnccontains.get().isMyboolean();
				}});

			break;

		}

		executor.shutdown();

		return future;
	}

	@Override
	public Future<List<String>> searchEntryContainingSentence(String field, String sentence) {
		Future<List<String>> future = null;
		String Encfield = null;
		String Encsentence = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		switch (securityType) {
		case NORMAL:
			Future<MyList> list = target.queryParam("field", field).queryParam("sentence", sentence).path("server/searchEntryContainingSentence/")
			.request()
			.accept(MediaType.APPLICATION_JSON)
			.async()
			.get(MyList.class);

			future = FutureGetListString(list);

			break;
		case ENCRYPTED:

			Encfield = HomoDet.encrypt(DetKey, field);
			Encsentence = HomoSearch.encrypt(SearchKey, sentence);

			Future<MyList> Enclist = target.queryParam("field", Encfield).queryParam("sentence", Encsentence).path("server/searchEntryContainingSentence/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyList.class);


			future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = Enclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { 
						String DecryptedKey = HomoDet.decrypt(DetKey, s);
						c.add(DecryptedKey);

						;});

					return c;
				}});

			break;
		case ENHANCED_ENCRYPTED:

			Encfield = HomoDet.encrypt(DetKey, field);
			Encsentence = HomoSearch.encrypt(SearchKey, sentence);
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);

			Future<MyList> EnEnclist = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("field", Encfield).queryParam("sentence", Encsentence).path("server/searchEntryContainingSentence/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyList.class);


			future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = EnEnclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { 
						String DecryptedKey = HomoDet.decrypt(DetKey, s);
						c.add(DecryptedKey);

						;});

					return c;
				}});

			break;

		}
		executor.shutdown();

		return future;
	}





	@Override
	public void incr(String key,String field) {

		incrBy(key,field, 1);

	}

	@Override
	public void incrBy(String key,String field, int value) {
		String val = Integer.toString(value);
		long begin=0;
		String EncKey = null;
		Element elem = null;
		switch (securityType) {
		case NORMAL:

			elem = new Element(field, val);
			target.path("server/incr/"+key)
			.request()
			.put(Entity.entity( elem, MediaType.APPLICATION_JSON));



			break;
		case ENCRYPTED:

			begin = getTime();
			EncKey = HomoDet.encrypt(DetKey, key);
			elem = encryptElement(field, val);
			elem.setKey(SumKey.getNsquare().toString());
			PrivacyincrTime += getTime() - begin;
			target.path("server/incr/"+EncKey)
			.request()
			.put(Entity.entity( elem, MediaType.APPLICATION_JSON));


			break;
		case ENHANCED_ENCRYPTED:

			begin = getTime();
			EncKey = HomoDet.encrypt(DetKey, key);
			elem = encryptElement(field, val);
			elem.setKey(SumKey.getNsquare().toString());
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);
			PrivacyincrTime += getTime() - begin;
			target.queryParam("iv", iv).queryParam("RandomKey", RandKey).path("server/incr/"+EncKey)
			.request()
			.put(Entity.entity( elem, MediaType.APPLICATION_JSON));

			break;

		}

	}

	@Override
	public Future<Integer> sum(String key1, String field, String key2) {

		Future<Integer> future = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		String EncKey1 = null;
		String EncKey2 =null;
		String Encfield = null;
		long begin=0;
		switch (securityType) {
		case NORMAL:
			future = target.queryParam("key1", key1).queryParam("key2", key2).queryParam("field", field).path("server/sum/")
			.request().async()
			.get(Integer.class);

			break;
		case ENCRYPTED:

			begin = getTime();
			EncKey1 = HomoDet.encrypt(DetKey, key1);
			EncKey2 = HomoDet.encrypt(DetKey, key2);
			Encfield = HomoDet.encrypt(DetKey, field);
			PrivacysumTime += getTime() - begin;

			Future<String> Encresponse = target.queryParam("nsquare", SumKey.getNsquare().toString()).queryParam("key1", EncKey1).queryParam("key2", EncKey2).queryParam("field", Encfield).path("server/sum/")
					.request().async()
					.get(String.class);

			future = executor.submit(new Callable<Integer>() {
				public Integer call() throws InterruptedException, ExecutionException {
					String resp= Encresponse.get();
					long begin = getTime();
					System.err.println(resp);
					String l = decryptValue(field,resp );
					System.err.println(l);
					Integer result = Integer.valueOf(l);
					PrivacysumTime += getTime() - begin;
					return result;
				}});




			break;
		case ENHANCED_ENCRYPTED:

			begin = getTime();
			EncKey1 = HomoDet.encrypt(DetKey, key1);
			EncKey2 = HomoDet.encrypt(DetKey, key2);
			Encfield = HomoDet.encrypt(DetKey, field);
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);
			PrivacysumTime += getTime() - begin;

			Future<String> EnEncresponse = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("nsquare", SumKey.getNsquare().toString()).queryParam("key1", EncKey1).queryParam("key2", EncKey2).queryParam("field", Encfield).path("server/sum/")
					.request().async()
					.get(String.class);


			future = executor.submit(new Callable<Integer>() {
				public Integer call() throws InterruptedException, ExecutionException {
					String resp= EnEncresponse.get();
					long begin = getTime();
					Integer result = Integer.valueOf(decryptValue(field,resp ));
					PrivacysumTime += getTime() - begin;
					return result;
				}});

			break;

		}
		executor.shutdown();

		return future;
	}
	@Override
	public Future<Integer> sumAll(String field) {
		Future<Integer> future = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		String Encfield = null;

		long begin=0;
		switch (securityType) {
		case NORMAL:
			future = target.queryParam("field", field).path("server/sumAll/")
			.request().async()
			.get(Integer.class);

			break;
		case ENCRYPTED:

			Encfield = HomoDet.encrypt(DetKey, field);

			Future<String> Encresponse = target.queryParam("nsquare", SumKey.getNsquare().toString()).queryParam("field", Encfield).path("server/sumAll/")
					.request().async()
					.get(String.class);


			future = executor.submit(new Callable<Integer>() {
				public Integer call() throws InterruptedException, ExecutionException {
					String resp= Encresponse.get();
					System.out.println(resp);
					long begin = getTime();
					Integer result = Integer.valueOf(decryptValue(field,resp ));

					return result;
				}});


			break;
		case ENHANCED_ENCRYPTED:


			Encfield = HomoDet.encrypt(DetKey, field);
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);

			Future<String> EnEncresponse = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("nsquare", SumKey.getNsquare().toString()).queryParam("field", Encfield).path("server/sumAll/")
					.request().async()
					.get(String.class);


			future = executor.submit(new Callable<Integer>() {
				public Integer call() throws InterruptedException, ExecutionException {
					String resp= EnEncresponse.get();
					System.out.println(resp);
					long begin = getTime();
					Integer result = Integer.valueOf(decryptValue(field,resp ));

					return result;
				}});

			break;

		default:
			break;
		}
		executor.shutdown();

		return future;
	}


	@Override
	public Future<Integer> multConst(String key, String field, int constant) {

		Future<Integer> future = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		String EncKey = null;
		String Encfield = null;
		long begin=0;
		switch (securityType) {
		case NORMAL:

			future = target.queryParam("key", key).queryParam("const", constant).queryParam("field", field).path("server/multConst/")
			.request().async()
			.get(Integer.class);

			break;
		case ENCRYPTED:
			begin = getTime();
			EncKey = HomoDet.encrypt(DetKey, key);
			Encfield = HomoDet.encrypt(DetKey, field);
			PrivacysumConstTime += getTime() - begin;
			Future<String> Encresponse = target.queryParam("nsquare", SumKey.getNsquare().toString()).queryParam("key", EncKey).queryParam("const", constant).queryParam("field", Encfield).path("server/multConst/")
					.request().async()
					.get(String.class);


			future = executor.submit(new Callable<Integer>() {
				public Integer call() throws InterruptedException, ExecutionException {
					String resp= Encresponse.get();
					long begin = getTime();
					Integer result = Integer.valueOf(decryptValue(field, resp));
					PrivacysumConstTime += getTime() - begin;
					return result;
				}});

			break;
		case ENHANCED_ENCRYPTED:

			begin = getTime();
			EncKey = HomoDet.encrypt(DetKey, key);
			Encfield = HomoDet.encrypt(DetKey, field);
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);
			PrivacysumConstTime += getTime() - begin;
			Future<String> EnEncresponse = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("nsquare", SumKey.getNsquare().toString()).queryParam("key", EncKey).queryParam("const", constant).queryParam("field", Encfield).path("server/multConst/")
					.request().async()
					.get(String.class);


			future = executor.submit(new Callable<Integer>() {
				public Integer call() throws InterruptedException, ExecutionException {
					String resp= EnEncresponse.get();
					long begin = getTime();
					Integer result = Integer.valueOf(decryptValue(field, resp));
					PrivacysumConstTime += getTime() - begin;
					return result;
				}});

			break;

		default:
			break;
		}

		executor.shutdown();
		return future;
	}

	@Override
	public Future<Integer> mult(String key1, String field, String key2) {
		Future<Integer> future = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		String EncKey1 = null;
		String EncKey2 = null;
		String Encfield = null;
		String publicRSAKey = null;
		long begin=0;
		switch (securityType) {
		case NORMAL:
			future = target.queryParam("key1", key1).queryParam("key2", key2).queryParam("field", field).path("server/mult/")
			.request().async()
			.get(Integer.class);

			break;
		case ENCRYPTED:

			begin = getTime();
			EncKey1 = HomoDet.encrypt(DetKey, key1);
			EncKey2 = HomoDet.encrypt(DetKey, key2);
			Encfield = HomoDet.encrypt(DetKey, field);
			publicRSAKey = HelpSerial.toString(publicKey);
			PrivacymultTime += getTime() - begin;

			Future<String> Encresponse = target.queryParam("publicKey", publicRSAKey).queryParam("key1", EncKey1).queryParam("key2", EncKey2).queryParam("field", Encfield).path("server/mult/")
					.request().async()
					.get(String.class);

			future = executor.submit(new Callable<Integer>() {
				public Integer call() throws InterruptedException, ExecutionException {
					String resp= Encresponse.get();
					long begin = getTime();
					Integer result = Integer.valueOf(decryptValue(field, resp));
					PrivacymultTime += getTime() - begin;
					return result;
				}});


			break;
		case ENHANCED_ENCRYPTED:

			begin = getTime();
			EncKey1 = HomoDet.encrypt(DetKey, key1);
			EncKey2 = HomoDet.encrypt(DetKey, key2);
			Encfield = HomoDet.encrypt(DetKey, field);
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);
			publicRSAKey = HelpSerial.toString(publicKey);
			PrivacymultTime += getTime() - begin;

			Future<String> EnEncresponse = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("publicKey", publicRSAKey).queryParam("key1", EncKey1).queryParam("key2", EncKey2).queryParam("field", Encfield).path("server/mult/")
					.request().async()
					.get(String.class);

			future = executor.submit(new Callable<Integer>() {
				public Integer call() throws InterruptedException, ExecutionException {
					String resp= EnEncresponse.get();
					long begin = getTime();
					Integer result = Integer.valueOf(decryptValue(field, resp));
					PrivacymultTime += getTime() - begin;
					return result;
				}});

			break;

		}

		executor.shutdown();
		return future;
	}

	@Override
	public Future<Integer> multAll(String field) {
		Future<Integer> future = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		String Encfield = null;
		String publicRSAKey = null;
		long begin=0;
		switch (securityType) {
		case NORMAL:
			future = target.queryParam("field", field).path("server/multAll/")
			.request().async()
			.get(Integer.class);

			break;
		case ENCRYPTED:
			Encfield = HomoDet.encrypt(DetKey, field);
			publicRSAKey = HelpSerial.toString(publicKey);
			Future<String> Encresponse = target.queryParam("publicKey", publicRSAKey).queryParam("field", Encfield).path("server/multAll/")
					.request().async()
					.get(String.class);

			future = executor.submit(new Callable<Integer>() {
				public Integer call() throws InterruptedException, ExecutionException {
					String resp= Encresponse.get();
					long begin = getTime();
					Integer result = Integer.valueOf(decryptValue(field,resp ));

					return result;
				}});

			break;
		case ENHANCED_ENCRYPTED:
			Encfield = HomoDet.encrypt(DetKey, field);
			publicRSAKey = HelpSerial.toString(publicKey);
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);
			Future<String> EnEncresponse = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("publicKey", publicRSAKey).queryParam("field", Encfield).path("server/multAll/")
					.request().async()
					.get(String.class);

			future = executor.submit(new Callable<Integer>() {
				public Integer call() throws InterruptedException, ExecutionException {
					String resp= EnEncresponse.get();
					long begin = getTime();
					Integer result = Integer.valueOf(decryptValue(field,resp ));

					return result;
				}});


			break;

		}
		executor.shutdown();
		return future;
	}


	@Override
	public Future<List<String>> searchElement(String field, String value) {
		Future<List<String>> future = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		String Encfield = null;
		String Encvalue = null;
		long begin=0;
		switch (securityType) {
		case NORMAL:
			Future<MyList> list = target.queryParam("field", field).queryParam("value", value).path("server/searchElement/")
			.request()
			.accept(MediaType.APPLICATION_JSON)				
			.async()
			.get(MyList.class);
			future = FutureGetListString(list);

			break;
		case ENCRYPTED:

			begin = getTime();
			Encfield = HomoDet.encrypt(DetKey, field);
			Encvalue = HomoDet.encrypt(DetKey, value);
			PrivacysearchElemTime += getTime() - begin;

			Future<MyList> Enclist = target.queryParam("field", Encfield).queryParam("value", Encvalue).path("server/searchElement/")
					.request()
					.accept(MediaType.APPLICATION_JSON)				
					.async()
					.get(MyList.class);


			future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = Enclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { c.add(HomoDet.decrypt(DetKey, s));});
					PrivacysearchElemTime += getTime() - begin;
					return c;
				}});

			break;
		case ENHANCED_ENCRYPTED:

			begin = getTime();
			Encfield = HomoDet.encrypt(DetKey, field);
			Encvalue = HomoDet.encrypt(DetKey, value);
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);
			PrivacysearchElemTime += getTime() - begin;

			Future<MyList> EnEnclist = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("field", Encfield).queryParam("value", Encvalue).path("server/searchElement/")
					.request()
					.accept(MediaType.APPLICATION_JSON)				
					.async()
					.get(MyList.class);


			future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = EnEnclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { c.add(HomoDet.decrypt(DetKey, s));});
					PrivacysearchElemTime += getTime() - begin;
					return c;
				}});

			break;


		}
		executor.shutdown();

		return future;

	}

	@Override
	public Future<List<String>> searchEntry(Map<String, String> set) {


		Future<List<String>> future = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		String Encfield = null;
		String Encvalue = null;

		long begin=0;
		switch (securityType) {
		case NORMAL:

			List<String> l = new LinkedList<String>();
			set.forEach((k, v) -> l.add(k+":"+v));

			Future<MyList> list = target.queryParam("query", l.toArray()).path("server/searchEntrys/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyList.class);

			future = FutureGetListString(list);

			break;
		case ENCRYPTED:

			begin = getTime();
			List<String> Encl = new LinkedList<String>();
			set.forEach((k, v) -> Encl.add(HomoDet.encrypt(DetKey, k)+":"+HomoDet.encrypt(DetKey, v)));
			PrivacysearchEntrysTime += getTime() - begin;
			Future<MyList> Enclist = target.queryParam("query", Encl.toArray()).path("server/searchEntrys/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyList.class);

			future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = Enclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { c.add(HomoDet.decrypt(DetKey, s));});
					PrivacysearchEntrysTime += getTime() - begin;
					return c;
				}});

			break;
		case ENHANCED_ENCRYPTED:

			begin = getTime();
			l = new LinkedList<String>();
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);
			set.forEach((k, v) -> l.add(":"+HomoDet.encrypt(DetKey, k)+":/"+HomoDet.encrypt(DetKey, v)));
			PrivacysearchEntrysTime += getTime() - begin;
			Future<MyList> EnEnclist = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("query", l.toArray()).path("server/searchEntrys/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyList.class);

			future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = EnEnclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { c.add(HomoDet.decrypt(DetKey, s));});
					PrivacysearchEntrysTime += getTime() - begin;
					return c;
				}});

			break;

		default:
			break;
		}
		executor.shutdown();
		return future;


	}

	@Override
	public Future<List<String>> orderEntrys(String field) {


		Future<List<String>> future = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		String Encfield = null;
		String Encvalue = null;
		long begin=0;
		switch (securityType) {
		case NORMAL:
			Future<MyList> list = target.queryParam("query", field).path("server/orderEntrys/")
			.request()
			.accept(MediaType.APPLICATION_JSON)
			.async()
			.get(MyList.class);

			future = FutureGetListString(list);

			break;
		case ENCRYPTED:
			 begin = getTime();
			 Encfield = HomoDet.encrypt(DetKey, field);
			PrivacyorderEntrysTime += getTime() - begin;
			Future<MyList> Enclist = target.queryParam("query", Encfield).path("server/orderEntrys/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyList.class);

			 future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = Enclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { 
						String DecryptedKey = HomoDet.decrypt(DetKey, s);
						c.add(DecryptedKey);

						;});
					PrivacyorderEntrysTime += getTime() - begin;
					return c;
				}});
			break;
		case ENHANCED_ENCRYPTED:
			 begin = getTime();
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);
			 Encfield = HomoDet.encrypt(DetKey, field);
			PrivacyorderEntrysTime += getTime() - begin;
			Future<MyList> EnEnclist = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("query", Encfield).path("server/orderEntrys/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyList.class);

			 future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = EnEnclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { 
						String DecryptedKey = HomoDet.decrypt(DetKey, s);
						c.add(DecryptedKey);
						
						;});
					PrivacyorderEntrysTime += getTime() - begin;
					return c;
				}});

			break;

		}

		executor.shutdown();

		return future;
	}

	@Override
	public Future<List<String>> searchGreaterThan(String field, int value) {

		Future<List<String>> future = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		String Encfield = null;
		long encValue = 0;
		long begin=0;
		switch (securityType) {
		case NORMAL:
			
			Future<MyList> list = target.queryParam("field", field).queryParam("value", value).path("server/searchGreaterThan/")
			.request()
			.accept(MediaType.APPLICATION_JSON)
			.async()
			.get(MyList.class);

	future =  FutureGetListString(list);

			break;
		case ENCRYPTED:
			
			 begin = getTime();
			 Encfield = HomoDet.encrypt(DetKey, field);
			 encValue = opeObject.encrypt(value);
			PrivacysearchGreaterTime += getTime() - begin;
			Future<MyList> Enclist = target.queryParam("field", Encfield).queryParam("value", encValue).path("server/searchGreaterThan/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyList.class);

			future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = Enclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { 
						String DecryptedKey = HomoDet.decrypt(DetKey, s);
						c.add(DecryptedKey);

						;});
					PrivacysearchGreaterTime += getTime() - begin;
					return c;
				}});

			break;
		case ENHANCED_ENCRYPTED:
			
			 begin = getTime();
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);
			 Encfield = HomoDet.encrypt(DetKey, field);
			 encValue = opeObject.encrypt(value);
			PrivacysearchGreaterTime += getTime() - begin;
			Future<MyList> EnEnclist = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("field", Encfield).queryParam("value", encValue).path("server/searchGreaterThan/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyList.class);
			
			 future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = EnEnclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { 
						String DecryptedKey = HomoDet.decrypt(DetKey, s);
						c.add(DecryptedKey);
						
						;});
					PrivacysearchGreaterTime += getTime() - begin;
					return c;
				}});

			break;

		}

		executor.shutdown();

		return future;
	}

	@Override
	public Future<List<String>> searchLesserThan(String field, int value) {

		Future<List<String>> future = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		String Encfield = null;
		long encValue = 0;
		long begin=0;
		
		switch (securityType) {
		case NORMAL:
			
			Future<MyList> list = target.queryParam("field", field).queryParam("value", value).path("server/searchLesserThan/")
			.request()
			.accept(MediaType.APPLICATION_JSON)
			.async()
			.get(MyList.class);

	return FutureGetListString(list);

			break;
		case ENCRYPTED:
			
			 begin = getTime();
			 Encfield = HomoDet.encrypt(DetKey, field);
			 encValue = opeObject.encrypt(value);
			PrivacysearchLesserTime += getTime() - begin;

			Future<MyList> Enclist = target.queryParam("field", Encfield).queryParam("value", encValue).path("server/searchLesserThan/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyList.class);

			future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = Enclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { 
						String DecryptedKey = HomoDet.decrypt(DetKey, s);
						c.add(DecryptedKey);

						;});
					PrivacysearchLesserTime += getTime() - begin;
					return c;
				}});

			break;
		case ENHANCED_ENCRYPTED:
			
			 begin = getTime();
			String RandKey = HelpSerial.toString(RandomKey);
			String iv = Base64.encodeBase64String(IV);
			 Encfield = HomoDet.encrypt(DetKey, field);
			 encValue = opeObject.encrypt(value);
			PrivacysearchLesserTime += getTime() - begin;
			
			Future<MyList> EnEnclist = target.queryParam("iv", iv).queryParam("RandomKey", RandKey).queryParam("field", Encfield).queryParam("value", encValue).path("server/searchLesserThan/")
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.async()
					.get(MyList.class);

			 future = executor.submit(new Callable<List<String>>() {
				public List<String> call() throws InterruptedException, ExecutionException {
					List<String> l = EnEnclist.get().getList();
					List<String> c = new LinkedList<String>();
					long begin = getTime();
					l.forEach((s) -> { 
						String DecryptedKey = HomoDet.decrypt(DetKey, s);
						c.add(DecryptedKey);
						
						;});
					PrivacysearchLesserTime += getTime() - begin;
					return c;
				}});

			break;

		}
		executor.shutdown();

		return future;


	}

	@Override
	public Future<Boolean> valuegreaterThan(String key1, String field, String key2) {

		switch (securityType) {
		case NORMAL:

			break;
		case ENCRYPTED:

			break;
		case ENHANCED_ENCRYPTED:

			break;

		default:
			break;
		}

		Future<MyBoolean> isGreater = target.queryParam("key1", key1).queryParam("field", field).queryParam("key2", key2).path("server/valuegreaterThan/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyBoolean.class);

		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<Boolean> future = executor.submit(new Callable<Boolean>() {
			public Boolean call() throws InterruptedException, ExecutionException {
				return isGreater.get().isMyboolean();
			}});
		executor.shutdown();

		return future;
	}


	//	private Future<List<MyEntry>> FutureGetListMyEntry(Future<MyListEntry> list) {
	//
	//		ExecutorService executor = Executors.newFixedThreadPool(1);
	//		Future<List<MyEntry>> future = executor.submit(new Callable<List<MyEntry>>() {
	//			public List<MyEntry> call() throws InterruptedException, ExecutionException {
	//				return list.get().getList();
	//			}});
	//		executor.shutdown();
	//
	//		return future;
	//	}

	private Future<List<String>> FutureGetListString(Future<MyList> list) {

		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<List<String>> future = executor.submit(new Callable<List<String>>() {
			public List<String> call() throws InterruptedException, ExecutionException {
				return list.get().getList();
			}});
		executor.shutdown();

		return future;
	}

	private Element encryptElement(String field , String elem){

		String key = HomoDet.encrypt(DetKey, field);
		String value = encryptValue(field, elem);

		return new Element(key, value);
	} 

	private String decryptValue (String field, String encElem) {
		String EncElem = null;
		switch (securityType) {
		case NORMAL:

			break;
		case ENCRYPTED:
			EncElem = encElem;
			break;
		case ENHANCED_ENCRYPTED:
			EncElem = HomoRand.decrypt(RandomKey,IV, encElem);
			break;

		default:
			break;
		}

		String value = null;
		Cipher c = null;
		if((c = mapping.get(field)) == null)
			c = Cipher.NONE;
		switch (c) {
		case MULT:
			value = HomoMult.decrypt(privateKey, new BigInteger(EncElem)).toString();
			break;
		case ADD:
			try {
				value = HomoAdd.decrypt(new BigInteger(EncElem), SumKey).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case OPE:
			value =  Long.toString(opeObject.decrypt(Long.valueOf(EncElem)));

			break;
		case DET:
			value = HomoDet.decrypt(DetKey, EncElem);
			break;
		case SEARCH:
			value = HomoSearch.decrypt(SearchKey, EncElem);
			break;
		default:
			value = HomoRand.decrypt(RandomKey,IV, EncElem);
			break;
		}
		return  value;

	}

	private String encryptValue(String field, String elem) {

		String value = null;

		Cipher c = null;
		if((c = mapping.get(field)) == null)
			c = Cipher.NONE;
		switch (c) {
		case MULT:
			value = HomoMult.encrypt(publicKey, new BigInteger(elem)).toString();
			break;
		case ADD:
			try {
				value = HomoAdd.encrypt(new BigInteger(elem), SumKey).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case OPE:
			value =  Long.toString(opeObject.encrypt(Integer.valueOf(elem)));

			break;
		case DET:
			value = HomoDet.encrypt(DetKey, elem);
			break;
		case SEARCH:
			value = HomoSearch.encrypt(SearchKey, elem);
			break;
		default:
			value = HomoRand.encrypt(RandomKey,IV,elem);
			break;
		}

		String EncElem = null;
		switch (securityType) {
		case NORMAL:

			break;
		case ENCRYPTED:
			EncElem = value;
			break;
		case ENHANCED_ENCRYPTED:
			EncElem = HomoRand.encrypt(RandomKey, IV, value);
			break;

		default:
			break;
		}
		return EncElem;
	}


	private Map<String, String> encryptMap(Map<String, String> set){

		Map<String,String> map = new HashMap<String,String>();
		set.forEach((k, v) -> { 
			String key = HomoDet.encrypt(DetKey, k);
			String value = encryptValue(k, v);
			map.put(key, value);
		}); 

		return map;
	} 

	private Map<String, String> decryptMap(Map<String, String> set){

		Map<String,String> map = new HashMap<String,String>();
		set.forEach((k, v) -> { 
			String key = HomoDet.decrypt(DetKey, k);
			String value = decryptValue(key, v);
			map.put(key, value);

		}); 

		return map;
	} 


	private long getTime() {
		return Calendar.getInstance().getTimeInMillis();
	}

	public long getServerputTime() {

		long result = target.path("server/putTime/")
				.request()
				.get(Integer.class);

		return result;

	}


	public long getServergetTime() {

		long result = target.path("server/getTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServerremoveTime() {

		long result = target.path("server/removeTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServerupdateTime() {

		long result = target.path("server/updateTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServerincrTime() {

		long result = target.path("server/incrTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServersumTime() {

		long result = target.path("server/sumTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServersumConstTime() {

		long result = target.path("server/sumConstTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServermultTime() {

		long result = target.path("server/multTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServersearchElemTime() {

		long result = target.path("server/searchElemTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServersearchEntrysTime() {

		long result = target.path("server/searchEntrysTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServerorderEntrysTime() {

		long result = target.path("server/orderEntrysTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServersearchGreaterTime() {

		long result = target.path("server/searchGreaterTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServersearchLesserTime() {

		long result = target.path("server/searchLesserTime/")
				.request()
				.get(Integer.class);

		return result;

	}

	public long getServervalueGreaterTime() {

		long result = target.path("server/valueGreaterTime/")
				.request()
				.get(Integer.class);

		return result;

	}


	private String  getUrl() {
		if(securityType == SecurityType.ENCRYPTED)
			return ENCRYPTED_URL;
		else
			return ENHANCED_ENCRYPTED_URL;

	}





}
