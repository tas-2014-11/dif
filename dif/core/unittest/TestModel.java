package com.lumenare.dif.core.unittest;

import junit.framework.*;

import com.lumenare.dif.core.Model;

public class TestModel extends TestCase {

	public TestModel(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return(new TestSuite(TestModel.class));
	}

	protected void setUp() {
	}

	protected void tearDown() {
	}

	public void testModel() {
		String modelName = "abcdefg";
		String expected = modelName;
		Model model = new Model(expected);
		String actual = model.getName();
		assert(actual.compareTo(expected) == 0);
	}

	public void testModelImmutability() {
		String modelName = "abcdefg";
		String expected = modelName;
		Model model = new Model(expected);
		String actual = model.getName();
		assert(actual.compareTo(expected) == 0);

		actual = actual + "NOT";
		String other = actual;
		actual = model.getName();
		assert(actual.compareTo(other) != 0);
	}
}
