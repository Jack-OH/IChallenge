package factory.app;

import factory.Factory;
import factory.Shape;

public class SquareFactory extends Factory {

	protected Shape createShape() {
		return new Square();
	}
}
