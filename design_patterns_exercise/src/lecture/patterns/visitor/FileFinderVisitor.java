package lecture.patterns.visitor;

import java.util.ArrayList;
import java.util.Iterator;

public class FileFinderVisitor extends Visitor{
	
	private String fileType="";
	private ArrayList<Item> found = new ArrayList<Item>();
	
	public FileFinderVisitor(String fileType) {
		this.fileType = fileType;
	}
	
	public Iterator<Item> getFoundFiles() {
		return found.iterator();
	}

	
	
	
	
	
	
	
	
	
	
}
