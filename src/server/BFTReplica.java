package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultRecoverable;
import utils.Element;
import utils.MyBoolean;
import utils.MyEntry;
import utils.MyList;
import utils.RequestType;

public class BFTReplica extends DefaultRecoverable{

	public ServerLogic serverLogic;
	public boolean byzantine;

	public BFTReplica(int port, int id, String configpath, boolean byzantine){

		serverLogic = new ServerLogic(port);
		this.byzantine = byzantine;

		new ServiceReplica(id,configpath, this,this,null,null);

	}

	public static void main(String[] args) {
		CommandLineParser parser = new DefaultParser();

		Options options = new Options();
		options.addOption("port", true, "redis port");
		options.addOption("id", true, "replica id");
		options.addOption("path", true, "configpath");
		options.addOption("byzantine", false, "byzantine");
		String path = "/home/mario/eclipse-workspace/MasterSlaveRedis/config/";
		String port = "6379";
		String id = "0";
		boolean byzantine = false;
		CommandLine line;
		try {
			line = parser.parse( options, args );

			if(line.hasOption("byzantine")) {
				byzantine = true;

			}
			if(line.hasOption("port")) {
				port = line.getOptionValue("port");

			}
			if(line.hasOption("id")) {
				id = line.getOptionValue("id");
			}
			if(line.hasOption("path")) {
				path = line.getOptionValue("path");
			}
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "BFT replica", options );
		}

		System.err.println(port);
		System.err.println(id);
		System.err.println(path);

		new BFTReplica(Integer.parseInt(port), Integer.parseInt(id),path,byzantine);
	}



	private byte[] executeSingle(byte[] command, MessageContext msgCtx) {

		byte[] resultBytes = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(command);
			ObjectInput in = new ObjectInputStream(bis);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos); 

			int reqType = in.readInt();
			switch (reqType) {

			case RequestType.PUTSET:
				putEntry(in);
				break;

			case RequestType.REMOVESET:
				removeEntry(in);
				break;

			case RequestType.ADDELEMENT:
				updateEntry(in);
				break;

			case RequestType.INCR:
				incr(in);
				break;

			case RequestType.GETSET:
				getEntry(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.GETELEMENT:
				getElement(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.ELEMENT_CONTAINS_SEQUENCE:
				elementContainsSentence(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SEARCHENTRYCONTAININGSENTENCE:
				searchEntryContainingWord(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SUM:
				sum(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SUMALL:
				sumAll(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.MULTCONST:
				sumConst(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.MULT:
				mult(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.MULTALL:
				multAll(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SEARCHELEMENT:
				searchElement(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SEARCHENTRY:
				searchEntrys(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.ORDERENTRYS:
				orderEntrys(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SEARCHGREATERTHAN:
				searchGreaterThan(in, out);
				resultBytes = bos.toByteArray();
				break;


			case RequestType.SEARCHLESSERTHAN:
				searchLesserThan(in, out);
				resultBytes = bos.toByteArray();
				break;


			case RequestType.VALUEGREATERTHAN:
				valuegreaterThan(in, out);
				resultBytes = bos.toByteArray();
				break;


			default:
				System.out.println("executeSingle Unknown request type: " + reqType);
				break;
			}
		} catch (IOException e) {
			System.out.println("Exception reading data in the replica: " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(byzantine) {
			if(Math.random()<0.50)
				return null;
		}
		return resultBytes;
	}


	public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {

		byte[] resultBytes = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(command);
			ObjectInput in = new ObjectInputStream(bis);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos); 
			int reqType = in.readInt();

			switch (reqType) {
			case RequestType.GETSET:
				getEntry(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.GETELEMENT:
				getElement(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.ELEMENT_CONTAINS_SEQUENCE:
				elementContainsSentence(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SEARCHENTRYCONTAININGSENTENCE:
				searchEntryContainingWord(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SUM:
				sum(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SUMALL:
				sumAll(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.MULTCONST:
				sumConst(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.MULT:
				mult(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.MULTALL:
				multAll(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SEARCHELEMENT:
				searchElement(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SEARCHENTRY:
				searchEntrys(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.ORDERENTRYS:
				orderEntrys(in, out);
				resultBytes = bos.toByteArray();
				break;

			case RequestType.SEARCHGREATERTHAN:
				searchGreaterThan(in, out);
				resultBytes = bos.toByteArray();
				break;


			case RequestType.SEARCHLESSERTHAN:
				searchLesserThan(in, out);
				resultBytes = bos.toByteArray();
				break;


			case RequestType.VALUEGREATERTHAN:
				valuegreaterThan(in, out);
				resultBytes = bos.toByteArray();
				break;



			default:
				System.out.println("appExecuteUnordered Unknown request type: " + reqType);
				break;
			}

		} catch (IOException e) {
			System.out.println("Exception reading data in the replica: " + e.getMessage());
			e.printStackTrace();

		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		if(byzantine) {
			if(Math.random()<0.50)
				return null;
		}
		return resultBytes;
	}


	public void installSnapshot(byte[] state) {

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(state);
			ObjectInput in = new ObjectInputStream(bis);
			@SuppressWarnings("unchecked")
			Map<String,Map<String,String>> map = (Map<String,Map<String,String>>) in.readObject();
			serverLogic.flushAll();
			for(String key: map.keySet()) {
				serverLogic.putEntry(key, new MyEntry(map.get(key)));
			}

			in.close();
			bis.close();
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			System.out.print("Exception installing the application state: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public byte[] getSnapshot() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);

			Map<String, Map<String,String>> map = serverLogic.getstore();
			out.writeObject(map);

			out.flush();
			out.close();
			bos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			System.out.println("Exception when trying to take a + " +
					"snapshot of the application state" + e.getMessage());
			e.printStackTrace();
			return new byte[0];
		}
	}

	@Override
	public byte[][] appExecuteBatch(byte[][] command, MessageContext[] mcs, boolean arg2) {


		byte[][] replies = new byte[command.length][];
		for (int i = 0; i < command.length; i++) {
			replies[i] = executeSingle(command[i], mcs[i]);
		}

		return replies;
	}

	private void getEntry(ObjectInput in, ObjectOutput out) throws InterruptedException {

		try {
			MyEntry entry = null;
			int reqType = in.readInt();
			String key = in.readUTF();
			switch (reqType) {
			case RequestType.NORMAL:
				entry = serverLogic.getEntry(key);
				break;
			case RequestType.ONIONENC:
				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				entry = serverLogic.getEntryEncEnc(key, iv, RandomKey);
				break;

			default:
				break;
			}
			out.writeObject(entry);

		} catch (IOException e) {

			e.printStackTrace();
		}
	}


	private void putEntry(ObjectInput in) throws InterruptedException { 
		try{
			String key = in.readUTF();
			MyEntry entry = (MyEntry) in.readObject();
			serverLogic.putEntry(key, entry);
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}

	}


	private void removeEntry(ObjectInput in) {
		try {
			String key = in.readUTF();
			serverLogic.removeEntry(key);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}


	private void updateEntry(ObjectInput in) {
		try {
			String key = in.readUTF();
			Element element = (Element) in.readObject();
			serverLogic.updateEntry(key, element);
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}


	private void getElement(ObjectInput in, ObjectOutput out) throws InterruptedException {

		try {
			String key = in.readUTF();
			String field = in.readUTF();
			String elem = serverLogic.getElement(key, field);
			out.writeUTF(elem);
			out.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}


	private void elementContainsSentence(ObjectInput in, ObjectOutput out) throws InterruptedException {

		try {
			MyBoolean myboolean = null;
			int reqType = in.readInt();
			String key = in.readUTF();
			String field = in.readUTF();
			String sentence = in.readUTF();
			switch (reqType) {
			case RequestType.NORMAL:
				myboolean = serverLogic.elementContainsSentence(key, field, sentence);
				break;
			case RequestType.ENC:
				myboolean = serverLogic.elementContainsSentenceEnc(key, field, sentence);
				break;
			case RequestType.ONIONENC:
				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				myboolean = serverLogic.elementContainsSentenceEnEnc(key, iv, RandomKey, field, sentence);
				break;

			default:
				break;
			}
			out.writeObject(myboolean);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}




	private void searchEntryContainingWord(ObjectInput in, ObjectOutput out) throws InterruptedException {

		try {
			MyList myList = null;
			int reqType = in.readInt();
			String field = in.readUTF();
			String sentence = in.readUTF();
			switch (reqType) {
			case RequestType.NORMAL:
				myList = serverLogic.searchEntryContainingWord( field, sentence);
				break;
			case RequestType.ENC:
				myList = serverLogic.searchEntryContainingWordEnc(field, sentence);
				break;
			case RequestType.ONIONENC:
				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				myList = serverLogic.searchEntryContainingWordEnEnc(iv, RandomKey, field, sentence);
				break;

			default:
				break;
			}
			out.writeObject(myList);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}



	private void incr(ObjectInput in) {
		try {
			int reqType = in.readInt();
			String key = in.readUTF();
			Element value = (Element) in.readObject();
			switch (reqType) {
			case RequestType.NORMAL:
				serverLogic.incr(key, value);
				break;
			case RequestType.ENC:
				serverLogic.incrEnc(key, value);
				break;
			case RequestType.ONIONENC:
				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				serverLogic.incrEnEnc(key, iv, RandomKey, value);
				break;

			default:
				break;
			}
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}


	private void sum(ObjectInput in, ObjectOutput out) {

		try {
			String sum = null;
			int reqType = in.readInt();
			String key1 = in.readUTF();
			String field = in.readUTF();
			String key2 = in.readUTF();
			String nsquare = null;
			switch (reqType) {
			case RequestType.NORMAL:
				BigInteger sumBig = serverLogic.sum(key1, field, key2);
				out.writeObject(sumBig);
				break;
			case RequestType.ENC:
				nsquare = in.readUTF();
				sum = serverLogic.sumEnc(key1, field, nsquare, key2);
				out.writeUTF(sum);
				break;
			case RequestType.ONIONENC:
				nsquare = in.readUTF();
				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				sum = serverLogic.sumEnEnc(key1, iv, RandomKey, field, nsquare, key2);
				out.writeUTF(sum);
				break;

			default:
				break;
			}
			out.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}


	}


	private void sumAll(ObjectInput in, ObjectOutput out) {

		try {
			String sum = null;
			int reqType = in.readInt();
			String field = in.readUTF();
			String nsquare = null;;
			switch (reqType) {
			case RequestType.NORMAL:
				BigInteger sumBig = serverLogic.sumAll(field);
				out.writeObject(sumBig);
				break;
			case RequestType.ENC:
				nsquare = in.readUTF();
				sum = serverLogic.sumAllEnc(field, nsquare);
				out.writeUTF(sum);
				break;
			case RequestType.ONIONENC:
				nsquare = in.readUTF();
				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				sum = serverLogic.sumAllEnEnc(field, iv, RandomKey, nsquare);
				out.writeUTF(sum);
				break;

			default:
				break;
			}
			out.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void sumConst(ObjectInput in, ObjectOutput out) {
		try {
			String sum = null;
			int reqType = in.readInt();
			String key = in.readUTF();
			String field = in.readUTF();
			int constant = in.readInt();
			String nsquare =null;
			switch (reqType) {
			case RequestType.NORMAL:
				BigInteger sumBig = serverLogic.sumConst(key, field, constant);
				out.writeObject(sumBig);
				break;
			case RequestType.ENC:
				nsquare = in.readUTF();
				sum = serverLogic.sumConstEnc(key, nsquare, field, constant);
				out.writeUTF(sum);
				break;
			case RequestType.ONIONENC:
				nsquare = in.readUTF();
				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				sum = serverLogic.sumConst(key, nsquare, iv, RandomKey, field, constant);
				out.writeUTF(sum);
				break;

			default:
				break;
			}
			out.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void mult(ObjectInput in, ObjectOutput out) {

		try {
			String mult = null;
			int reqType = in.readInt();
			String key1 = in.readUTF();
			String field = in.readUTF();
			String key2 = in.readUTF();
			String publicKey = null;
			switch (reqType) {
			case RequestType.NORMAL:
				long sumBig = serverLogic.mult(key1, field, key2);
				out.writeLong(sumBig);
				out.flush();
				break;
			case RequestType.ENC:
				publicKey = in.readUTF();
				mult = serverLogic.multEnc(key1, field, publicKey, key2);
				out.writeUTF(mult);
				break;
			case RequestType.ONIONENC:
				publicKey = in.readUTF();
				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				mult = serverLogic.multEnEnc(key1, field, iv, RandomKey, publicKey, key2);
				out.writeUTF(mult);
				break;

			default:
				break;
			}
			out.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}


	}

	private void multAll(ObjectInput in, ObjectOutput out) {
		try {
			String sum = null;
			int reqType = in.readInt();
			String field = in.readUTF();
			String publicKey = null;;
			switch (reqType) {
			case RequestType.NORMAL:
				long sumBig = serverLogic.multAll(field);
				out.writeLong(sumBig);
				out.flush();
				break;
			case RequestType.ENC:
				publicKey = in.readUTF();
				sum = serverLogic.multAllEnc(field, publicKey);
				out.writeUTF(sum);
				break;
			case RequestType.ONIONENC:
				publicKey = in.readUTF();
				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				sum = serverLogic.multAllEnEnc(field, iv, RandomKey, publicKey);
				out.writeUTF(sum);
				break;

			default:
				break;
			}
			out.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void searchElement( ObjectInput in, ObjectOutput out) {

		try {
			MyList mylist = null;
			int reqType = in.readInt();
			String field = in.readUTF();
			String value = in.readUTF();

			switch (reqType) {
			case RequestType.NORMAL:
				mylist = serverLogic.searchElement(field, value);

				break;
			case RequestType.ENC:

				mylist = serverLogic.searchElementEnc(field, value);

				break;
			case RequestType.ONIONENC:

				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				mylist = serverLogic.searchElementEnEnc(field, iv, RandomKey, value);

				break;

			default:
				break;
			}
			out.writeObject(mylist);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void searchEntrys(  ObjectInput in, ObjectOutput out) {

		try {
			MyList mylist = null;
			int reqType = in.readInt();
			@SuppressWarnings("unchecked")
			List<String>  query = (List<String>) in.readObject();

			switch (reqType) {
			case RequestType.NORMAL:
				mylist = serverLogic.searchEntrys(query);

				break;
			case RequestType.ENC:

				mylist = serverLogic.searchEntrysEnc(query);

				break;
			case RequestType.ONIONENC:

				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				mylist = serverLogic.searchEntrysEnEnc(query, iv, RandomKey);

				break;

			default:
				break;
			}
			out.writeObject(mylist);
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}



	private void orderEntrys(ObjectInput in, ObjectOutput out) {

		try {
			MyList mylist = null;
			int reqType = in.readInt();
			String  field = in.readUTF();

			switch (reqType) {
			case RequestType.NORMAL:
				mylist = serverLogic.orderEntrys(field);
				//				List <String> l =mylist.getList();
				//				for (Iterator iterator = l.iterator(); iterator.hasNext();) {
				//					String string = (String) iterator.next();
				//					System.err.println(string);
				//
				//				}
				break;
			case RequestType.ENC:

				mylist = serverLogic.orderEntrysEnc(field);

				break;
			case RequestType.ONIONENC:

				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				mylist = serverLogic.orderEntrysEnEnc(field, iv, RandomKey);

				break;

			default:
				break;
			}
			out.writeObject(mylist);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}


	private void searchGreaterThan( ObjectInput in, ObjectOutput out) {
		try {
			MyList mylist = null;
			int reqType = in.readInt();
			String  field = in.readUTF();
			String  value = in.readUTF();
			switch (reqType) {
			case RequestType.NORMAL:
				mylist = serverLogic.searchGreaterThan(field, value);
				//				List <String> l =mylist.getList();
				//				for (Iterator iterator = l.iterator(); iterator.hasNext();) {
				//					String string = (String) iterator.next();
				//					System.err.println(string);
				//
				//				}
				break;
			case RequestType.ENC:

				mylist = serverLogic.searchGreaterThanEnc(field, value);

				break;
			case RequestType.ONIONENC:

				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				mylist = serverLogic.searchGreaterThanEnEnc(field, value, iv, RandomKey);

				break;

			default:
				break;
			}
			out.writeObject(mylist);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}





	private void searchLesserThan( ObjectInput in, ObjectOutput out) {
		try {
			MyList mylist = null;
			int reqType = in.readInt();
			String  field = in.readUTF();
			String  value = in.readUTF();
			switch (reqType) {
			case RequestType.NORMAL:
				mylist = serverLogic.searchLesserThan(field, value);

				break;
			case RequestType.ENC:

				mylist = serverLogic.searchLesserThanEnc(field, value);

				break;
			case RequestType.ONIONENC:

				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				mylist = serverLogic.searchLesserThanEnEnc(field, value, iv, RandomKey);

				break;

			default:
				break;
			}
			out.writeObject(mylist);
		} catch (IOException e) {

			e.printStackTrace();
		}


	}





	private void valuegreaterThan( ObjectInput in, ObjectOutput out) {

		try {
			MyBoolean mylist = null;
			int reqType = in.readInt();
			String  key1 = in.readUTF();
			String  field = in.readUTF();
			String  key2 = in.readUTF();
			switch (reqType) {
			case RequestType.NORMAL:
				mylist = serverLogic.valuegreaterThan(key1,field, key2);

				break;
			case RequestType.ENC:

				mylist = serverLogic.valuegreaterThanEnc(key1,field, key2);

				break;
			case RequestType.ONIONENC:

				String iv = in.readUTF();
				String RandomKey = in.readUTF();
				mylist = serverLogic.valuegreaterThanEnEnc(key1,field, key2, iv, RandomKey);

				break;

			default:
				break;
			}
			out.writeObject(mylist);
		} catch (IOException e) {

			e.printStackTrace();
		}


	}

}
