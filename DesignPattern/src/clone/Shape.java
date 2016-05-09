package clone;

public class Shape implements Cloneable {
	
	private int value = 0;
	
	public Shape(int v) {
		value = v;
	}
	
	public void draw() {
		System.out.println("Shape draw" + value);
	}
	
	public Shape clone() {
		Shape cloned = null;
		try{
			cloned = (Shape) super.clone();
		} catch(CloneNotSupportedException e){
			System.out.println(e);
		}
		return cloned;		
	}

}
