package com.jivrus.jcp.boxjava.library.box.meta;

import java.io.InputStream;

import org.json.JSONObject;

import com.jivrus.jcp.boxjava.library.common.Descriptive;
import com.jivrus.jcp.boxjava.library.common.Descriptor;

public class MetaManager {
	private static final String path = "/schema/";
	public static Descriptive getMeta(String id) {
		Descriptive desc = null;
		String fileName = path + id + ".json";
		System.out.println("File: " + fileName);
		try {
			InputStream jsonStream = MetaManager.class.getResourceAsStream(fileName);
			byte [] bytes = jsonStream.readAllBytes();
			String json = new String(bytes);
			JSONObject jsonObj = new JSONObject(json);
			desc = new Descriptor(jsonObj.toMap());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return desc;
	}
   public static void main(String[] args) {
     System.out.println("Meta: " + getMeta("mysql"));
   }
}
