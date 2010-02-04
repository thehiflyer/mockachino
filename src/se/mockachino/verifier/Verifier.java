package se.mockachino.verifier;

import se.mockachino.cleaner.StacktraceCleaner;
import se.mockachino.verifier.VerificationHandler;
import se.mockachino.MockData;
import se.mockachino.MethodCall;
import se.mockachino.exceptions.VerificationError;
import se.mockachino.matchers.MethodMatcher;

import java.util.List;

public class Verifier<T> extends VerificationHandler {
	private final MockData<T> mockData;
	private final int minCalls;
	private final int maxCalls;

	public Verifier(T mock, MockData<T> mockData, int minCalls, int maxCalls) {
		super("Verifier", mock.toString());
		this.mockData = mockData;
		this.minCalls = minCalls;
		this.maxCalls = maxCalls;
	}

	@Override
	public void verify(Object o, MethodMatcher matcher) {
		int counter = 0;
		List<MethodCall> calls = mockData.getCalls();
		for (MethodCall call : calls) {
			if (matcher.matches(call)) {
				counter++;
			}
		}
		boolean exact = minCalls == maxCalls;
		if (counter < minCalls) {
			String expected;
			if (exact) {
				expected = "Expected exactly " + calls(minCalls);
			} else {
				expected = "Expected at least " + calls(minCalls);
			}
			error(expected + ", but got " + calls(counter), matcher);
		}
		if (counter > maxCalls) {
			String expected;
			if (exact) {
				expected = "Expected exactly " + calls(maxCalls);
			} else {
				expected = "Expected at most" + calls(maxCalls);
			}
			error(expected + ", but got " + calls(counter), matcher);
		}
	}

	private String calls(int num) {
		if (num == 1) {
			return "1 call";
		}
		if (num == 0) {
			return "no calls";
		}
		return num + " calls";
	}

	private void error(String msg, MethodMatcher matcher) {
		String matchingMethods = getBestMatches(matcher);
		String expected = "EXPECTED:     mock." + matcher.toString();
		throw new VerificationError(msg + "\n" + matchingMethods + expected);
	}

	private String getBestMatches(MethodMatcher matcher) {
		String matchingMethods = "";
		List<MethodCall> calls = mockData.getCalls();
		for (MethodCall call : calls) {
			if (call.getMethod().equals(matcher.getMethod())) {
				String prefix = (matcher.matches(call)) ? "ACTUAL: (HIT) " : "ACTUAL:       ";
				matchingMethods += prefix + "mock." + call + "\n" + getStacktrace(call);
			}
		}
		return matchingMethods;
	}

	private String getStacktrace(MethodCall call) {
		return "";
		//return call.getStackTrace();
	}
}
