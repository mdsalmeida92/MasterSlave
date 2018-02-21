
package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import org.apache.commons.codec.binary.Base64;
import hlib.hj.mlib.HelpSerial;
import hlib.hj.mlib.HomoAdd;
import hlib.hj.mlib.HomoMult;
import hlib.hj.mlib.HomoRand;
import hlib.hj.mlib.HomoSearch;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import utils.Element;
import utils.MyBoolean;
import utils.MyEntry;
import utils.MyList;
import utils.RequestType;


@Path("/server")
public class ServerResources {

	public ServerLogic serverLogic;

	public ServerResources(int port){
		serverLogic = new ServerLogic(port);
	}

	@PUT
	@Path("/resetTimes")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateEntry(int element) {

		serverLogic.updateTime();

	}

	@GET
	@Path("/Encrypted/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public MyEntry getEntry(@PathParam("key") String key,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) throws InterruptedException {

		return serverLogic.getEntryEncEnc(key, iv, RandomKey);
	}
	
	@GET
	@Path("/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public MyEntry getEntry(@PathParam("key") String key) throws InterruptedException {

		return serverLogic.getEntry(key);
	}

	@POST 
	@Path("/{key}") 
	@Consumes(MediaType.APPLICATION_JSON) 
	public void putEntry(@PathParam("key") String key, MyEntry entry) throws InterruptedException { 

		serverLogic.putEntry(key, entry);


	}

	@DELETE
	@Path("/flushall")
	@Consumes(MediaType.APPLICATION_JSON)
	public void flushall() {

		try {
			serverLogic.flushAll();
		} catch (Exception e) {
			e.printStackTrace();
		}




	}
	
	@DELETE
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeEntry(@PathParam("key") String key) {
		serverLogic.removeEntry(key);


	}

	@PUT
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateEntry(@PathParam("key") String key, Element element) {
		serverLogic.updateEntry(key, element);
	}

	@GET
	@Path("/getElem")
	@Produces(MediaType.APPLICATION_JSON)
	public String getElement(@QueryParam("key") String key, @QueryParam("field") String field) throws InterruptedException {
		return serverLogic.getElement(key, field);

	}

	@GET
	@Path("/getElem/Encrypted")
	@Produces(MediaType.APPLICATION_JSON)
	public String getElement(@QueryParam("key") String key, 
			@QueryParam("field") String field,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) throws InterruptedException {
		return serverLogic.getElementEncEnc(key, field, iv, RandomKey);

	}


	@GET
	@Path("/elementContainsSentence")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean elementContainsSentence(@QueryParam("key") String key, 
			@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {

		return serverLogic.elementContainsSentence(key, field, sentence);

	}

	@GET
	@Path("/elementContainsSentence/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean elementContainsSentenceEnc(@QueryParam("key") String key, 
			@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {
		return serverLogic.elementContainsSentenceEnc(key, field, sentence);



	}


	@GET
	@Path("/elementContainsSentence/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean elementContainsSentenceEnEnc(@QueryParam("key") String key, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("field") String field,
			@QueryParam("sentence") String sentence) throws InterruptedException {

		return serverLogic.elementContainsSentenceEnEnc(key, iv, RandomKey, field, sentence);

	}

	@GET
	@Path("/searchEntryContainingSentence")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntryContainingWord(@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {

		return serverLogic.searchEntryContainingWord(field, sentence);
	}

	@GET
	@Path("/searchEntryContainingSentence/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntryContainingWordEnc(@QueryParam("field") String field, 
			@QueryParam("sentence") String sentence) throws InterruptedException {

		return serverLogic.searchEntryContainingWordEnc(field, sentence);


	}

	@GET
	@Path("/searchEntryContainingSentence/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntryContainingWordEnEnc(@QueryParam("field") String field, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("sentence") String sentence) throws InterruptedException {

		return serverLogic.searchEntryContainingWordEnEnc(field, iv, RandomKey, sentence);
	}

	@PUT
	@Path("/incr/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void incr(@PathParam("key") String key, Element value) {
		serverLogic.incr(key, value);
	}

	@PUT
	@Path("/incr/Encrypted/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void incrEnc(@PathParam("key") String key, Element value) {

		serverLogic.incrEnc(key, value);
	}

	@PUT
	@Path("/incr/EnhancedEncrypted/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void incrEnEnc(@PathParam("key") String key, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey, Element value) {
		serverLogic.incrEnEnc(key, iv, RandomKey, value);
	}


	@GET
	@Path("/sum")
	@Produces(MediaType.APPLICATION_JSON)
	public BigInteger sum(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {
		return serverLogic.sum(key1, field, key2);

	}

	@GET
	@Path("/sum/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("nsquare")  String nsquare, 
			@QueryParam("key2") String key2) {
		return serverLogic.sumEnc(key1, field, nsquare, key2);

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
		return serverLogic.sumEnEnc(key1, iv, RandomKey, field, nsquare, key2);
	}

	@GET
	@Path("/sumAll")
	@Produces(MediaType.APPLICATION_JSON)
	public BigInteger sumAll(@QueryParam("field")  String field) {

		return serverLogic.sumAll(field);
	}


	@GET
	@Path("/sumAll/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumAllEnc(@QueryParam("field")  String field,
			@QueryParam("nsquare")  String nsquare) {
		return serverLogic.sumAllEnc(field, nsquare);
	}

	@GET
	@Path("/sumAll/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumAllEnEnc(@QueryParam("field")  String field,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("nsquare")  String nsquare) {
		return serverLogic.sumAllEnEnc(field, iv, RandomKey, nsquare);
	}

	@GET
	@Path("/multConst")
	@Produces(MediaType.APPLICATION_JSON)
	public BigInteger sumConst(@QueryParam("key") String key, 
			@QueryParam("field")  String field, 
			@QueryParam("const") int constant) {
		return serverLogic.sumConst(key, field, constant);
	}

	@GET
	@Path("/multConst/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String sumConstEnc(@QueryParam("key") String key, 
			@QueryParam("nsquare")  String nsquare, 
			@QueryParam("field")  String field, 
			@QueryParam("const") int constant) {
		return serverLogic.sumConstEnc(key, nsquare, field, constant);
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
		return serverLogic.sumConst(key, nsquare, iv, RandomKey, field, constant);
	}

	@GET
	@Path("/mult")
	@Produces(MediaType.APPLICATION_JSON)
	public long mult(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {
		return serverLogic.mult(key1, field, key2);
	}

	@GET
	@Path("/mult/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String multEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("publicKey")  String publicKey, 
			@QueryParam("key2") String key2) {
		return serverLogic.multEnc(key1, field, publicKey, key2);


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
		return serverLogic.multEnEnc(key1, field, iv, RandomKey, publicKey, key2);
	}

	@GET
	@Path("/multAll")
	@Produces(MediaType.APPLICATION_JSON)
	public long multAll(@QueryParam("field")  String field) {
		return serverLogic.multAll(field);
	}

	@GET
	@Path("/multAll/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String multAllEnc(@QueryParam("field")  String field,
			@QueryParam("publicKey")  String publicKey) {
		return serverLogic.multAllEnc(field, publicKey);



	}

	@GET
	@Path("/multAll/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public String multAllEnEnc(@QueryParam("field")  String field,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("publicKey")  String publicKey) {
		return serverLogic.multAllEnEnc(field, iv, RandomKey, publicKey);

	}

	@GET
	@Path("/searchElement")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchElement( @QueryParam("field")  String field, 
			@QueryParam("value") String value) {
		return serverLogic.searchElement(field, value);
	}

	@GET
	@Path("/searchElement/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchElementEnc( @QueryParam("field")  String field, 
			@QueryParam("value") String value) {
		return serverLogic.searchElementEnc(field, value);
	}

	@GET
	@Path("/searchElement/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchElementEnEnc( @QueryParam("field")  String field, 
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey,
			@QueryParam("value") String value) {
		return serverLogic.searchElementEnEnc(field, iv, RandomKey, value);
	}

	@GET
	@Path("/searchEntrys")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntrys( @QueryParam("query")  List<String>  query) {

		return serverLogic.searchEntrys(query);

	}

	@GET
	@Path("/searchEntrys/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntrysEnc( @QueryParam("query")  List<String>  query) {
		return serverLogic.searchEntrysEnc(query);
	}

	@GET
	@Path("/searchEntrys/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchEntrysEnEnc( @QueryParam("query")  List<String>  query,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		return serverLogic.searchEntrysEnEnc(query, iv, RandomKey);
	}

	@GET
	@Path("/orderEntrys")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList orderEntrys( @QueryParam("query")  String field) {
		return serverLogic.orderEntrys(field);
	}

	@GET
	@Path("/orderEntrys/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList orderEntrysEnc( @QueryParam("query")  String field) {
		return serverLogic.orderEntrysEnc(field);
	}

	@GET
	@Path("/orderEntrys/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList orderEntrysEnEnc( @QueryParam("query")  String field,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		return serverLogic.orderEntrysEnEnc(field, iv, RandomKey);

	}

	@GET
	@Path("/searchGreaterThan")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchGreaterThan( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		return serverLogic.searchGreaterThan(field, value);
	}

	@GET
	@Path("/searchGreaterThan/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchGreaterThanEnc( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		return serverLogic.searchGreaterThanEnc(field, value);
	}


	@GET
	@Path("/searchGreaterThan/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchGreaterThanEnEnc( @QueryParam("field")  String field, @QueryParam("value")  String value,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		return serverLogic.searchGreaterThanEnEnc(field, value, iv, RandomKey);
	}

	@GET
	@Path("/searchLesserThan")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchLesserThan( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		return serverLogic.searchLesserThan(field, value);

	}

	@GET
	@Path("/searchLesserThan/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchLesserThanEnc( @QueryParam("field")  String field, @QueryParam("value")  String value) {
		return serverLogic.searchLesserThanEnc(field, value);

	}

	@GET
	@Path("/searchLesserThan/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyList searchLesserThanEnEnc( @QueryParam("field")  String field, @QueryParam("value")  String value,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		return serverLogic.searchLesserThanEnEnc(field, value, iv, RandomKey);

	}

	@GET
	@Path("/valuegreaterThan")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean valuegreaterThan(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {
		return serverLogic.valuegreaterThan(key1, field, key2);
	}

	@GET
	@Path("/valuegreaterThan/Encrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean valuegreaterThanEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2) {
		return serverLogic.valuegreaterThanEnc(key1, field, key2);

	}

	@GET
	@Path("/valuegreaterThan/EnhancedEncrypted/")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBoolean valuegreaterThanEnEnc(@QueryParam("key1") String key1, 
			@QueryParam("field")  String field, 
			@QueryParam("key2") String key2,
			@QueryParam("iv") String iv,
			@QueryParam("RandomKey") String RandomKey) {
		return serverLogic.valuegreaterThanEnEnc(key1, field, key2, iv, RandomKey);

	}

	@GET
	@Path("/getTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long getTimes() {
		return serverLogic.getTimes();
	}

	@GET
	@Path("/putTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long putTime() {
		return serverLogic.putTime();
	}



	@GET
	@Path("/removeTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long removeTime() {
		return serverLogic.removeTime();
	}


	@GET
	@Path("/updateTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long updateTime() {
		return serverLogic.updateTime();
	}


	@GET
	@Path("/incrTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long incrTime() {
		return serverLogic.incrTime();
	}


	@GET
	@Path("/sumTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long sumTime() {
		return serverLogic.sumTime();
	}


	@GET
	@Path("/sumConstTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long sumConstTime() {
		return serverLogic.sumConstTime();
	}


	@GET
	@Path("/multTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long multTime() {
		return serverLogic.multTime();
	}


	@GET
	@Path("/searchElemTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long searchElemTime() {
		return serverLogic.searchElemTime();
	}


	@GET
	@Path("/searchEntrysTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long searchEntrysTime() {
		return serverLogic.searchEntrysTime();
	}


	@GET
	@Path("/orderEntrysTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long orderEntrysTime() {
		return serverLogic.orderEntrysTime();
	}


	@GET
	@Path("/searchGreaterTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long searchGreaterTime() {
		return serverLogic.searchGreaterTime();
	}


	@GET
	@Path("/searchLesserTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long searchLesserTime() {
		return serverLogic.searchLesserTime();
	}


	@GET
	@Path("/valueGreaterTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long valueGreaterTime() {
		return serverLogic.valueGreaterTime();
	}

	@GET
	@Path("/getElemTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long getElemTime() {
		return serverLogic.getElemTime();
	}

	@GET
	@Path("/elementContainsSentenceTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long elementContainsSentenceTime() {
		return serverLogic.elementContainsSentenceTime();
	}

	@GET
	@Path("/searchEntryContainingSentenceTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long searchEntryContainingSentenceTime() {
		return serverLogic.searchEntryContainingSentenceTime();
	}

	@GET
	@Path("/sumAllTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long sumAllTime() {
		return serverLogic.sumAllTime();
	}

	@GET
	@Path("/multAllTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long multAllTime() {
		return serverLogic.multAllTime();
	}

}
