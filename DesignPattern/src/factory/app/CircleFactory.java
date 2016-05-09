package factory.app;

import factory.Factory;
import factory.Shape;

public class CircleFactory extends Factory {
	
	protected Shape createShape() {
		return new Circle();
	}
}
