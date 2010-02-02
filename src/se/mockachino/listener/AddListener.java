package se.mockachino.listener;

import se.mockachino.listener.MethodCallListener;
import se.mockachino.verifier.VerificationHandler;
import se.mockachino.listener.MethodListener;
import se.mockachino.MockData;
import se.mockachino.matchers.MethodMatcher;

public class AddListener extends VerificationHandler {
	private final MockData mockData;
	private final Object mock;
	private final MethodCallListener listener;

	public AddListener(MockData mockData, Object mock, MethodCallListener listener) {
		super("Listener");
		this.mockData = mockData;
		this.mock = mock;
		this.listener = listener;
	}

	@Override
	public void verify(Object o, MethodMatcher matcher) {
		mockData.getListeners(matcher.getMethod()).add(new MethodListener(mock, listener, matcher));
	}
}
