package lecture.patterns.visitor;

public class File extends Item{

    private String name;
    private int size;
    
    public File(String name, int size) {
    	this.name = name;
        this.size = size;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
    	return size;
    }
    
    
    
    
}



