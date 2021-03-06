package se.mockachino.verifier;

import se.mockachino.Invocation;
import se.mockachino.MethodCall;
import se.mockachino.Primitives;
import se.mockachino.matchers.matcher.Matcher;
import se.mockachino.util.MockachinoMethod;

import java.util.Comparator;
import java.util.List;

public class InvocationComparator implements Comparator<Invocation> {
	private final MockachinoMethod method;
	private final List<Matcher> argumentMatchers;

	public InvocationComparator(MockachinoMethod method, List<Matcher> argumentMatchers) {
		this.method = method;
		this.argumentMatchers = argumentMatchers;
	}

	@Override
	public int compare(Invocation c1, Invocation c2) {
		MethodCall o1 = c1.getMethodCall();
		MethodCall o2 = c2.getMethodCall();
		// First compare on names
		{
			int value = comp(nameMatches(o1), nameMatches(o2));
			if (value != 0) {
				return value;
			}
		}

		// Compare on method signature
		{
			int value = comp(methodMatches(o1), methodMatches(o2));
			if (value != 0) {
				return value;
			}
		}


		// Compare on actual arguments and types
		{
			Class[] parameters = method.getParameters();

			int i = 0;
			for (Matcher matcher : argumentMatchers) {
				int value = comp(matchNull(o1, i), matchNull(o2, i));
				if (value != 0) {
					return value;
				}

				value = comp(matchArg(o1, i, matcher), matchArg(o2, i, matcher));
				if (value != 0) {
					return value;
				}

				value = comp(matchType(o1, i, parameters), matchType(o1, i, parameters));
				if (value != 0) {
					return value;
				}

				i++;
			}
		}

		// Compare number of arguments
		int value = comp(compareLength(o1), compareLength(o2));
		if (value != 0) {
			return value;
		}

		// Compare number of arguments
		int len1 = getNumArgs(o1);
		int len2 = getNumArgs(o2);
		if (len1 < len2) {
			return -1;
		}
		if (len1 > len2) {
			return 1;
		}

		// Compare on argument hashcodes in order to put equal
		// argument lists next to each other

		return compareOrigin(c1, c2);

	}

	private int compareOrigin(Invocation c1, Invocation c2) {
		MethodCall o1 = c1.getMethodCall();
		MethodCall o2 = c2.getMethodCall();
		int n = getNumArgs(o1);
		Object[] args1 = o1.getArguments();
		Object[] args2 = o2.getArguments();
		for (int i = 0; i < n; i++) {
			long arg1Hash = getArgumentHashCode(args1[i]);
			long arg2Hash = getArgumentHashCode(args2[i]);
			long val = arg1Hash - arg2Hash;
			if (val != 0) {
				return Long.signum(val);
			}
		}
		int len1 = c1.getStacktrace().length;
		int len2 = c2.getStacktrace().length;
		if (len1 == 0 && len2 == 0) {
			return 0;
		}
		int val = comp(len1 > 0, len2 > 0);
		if (val != 0) {
			return val;
		}

		StackTraceElement element1 = c1.getStacktrace()[0];
		StackTraceElement element2 = c2.getStacktrace()[0];

		val = element1.getClassName().compareTo(element2.getClassName());
		if (val != 0) {
			return val;
		}

		val = element1.getMethodName().compareTo(element2.getMethodName());
		if (val != 0) {
			return val;
		}

		String file1 = element1.getFileName();
		String file2 = element2.getFileName();

		if (file1 == null && file2 == null) {
			return 0;
		}
		if (file1 == null) {
			return 1;
		}
		if (file2 == null) {
			return -1;
		}
		val = file1.compareTo(file2);
		if (val != 0) {
			return val;
		}

		return element1.getLineNumber() - element2.getLineNumber();
	}

	private int getArgumentHashCode(Object o) {
		if (o != null) {
			return o.hashCode();
		}
		return 0;
	}

	private boolean matchNull(MethodCall methodCall, int index) {
		Object[] args = methodCall.getArguments();
		if (index >= args.length) {
			return false;
		}
		return args[index] != null;
	}

	private boolean compareLength(MethodCall o1) {
		return getNumArgs(o1) == argumentMatchers.size();
	}

	private int getNumArgs(MethodCall o1) {
		return o1.getArguments().length;
	}

	private int comp(boolean first, boolean second) {
		if (first && !second) {
			return -1;
		} else if (second && !first) {
			return 1;
		}
		return 0;
	}

	private boolean matchType(MethodCall methodCall, int index, Class[] parameters) {
		Object[] args = methodCall.getArguments();
		if (index >= args.length) {
			return false;
		}
		Object obj = args[index];
		if (obj == null) {
			return false;
		}
		Class<?> realClass = Primitives.getRealClass(parameters[index]);
		return realClass.isInstance(obj);
	}

	private boolean matchArg(MethodCall methodCall, int index, Matcher matcher) {
		Object[] args = methodCall.getArguments();
		if (index >= args.length) {
			return false;
		}
		return matcher.matches(args[index]);
	}

	private boolean methodMatches(MethodCall o1) {
		return o1.getMethod().equals(method);
	}

	private boolean nameMatches(MethodCall o1) {
		return o1.getMethod().getName().equals(method.getName());
	}
}
