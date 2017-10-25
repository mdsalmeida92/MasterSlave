package API;
import java.util.List;
import java.util.Map;

public interface clientAPI {
	
	 public Map<String, Object> getSet (String key);
	 public String addSet (String key, Map<String, Object> set);
	 public  boolean removeSet (String key);

	 public String addElement (String key, Object element);
	 
	 public void incr(String key);
	 public void incrBy(String key, int value);

	 public int sum (String key1, String field, String key2);
	 public int multConst(String key, String field, int constant);

	 public int mult (String key1, String field, String key2);

	 
	 public List<String> searchElement (String field, Object value);
	 public List<String> searchEntry (Map<String, Object> set);

	 public List<Map<String, Object>> orderEntrys (String field);//crescente ou acrescentar flag
	 public List<Map<String, Object>> searchGreaterThan (String field, int value);
	 public List<Map<String, Object>> searchLesserThan (String field, int value);
	 
	 public boolean valuegreaterThan(String key1, String field, String key2);
	 



}
