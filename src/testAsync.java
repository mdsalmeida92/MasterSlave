import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
		for(int i =0; i<20;i++) {
			c[i] = new clientMasterSlave("http://localhost:8080/");
			c[i].getSet("ola1005");
		}

	}

}
