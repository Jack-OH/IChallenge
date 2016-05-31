package lecture.patterns.observer;

public class GraphObserver {

    @Override
	public void update(NumberGenerator generator) {
		System.out.print("GraphObserver: ");
		

		
		
		
		
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}
    
}
