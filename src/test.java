import API.clientAPI;
import client.clientMasterSlave;
import utils.Cipher;
import utils.MyBoolean;
import utils.MyEntry;
import utils.MyList;
import utils.MyListEntry;
import utils.SecurityType;

import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class test {

	public static void main(String[] args) throws InterruptedException, ExecutionException, ParseException {
		
		String port = "8443";
		String ip = "localhost";
		
		CommandLineParser parser = new DefaultParser();

		Options options = new Options();
		options.addOption("port", true, "server port");
		options.addOption("ip", true, "ip adress");
		options.addOption("N", false, "unencrypted data in kv store");
		options.addOption("E", false, "encrypted data in kv store");
		options.addOption("EE", false, "encrypted data with two layers in kv store");
		CommandLine line = parser.parse( options, args );

		if(line.hasOption("port")) {
			port= line.getOptionValue("port");

		}
		if(line.hasOption("ip")) {
			ip= line.getOptionValue("ip");
		}
		
	



		Map<String,Cipher> mapping = new HashMap<String,Cipher>();
		mapping.put("field1", Cipher.DET);
		mapping.put("field2", Cipher.DET);
		mapping.put("field3", Cipher.ADD);
		mapping.put("field4", Cipher.MULT);
		mapping.put("field5", Cipher.OPE);
		mapping.put("field6", Cipher.SEARCH);
		
		//NORMAL

			clientAPI client= new clientMasterSlave("https://"+ip+":"+port+"/");

		
		//ENCRYPTED
		if(line.hasOption("E")) {
			client= new clientMasterSlave("https://"+ip+":"+port+"/",SecurityType.ENCRYPTED, "" ,mapping);
		}
		
		
		//ENHACED ENCRYPTED
		if(line.hasOption("EE")) {
			client= new clientMasterSlave("https://"+ip+":"+port+"/", SecurityType.ENHANCED_ENCRYPTED, "" ,mapping);
		}
		
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("field1", "Hello");
		map.put("field2", "World");
		map.put("field3", "2");
		map.put("field4", "5");
		map.put("field5", "10");
		map.put("field6", "Saw yet kindness too replying whatever marianne.");

		Map<String,String> map2 = new HashMap<String,String>();
		map2.put("field1", "Hell");
		map2.put("field2", "Heaven");
		map2.put("field3", "3");
		map2.put("field4", "6");
		map2.put("field5", "11");
		map2.put("field6", "Saw yet kindness 2 replying whatever marianne.");

		Map<String,String> map3 = new HashMap<String,String>();
		map3.put("field1", "bye");
		map3.put("field2", "water");
		map3.put("field3", "4");
		map3.put("field4", "7");
		map3.put("field5", "12");
		map3.put("field6", "Saw yet kindness TOO FAR replying whatever marianne.");


		addGetTest(client, map);
		addElementTest(client, map);
		getElementTest(client, map);
		removeTest(client, map);
		elementContainsSentence(client, map);
		searchEntryContainingSentence(client, map, map2, map3);
		incrTest(client, map);
		sumTest(client, map, map2);
		sumAll(client, map, map2, map3);
		sumConstTest(client, map);
		multTest(client, map, map2);
		multAll(client, map, map2, map3);
		searchElemTest(client, map, map2);
		searchEntriesTest(client, map, map2);
		orderEntrysTest(client, map, map2, map3);
		searchGreaterThanTest(client, map, map2, map3);
		searchLesserThanTest(client, map, map2, map3);
		isGreaterThanTest(client, map, map2);


		client.Close();
		System.err.println("acabou");


	}

	private static void multAll(clientAPI client, Map<String, String> map, Map<String, String> map2,
			Map<String, String> map3) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		client.addSet("ola1007", map3);
		Future<Integer> result = client.multAll("field4");
		long mult = result.get();


		assert mult == 210;




		client.removeSet("ola1005");
		client.removeSet("ola1006");
		client.removeSet("ola1007");

	}

	private static void sumAll(clientAPI client, Map<String, String> map, Map<String, String> map2,
			Map<String, String> map3)throws InterruptedException, ExecutionException {

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		client.addSet("ola1007", map3);
		Future<Integer> result = client.sumAll("field3");
		long sum = result.get();


		assert sum == 9;




		client.removeSet("ola1005");
		client.removeSet("ola1006");
		client.removeSet("ola1007");

	}

	private static void searchEntryContainingSentence(clientAPI client, Map<String, String> map, Map<String, String> map2, Map<String, String> map3) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		client.addSet("ola1007", map3);
		Future<List<String>> result = client.searchEntryContainingSentence("field6", "Saw yet kindness too");
		List<String> list = result.get();

		list.forEach((s) ->{
			assert s.equals("ola1005");
		} );



		client.removeSet("ola1005");
		client.removeSet("ola1006");
		client.removeSet("ola1007");

	}

	private static void elementContainsSentence(clientAPI client, Map<String, String> map)throws InterruptedException, ExecutionException {
		client.addSet("ola1005", map);
		Future<Boolean> result = client.elementContainsSentence("ola1005", "field6", "Saw yet kindness too");
		boolean elem = result.get();

		assert elem;


		client.removeSet("ola1005");

	}

	private static void getElementTest(clientAPI client, Map<String, String> map)throws InterruptedException, ExecutionException {

		client.addSet("ola1005", map);
		Future<String> result = client.getElement("ola1005", "field1");
		String elem = result.get();

		assert elem.equals("Hello");


		client.removeSet("ola1005");

	}

	static void addGetTest(clientAPI client, Map<String,String> map) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);

		Future<Map<String,String>>  myEntry =  client.getSet("ola1005");
		Map<String,String> mapGET = myEntry.get();

		assert map.equals(mapGET);
		client.removeSet("ola1005");


	}

	static void addElementTest(clientAPI client, Map<String,String> map) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.addElement("ola1005", "newfield", "something");
		Map<String,String> newmap = map;
		newmap.put("newfield", "something");
		Future<Map<String,String>>  myEntry =  client.getSet("ola1005");
		Map<String,String> mapGET = myEntry.get();
		client.removeSet("ola1005");
		assert newmap.equals(mapGET);


	}

	static void removeTest(clientAPI client, Map<String,String> map) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.removeSet("ola1005");
		Future<Map<String,String>>  myEntry =  client.getSet("ola1005");
		Map<String,String> mapGET = myEntry.get();
		assert mapGET.isEmpty();


	}

	static void incrTest(clientAPI client, Map<String,String> map) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.incr("ola1005", "field3");
		Future<Map<String,String>>  myEntry =  client.getSet("ola1005");
		Map<String,String> mapGET = myEntry.get();
		client.removeSet("ola1005");
		assert Integer.valueOf(map.get("field3")) +1 ==   Integer.valueOf(mapGET.get("field3"));



	}

	static void sumTest(clientAPI client, Map<String,String> map, Map<String,String> map2) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		Future<Integer> value = client.sum("ola1005", "field3", "ola1006");

		long v = value.get();
		assert v == 5;
		client.removeSet("ola1005");
		client.removeSet("ola1006");



	}

	static void sumConstTest(clientAPI client, Map<String,String> map) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		Future<Integer> value = client.multConst("ola1005", "field3", 6);

		assert value.get() == 12;
		client.removeSet("ola1005");



	}

	static void multTest(clientAPI client, Map<String,String> map, Map<String,String> map2) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		Future<Integer> value = client.mult("ola1005", "field4", "ola1006");

		assert value.get() == 30;
		client.removeSet("ola1005");
		client.removeSet("ola1006");


	}

	static void searchElemTest(clientAPI client, Map<String,String> map, Map<String,String> map2) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		Future<List<String>> list = client.searchElement("field2", "World");

		assert list.get().contains("ola1005");
		client.removeSet("ola1005");
		client.removeSet("ola1006");




	}

	static void searchEntriesTest(clientAPI client, Map<String,String> map, Map<String,String> map2) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		Map<String,String> mapTemp = new HashMap<String,String>();
		mapTemp.put("field1", "Hello");
		mapTemp.put("field2", "World");
		Future<List<String>> list = client.searchEntry(mapTemp);

		assert list.get().contains("ola1005");
		client.removeSet("ola1005");
		client.removeSet("ola1006");


	}

	static void orderEntrysTest(clientAPI client, Map<String,String> map, Map<String,String> map2, Map<String,String> map3) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		client.addSet("ola1007", map3);


		Future<List<String>> list = client.orderEntrys("field5");
		List<String> keys = new LinkedList<>();
		keys.add("ola1005");
		keys.add("ola1006");
		keys.add("ola1007");

		List<String> l = list.get();

		Iterator<String> it = keys.iterator();
		for (Iterator<String> iterator = l.iterator()  ; iterator.hasNext() && it.hasNext();) {
			String myEntry = iterator.next();
			String string = it.next();
			assert string.equals(myEntry);
		}
		client.removeSet("ola1005");
		client.removeSet("ola1006");
		client.removeSet("ola1007");
	}

	static void searchGreaterThanTest(clientAPI client, Map<String,String> map, Map<String,String> map2, Map<String,String> map3) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		client.addSet("ola1007", map3);


		Future<List<String>> list = client.searchGreaterThan("field5", 11);


		List<String> l = list.get();
		for (Iterator<String> iterator = l.iterator()  ; iterator.hasNext();) {
			String myEntry = iterator.next();
			assert "ola1007".equals(myEntry);
		}
		client.removeSet("ola1005");
		client.removeSet("ola1006");
		client.removeSet("ola1007");

	}

	static void searchLesserThanTest(clientAPI client, Map<String,String> map, Map<String,String> map2, Map<String,String> map3) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		client.addSet("ola1007", map3);


		Future<List<String>> list = client.searchLesserThan("field5", 11);



		List<String> l = list.get();

		for (Iterator<String> iterator = l.iterator()  ; iterator.hasNext();) {
			String myEntry = iterator.next();
			assert "ola1005".equals(myEntry);
		}
		client.removeSet("ola1005");
		client.removeSet("ola1006");
		client.removeSet("ola1007");
	}

	static void isGreaterThanTest(clientAPI client, Map<String,String> map, Map<String,String> map2) throws InterruptedException, ExecutionException{

		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		Future<Boolean> isGreaterThan = client.valuegreaterThan("ola1005", "field5", "ola1006");

		assert !(isGreaterThan.get());
		client.removeSet("ola1005");
		client.removeSet("ola1006");

	}

}

