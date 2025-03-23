package com.yaksha.assignment;

//Interface representing the ability to fly
interface Flyable {
	void fly(); // Method for flying
}

//Interface representing the ability to run
interface Runnable {
	void run(); // Method for running
}

//Bird class implements both Flyable and Runnable interfaces
class Bird implements Flyable, Runnable {

	@Override
	public void fly() {
		System.out.println("The bird is flying.");
	}

	@Override
	public void run() {
		System.out.println("The bird is running.");
	}
}

public class MultipleInheritanceAssignment {
	public static void main(String[] args) {
		Bird bird = new Bird(); // Creating a Bird object
		bird.fly(); // Should print "The bird is flying." as implemented in Flyable
		bird.run(); // Should print "The bird is running." as implemented in Runnable
	}
}
