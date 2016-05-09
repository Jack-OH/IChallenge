package factory;

public abstract class Factory {
	
	public Shape create() {
		return createShape();
	}
	protected abstract Shape createShape();
	

}
