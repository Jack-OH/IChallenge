package lecture.patterns.templatemehod;

public class Main {

    public static void main(String[] args) {
		Beverage coffee = new Coffee();
        Beverage tea = new Tea();
		
		System.out.println("\n[Making coffee...]");
		coffee.prepareRecipe();
		
        System.out.println("\n[Making tea...]");
        tea.prepareRecipe();
	}
    
}

    