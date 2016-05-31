package lecture.patterns.observer;

public class DigitObserver {

    @Override
	public void update(NumberGenerator generator) {
		System.out.println("DigitObserver: " + generator.getNumber());
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}
    
}
