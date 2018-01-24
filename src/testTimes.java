import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import API.clientAPI;
import client.clientMasterSlave;
import utils.Cipher;
import utils.MyEntry;
import utils.SecurityType;


public class testTimes {
	private static double TOTAL_OPERATIONS = 100;
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
		options.addOption("n", true, "number of operations");
		
		CommandLine line = parser.parse( options, args );

		if(line.hasOption("port")) {
			port= line.getOptionValue("port");

		}
		if(line.hasOption("ip")) {
			ip= line.getOptionValue("ip");
		}
		
		if(line.hasOption("n")) {
			TOTAL_OPERATIONS= Double.parseDouble(line.getOptionValue("n"));
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
			System.out.println("encryted");
			client= new clientMasterSlave("https://"+ip+":"+port+"/",SecurityType.ENCRYPTED, "" ,mapping);
		}


		//ENHACED ENCRYPTED
		if(line.hasOption("EE")) {
			System.out.println("enhanced encryted");
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

		;
		long a = getTime();
		client.getServergetTime();
		System.out.println((getTime() - a) );

		System.out.printf("%35s %20s %20s %20s %20s %20s", "Operation", "Op/S", "Server Time", "Server %", "Privacy Time", "Privacy %");
		System.out.println();

		
		client.resetServerTimes();
		client.initTimes();
	

//		addTest(client, map);
//		addElementTest(client);
//		getElementTest(client);
//		getTest(client);
//
//
//		client.addSet("ola1005", map);
//		client.addSet("ola1006", map2);
//		client.addSet("ola1007", map3);
//
//		elementContainsSentence(client);
//		searchEntryContainingSentence(client);
//
//		incrTest(client);
//		sumTest(client);
//		sumAll(client);
//		sumConstTest(client);
//
//		multTest(client);
//		multAll(client);
//
//		searchElemTest(client);
//		searchEntriesTest(client);
//		isGreaterThanTest(client);
//
//		orderEntrysTest(client);
//		searchGreaterThanTest(client);
//		searchLesserThanTest(client);
//
//		removeTest(client);
//		
//		
//		client.removeSet("ola1005");
//		client.removeSet("ola1006");
//		client.removeSet("ola1007");
//		
		

		Map<String,String> map4 = new HashMap<String,String>();
		map.put("field1", "Hello");
		map.put("field2", "World");
		map.put("field4", "5");
		map.put("field5", "10");
		map.put("field6", "Saw yet kindness too replying whatever marianne.");
		
		client.resetServerTimes();
		client.initTimes();
	
		addTestNotAdd(client, map4);
		getTestNotAdd(client);



		client.resetServerTimes();
		client.Close();
		System.err.println("acabou");


	}


	private static void multAll(clientAPI client)throws InterruptedException, ExecutionException {

		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<BigInteger> result = client.multAll("field4");
			result.get();

		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServermultAllTime();
		long privacyTime = client.getPrivacymultAllTime();
		print("multAll", elapsed, privacyTime, serverTime);


	}

	private static void sumAll(clientAPI client)throws InterruptedException, ExecutionException {

		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<BigInteger> result = client.sumAll("field3");
			result.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersumAllTime();
		long privacyTime = client.getPrivacysumAllTime();
		print("sumAll", elapsed, privacyTime, serverTime);


	}


	private static void searchEntryContainingSentence(clientAPI client)throws InterruptedException, ExecutionException {

		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<List<String>> result = client.searchEntryContainingSentence("field6", "Saw yet kindness too");
			result.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersearchEntryContainingSentenceTime();
		long privacyTime = client.getPrivacysearchEntryContainingSentenceTime();
		print("searchEntryContainingSentence", elapsed, privacyTime, serverTime);


	}


	private static void elementContainsSentence(clientAPI client)throws InterruptedException, ExecutionException {

		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<Boolean> result = client.elementContainsSentence("ola1005", "field6", "Saw yet kindness too");
			result.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerElementContainsSenteceTime();
		long privacyTime = client.getPrivacyElementContainsSenteceTime();
		print("elementcontainssentence", elapsed, privacyTime, serverTime);


	}

	private static void getElementTest(clientAPI client)throws InterruptedException, ExecutionException {

		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<String> result = client.getElement("ola1005"+n, "field1");
			result.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServergetElementTime();
		long privacyTime = client.getPrivacyGetElementTime();
		print("get_element", elapsed, privacyTime, serverTime);


	}

	private static void isGreaterThanTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<Boolean> f = client.valuegreaterThan("ola1005", "field5", "ola1006");
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServervalueGreaterTime();
		long privacyTime = client.getPrivacyvalueGreaterTime();
		print("Value_order", elapsed, privacyTime, serverTime);

	}

	private static void searchLesserThanTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<List<String>> f = client.searchLesserThan("field5", 5);
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersearchLesserTime();
		long privacyTime = client.getPrivacysearchLesserTime();
		print("Search_Lesser", elapsed, privacyTime, serverTime);

	}



	private static void searchGreaterThanTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<List<String>> f = client.searchGreaterThan("field5", 5);
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersearchGreaterTime();
		long privacyTime = client.getPrivacysearchGreaterTime();
		print("Search_Greater", elapsed, privacyTime, serverTime);

	}

	private static void orderEntrysTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<List<String>> f = client.orderEntrys("field5");
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerorderEntrysTime();
		long privacyTime = client.getPrivacyorderEntrysTime();
		print("order_Entries", elapsed, privacyTime, serverTime);

	}

	private static void searchEntriesTest(clientAPI client) throws InterruptedException, ExecutionException {

		Map<String,String> mapTemp = new HashMap<String,String>();
		mapTemp.put("field1", "Hello");
		mapTemp.put("field2", "World");

		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<List<String>> f = client.searchEntry(mapTemp);
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersearchEntrysTime();
		long privacyTime = client.getPrivacysearchEntrysTime();
		print("search_Entries", elapsed, privacyTime, serverTime);

	}

	private static void searchElemTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<List<String>> f = client.searchElement("field2", "World");
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersearchElemTime();
		long privacyTime = client.getPrivacysearchElemTime();
		print("search_Elem", elapsed, privacyTime, serverTime);

	}

	private static void multTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<BigInteger> f = client.mult("ola1005", "field4", "ola1006");
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServermultTime();
		long privacyTime = client.getPrivacymultTime();
		print("mult", elapsed, privacyTime, serverTime);

	}

	private static void sumConstTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<BigInteger> f = client.multConst("ola1005", "field3", 6);
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersumConstTime();
		long privacyTime = client.getPrivacysumConstTime();
		print("sumConst", elapsed, privacyTime, serverTime);

	}



	private static void sumTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<BigInteger> f = client.sum("ola1005", "field3", "ola1006");
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersumTime();
		long privacyTime = client.getPrivacysumTime();
		print("sum", elapsed, privacyTime, serverTime);

	}


	private static void incrTest(clientAPI client) {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			client.incr("ola1005", "field3");
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerincrTime();
		long privacyTime = client.getPrivacyincrTime();
		print("incr", elapsed, privacyTime, serverTime);


	}

	private static void removeTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			client.removeSet("ola1005"+n);
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerremoveTime();
		long privacyTime = client.getPrivacyremoveTime();
		print("remove", elapsed, privacyTime, serverTime);
		}

	private static void addElementTest(clientAPI client) {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			client.addElement("ola1005"+n, "newfield", "something");
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerupdateTime();		
		long privacyTime = client.getPrivacyupdateTime();
		print("add_element", elapsed, privacyTime, serverTime);

			}


	static void addTest(clientAPI client, Map<String, String> map) throws InterruptedException, ExecutionException {

		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			client.addSet("ola1005"+n, map);
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerputTime();
		long privacyTime = client.getPrivacyputTime();

		print("put", elapsed, privacyTime, serverTime);
	}


	static void getTest(clientAPI client) throws InterruptedException, ExecutionException {

		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<Map<String, String>> f = client.getSet("ola1005"+n);
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServergetTime();
		long privacyTime = client.getPrivacygetTime();
		
		print("get", elapsed, privacyTime, serverTime);



	}

	
	static void print(String operation, long elapsed, long privacyTime, long serverTime) {
		
		System.out.format("%35s %20f %20f %20d %20f %20d", 
				operation, ((1000 * TOTAL_OPERATIONS) / elapsed), (serverTime/TOTAL_OPERATIONS), (serverTime*100/elapsed), (privacyTime/TOTAL_OPERATIONS), (privacyTime*100/elapsed));
		System.out.println();

		
	}
	
	static void addTestNotAdd(clientAPI client, Map<String, String> map) throws InterruptedException, ExecutionException {

		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			client.addSet("ola1005"+n, map);
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerputTime();
		long privacyTime = client.getPrivacyputTime();

		print("put not add", elapsed, privacyTime, serverTime);
	}


	static void getTestNotAdd(clientAPI client) throws InterruptedException, ExecutionException {

		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<Map<String, String>> f = client.getSet("ola1005"+n);
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServergetTime();
		long privacyTime = client.getPrivacygetTime();
		
		print("get not add", elapsed, privacyTime, serverTime);



	}


	static long getTime() {
		return Calendar.getInstance().getTimeInMillis();
	}

}
