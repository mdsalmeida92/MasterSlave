import API.clientAPI;
import client.clientMasterSlave;

import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {

	public static void main(String[] args) {

		clientAPI client= new clientMasterSlave("http://localhost:8080/");
		Map<String,String> map = new HashMap<String,String>();
		map.put("field1", "Hello");
		map.put("field2", "World");
		map.put("field4", "25");

		Map<String,String> map2 = new HashMap<String,String>();
		map2.put("field1", "Hello");
		map2.put("field2", "World");
		map2.put("field4", "5");
		;


		addGetTest(client, map);
		addElementTest(client, map);
		removeTest(client, map);
		incrTest(client, map);
		sumTest(client, map, map2);
		sumConstTest(client, map);
		multTest(client, map, map2);
		searchElemTest(client, map, map2);
		searchEntriesTest(client, map, map2);

	}

	static void addGetTest(clientAPI client, Map<String,String> map){

		client.addSet("ola1005", map);
		Map<String,String> mapGET =  client.getSet("ola1005");
		assert map.equals(mapGET);
		client.removeSet("ola1005");


	}

	static void addElementTest(clientAPI client, Map<String,String> map){

		client.addSet("ola1005", map);
		client.addElement("ola1005", "newfield", "something");
		Map<String,String> newmap = map;
		newmap.put("newfield", "something");
		Map<String,String> mapGET =  client.getSet("ola1005");
		client.removeSet("ola1005");
		assert newmap.equals(mapGET);


	}

	static void removeTest(clientAPI client, Map<String,String> map){

		client.addSet("ola1005", map);
		client.removeSet("ola1005");
		Map<String,String> mapGET =  client.getSet("ola1005");
		assert mapGET.isEmpty();


	}

	static void incrTest(clientAPI client, Map<String,String> map){

		client.addSet("ola1005", map);
		client.incr("ola1005", "field4");
		Map<String,String> mapGET =  client.getSet("ola1005");
		client.removeSet("ola1005");
		assert Integer.valueOf(map.get("field4")) +1 ==   Integer.valueOf(mapGET.get("field4"));



	}

	static void sumTest(clientAPI client, Map<String,String> map, Map<String,String> map2){

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		int value = client.sum("ola1005", "field4", "ola1006");
		client.removeSet("ola1005");
		client.removeSet("ola1006");
		assert value == 30;



	}

	static void sumConstTest(clientAPI client, Map<String,String> map){

		client.addSet("ola1005", map);
		int value = client.multConst("ola1005", "field4", 6);
		client.removeSet("ola1005");
		assert value == 150;



	}
	
	static void multTest(clientAPI client, Map<String,String> map, Map<String,String> map2){

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		int value = client.mult("ola1005", "field4", "ola1006");
		client.removeSet("ola1005");
		client.removeSet("ola1006");
		assert value == 125;


	}
	
	static void searchElemTest(clientAPI client, Map<String,String> map, Map<String,String> map2){

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		List<String> list = client.searchElement("field2", "World");
		client.removeSet("ola1005");
		client.removeSet("ola1006");

		assert list.contains("ola1005");
		assert list.contains("ola1006");



	}
	
	static void searchEntriesTest(clientAPI client, Map<String,String> map, Map<String,String> map2){

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		Map<String,String> mapTemp = new HashMap<String,String>();
		mapTemp.put("field1", "Hello");
		mapTemp.put("field2", "World");
		List<String> list = client.searchEntry(mapTemp);
		//client.removeSet("ola1005");
		//client.removeSet("ola1006");


		assert list.contains("ola1005");
		assert list.contains("ola1006");


	}

}

