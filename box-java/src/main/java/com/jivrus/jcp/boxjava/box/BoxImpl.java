
package com.jivrus.jcp.boxjava.box;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.jivrus.jcp.boxjava.common.Descriptive;
import com.jivrus.jcp.boxjava.common.Descriptor;
import com.jivrus.jcp.boxjava.connector.CObject;
import com.jivrus.jcp.boxjava.connector.Connector;
import com.jivrus.jcp.boxjava.connector.ConnectorManager;
import com.jivrus.jcp.boxjava.util.Encryptor;

public class BoxImpl extends Descriptor implements Box {
	private static Logger logger = Logger.getLogger(BoxImpl.class.getName());
	private Connector connector = null;
	
	private Map<String, Object> config = new HashMap<>();

	public BoxImpl(Map<String, Object> descriptor) {
		super(descriptor);
		try {
			connector = ConnectorManager.getConnector(getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public List<Descriptive> getObjects(String pattern) {
		List <Descriptive> result = null;
		try {
			result =  connector.getObjects(pattern);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Connector getConnector() {
		return connector;
	}

	@Override
	public BoxObject getObject(String id) {
		BoxObject result = null;
		try {
			CObject object =  connector.getObject(id);
			result = new BoxObjectImpl(object.getAll(), this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}


	@Override
	public Map<String, Object> getTemplates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List <Descriptive> getConfigParameters(String type) {
		return connector.getConnectionParameters(null);
	}

	@Override
	public String setConfig(Map<String, Object> config) throws Exception {
		this.config = config;
		// try {
			connector.connect(this.getId(), (Map<String, Object>) config.get("config"), null);
		// } catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// throw e;
		// }
		return this.getEncryptedConfig();
	}

	@Override
	public String getEncryptedConfig() {
		String configStr = new JSONObject(this.config).toString();
		String cipherText = null;
		try {
			cipherText = Encryptor.encrypt("aes", Map.of("text", configStr));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// logger.info(String.format("------> Config Str: %s cipherText: %s", configStr, cipherText));
		return cipherText;
	}

	@Override
	public void setEncryptedConfig(String cipher) throws Exception {
		String text = "";
		try {
			text = Encryptor.decrypt("aes", Map.of("cipherText", cipher));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.printf("Cipher: %s and text %s", cipher, text);
		Map<String, Object> config = new JSONObject(text).toMap();
		this.setConfig(config);
	}

	@Override
	public Descriptive createBoxObject(String name, Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BoxFunction> getFunctions(String pattern) {
		List <Descriptive> descs = connector.getFunctions();
		List<BoxFunction> funs = descs.stream().map(desc -> new BoxFunctionImpl(desc.getAll(), this)).collect(Collectors.toList());
		return funs;
	}
	
	@Override
	public BoxFunction getFunction(String id, Map<String, Object> options) {
		BoxFunction fun = null;
		List <Descriptive> descs = connector.getFunctions();
		Iterator<Descriptive> iterator = descs.iterator();
		while (iterator.hasNext()) {
			Descriptive desc = iterator.next();
			if (desc.getId().equals(id)) {
				Map<String, Object> map = new HashMap<>(desc.getAll()); // To make it mutable
				map.put("options", options);
				fun = new BoxFunctionImpl(map, this);
				break;
			}
		}
		return fun;
	}

}
