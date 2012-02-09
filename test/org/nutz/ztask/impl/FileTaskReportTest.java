package org.nutz.ztask.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.nutz.lang.Files;

public class FileTaskReportTest {

	@Test
	public void test_simple_report_parsing() throws IOException {
		String path = "~/tmp/ztask/unit/reports/ztask/2012/w06_02-06.txt";
		File f = Files.createFileIfNoExists(path);
		FileTaskReport rpt = new FileTaskReport(f);
		assertEquals("2012.w06.02-06", rpt.getFullName());
	}

}
