package client;

import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import API.clientAPI;
import utils.Element;
import utils.InsecureHostnameVerifier;
import utils.MyBoolean;
import utils.MyEntry;
import utils.MyList;
import utils.MyListEntry;

public class clientMasterSlave implements clientAPI{

	WebTarget target;
	Client client;
	

	public clientMasterSlave(String targelUrl) {
		//ClientConfig config = new ClientConfig().register(JacksonFeature.class);
		//client = ClientBuilder.newClient(config);
		client = ClientBuilder.newBuilder().hostnameVerifier(new InsecureHostnameVerifier()).build();
		URI baseURI = UriBuilder.fromUri(targelUrl).build();
		target = client.target(baseURI);

	}

	public void Close() {
		client.close();
	}
	
	@Override
	public Future<Map<String,String>> getSet(String key) throws InterruptedException, ExecutionException {

		
		Future<MyEntry> entry = target.path("server/"+key)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyEntry.class);

		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<Map<String,String>> future = executor.submit(new Callable<Map<String,String>>() {
			public Map<String,String> call() throws InterruptedException, ExecutionException {
				return entry.get().getAttributes();
			}});

		executor.shutdown();
		
		return future;
	}



	@Override
	public String addSet(String key, Map<String, String> set) throws InterruptedException, ExecutionException {

		MyEntry entry = new MyEntry(set);
		Response response = target.path("server/"+key)
				.request()
				.post( Entity.entity( entry, MediaType.APPLICATION_JSON));
		response.getStatus();
		return null;
	}
	



	@Override
	public boolean removeSet(String key) throws InterruptedException, ExecutionException {

		Response  response = target.path("server/"+key)
				.request()
				.delete();
		response.getStatus();
		return true;
	}

	@Override
	public String addElement(String key, String field, String element) {

		Element elem = new Element(field, element);
		Response response = target.path("server/"+key)
				.request()
				.put(Entity.entity( elem, MediaType.APPLICATION_JSON));
		response.getStatus();

		return null;
	}
	
	@Override
	public Future<String> getElement(String key, String field) {
		

		
		Future<String> response = target.queryParam("key", key).queryParam("field", field)
				.path("server/getElem")
				.request()
				.async()
				.get(String.class);

		return response;
	}
	

	
	@Override
	public Future<Boolean> elementContainsSentence(String key, String field, String sentence) {
		Future<MyBoolean> contains = target.queryParam("key", key).queryParam("field", field).queryParam("sentence", sentence).path("server/elementContainsSentence/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyBoolean.class);

		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<Boolean> future = executor.submit(new Callable<Boolean>() {
			public Boolean call() throws InterruptedException, ExecutionException {
				return contains.get().isMyboolean();
			}});
		executor.shutdown();

		return future;
	}

	@Override
	public Future<List<String>> searchEntryContainingSentence(String field, String sentence) {
		Future<MyList> list = target.queryParam("field", field).queryParam("sentence", sentence).path("server/searchEntryContainingSentence/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyList.class);

		return FutureGetListString(list);
	}





	@Override
	public void incr(String key,String field) {

		incrBy(key,field, 1);

	}

	@Override
	public void incrBy(String key,String field, int value) {
		String val = Integer.toString(value);
		Element elem = new Element(field, val);
		Response response = target.path("server/incr/"+key)
				.request()
				.put(Entity.entity( elem, MediaType.APPLICATION_JSON));
		response.getStatus();

	}

	@Override
	public Future<Integer> sum(String key1, String field, String key2) {

		Future<Integer> response = target.queryParam("key1", key1).queryParam("key2", key2).queryParam("field", field).path("server/sum/")
				.request().async()
				.get(Integer.class);
		return response;
	}
	@Override
	public Future<Integer> sumAll(String field) {
		
		Future<Integer> response = target.queryParam("field", field).path("server/sumAll/")
				.request().async()
				.get(Integer.class);
		return response;
	}


	@Override
	public Future<Integer> multConst(String key, String field, int constant) {

		Future<Integer> response = target.queryParam("key", key).queryParam("const", constant).queryParam("field", field).path("server/multConst/")
				.request().async()
				.get(Integer.class);
		return response;
	}

	@Override
	public Future<Integer> mult(String key1, String field, String key2) {

		Future<Integer> response = target.queryParam("key1", key1).queryParam("key2", key2).queryParam("field", field).path("server/mult/")
				.request().async()
				.get(Integer.class);
		return response;
	}
	
	@Override
	public Future<Integer> multAll(String field) {
		Future<Integer> response = target.queryParam("field", field).path("server/multAll/")
				.request().async()
				.get(Integer.class);
		return response;
	}


	@Override
	public Future<List<String>> searchElement(String field, String value) {
		Future<MyList> list = target.queryParam("field", field).queryParam("value", value).path("server/searchElement/")
				.request()
				.accept(MediaType.APPLICATION_JSON)				
				.async()
				.get(MyList.class);

		return FutureGetListString(list);

	}

	@Override
	public Future<List<String>> searchEntry(Map<String, String> set) {

		List<String> l = new LinkedList<String>();
		set.forEach((k, v) -> l.add(k+":"+v));

		Future<MyList> list = target.queryParam("query", l.toArray()).path("server/searchEntrys/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyList.class);

		return FutureGetListString(list);
	}

	@Override
	public Future<List<String>> orderEntrys(String field) {


		Future<MyList> list = target.queryParam("query", field).path("server/orderEntrys/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyList.class);

		return FutureGetListString(list);
	}

	@Override
	public Future<List<String>> searchGreaterThan(String field, int value) {

		Future<MyList> list = target.queryParam("field", field).queryParam("value", value).path("server/searchGreaterThan/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyList.class);

		return FutureGetListString(list);
	}

	@Override
	public Future<List<String>> searchLesserThan(String field, int value) {
		Future<MyList> list = target.queryParam("field", field).queryParam("value", value).path("server/searchLesserThan/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyList.class);

		return FutureGetListString(list);

	}

	@Override
	public Future<Boolean> valuegreaterThan(String key1, String field, String key2) {

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


	private Future<List<MyEntry>> FutureGetListMyEntry(Future<MyListEntry> list) {

		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<List<MyEntry>> future = executor.submit(new Callable<List<MyEntry>>() {
			public List<MyEntry> call() throws InterruptedException, ExecutionException {
				return list.get().getList();
			}});
		executor.shutdown();

		return future;
	}

	private Future<List<String>> FutureGetListString(Future<MyList> list) {

		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<List<String>> future = executor.submit(new Callable<List<String>>() {
			public List<String> call() throws InterruptedException, ExecutionException {
				return list.get().getList();
			}});
		executor.shutdown();

		return future;
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





}
