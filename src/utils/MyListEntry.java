package utils;

import java.util.List;

public class MyListEntry {
	
	public List<MyEntry> getList() {
		return list;
	}

	public void setList(List<MyEntry> list) {
		this.list = list;
	}

	List<MyEntry> list;

	public MyListEntry(List<MyEntry> list) {
		super();
		this.list = list;
	}
	
	public MyListEntry() {

	}

}
