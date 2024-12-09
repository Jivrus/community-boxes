package com.jivrus.jcp.boxjava.box;

import java.util.Map;

import com.jivrus.jcp.boxjava.common.Descriptive;
import com.jivrus.jcp.boxjava.common.query.Result;

public interface BoxFunction extends Descriptive {

	Map<String, Object> getInput();

	Box getBox();

	BoxObject getBoxObject();

	Result execute(Map<String, Object> data) throws Exception;
}
