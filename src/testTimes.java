import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import API.clientAPI;
import client.clientMasterSlave;
import utils.MyEntry;


public class testTimes {
	private static final double TOTAL_OPERATIONS = 100;
	public static void main(String[] args) throws InterruptedException, ExecutionException {

		clientAPI client= new clientMasterSlave("http://localhost:8080/");
		Map<String,String> map = new HashMap<String,String>();
		map.put("field1", "Hello");
		map.put("field2", "World");
		map.put("field4", "1");

		Map<String,String> map2 = new HashMap<String,String>();
		map2.put("field1", "Hell");
		map2.put("field2", "Heaven");
		map2.put("field4", "5");
		;

		Map<String,String> map3 = new HashMap<String,String>();
		map3.put("field1", "bye");
		map3.put("field2", "water");
		map3.put("field4", "17");
		;
		long a = getTime();
		client.getServergetTime();
		System.out.println((getTime() - a) );


		addTest(client, map);
		addElementTest(client);
		getTest(client);
		removeTest(client);
		
		client.addSet("ola1005", map);
		client.addSet("ola1006", map2);
		client.addSet("ola1007", map3);
		
		incrTest(client);
		sumTest(client);
		sumConstTest(client);
		multTest(client);
		searchElemTest(client);
		searchEntriesTest(client);
		isGreaterThanTest(client);
		
		orderEntrysTest(client);
		searchGreaterThanTest(client);
		searchLesserThanTest(client);

		client.Close();
		System.err.println("acabou");


	}
	
	private static void isGreaterThanTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<Boolean> f = client.valuegreaterThan("ola1005", "field4", "ola1006");
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServervalueGreaterTime();

		System.out.println("Value order operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS+ " ms/  percentage: " +serverTime*100/elapsed +"%");
	}

	private static void searchLesserThanTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<List<String>> f = client.searchLesserThan("field4", 5);
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersearchLesserTime();

		System.out.println("Search Lesser operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS+ " ms/  percentage: " +serverTime*100/elapsed +"%");
	}
		
	

	private static void searchGreaterThanTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<List<String>> f = client.searchGreaterThan("field4", 5);
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersearchGreaterTime();

		System.out.println("Search Greater operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS+ " ms/  percentage: " +serverTime*100/elapsed +"%");
	}

	private static void orderEntrysTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<List<String>> f = client.orderEntrys("field4");
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerorderEntrysTime();

		System.out.println("order Entries operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS+ " ms/  percentage: " +serverTime*100/elapsed +"%");
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

		System.out.println("search Entries operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS+ " ms/  percentage: " +serverTime*100/elapsed +"%");
	}

	private static void searchElemTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<List<String>> f = client.searchElement("field2", "World");
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersearchElemTime();

		System.out.println("search Elem operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS+ " ms/  percentage: " +serverTime*100/elapsed +"%");
	}

	private static void multTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<Integer> f = client.mult("ola1005", "field4", "ola1006");
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServermultTime();

		System.out.println("mult operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS+ " ms/  percentage: " +serverTime*100/elapsed +"%");
	}

	private static void sumConstTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<Integer> f = client.multConst("ola1005", "field4", 6);
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersumConstTime();

		System.out.println("sumConst operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS+ " ms/  percentage: " +serverTime*100/elapsed +"%");
	}
		
	

	private static void sumTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<Integer> f = client.sum("ola1005", "field4", "ola1006");
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServersumTime();

		System.out.println("sum operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS+ " ms/  percentage: " +serverTime*100/elapsed +"%");
	}
	

	private static void incrTest(clientAPI client) {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			client.incr("ola1005", "field4");
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerincrTime();

		System.out.println("incr operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS + " ms/  percentage: " +serverTime*100/elapsed +"%"  );

	}

	private static void removeTest(clientAPI client) throws InterruptedException, ExecutionException {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			client.removeSet("ola1005"+n);
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerremoveTime();

		System.out.println("remove operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS + " ms/  percentage: " +serverTime*100/elapsed +"%"  );
	}

	private static void addElementTest(clientAPI client) {
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			client.addElement("ola1005"+n, "newfield", "something");
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerupdateTime();

		System.out.println("add element operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS + " ms/  percentage: " +serverTime*100/elapsed +"%"  );
	}


	static void addTest(clientAPI client, Map<String, String> map) throws InterruptedException, ExecutionException {
		
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			client.addSet("ola1005"+n, map);
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServerputTime();

		System.out.println("put operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS + " ms/  percentage: " +serverTime*100/elapsed +"%"  );
	}
	
	
	static void getTest(clientAPI client) throws InterruptedException, ExecutionException {
		
		long begin = getTime();
		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			Future<Map<String, String>> f = client.getSet("ola10050");
			f.get();
		}
		long elapsed = getTime() - begin ;
		long serverTime = client.getServergetTime();

		System.out.println("get operation: " + elapsed/TOTAL_OPERATIONS + " ms/  server time: " + serverTime/TOTAL_OPERATIONS+ " ms/  percentage: " +serverTime*100/elapsed +"%");
	}
	

	
	static long getTime() {
		return Calendar.getInstance().getTimeInMillis();
	}

}
