package org.nutz.ztask.api;

import java.util.Date;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;

import static org.junit.Assert.*;
import static org.nutz.ztask.api.TaskQuery.*;

public class TaskQueryTest {

	@Test
	public void test_by_stack() {
		TaskQuery tq;

		tq = NEW("S(A,B,$favo)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());
		ASTR(tq.qStacks(), "A", "B", "$favo");

	}

	@Test
	public void test_by_date_scope() {
		TaskQuery tq;

		tq = NEW("&D(2012-08-21)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope(), Lang.array(Times.D("2012-08-21"), Times.D("2012-08-21 23:59:59")));
		AS(tq.qStatus());
		ASTR(tq.qStacks());

		tq = NEW("&D(2012-01-09, 2012-05-06)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope(), Lang.array(Times.D("2012-01-09"), Times.D("2012-05-06 23:59:59")));
		AS(tq.qStatus());

		tq = NEW("&Dt(2012-01-09 14:56:12, 2012-05-06 08:32:19)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(	tq.qTimeScope(),
			Lang.array(Times.D("2012-01-09 14:56:12"), Times.D("2012-05-06 08:32:19")));
		AS(tq.qStatus());

	}

	@Test
	public void test_by_watchers() {
		TaskQuery tq;

		tq = NEW("F()");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());
		assertEquals(0, tq.qWatchers().length);

		tq = NEW("abc");
		assertEquals("abc", tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());
		assertNull(tq.qWatchers());

		tq = NEW("abc F()");
		assertEquals("abc", tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());
		assertEquals(0, tq.qWatchers().length);

		tq = NEW("abc F(a)");
		assertEquals("abc", tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());
		ASTR(tq.qWatchers(), "a");

		tq = NEW("abc F(aaa,bbb,ccd)");
		assertEquals("abc", tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());
		ASTR(tq.qWatchers(), "aaa", "bbb", "ccd");

	}

	@Test
	public void test_label_by_or() {
		TaskQuery tq;

		tq = NEW("#(aaa|bbb|ccc)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels(), "aaa", "bbb", "ccc");
		assertTrue(tq.qLabelsOr());
		AD(tq.qTimeScope());
		AS(tq.qStatus());

	}

	@Test
	public void test_label_with_color() {
		TaskQuery tq;

		tq = NEW("#( abc#FF0, xyz#CCC  )");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels(), "abc#FF0", "xyz#CCC");
		assertFalse(tq.qLabelsOr());
		AD(tq.qTimeScope());
		AS(tq.qStatus());

	}

	@Test
	public void test_simple_mixmode() {
		TaskQuery tq;

		tq = NEW("  ok @C( abc  )  ");
		assertEquals("ok", tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters(), "abc");
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());

		tq = NEW(" haha @(x,  y  ,z )  %(DONE,hungUP) ok @C( abc  )  #(A,B,C) &W(0) %REG%:.*");
		assertEquals("haha ok", tq.qText());
		assertEquals(".*", tq.qRegex().pattern());
		assertNull(tq.qID());
		ASTR(tq.qCreaters(), "abc");
		ASTR(tq.qOwners(), "x", "y", "z");
		ASTR(tq.qLabels(), "A", "B", "C");
		AD(tq.qTimeScope(), Times.week(0));
		AS(tq.qStatus(), TaskStatus.DONE, TaskStatus.HUNGUP);

	}

	@Test
	public void test_simple_case() {
		TaskQuery tq;

		tq = NEW("%(Ing)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus(), TaskStatus.ING);

		tq = NEW("%(Ing, new, DONE)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus(), TaskStatus.ING, TaskStatus.NEW, TaskStatus.DONE);

		tq = NEW("@C(abc)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters(), "abc");
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());

		tq = NEW("@C(abc,bcd)");
		assertNull(tq.qText());
		assertNull(tq.qID());
		assertNull(tq.qID());
		ASTR(tq.qCreaters(), "abc", "bcd");
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());

		tq = NEW("@(abc)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners(), "abc");
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());

		tq = NEW("@(abc,bcd)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners(), "abc", "bcd");
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());

		tq = NEW("#(x,y)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels(), "x", "y");
		AD(tq.qTimeScope());
		AS(tq.qStatus());

		tq = NEW("#(x)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels(), "x");
		AD(tq.qTimeScope());
		AS(tq.qStatus());

		tq = NEW("#()");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		assertEquals(0, tq.qLabels().length);
		AD(tq.qTimeScope());
		AS(tq.qStatus());

		tq = NEW("#( )");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		assertEquals(0, tq.qLabels().length);
		AD(tq.qTimeScope());
		AS(tq.qStatus());

		tq = NEW("&W(-1, 4)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope(), Times.weeks(-1, 4));
		AS(tq.qStatus());

		tq = NEW("&W(-1, 0)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope(), Times.weeks(-1, 0));
		AS(tq.qStatus());

		tq = NEW("&W(0)");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope(), Times.week(0));
		AS(tq.qStatus());

		tq = NEW("abc");
		assertEquals("abc", tq.qText());
		assertNull(tq.qRegex());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());

		tq = NEW("%REG%:abc");
		assertNull(tq.qText());
		assertEquals("abc", tq.qRegex().toString());
		assertNull(tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());

		tq = NEW("4f2d104a744ed795071e0367");
		assertNull(tq.qText());
		assertNull(tq.qRegex());
		assertEquals("4f2d104a744ed795071e0367", tq.qID());
		ASTR(tq.qCreaters());
		ASTR(tq.qOwners());
		ASTR(tq.qLabels());
		AD(tq.qTimeScope());
		AS(tq.qStatus());

	}

	private static void ASTR(String[] ss, String... expects) {
		if (0 == expects.length) {
			assertNull(ss);
			return;
		}
		assertEquals(expects.length, ss.length);
		for (int i = 0; i < ss.length; i++)
			assertEquals(expects[i], ss[i]);
	}

	private static void AS(TaskStatus[] tss, TaskStatus... expects) {
		if (0 == expects.length) {
			assertNull(tss);
			return;
		}
		assertEquals(expects.length, tss.length);
		for (int i = 0; i < tss.length; i++)
			assertEquals(expects[i], tss[i]);
	}

	private static void AD(Date[] scope) {
		AD(scope, null);
	}

	private static void AD(Date[] scope, Date[] expects) {
		if (null == expects) {
			assertNull(scope);
			return;
		}
		assertEquals(expects[0].getTime() / 1000, scope[0].getTime() / 1000);
		assertEquals(expects[1].getTime() / 1000, scope[1].getTime() / 1000);
	}
}
