package de.aspera.dataexport.dataFaker;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
@Component
public class DataFaker {
	
	private Faker faker;
	
	public DataFaker() {
		this.faker = new Faker();
	}
	public  String fakeDate() {
		return null;
	}
	public  String fakeNumber() {
		return null;
	}
	public  String fakeString() {
		return null;
	}
	public  String fakeStringWithLength(int Length) {
		return faker.regexify("[a-zA-Z]{"+Length+"}");
	}
	public  String fakeNumberWithinRange(int firstNum, int secondNum) {
		return "fakeNum called";
	}
	public String fakeBoolean() {
		return null;
	}
	public String fakeSHA256() {
		return null;
	}
	public String fakeEmail() {
		return null;
	}

}
