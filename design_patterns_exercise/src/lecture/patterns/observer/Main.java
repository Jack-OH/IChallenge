package lecture.patterns.observer;

public class Main {
	
	public static void main(String[] args) {
		NumberGenerator generator = new IncrementalNumberGenerator(10, 50, 5);
		
		Observer digitobserver = new DigitObserver();
		Observer graphobserver = new GraphObserver();
		
		generator.addObserver(digitobserver);
		generator.addObserver(graphobserver);
		
		generator.execute();
	}

}
