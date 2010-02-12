package se.mockachino.verifier;

import org.junit.Test;
import se.mockachino.Mockachino;
import se.mockachino.exceptions.VerificationError;

import java.util.List;

import static org.junit.Assert.fail;
import static se.mockachino.Mockachino.*;

public class VerifyHandlerTest {
	@Test
	public void testError() {
		try {
			List mock = mock(List.class);
			mock.add("Hello");
			mock.add("World");
			for (int i = 0; i < 20; i++) {
				mock.add(i);
			}

			verifyAtLeast(2).on(mock).add(3);
			fail("Should not succeed");
		} catch (VerificationError e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMatchOrdering() {
		try {
			List mock = mock(List.class);

			mock.remove(null);
			mock.add(null);
			mock.add("World"); // Should be #4
			mock.add(3); // Should be #3 - matches first argument
			mock.add(2, "Hello"); // Should be #2 - signature + matches second argument
			foo(mock); // Should be #1 - signature + matches first argument

			for (int i = 0; i < 20; i++) {
				mock.add(123);
			}

			verifyOnce().on(mock).add(3, "Hello");
			fail("Should not succeed");
		} catch (VerificationError e) {
			e.printStackTrace();
		}
	}

	private void foo(List mock) {
		mock.add(3, "World");
	}

	@Test
	public void testMatchNever() {
		try {
			List mock = mock(List.class);

			mock.add(3, "Hello");
			mock.add(3, "World");

			verifyNever().on(mock).add(3, "Hello");
			fail("Should not succeed");
		} catch (VerificationError e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMultipleInvocations() {
		List mock = Mockachino.mock(List.class);
		for (int i = 0; i < 100; i++) {
			mock.add("Hello");
			mock.add("Goodbye");
		}

		try {
			Mockachino.verifyOnce().on(mock).add("World");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMultipleInvocationGrouping() {
		List mock = Mockachino.mock(List.class);
		for (int i = 0; i < 100; i++) {
			mock.add("Hello");
			mock.add("Goodbye");
			mock.add("Hello");
			mock.add("Skipped");
			mock.add("World");
		}

		try {
			Mockachino.verifyAtLeast(200).on(mock).add("World");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
