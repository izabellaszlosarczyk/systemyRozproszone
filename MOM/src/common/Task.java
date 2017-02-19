package common;

import java.io.Serializable;
import java.util.Random;

public class Task implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4879031922701230818L;

	public enum TypeOfTask {
		MULTIPLICATION, ADDITION, SUBSTRACTION, DIVISION, EXPONENTATION, ROOT
	}
	
	private TypeOfTask type;
	private String firstNumber;
	private String secondNumber;
	private String result;
	
	
	public Task(){
		Random rm = new Random();
		this.firstNumber = Integer.toString(rm.nextInt());
		this.secondNumber = Integer.toString(rm.nextInt());
		this.type = getRandomType(Math.abs(rm.nextInt()));
	}
	
	public TypeOfTask getType() {
		return type;
	}
	public void setType(TypeOfTask type) {
		this.type = type;
	}
	public String getFirstNumber() {
		return firstNumber;
	}
	public void setFirstNumber(String firstNumber) {
		this.firstNumber = firstNumber;
	}
	public String getSecondNumber() {
		return secondNumber;
	}
	public void setSecondNumber(String secondNumber) {
		this.secondNumber = secondNumber;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	private TypeOfTask getRandomType(int i){
		TypeOfTask[] tasks = TypeOfTask.values();
		return tasks[i%(tasks.length)];
	}
	
	@Override
	public String toString() {
		return this.firstNumber + " " + this.type.toString() + " " + this.secondNumber;
	}
}
