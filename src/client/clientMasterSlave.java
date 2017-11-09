package client;

import java.net.URI;
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

import API.clientAPI;
import utils.Element;
import utils.MyBoolean;
import utils.MyEntry;
import utils.MyList;
import utils.MyListEntry;

public class clientMasterSlave implements clientAPI{

	WebTarget target;

	public clientMasterSlave(String targelUrl) {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		URI baseURI = UriBuilder.fromUri(targelUrl).build();
		target = client.target(baseURI);
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
			
			
			return future;
		}


	@Override
	public String addSet(String key, Map<String, String> set) throws InterruptedException, ExecutionException {

		MyEntry entry = new MyEntry(set);
		Response response = target.path("server/"+key)
				.request()
				.post( Entity.entity( entry, MediaType.APPLICATION_JSON));
		System.out.println("insert " + key + " " +response.getStatus());

		return null;
	}



	@Override
	public boolean removeSet(String key) throws InterruptedException, ExecutionException {

		Future<Response>  response = target.path("server/"+key)
				.request().async()
				.delete();
		System.out.println("delete " + key + " " +response.get().getStatus());
		return true;
	}

	@Override
	public String addElement(String key, String field, String element) {

		Element elem = new Element(field, element);
		Response response = target.path("server/"+key)
				.request()
				.put(Entity.entity( elem, MediaType.APPLICATION_JSON));

		return null;
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

	}

	@Override
	public Future<Integer> sum(String key1, String field, String key2) {

		Future<Integer> response = target.queryParam("key1", key1).queryParam("key2", key2).queryParam("field", field).path("server/sum/")
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
	public Future<List> searchElement(String field, String value) {
		Future<MyList> list = target.queryParam("field", field).queryParam("value", value).path("server/searchElement/")
				.request()
				.accept(MediaType.APPLICATION_JSON)				
				.async()
				.get(MyList.class);
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<List> future = executor.submit(new Callable<List>() {
	         public List call() throws InterruptedException, ExecutionException {
	             return list.get().getList();
	         }});
		
		
		return future;

	}

	@Override
	public Future<MyList> searchEntry(Map<String, String> set) {

		List<String> l = new LinkedList<String>();
		set.forEach((k, v) -> l.add(k+":"+v));

		Future<MyList> list = target.queryParam("query", l.toArray()).path("server/searchEntrys/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyList.class);
		return list;
	}

	@Override
	public Future<MyListEntry> orderEntrys(String field) {


		Future<MyListEntry> list = target.queryParam("query", field).path("server/orderEntrys/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyListEntry.class);
		return list;
	}

	@Override
	public Future<MyListEntry> searchGreaterThan(String field, int value) {

		Future<MyListEntry> list = target.queryParam("field", field).queryParam("value", value).path("server/searchGreaterThan/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyListEntry.class);
		return list;
	}

	@Override
	public Future<MyListEntry> searchLesserThan(String field, int value) {
		Future<MyListEntry> list = target.queryParam("field", field).queryParam("value", value).path("server/searchLesserThan/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyListEntry.class);
		return list;
	}

	@Override
	public Future<MyBoolean> valuegreaterThan(String key1, String field, String key2) {

		Future<MyBoolean> isGreater = target.queryParam("key1", key1).queryParam("field", field).queryParam("key2", key2).path("server/valuegreaterThan/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.async()
				.get(MyBoolean.class);
		return isGreater;
	}



}
