package se.mockachino.order;

import org.junit.Test;
import se.mockachino.MockContext;
import se.mockachino.Mockachino;
import se.mockachino.exceptions.UsageError;
import se.mockachino.exceptions.VerificationError;

import java.util.List;

import static org.junit.Assert.fail;
import static se.mockachino.Mockachino.between;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.newOrdering;

public class MockPointTest {
	@Test
	public void testSimpleMockPoint() {
		List<String> mock = mock(List.class);

		mock.add("A");
		mock.add("B");
		mock.add("C");

		OrderingContext context = newOrdering();

		context.verify().on(mock).add("A");
		MockPoint first = context.afterLastCall();

		context.verify().on(mock).add("C");
		MockPoint last = context.beforeLastCall();

		between(first, last).verifyNever().on(mock).add("A");

		between(first, last).verifyOnce().on(mock).add("B");
		try {
			between(first, last).verifyNever().on(mock).add("B");
			fail("Should fail");
		} catch (VerificationError e) {
			e.printStackTrace();
		}

		between(first, last).verifyNever().on(mock).add("C");
	}

	@Test
	public void testUsageFail() {
		MockContext context = new MockContext();
		MockPoint first = context.newOrdering().afterLastCall();
		MockPoint last = context.newOrdering().afterLastCall();

		List<String> mock = mock(List.class);
		mock.add("A");
		try {
			between(first, last).verifyAtLeast(1).on(mock).add("A");
			fail("Should fail");
		} catch (UsageError e) {
            e.printStackTrace();
		}
	}

	@Test
	public void testMockPoint() {
		List mock = mock(List.class);

		mock.add("B");
		mock.add("A");
		mock.add("B");
		mock.add("C");
		mock.add("B");
		mock.add("D");

		OrderingContext context = newOrdering();

		context.verify().on(mock).add("A");
		MockPoint first = context.atLastCall();

		context.verify().on(mock).add("C");
		MockPoint last = context.atLastCall();

		Mockachino.between(first, last).verifyOnce().on(mock).add("A");
		Mockachino.between(first, last).verifyOnce().on(mock).add("B");
		Mockachino.between(first, last).verifyOnce().on(mock).add("C");
		Mockachino.between(first, last).verifyNever().on(mock).add("D");
	}

	@Test
	public void testMockPoint2() {
		List mock = mock(List.class);

		mock.add("B");
		mock.add("A");
		mock.add("B");
		mock.add("C");
		mock.add("B");
		mock.add("D");

		OrderingContext context = newOrdering();

		context.verify().on(mock).add("A");
		MockPoint first = context.afterLastCall();

		context.verify().on(mock).add("C");
		MockPoint last = context.beforeLastCall();

		Mockachino.between(first, last).verifyNever().on(mock).add("A");
		Mockachino.between(first, last).verifyOnce().on(mock).add("B");
		Mockachino.between(first, last).verifyNever().on(mock).add("C");
	}

}