package se.mockachino;

import se.mockachino.cleaner.StacktraceCleaner;
import se.mockachino.invocationhandler.*;
import se.mockachino.listener.ListenerAdder;
import se.mockachino.listener.MethodCallListener;
import se.mockachino.mock.Mock;
import se.mockachino.order.InOrder;
import se.mockachino.proxy.ProxyUtil;
import se.mockachino.matchers.MatcherThreadHandler;
import se.mockachino.stub.*;
import se.mockachino.verifier.VerifyRangeStart;

import java.lang.reflect.InvocationHandler;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class MockContext {
	private static final InvocationHandler DEFAULT_INVOCATION_HANDLER = new DefaultInvocationHandler();

	private final Map<Object, MockData> mockData = Collections.synchronizedMap(new WeakHashMap<Object, MockData>());
	private final AtomicInteger nextSequenceNumber = new AtomicInteger();
	private final AtomicInteger nextMockId = new AtomicInteger();

	public <T> T mock(Class<T> clazz) {
		assertClass(clazz);
		return mock(clazz, DEFAULT_INVOCATION_HANDLER);
	}

	public <T> T mock(Class<T> clazz, InvocationHandler handler) {
		assertClass(clazz);
		assertHandler(handler);
		return spy(clazz, ProxyUtil.newProxy(clazz, handler), "Mock");
	}

	public <T> T spy(Class<T> clazz, T impl) {
		assertClass(clazz);
		if (impl == null) {
			throw StacktraceCleaner.cleanError(
					new IllegalArgumentException(
							"impl can not be null"));
		}
		return spy(clazz, impl, "Spy");
	}

	private <T> T spy(Class<T> clazz, T impl, String kind) {
		assertClass(clazz);
		MatcherThreadHandler.assertEmpty();
		T mock = ProxyUtil.newProxy(clazz, new Mock(this, impl, kind, clazz.getSimpleName(), nextMockId()));
		mockData.put(mock, new MockData(clazz));
		return mock;
	}

	private <T> void assertClass(Class<T> clazz) {
		if (clazz == null) {
			throw StacktraceCleaner.cleanError(
					new IllegalArgumentException(
							"class can not be null"));
		}
	}

	private <T> void assertHandler(InvocationHandler handler) {
		if (handler == null) {
			throw StacktraceCleaner.cleanError(
					new IllegalArgumentException(
							"handler can not be null"));
		}
	}

	private String nextMockId() {
		return Integer.toString(nextMockId.incrementAndGet());
	}

	public InOrder verifyOrder() {
		MatcherThreadHandler.assertEmpty();
		return new InOrder(this);
	}

	public VerifyRangeStart verifyRange(int min, int max) {
		return new VerifyRangeStart(this, min, max);
	}

	public VerifyRangeStart verifyExactly(int count) {
		return verifyRange(count, count);
	}

	public VerifyRangeStart verifyNever() {
		return verifyExactly(0);
	}

	public VerifyRangeStart verifyOnce() {
		return verifyExactly(1);
	}

	public VerifyRangeStart verifyAtLeast(int min) {
		return verifyRange(min, Integer.MAX_VALUE);
	}

	public VerifyRangeStart verifyAtMost(int max) {
		return verifyRange(0, max);
	}
	
	public <T> T createProxy(T mock, InvocationHandler handler) {
		MatcherThreadHandler.assertEmpty();
		MockData data = getData(mock);
		Class<T> iface = data.getInterface();
		return ProxyUtil.newProxy(iface, handler);
	}

	public StubThrow stubThrow(Throwable e) {
		if (e == null) {
			throw StacktraceCleaner.cleanError(
					new IllegalArgumentException(
							"exception can not be null"));
		}
		return new StubThrow(this, e);
	}

	public StubReturn stubReturn(Object returnValue) {
		return new StubReturn(this, returnValue);
	}

	public StubAnswer stubAnswer(Answer answer) {
		if (answer== null) {
			throw StacktraceCleaner.cleanError(
					new IllegalArgumentException(
							"Answer can not be null"));
		}
		return new StubAnswer(this, answer);
	}

	public ListenerAdder listenWith(MethodCallListener listener) {
		if (listener == null) {
			throw StacktraceCleaner.cleanError(
					new IllegalArgumentException(
							"Listener can not be null"));
		}
		return new ListenerAdder(this, listener);
	}

	public <T> MockData<T> getData(T mock) {
		MockData data = mockData.get(mock);
		if (data == null) {
			throw StacktraceCleaner.cleanError(
					new IllegalArgumentException(
							"Argument " + mock + " is not a mocked object."));
		}
		return data;
	}

	public List<MethodCall> getCalls(Object mock) {
		return getData(mock).getCalls();
	}

	public int incrementSequence() {
		return nextSequenceNumber.incrementAndGet();
	}
}