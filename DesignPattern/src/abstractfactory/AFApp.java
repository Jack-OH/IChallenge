package abstractfactory;

import abstractfactory.linux.LinuxShapeFactory;

public class AFApp {

	public static void main(String[] args) {
		
		AbstractShapeFactory factory = new LinuxShapeFactory();
		
		Circle circle = factory.createCircle();
		Square square = factory.createSquare();
		
		circle.draw();
		square.draw();

	}

}
