package client;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	public Map<String, String> getSet(String key) {

		//target.path("getSet/"+key).request().async().get()

		MyEntry entry = target.path("server/"+key)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get(MyEntry.class);
		return entry.getAttributes();
	}


	@Override
	public String addSet(String key, Map<String, String> set) {

		MyEntry entry = new MyEntry(set);
		Response response = target.path("server/"+key)
				.request()
				.post( Entity.entity( entry, MediaType.APPLICATION_JSON));
		return null;
	}

	@Override
	public boolean removeSet(String key) {

		Response response = target.path("server/"+key)
				.request()
				.delete();
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
	public int sum(String key1, String field, String key2) {

		int response = target.queryParam("key1", key1).queryParam("key2", key2).queryParam("field", field).path("server/sum/")
				.request()
				.get(Integer.class);
		return response;
	}

	@Override
	public int multConst(String key, String field, int constant) {

		int response = target.queryParam("key", key).queryParam("const", constant).queryParam("field", field).path("server/multConst/")
				.request()
				.get(Integer.class);
		return response;
	}

	@Override
	public int mult(String key1, String field, String key2) {

		int response = target.queryParam("key1", key1).queryParam("key2", key2).queryParam("field", field).path("server/mult/")
				.request()
				.get(Integer.class);
		return response;
	}

	@Override
	public List<String> searchElement(String field, String value) {
		MyList list = target.queryParam("field", field).queryParam("value", value).path("server/searchElement/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get(MyList.class);
		return list.getList();
	}

	@Override
	public List<String> searchEntry(Map<String, String> set) {

		List<String> l = new LinkedList<String>();
		set.forEach((k, v) -> l.add(k+":"+v));

		MyList list = target.queryParam("query", l.toArray()).path("server/searchEntrys/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get(MyList.class);
		return list.getList();
	}

	@Override
	public List<MyEntry> orderEntrys(String field) {


		MyListEntry list = target.queryParam("query", field).path("server/orderEntrys/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get(MyListEntry.class);
		return list.getList();
	}

	@Override
	public List<MyEntry> searchGreaterThan(String field, int value) {

		MyListEntry list = target.queryParam("field", field).queryParam("value", value).path("server/searchGreaterThan/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get(MyListEntry.class);
		return list.getList();
	}

	@Override
	public List<MyEntry> searchLesserThan(String field, int value) {
		MyListEntry list = target.queryParam("field", field).queryParam("value", value).path("server/searchLesserThan/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get(MyListEntry.class);
		return list.getList();
	}

	@Override
	public boolean valuegreaterThan(String key1, String field, String key2) {
		
		MyBoolean isGreater = target.queryParam("key1", key1).queryParam("field", field).queryParam("key2", key2).path("server/valuegreaterThan/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get(MyBoolean.class);
		return isGreater.isMyboolean();
	}



}
