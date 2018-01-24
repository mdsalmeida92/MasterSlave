package API;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface clientAPI {

	public Future<Map<String,String>> getSet (String key) throws InterruptedException, ExecutionException;
	public String addSet (String key, Map<String, String> set) throws InterruptedException, ExecutionException;
	public  boolean removeSet (String key) throws InterruptedException, ExecutionException;

	public String addElement (String key,String field, String element);
	public Future<String> getElement(String key, String field);
	
	//WORD SEARCH
	public Future<Boolean> elementContainsSentence(String key, String field, String word);
	public Future<List<String>> searchEntryContainingSentence(String field, String word);

	//SOMAS
	public void incr(String key, String field);
	public void incrBy(String key, String field, int value);
	public Future<BigInteger> sum (String key1, String field, String key2);
	public Future<BigInteger> sumAll (String field);//TODO
	public Future<BigInteger> multConst(String key, String field, int constant);
	//MULTIPLICACOES
	public Future<BigInteger> mult (String key1, String field, String key2);
	public Future<BigInteger> multAll (String field);//TODO
	//COMPARACOES
	public Future<List<String>> searchElement (String field, String value);
	public Future<List<String>> searchEntry (Map<String, String> set);
	//ORDENACOES
	public Future<List<String>> orderEntrys (String field);//crescente ou acrescentar flag
	public Future<List<String>>  searchGreaterThan (String field, int value);
	public Future<List<String>>  searchLesserThan (String field, int value);
	public Future<Boolean> valuegreaterThan(String key1, String field, String key2);

	public void Close();
	public long getServerputTime();
	public long getServergetTime() ;
	public long getServerremoveTime();
	public long getServerupdateTime() ;
	public long getServerincrTime();
	public long getServersumTime();
	public long getServersumConstTime();
	public long getServermultTime();
	public long getServersearchElemTime();
	public long getServersearchEntrysTime();
	public long getServerorderEntrysTime();
	public long getServersearchGreaterTime();
	public long getServersearchLesserTime();
	public long getServervalueGreaterTime();
	public void resetServerTimes();
	public long getServergetElementTime();
	public long getServerElementContainsSenteceTime();
	public long getServersearchEntryContainingSentenceTime();
	public long getServersumAllTime();
	public long getServermultAllTime();
	
	
	public long getPrivacygetTime();
	public long getPrivacyputTime();
	public long getPrivacyupdateTime();
	public long getPrivacymultAllTime();
	public long getPrivacysumAllTime();
	public long getPrivacysearchEntryContainingSentenceTime();
	public long getPrivacyElementContainsSenteceTime();
	public long getPrivacyGetElementTime();
	public long getPrivacyvalueGreaterTime();
	public long getPrivacysearchLesserTime();
	public long getPrivacysearchGreaterTime();
	public long getPrivacyorderEntrysTime();
	public long getPrivacysearchEntrysTime();
	public long getPrivacysearchElemTime();
	public long getPrivacymultTime();
	public long getPrivacysumConstTime();
	public long getPrivacysumTime();
	public long getPrivacyincrTime();
	public long getPrivacyremoveTime();
	public void initTimes();
	
	
	

}
