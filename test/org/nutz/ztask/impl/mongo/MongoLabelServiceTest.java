package org.nutz.ztask.impl.mongo;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.ztask.ZTaskCase;
import org.nutz.ztask.api.Label;
import org.nutz.ztask.api.LabelService;

public class MongoLabelServiceTest extends ZTaskCase {

	private LabelService lbls;

	protected void onBefore() {
		lbls = this.getService(LabelService.class);
		lbls.dao().create(Label.class, true);
	}

	@Test
	public void test_simple_add_and_remove() {
		lbls.addIfNoExists("A", "B", "C");
		assertEquals(3, lbls.count());

		lbls.addIfNoExists("A", "B", "C", "E", "D");
		assertEquals(5, lbls.count());

		lbls.addIfNoExists("F", "G", "H");
		assertEquals(8, lbls.count());

		lbls.remove("H");
		assertEquals(7, lbls.count());

		List<Label> ls = lbls.getTopLabels();
		assertEquals(7, ls.size());
		assertEquals("A", ls.get(0).getName());
		assertEquals("B", ls.get(1).getName());
		assertEquals("C", ls.get(2).getName());
		assertEquals("D", ls.get(3).getName());
		assertEquals("E", ls.get(4).getName());
		assertEquals("F", ls.get(5).getName());
		assertEquals("G", ls.get(6).getName());

		lbls.moveTo("A", "B", "C");
		lbls.moveTo("C", "D");
		lbls.moveTo("C", "E", "F");

		ls = lbls.getTopLabels();
		assertEquals(2, ls.size());
		assertEquals("A", ls.get(0).getName());
		assertEquals("G", ls.get(1).getName());

		ls = lbls.getChildren("A");
		assertEquals(2, ls.size());
		assertEquals("B", ls.get(0).getName());
		assertEquals("C", ls.get(1).getName());

		ls = lbls.getChildren("C");
		assertEquals(3, ls.size());
		assertEquals("D", ls.get(0).getName());
		assertEquals("E", ls.get(1).getName());
		assertEquals("F", ls.get(2).getName());

		ls = lbls.getChildren("G");
		assertEquals(0, ls.size());
	}

}
