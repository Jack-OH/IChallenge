package factory.app;

import java.util.ArrayList;

import factory.Factory;
import factory.Shape;

public class ShapeApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Factory circleFactory = new CircleFactory();
		Factory squareFactory = new SquareFactory();
		
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		
		shapes.add(circleFactory.create());
		shapes.add(squareFactory.create());
		shapes.add(circleFactory.create());
		shapes.add(squareFactory.create());		
		
		drawAllShapes(shapes);

	}
	
	public static void drawAllShapes(ArrayList<Shape> shapes) {
		for( Shape s : shapes )
			s.draw();
	}
 
}

