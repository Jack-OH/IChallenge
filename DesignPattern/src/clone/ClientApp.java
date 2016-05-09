package clone;

import java.util.ArrayList;



public class ClientApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Shape s = new Shape(1);
		
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		
		shapes.add(s);
		
		shapes.add(s.clone());
		
		drawAllShapes(shapes);

	}
	
	public static void drawAllShapes(ArrayList<Shape> shapes) {
		for( Shape s : shapes )
			s.draw();
	}

}
