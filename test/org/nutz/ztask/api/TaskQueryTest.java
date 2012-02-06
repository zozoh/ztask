package org.nutz.ztask.api;

import java.util.Date;

import org.junit.Test;
import org.nutz.ztask.ZTasks;

import static org.junit.Assert.*;
import static org.nutz.ztask.api.TaskQuery.*;

public class TaskQueryTest {

	@Test
	public void test_simple_mixmode() {
		TaskQuery tq;

		tq = NEW("  ok @C( abc  )  ");
		assertEquals("ok", tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASS(tq.qCreaters(), "abc");
		ASS(tq.qOwners());
		ASS(tq.qLabels());
		ATS(tq.qTimeScope());

		tq = NEW(" haha @(x,  y  ,z ) ok @C( abc  )  #(A,B,C) &W(0) %REG%:.*");
		assertEquals("haha ok", tq.qText());
		assertEquals(".*", tq.qRegex().pattern());
		assertNull(tq.qID());
		ASS(tq.qCreaters(), "abc");
		ASS(tq.qOwners(), "x", "y", "z");
		ASS(tq.qLabels(), "A", "B", "C");
		ATS(tq.qTimeScope(), ZTasks.week(0));

	}

	@Test
	public void test_simple_case() {
		TaskQuery tq;

		tq = NEW("@C(abc)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASS(tq.qCreaters(), "abc");
		ASS(tq.qOwners());
		ASS(tq.qLabels());
		ATS(tq.qTimeScope());

		tq = NEW("@C(abc,bcd)");
		assertNull(tq.qText());
		assertNull(tq.qID());
		assertNull(tq.qID());
		ASS(tq.qCreaters(), "abc", "bcd");
		ASS(tq.qOwners());
		ASS(tq.qLabels());
		ATS(tq.qTimeScope());

		tq = NEW("@(abc)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASS(tq.qCreaters());
		ASS(tq.qOwners(), "abc");
		ASS(tq.qLabels());
		ATS(tq.qTimeScope());

		tq = NEW("@(abc,bcd)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASS(tq.qCreaters());
		ASS(tq.qOwners(), "abc", "bcd");
		ASS(tq.qLabels());
		ATS(tq.qTimeScope());

		tq = NEW("#(x,y)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASS(tq.qCreaters());
		ASS(tq.qOwners());
		ASS(tq.qLabels(), "x", "y");
		ATS(tq.qTimeScope());

		tq = NEW("#(x)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASS(tq.qCreaters());
		ASS(tq.qOwners());
		ASS(tq.qLabels(), "x");
		ATS(tq.qTimeScope());

		tq = NEW("&W(-1, 4)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASS(tq.qCreaters());
		ASS(tq.qOwners());
		ASS(tq.qLabels());
		ATS(tq.qTimeScope(), ZTasks.weeks(-1, 4));

		tq = NEW("&W(-1, 0)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASS(tq.qCreaters());
		ASS(tq.qOwners());
		ASS(tq.qLabels());
		ATS(tq.qTimeScope(), ZTasks.weeks(-1, 0));

		tq = NEW("&W(0)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASS(tq.qCreaters());
		ASS(tq.qOwners());
		ASS(tq.qLabels());
		ATS(tq.qTimeScope(), ZTasks.week(0));

		tq = NEW("abc");
		assertEquals("abc", tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASS(tq.qCreaters());
		ASS(tq.qOwners());
		ASS(tq.qLabels());
		ATS(tq.qTimeScope());

		tq = NEW("%REG%:abc");
		assertNull(tq.qText());
		assertEquals("abc", tq.qRegex().toString());
		assertNull(tq.qID());
		ASS(tq.qCreaters());
		ASS(tq.qOwners());
		ASS(tq.qLabels());
		ATS(tq.qTimeScope());

		tq = NEW("4f2d104a744ed795071e0367");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertEquals("4f2d104a744ed795071e0367", tq.qID());
		ASS(tq.qCreaters());
		ASS(tq.qOwners());
		ASS(tq.qLabels());
		ATS(tq.qTimeScope());

	}

	private static void ASS(String[] ss, String... expects) {
		if (0 == expects.length) {
			assertNull(ss);
			return;
		}
		assertEquals(expects.length, ss.length);
		for (int i = 0; i < ss.length; i++)
			assertEquals(expects[i], ss[i]);
	}

	private static void ATS(Date[] scope) {
		ATS(scope, null);
	}

	private static void ATS(Date[] scope, Date[] expects) {
		if (null == expects) {
			assertNull(scope);
			return;
		}
		assertEquals(expects[0].getTime() / 1000, scope[0].getTime() / 1000);
		assertEquals(expects[1].getTime() / 1000, scope[1].getTime() / 1000);
	}
}
