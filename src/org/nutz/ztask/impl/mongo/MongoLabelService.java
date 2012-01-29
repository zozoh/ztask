package org.nutz.ztask.impl.mongo;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.util.MCur;
import org.nutz.mongo.util.Moo;
import org.nutz.ztask.api.Label;
import org.nutz.ztask.api.LabelService;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 *
 */
public class MongoLabelService extends AbstractMongoService implements LabelService {

	public MongoLabelService(MongoConnector conn, String dbname) {
		super(conn, dbname);
	}

	@Override
	public Label get(String labelName) {
		return dao.findOne(Label.class, Moo.born("name", labelName));
	}

	@Override
	public Label remove(String labelName) {
		//TODO 无需先查询,直接执行remove即可
		Label l = get(labelName);
		if (null != l)
			dao.removeById(Label.class, l.getId());
		return l;
	}

	@Override
	public boolean hasLabel(String labelName) {
		return get(labelName) != null;
	}

	@Override
	public List<Label> getTopLabels() {
		return dao.find(Label.class, Moo.born("parent", null), MCur.born().asc("name"));
	}

	@Override
	public List<Label> getChildren(String labelName) {
		return dao.find(Label.class, Moo.born("parent", labelName), MCur.born().asc("name"));
	}

	@Override
	public List<Label> addIfNoExists(String... labelNames) {
		//TODO 使用findAndModify
		ArrayList<Label> list = new ArrayList<Label>(labelNames.length);
		for (String labelName : labelNames) {
			if (hasLabel(labelName))
				continue;
			Label l = new Label();
			l.setName(labelName);
			dao.save(l);
			list.add(l);
		}
		return list;
	}

	@Override
	public List<Label> moveTo(String parentName, String... labelNames) {
		ArrayList<Label> list = new ArrayList<Label>(labelNames.length);
		for (String labelName : labelNames) {
			Label l = get(labelName);
			if (null != parentName && Lang.equals(parentName, l.getParent()))
				continue;
			dao.updateById(Label.class, l.getId(), Moo.born().set("parent", parentName));
			list.add(l);
		}
		return list;
	}

	@Override
	public long count() {
		return dao.count(Label.class, null);
	}

}
