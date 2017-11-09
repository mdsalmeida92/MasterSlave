import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import API.clientAPI;
import client.clientMasterSlave;

public class testAsync {
	public static void main(String[] args) throws InterruptedException, ExecutionException {

		
		Map<String,String> map = new HashMap<String,String>();
		map.put("field1", "Hello");
		map.put("field2", "World");
		map.put("field4", "1");
		
		clientMasterSlave ed = new clientMasterSlave("http://localhost:8080/");
		ed.addSet("ola1005", map);
		clientAPI[] c = new clientAPI[20];
		List <Future<Map<String,String>>> f = new ArrayList<Future<Map<String,String>>>();
		for(int i =0; i<20;i++) {
			System.out.println(i);
			f.add(ed.getSet("ola1005"));
			System.out.println(i + "depois");
		}
		for(int i =0; i<20;i++) {
			System.out.println(i);
			Map<String,String> mapGET = f.get(i).get();
			System.out.println(i + "depois");
		}
		
		System.out.println("acabou");

	}

}
