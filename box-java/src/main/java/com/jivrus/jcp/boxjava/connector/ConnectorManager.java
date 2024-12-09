package com.jivrus.jcp.boxjava.connector;

import com.jivrus.jcp.boxjava.box.meta.MetaManager;
import com.jivrus.jcp.boxjava.common.Descriptive;
import com.jivrus.jcp.boxjava.connector.odoo.OdooConnector;

public class ConnectorManager {

	public static Connector getConnector(String connectorId) throws Exception {
		Descriptive desc = MetaManager.getMeta(connectorId);
		if (desc == null) throw new Exception("Could not find meta data for connector: " + connectorId);

		Connector connector = null;

		if (connectorId.contains("odoo-")) {
			connector = new OdooConnector(desc);
		}
		return connector;
	}

	// public static CObject getObject(Connector connector) {
	// 	return new DatabaseObject(new Descriptor(connector.getAll()), connector);
	// }
}
