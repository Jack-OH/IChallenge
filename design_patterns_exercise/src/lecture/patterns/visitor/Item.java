package lecture.patterns.visitor;

public abstract class Item implements Element{

    public abstract String getName();
    
    public abstract int getSize();
    
    public String toString() {
    	return getName() + " (" + getSize() + ")";
    }

    public void add(Item item) {
    	throw new UnsupportedOperationException();
    }
}



