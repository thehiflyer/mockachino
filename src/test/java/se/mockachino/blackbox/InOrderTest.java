package se.mockachino.blackbox;

import org.junit.Test;
import se.mockachino.Mockachino;
import se.mockachino.matchers.Matchers;
import se.mockachino.order.OrderingContext;

import java.util.List;

import static org.junit.Assert.fail;

public class InOrderTest {
	@Test
	public void testVerifyWithZero() {
		List mock = Mockachino.mock(List.class);

		OrderingContext order = Mockachino.newOrdering();
		order.verifyAtLeast(0).on(mock).add("Hello world");
	}

	@Test
	public void testInOrder() {
		List mock = Mockachino.mock(List.class);
		mock.add("World");
		mock.add("Hello");
		mock.remove("Hello");
		mock.add("World");

		Mockachino.verifyExactly(1).on(mock).add("Hello");

		OrderingContext order = Mockachino.newOrdering();
		order.verify().on(mock).add("Hello");
		order.verify().on(mock).add("World");

		OrderingContext order2 = Mockachino.newOrdering();
		order2.verify().on(mock).add("Hello");
		order2.verify().on(mock).remove(Matchers.type(Object.class));
	}

	@Test
	public void testWithAtleast() {
		List mock = Mockachino.mock(List.class);
		mock.add("World");
		mock.add("Hello");
		mock.add("Hello");
		mock.add("World");

		OrderingContext order = Mockachino.newOrdering();
		order.verify().on(mock).add("World");
		order.verifyAtLeast(2).on(mock).add("Hello");
		order.verify().on(mock).add("World");
	}

	@Test
	public void testInOrderFail() {
		List mock = Mockachino.mock(List.class);
		mock.add("Hello");
		mock.remove("Hello");
		mock.add("World");

		try {
			OrderingContext order = Mockachino.newOrdering();
			order.verify().on(mock).add("World");
			order.verify().on(mock).add("Hello");
			fail("Expected out of order calls to fail");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInOrderFailAtLeast() {
		List mock = Mockachino.mock(List.class);
		mock.add("World");
		mock.add("Hello");
		mock.add("Hello");

		try {
			OrderingContext order = Mockachino.newOrdering();
			order.verify().on(mock).add("World");
			order.verifyAtLeast(3).on(mock).add("Hello");
			fail("Expected out of order calls to fail");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInOrderFailNoFirst() {
		List mock = Mockachino.mock(List.class);
		mock.add("World");
		mock.add("Hello");
		mock.add("Hello");

		try {
			OrderingContext order = Mockachino.newOrdering();
			order.verify().on(mock).add("Foo");
			fail("Expected out of order calls to fail");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInOrderVerifyZero() {
		List mock = Mockachino.mock(List.class);
		OrderingContext order = Mockachino.newOrdering();
		order.verifyAtLeast(0).on(mock).add("Foo");
	}
}
