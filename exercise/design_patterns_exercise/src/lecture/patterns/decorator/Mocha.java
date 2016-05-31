package lecture.patterns.decorator;

public class Mocha extends CondimentDecorator {
 
	public Mocha(Beverage beverage) {
		super(beverage);
	}
 
	public String getDescription() {
		return beverage.getDescription() + ", Mocha";
	}
 
	public double cost() {
		
	}
}
