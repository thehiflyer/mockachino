package se.mockachino.expectations;

import se.mockachino.MethodCall;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class MethodExpectations {
	private final List<MethodExpectation> expectations;

	public MethodExpectations() {
		expectations = new ArrayList<MethodExpectation>();
	}


	public synchronized MethodExpectation findMatch(MethodCall call) {
		int n = expectations.size();
		for (int i = n - 1; i >= 0; i--) {
			MethodExpectation expectation = expectations.get(i);
			if (expectation.matches(call)) {
				return expectation;
			}
		}
		return null;
	}

	public synchronized void add(MethodExpectation expectation) {
		expectations.add(expectation);
	}
}
