package abstractfactory.window;

import abstractfactory.AbstractShapeFactory;
import abstractfactory.Circle;
import abstractfactory.Square;

public class WindowShapeFactory extends AbstractShapeFactory {

	@Override
	public Circle createCircle() {
		// TODO Auto-generated method stub
		return new WindowCircle();
	}

	@Override
	public Square createSquare() {
		// TODO Auto-generated method stub
		return new WindowSquare();
	}
	

}
