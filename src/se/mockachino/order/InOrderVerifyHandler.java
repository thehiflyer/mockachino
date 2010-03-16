package se.mockachino.order;

import se.mockachino.MethodCall;
import se.mockachino.Mockachino;
import se.mockachino.exceptions.VerificationError;
import se.mockachino.matchers.MethodMatcher;
import se.mockachino.util.MockachinoMethod;
import se.mockachino.verifier.BadUsageBuilder;
import se.mockachino.verifier.BadUsageHandler;
import se.mockachino.verifier.MatchingHandler;
import se.mockachino.verifier.Reporter;

public class InOrderVerifyHandler extends MatchingHandler {
    public static final BadUsageHandler BAD_USAGE_HANDLER = new BadUsageHandler(
            new BadUsageBuilder(
                    "Incorrect usage. You can not chain calls when verifying a deep mock. " +
                    "You probably used verify().on(mock).method1().method2(). " +
                    "Correct usage is verify().on(mock.method1()).method2()"));

	private final Iterable<MethodCall> calls;
	private final int min;
	private final OrderingContext orderingContext;

    public InOrderVerifyHandler(OrderingContext orderingContext, Object mock, Iterable<MethodCall> calls, int min) {
		super("InOrderVerifyHandler", mock.toString(), BAD_USAGE_HANDLER);
		this.orderingContext = orderingContext;
		this.calls = calls;
		this.min = min;
	}

	@Override
	public void match(Object o, MockachinoMethod method, MethodMatcher matcher) {
		if (min <= 0) {
			return;
		}

		MethodCall lastCall = orderingContext.getCurrentCall();
		int lastCallNumber = lastCall.getCallNumber();

		int count = 0;

		for (MethodCall call : calls) {
			int number = call.getCallNumber();

			// Skip already visited calls
			if (number <= lastCallNumber) {
				continue;
			}

			if (matcher.matches(call)) {
				count++;
				if (count >= min) {
					orderingContext.setCurrent(call);
					return;
				}
			}
		}
		String errorMessage = new Reporter(count, min, Integer.MAX_VALUE).getErrorLine();
		errorMessage += "\nMethod pattern:\n"  + matcher.toString() + "\n";
		if (lastCallNumber > 0) {
			errorMessage += "\nAfter:\n" + lastCall + "\n" + lastCall.getStackTraceString();
		}
		throw new VerificationError(errorMessage);
	}
}
