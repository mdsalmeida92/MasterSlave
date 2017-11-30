import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import API.clientAPI;
import client.clientMasterSlave;

public class testLoad {

	public static void main(String[] args) throws InterruptedException, ExecutionException {


		clientAPI client= new clientMasterSlave("http://localhost:8080/");
		Map<String,String> map = new HashMap<String,String>();
		map.put("field1", "Hello");
		map.put("field2", "World");
		map.put("field3", "2");
		map.put("field4", "5");
		map.put("field5", "10");
		map.put("field6", "Saw yet kindness too replying whatever marianne.");
		for(int i = 0; i<1000 ; i++) {
			client.addSet(Integer.toString(i), map);
		}

//		for(int i = 0; i<1000 ; i++) {
//			client.getSet(Integer.toString(i));
//
//		}

		client.Close();
		System.err.println("acabou");


	}

}
