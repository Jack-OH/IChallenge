package lecture.patterns.decorator;
 
public class Whip extends CondimentDecorator {
 
	public Whip(Beverage beverage) {
		super(beverage);
	}
 
	public String getDescription() {
		return beverage.getDescription() + ", Whip";
	}
 
	public double cost() {
		
	}
}
