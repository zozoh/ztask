package org.nutz.ztask;

import static org.junit.Assert.*;
import static org.nutz.ztask.ZTaskCase.*;

import java.util.Date;

import org.junit.Test;

public class ZTasksTest {

	@Test
	public void test_d() {
		Date d = new Date(System.currentTimeMillis());
		assertEquals(ZTasks.now().getTime() / 1000, ZTasks.D(ZTasks.D(d)).getTime() / 1000);
	}

	@Test
	public void test_ztask_weeks() {
		long base = ZTasks.D("2012-02-06 17:35:12").getTime();

		AD("2012-02-05 00:00:00", "2012-02-11 23:59:59", ZTasks.week(base, 0));
		AD("2012-01-29 00:00:00", "2012-02-04 23:59:59", ZTasks.week(base, -1));
		AD("2012-01-22 00:00:00", "2012-01-28 23:59:59", ZTasks.week(base, -2));
		AD("2012-02-12 00:00:00", "2012-02-18 23:59:59", ZTasks.week(base, 1));
		AD("2012-02-19 00:00:00", "2012-02-25 23:59:59", ZTasks.week(base, 2));

		AD("2012-01-22 00:00:00", "2012-02-11 23:59:59", ZTasks.weeks(base, -2, 0));
		AD("2012-01-22 00:00:00", "2012-02-25 23:59:59", ZTasks.weeks(base, -2, 2));

		// 测测跨年
		base = ZTasks.D("2012-01-04 17:35:12").getTime();

		AD("2011-12-25 00:00:00", "2012-01-14 23:59:59", ZTasks.weeks(base, 1, -1));
	}

}
