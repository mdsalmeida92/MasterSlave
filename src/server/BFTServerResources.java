package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import bftsmart.tom.ServiceProxy;
import utils.Element;
import utils.MyBoolean;
import utils.MyEntry;
import utils.MyList;
import utils.RequestType;

@Path("/server")
public class BFTServerResources {

	private ServiceProxy clientProxy;

	private long putTime;
	private long getTime;
	private long getElemTime;
	private long searchEntryContainingSentenceTime;
	private long elementContainsSentenceTime;
	private long removeTime;
	private long updateTime;
	private long incrTime;
	private long sumTime;
	private long sumConstTime;
	private long multTime;
	private long searchElemTime;
	private long searchEntrysTime;
	private long orderEntrysTime;
	private long searchGreaterTime;
	private long searchLesserTime;
	private long valueGreaterTime;
	private long sumAllTime;
	private long multAllTime;

	public BFTServerResources(int clientId) {
		System.err.println("client id :  "+clientId);
		clientProxy = new ServiceProxy(clientId);

		putTime = 0;
		getTime= 0;
		getElemTime = 0;
		removeTime= 0;
		searchEntryContainingSentenceTime =0;
		elementContainsSentenceTime =0;
		updateTime= 0;
		incrTime= 0;
		sumTime= 0;
		sumConstTime= 0;
		multTime= 0;
		searchElemTime= 0;
		searchEntrysTime= 0;
		orderEntrysTime= 0;
		searchGreaterTime= 0;
		searchLesserTime= 0;
		valueGreaterTime= 0;
		sumAllTime = 0;
		multAllTime =0;


	}


	@GET
	@Path("/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public MyEntry getEntry(@PathParam("key") String key) throws InterruptedException {


		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.GETSET);

			out.writeUTF(key);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyEntry set = (MyEntry) in.readObject(); 

			return set;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}	 catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	@POST 
	@Path("/{key}") 
	@Consumes(MediaType.APPLICATION_JSON) 
	public void putEntry(@PathParam("key") String key, MyEntry entry) throws InterruptedException { 


		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);

			out.writeInt(RequestType.PUTSET);
			out.writeUTF(key);
			out.writeObject(entry);
			out.flush();
			byte[] reply = clientProxy.invokeOrdered(bos.toByteArray());

		} catch(IOException ioe) {
			System.out.println("Exception putting value into hashmap: " + ioe.getMessage());

		} catch (Exception e) {
			e.printStackTrace();

		}


	}
	
	@DELETE
	@Path("/flushall")
	@Consumes(MediaType.APPLICATION_JSON)
	public void flushall() {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);

			out.writeInt(RequestType.FLUSHALL);
			out.flush();
			clientProxy.invokeOrdered(bos.toByteArray());

		} catch(IOException ioe) {
			System.out.println("Exception flushing values: " + ioe.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}




	}

	@DELETE
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeEntry(@PathParam("key") String key) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);

			out.writeInt(RequestType.REMOVESET);
			out.writeUTF(key);
			out.flush();
			byte[] reply = clientProxy.invokeOrdered(bos.toByteArray());

		} catch(IOException ioe) {
			System.out.println("Exception putting value into hashmap: " + ioe.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}




	}

	@PUT
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateEntry(@PathParam("key") String key, Element element) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);

			out.writeInt(RequestType.ADDELEMENT);
			out.writeUTF(key);
			out.writeObject(element);
			out.flush();
			byte[] reply = clientProxy.invokeOrdered(bos.toByteArray());

		} catch(IOException ioe) {
			System.out.println("Exception putting value into hashmap: " + ioe.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GET
	@Path("/getElem")
	@Produces(MediaType.APPLICATION_JSON)
	public String getElement(@QueryParam("key") String key, @QueryParam("field") String field) throws InterruptedException {


		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.GETELEMENT);

			out.writeUTF(key);
			out.writeUTF(field);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			String elem =  in.readUTF(); 

			return elem;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		}	 catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}



	@GET
	@Path("/elementContainsSentence")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean elementContainsSentence(@QueryParam("key") String key, 
			@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.ELEMENT_CONTAINS_SEQUENCE);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(key);
			out.writeUTF(field);
			out.writeUTF(sentence);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyBoolean myboolean =  (MyBoolean) in.readObject(); 

			return myboolean;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}	 catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Path("/elementContainsSentence/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean elementContainsSentenceEnc(@QueryParam("key") String key, 
			@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.ELEMENT_CONTAINS_SEQUENCE);
			out.writeInt(RequestType.ENC);
			out.writeUTF(key);
			out.writeUTF(field);
			out.writeUTF(sentence);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyBoolean myboolean =  (MyBoolean) in.readObject(); 

			return myboolean;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}	

	}


	@GET
	@Path("/elementContainsSentence/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean elementContainsSentenceEnEnc(@QueryParam("key") String key, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("field") String field,
			@QueryParam("sentence") String sentence) throws InterruptedException {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.ELEMENT_CONTAINS_SEQUENCE);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(key);
			out.writeUTF(field);
			out.writeUTF(sentence);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyBoolean myboolean =  (MyBoolean) in.readObject(); 

			return myboolean;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}	

	}

	@GET
	@Path("/searchEntryContainingSentence")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntryContainingWord(@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHENTRYCONTAININGSENTENCE);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(field);
			out.writeUTF(sentence);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList myList =  (MyList) in.readObject(); 

			return myList;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}	 catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/searchEntryContainingSentence/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntryContainingWordEnc(@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHENTRYCONTAININGSENTENCE);
			out.writeInt(RequestType.ENC);
			out.writeUTF(field);
			out.writeUTF(sentence);

			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList myList =  (MyList) in.readObject(); 

			return myList;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}	


	}

	@GET
	@Path("/searchEntryContainingSentence/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntryContainingWordEnEnc(@QueryParam("field") String field, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("sentence") String sentence) throws InterruptedException {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHENTRYCONTAININGSENTENCE);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(field);
			out.writeUTF(sentence);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList myList =  (MyList) in.readObject(); 

			return myList;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}	
	}

	@PUT
	@Path("/incr/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void incr(@PathParam("key") String key, Element value) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);

			out.writeInt(RequestType.INCR);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(key);
			out.writeObject(value);
			out.flush();
			byte[] reply = clientProxy.invokeOrdered(bos.toByteArray());

		} catch(IOException ioe) {
			System.out.println("Exception putting value into hashmap: " + ioe.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PUT
	@Path("/incr/Encrypted/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void incrEnc(@PathParam("key") String key, Element value) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);

			out.writeInt(RequestType.INCR);
			out.writeInt(RequestType.ENC);
			out.writeUTF(key);
			out.writeObject(value);
			out.flush();
			byte[] reply = clientProxy.invokeOrdered(bos.toByteArray());

		} catch(IOException ioe) {
			System.out.println("Exception putting value into hashmap: " + ioe.getMessage());

		}
	}

	@PUT
	@Path("/incr/EnhancedEncrypted/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void incrEnEnc(@PathParam("key") String key, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey, Element value) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);

			out.writeInt(RequestType.INCR);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(key);
			out.writeObject(value);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);
			out.flush();
			byte[] reply = clientProxy.invokeOrdered(bos.toByteArray());

		} catch(IOException ioe) {
			System.out.println("Exception putting value into hashmap: " + ioe.getMessage());

		}
	}


	@GET
	@Path("/sum")
	@Produces(MediaType.APPLICATION_JSON)
	public BigInteger sum(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SUM);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(key1);
			out.writeUTF(field);
			out.writeUTF(key2);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			BigInteger bigInt =  (BigInteger) in.readObject(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}	 catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Path("/sum/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("nsquare")  String nsquare, 
			@QueryParam("key2") String key2) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SUM);
			out.writeInt(RequestType.ENC);
			out.writeUTF(key1);
			out.writeUTF(field);
			out.writeUTF(key2);
			out.writeUTF(nsquare);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			String bigInt =  in.readUTF(); 
			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			ioe.printStackTrace();
			return null;
		}	

	}

	@GET
	@Path("/sum/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumEnEnc(@QueryParam("key1") String key1, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("field")  String field, 
			@QueryParam("nsquare")  String nsquare, 
			@QueryParam("key2") String key2) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SUM);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(key1);
			out.writeUTF(field);
			out.writeUTF(key2);
			out.writeUTF(nsquare);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);

			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			String bigInt =  in.readUTF(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		}
	}

	@GET
	@Path("/sumAll")
	@Produces(MediaType.APPLICATION_JSON)
	public BigInteger sumAll(@QueryParam("field")  String field) {


		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SUMALL);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(field);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			BigInteger bigInt =  (BigInteger) in.readObject(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}	 catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}


	@GET
	@Path("/sumAll/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumAllEnc(@QueryParam("field")  String field,
			@QueryParam("nsquare")  String nsquare) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SUMALL);
			out.writeInt(RequestType.ENC);
			out.writeUTF(field);
			out.writeUTF(nsquare);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			String bigInt =  in.readUTF(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		}	
	}

	@GET
	@Path("/sumAll/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumAllEnEnc(@QueryParam("field")  String field,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("nsquare")  String nsquare) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SUMALL);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(field);
			out.writeUTF(nsquare);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			String bigInt =  in.readUTF(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		}
	}

	@GET
	@Path("/multConst")
	@Produces(MediaType.APPLICATION_JSON)
	public BigInteger sumConst(@QueryParam("key") String key, 
			@QueryParam("field")  String field, 
			@QueryParam("const") int constant) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.MULTCONST);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(key);
			out.writeUTF(field);
			out.writeInt(constant);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			BigInteger bigInt =  (BigInteger) in.readObject(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}	 catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/multConst/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumConstEnc(@QueryParam("key") String key, 
			@QueryParam("nsquare")  String nsquare, 
			@QueryParam("field")  String field, 
			@QueryParam("const") int constant) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.MULTCONST);
			out.writeInt(RequestType.ENC);
			out.writeUTF(key);
			out.writeUTF(field);
			out.writeInt(constant);
			out.writeUTF(nsquare);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			String bigInt =  in.readUTF(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		}	
	}

	@GET
	@Path("/multConst/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumConst(@QueryParam("key") String key, 
			@QueryParam("nsquare")  String nsquare, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("field")  String field, 
			@QueryParam("const") int constant) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.MULTCONST);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(key);
			out.writeUTF(field);
			out.writeInt(constant);
			out.writeUTF(nsquare);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			String bigInt =  in.readUTF(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		}
	}

	@GET
	@Path("/mult")
	@Produces(MediaType.APPLICATION_JSON)
	public long mult(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.MULT);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(key1);
			out.writeUTF(field);
			out.writeUTF(key2);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			long bigInt = in.readLong(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			ioe.printStackTrace();
			return 0;
		}	 catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@GET
	@Path("/mult/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String multEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("publicKey")  String publicKey, 
			@QueryParam("key2") String key2) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.MULT);
			out.writeInt(RequestType.ENC);
			out.writeUTF(key1);
			out.writeUTF(field);
			out.writeUTF(key2);
			out.writeUTF(publicKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			String bigInt =  in.readUTF(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		}	


	}

	@GET
	@Path("/mult/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String multEnEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("publicKey")  String publicKey, 
			@QueryParam("key2") String key2) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.MULT);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(key1);
			out.writeUTF(field);
			out.writeUTF(key2);
			out.writeUTF(publicKey);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);

			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			String bigInt =  in.readUTF(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		}
	}

	@GET
	@Path("/multAll")
	@Produces(MediaType.APPLICATION_JSON)
	public long multAll(@QueryParam("field")  String field) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.MULTALL);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(field);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			long bigInt = in.readLong(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return 0;
		}	 catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@GET
	@Path("/multAll/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String multAllEnc(@QueryParam("field")  String field,
			@QueryParam("publicKey")  String publicKey) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.MULTALL);
			out.writeInt(RequestType.ENC);
			out.writeUTF(field);
			out.writeUTF(publicKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			String bigInt =  in.readUTF(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		}	



	}

	@GET
	@Path("/multAll/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String multAllEnEnc(@QueryParam("field")  String field,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("publicKey")  String publicKey) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.MULTALL);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(field);
			out.writeUTF(publicKey);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			String bigInt =  in.readUTF(); 

			return bigInt;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		}

	}

	@GET
	@Path("/searchElement")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchElement( @QueryParam("field")  String field, 
			@QueryParam("value") String value) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHELEMENT);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(field);
			out.writeUTF(value);

			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/searchElement/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchElementEnc( @QueryParam("field")  String field, 
			@QueryParam("value") String value) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHELEMENT);
			out.writeInt(RequestType.ENC);
			out.writeUTF(field);
			out.writeUTF(value);

			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/searchElement/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchElementEnEnc( @QueryParam("field")  String field, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("value") String value) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHELEMENT);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(field);
			out.writeUTF(value);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);

			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/searchEntrys")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntrys( @QueryParam("query")  List<String>  query) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHENTRY);
			out.writeInt(RequestType.NORMAL);
			out.writeObject(query);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Path("/searchEntrys/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntrysEnc( @QueryParam("query")  List<String>  query) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHENTRY);
			out.writeInt(RequestType.ENC);
			out.writeObject(query);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Path("/searchEntrys/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntrysEnEnc( @QueryParam("query")  List<String>  query,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHENTRY);
			out.writeInt(RequestType.ONIONENC);
			out.writeObject(query);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Path("/orderEntrys")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList orderEntrys( @QueryParam("query")  String field) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.ORDERENTRYS);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(field);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/orderEntrys/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList orderEntrysEnc( @QueryParam("query")  String field) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.ORDERENTRYS);
			out.writeInt(RequestType.ENC);
			out.writeUTF(field);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/orderEntrys/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList orderEntrysEnEnc( @QueryParam("query")  String field,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.ORDERENTRYS);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(field);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Path("/searchGreaterThan")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchGreaterThan( @QueryParam("field")  String field, @QueryParam("value")  String value) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHGREATERTHAN);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(field);
			out.writeUTF(value);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/searchGreaterThan/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchGreaterThanEnc( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHGREATERTHAN);
			out.writeInt(RequestType.ENC);
			out.writeUTF(field);
			out.writeUTF(value);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}
	}


	@GET
	@Path("/searchGreaterThan/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchGreaterThanEnEnc( @QueryParam("field")  String field, @QueryParam("value")  String value,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHGREATERTHAN);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(field);
			out.writeUTF(value);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/searchLesserThan")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchLesserThan( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHLESSERTHAN);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(field);
			out.writeUTF(value);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Path("/searchLesserThan/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchLesserThanEnc( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHLESSERTHAN);
			out.writeInt(RequestType.ENC);
			out.writeUTF(field);
			out.writeUTF(value);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Path("/searchLesserThan/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchLesserThanEnEnc( @QueryParam("field")  String field, @QueryParam("value")  String value,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.SEARCHLESSERTHAN);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(field);
			out.writeUTF(value);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyList mylist =  (MyList) in.readObject(); 

			return mylist;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Path("/valuegreaterThan")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean valuegreaterThan(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.VALUEGREATERTHAN);
			out.writeInt(RequestType.NORMAL);
			out.writeUTF(key1);
			out.writeUTF(field);
			out.writeUTF(key2);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyBoolean MyBoolean =  (MyBoolean) in.readObject(); 

			return MyBoolean;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Path("/valuegreaterThan/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean valuegreaterThanEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.VALUEGREATERTHAN);
			out.writeInt(RequestType.ENC);
			out.writeUTF(key1);
			out.writeUTF(field);
			out.writeUTF(key2);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyBoolean MyBoolean =  (MyBoolean) in.readObject(); 

			return MyBoolean;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Path("/valuegreaterThan/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean valuegreaterThanEnEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(RequestType.VALUEGREATERTHAN);
			out.writeInt(RequestType.ONIONENC);
			out.writeUTF(key1);
			out.writeUTF(field);
			out.writeUTF(key2);
			out.writeUTF(iv);
			out.writeUTF(RandomKey);
			out.flush();

			byte[] reply = clientProxy.invokeUnordered(bos.toByteArray());

			ByteArrayInputStream bis = new ByteArrayInputStream(reply);
			ObjectInput in = new ObjectInputStream(bis);
			MyBoolean MyBoolean =  (MyBoolean) in.readObject(); 

			return MyBoolean;
		} catch(IOException ioe) {
			System.out.println("Exception getting value from the hashmap: " + ioe.getMessage());
			return null;
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
		}

	}




}
