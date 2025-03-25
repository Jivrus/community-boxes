package com.jivrus.jcp.boxjava.manager;

import com.jivrus.jcp.boxjava.connector.odoo.OdooConnector;
import com.jivrus.jcp.boxjava.library.box.meta.MetaManager;
import com.jivrus.jcp.boxjava.library.common.Descriptive;
import com.jivrus.jcp.boxjava.library.manager.Connector;

public class ConnectorManager {

	public static Connector getConnector(String connectorId) throws Exception {
		Descriptive desc = MetaManager.getMeta(connectorId);
		if (desc == null) throw new Exception("Could not find meta data for connector: " + connectorId);

		Connector connector = null;
		System.out.println("TESTING PRODUCT MANAGER");
		if (connectorId.contains("odoo-")) {
			connector = (Connector) new OdooConnector(desc);
		}
		return connector;
	}

	// public static CObject getObject(Connector connector) {
	// 	return new DatabaseObject(new Descriptor(connector.getAll()), connector);
	// }
}
