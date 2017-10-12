package interfaceApplication;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Model.model;
import apps.appsProxy;
import database.DBHelper;
import database.db;
import session.session;
import string.StringHelper;

public class cObject {
	private String appid = appsProxy.appidString();
	private DBHelper cInfo;
	private session se;
	private JSONObject userinfo = null;
	private String currentUserID = null;

	public cObject() {
		cInfo = new DBHelper(appsProxy.configValue().getString("db"), "cObject");
		se = new session();
		userinfo = se.getSession();
		if (userinfo != null && userinfo.size() > 0) {
			currentUserID = JSONObject.toJSON(userinfo.getString("_id")).getString("$oid");
		}
	}

	private db bind() {
		return cInfo.bind(appid);
	}

	@SuppressWarnings("unchecked")
	protected String AddObject(JSONArray array) {
		db db = bind();
		String cid = "";
		Object temp = null;
		JSONObject objs = new JSONObject();
		JSONObject object = new JSONObject();
		if (array != null && array.size() > 0) {
			for (Object obj : array) {
				object = (JSONObject) obj;
				if (object != null && object.size() > 0) {
					objs.put("content", object.toJSONString());
					objs.put("userid", currentUserID);
					temp = db.data(objs).insertOnce();
					if (temp != null) {
						cid += temp + ",";
					}
				}
			}
		}
		return StringHelper.fixString(cid, ',');
	}

	@SuppressWarnings("unchecked")
	protected JSONObject findObject(String infoId) {
		String[] value = null;
		JSONArray array = null;
		JSONObject temp, object = null, content = null;
		String ids;
		db db = bind();
		if (infoId != null && !infoId.equals("")) {
			value = infoId.split(",");
			if (value != null) {
				db.or();
				for (String id : value) {
					db.eq("_id", id);
				}
				array = db.field("content").select();
			}
		}
		if (array != null && array.size() > 0) {
			object = new JSONObject();
			for (Object obj : array) {
				temp = (JSONObject) obj;
				ids = JSONObject.toJSON(temp.getString("_id")).getString("$oid");
				content = JSONObject.toJSON(temp.getString("content"));
				object.put(ids, (content == null) ? new JSONObject() : content);
			}
		}
		return object;
	}
}
