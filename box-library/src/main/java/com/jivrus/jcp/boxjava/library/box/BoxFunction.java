package com.jivrus.jcp.boxjava.library.box;

import java.util.Map;

import com.jivrus.jcp.boxjava.library.common.Descriptive;
import com.jivrus.jcp.boxjava.library.common.query.Result;

public interface BoxFunction extends Descriptive {

	Map<String, Object> getInput();

	Box getBox();

	BoxObject getBoxObject();

	Result execute(Map<String, Object> data) throws Exception;
}
