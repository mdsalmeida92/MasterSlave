package API;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import utils.MyBoolean;
import utils.MyEntry;
import utils.MyList;
import utils.MyListEntry;

public interface clientAPI {

	public Future<Map<String,String>> getSet (String key) throws InterruptedException, ExecutionException;
	public String addSet (String key, Map<String, String> set) throws InterruptedException, ExecutionException;
	public  boolean removeSet (String key) throws InterruptedException, ExecutionException;

	public String addElement (String key,String field, String element);
	public String getElement(String key);//TODO 
	
	//WORD SEARCH
	public Future<Boolean> elementContains(String key, String field, String word);
	public Future<String> searchEntryContainingWord(String key, String field, String word);

	//SOMAS
	public void incr(String key, String field);
	public void incrBy(String key, String field, int value);
	public Future<Integer> sum (String key1, String field, String key2);
	public Future<Integer> sumAll (String field);//TODO
	public Future<Integer> multConst(String key, String field, int constant);
	//MULTIPLICACOES
	public Future<Integer> mult (String key1, String field, String key2);
	public Future<Integer> multAll (String field);//TODO
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
	
	
	
	
	

}
