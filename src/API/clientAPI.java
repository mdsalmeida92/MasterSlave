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
	 
	 public void incr(String key, String field);
	 public void incrBy(String key, String field, int value);
	 public Future<Integer> sum (String key1, String field, String key2);
	 public Future<Integer> multConst(String key, String field, int constant);

	 public Future<Integer> mult (String key1, String field, String key2);


	 
	 public Future<List> searchElement (String field, String value);
	 public Future<MyList> searchEntry (Map<String, String> set);

	 public Future<MyListEntry> orderEntrys (String field);//crescente ou acrescentar flag
	 public Future<MyListEntry>  searchGreaterThan (String field, int value);
	 public Future<MyListEntry>  searchLesserThan (String field, int value);
	 
	 public Future<MyBoolean> valuegreaterThan(String key1, String field, String key2);
	 



}
