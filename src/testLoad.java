import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import API.clientAPI;
import client.clientMasterSlave;
import utils.Cipher;
import utils.SecurityType;

public class testLoad {

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
		for(int i = 0; i<1000 ; i++) {
			client.addSet(Integer.toString(i), map);
		}

		for(int i = 0; i<1000 ; i++) {
			client.getSet(Integer.toString(i));

		}

		client.Close();
		System.err.println("acabou");


	}

}
