package abstractfactory.linux;

import abstractfactory.AbstractShapeFactory;
import abstractfactory.Circle;
import abstractfactory.Square;

public class LinuxShapeFactory extends AbstractShapeFactory {

	@Override
	public Circle createCircle() {
		// TODO Auto-generated method stub
		return new LinuxCircle();
	}

	@Override
	public Square createSquare() {
		// TODO Auto-generated method stub
		return new LinuxSquare();
	}
	
	

}
