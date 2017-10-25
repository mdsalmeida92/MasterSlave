import API.clientAPI;
import client.clientMasterSlave;


public class test {

	public static void main(String[] args) {
		
		clientAPI client= new clientMasterSlave("http://localhost:8080/");
		client.getSet("ola");
		
	}

}
