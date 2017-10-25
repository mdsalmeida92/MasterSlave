package client;

import java.net.URI;
import java.util.HashMap;
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

public class clientMasterSlave implements clientAPI{

	WebTarget target;

	public clientMasterSlave(String targelUrl) {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		URI baseURI = UriBuilder.fromUri(targelUrl).build();
		target = client.target(baseURI);
	}

	@Override
	public Map<String, Object> getSet(String key) {
		//target.path("getSet/"+key).request().async().get()
		System.out.println(target.getUri()+"server/getSet/"+key);
		MyEntry entry = target.path("server/getSet/"+key)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get(MyEntry.class);
		System.out.println(entry.getAttributes().get("somekey"));
		return entry.getAttributes();
	}
	

	@Override
	public String addSet(String key, Map<String, Object> set) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeSet(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String addElement(String key, Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void incr(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void incrBy(String key, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int sum(String key1, String field, String key2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int multConst(String key, String field, int constant) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int mult(String key1, String field, String key2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> searchElement(String field, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> searchEntry(Map<String, Object> set) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> orderEntrys(String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> searchGreaterThan(String field, int value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> searchLesserThan(String field, int value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean valuegreaterThan(String key1, String field, String key2) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
