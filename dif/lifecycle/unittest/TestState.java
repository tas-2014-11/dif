package com.lumenare.dif.lifecycle.unittest;

import junit.framework.*;

import com.lumenare.dif.lifecycle.State;

public class TestState extends TestCase {

	public TestState(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return(new TestSuite(TestState.class));
	}

	protected PhaseOne one;
	protected PhaseTwo two;
	protected PhaseOne anotherOne;

	protected void setUp() {
		one = new PhaseOne();
		two = new PhaseTwo();
		anotherOne = new PhaseOne();
	}

	protected void tearDown() {
		one = null;
		two = null;
		anotherOne = null;
	}

	public void testSimpleEquals() {
		// These damn well better work.
		assertEquals(one,one);
		assertEquals(two,two);
		assertEquals(anotherOne,anotherOne);
	}

	public void testCustomEquals() {
		// Phases are affine.  All of one type are congruent.
		assertEquals(one,anotherOne);
		assertEquals(anotherOne,one);
	}

	public void testNotEquals() {
		assert("one.two",!one.equals(two));
		assert("two.one",!two.equals(one));

		assert("anotherOne.two",!anotherOne.equals(two));
		assert("two.anotherOne",!two.equals(anotherOne));
	}

	public void testNull() {
		assert("one.null",!one.equals(null));
		assert("two.null",!two.equals(null));
		assert("anotherOne.null",!anotherOne.equals(null));
	}
}

class PhaseOne extends State { }
class PhaseTwo extends State { }
