package interfaceApplication;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import JGrapeSystem.rMsg;
import Model.model;
import apps.appsProxy;
import database.DBHelper;
import database.db;
import security.codec;
import session.session;
import time.TimeHelper;

public class cRule {
	private String appid = appsProxy.appidString();
	private DBHelper cRule;
	private session se;
	private JSONObject userinfo = null;
	private String currentUserID = null;
	private model model = new model();

	public cRule() {
		cRule = new DBHelper(appsProxy.configValue().getString("db"), "cRule");
		se = new session();
		userinfo = se.getSession();
		if (userinfo != null && userinfo.size() > 0) {
			currentUserID = JSONObject.toJSON(userinfo.getString("_id")).getString("$oid");
		}
	}

	private db bind() {
		return cRule.bind(appid);
	}

	/**
	 * 添加规则
	 * 
	 * @project GrapeInfoCollection
	 * @package interfaceApplication
	 * @file CollectionService.java
	 * 
	 * @param ruleInfo
	 * @param Result
	 * @return 返回新增的规则id
	 *
	 */
	@SuppressWarnings("unchecked")
	protected String AddRule(String ruleInfo, String Result) {
		Object info = null;
		JSONObject object = JSONObject.toJSON(Result);
		JSONObject rules = JSONObject.toJSON(ruleInfo);
		JSONObject rule = new JSONObject();
		if (object != null && object.size() > 0) {
			if (Integer.parseInt(object.getString("errorcode")) == 0) {
				rule.put("userid", currentUserID);
				rule.put("time", TimeHelper.nowMillis());
//				rules = encodes(rules);
				rule.put("rule", (rules != null && rules.size() > 0) ? rules.toJSONString() : "");
				info = bind().data(rule).insertOnce();
			}
		}
		return info == null ? null : info.toString();
	}

	/**
	 * 解码
	 * 
	 * @project GrapeInfoCollection
	 * @package interfaceApplication
	 * @file cRule.java
	 * 
	 * @param rules
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	private JSONObject encodes(JSONObject rules) {
		if (rules != null && rules.size() > 0) {
			JSONObject startURL = JSONObject.toJSON(rules.getString("startURL"));
			if (startURL != null && startURL.size() > 0) {
				String baseURL = startURL.getString("baseURL");
				startURL.put("baseURL", codec.DecodeHtmlTag(baseURL));
				rules.put("startURL", startURL);
			}
		}
		return rules;
	}

	protected JSONObject find(String ruleInfo) {
		JSONObject temp, object = null;
		db db = bind();
		temp = JSONObject.toJSON(ruleInfo);
//		temp = encodes(temp);
		if (temp != null && temp.size() > 0) {
			object = db.eq("rule", temp.toJSONString()).eq("userid", currentUserID).find();
		}
		return object != null && object.size() > 0 ? object : null;
	}

	/**
	 * 删除规则
	 * 
	 * @project GrapeInfoCollection
	 * @package interfaceApplication
	 * @file cRule.java
	 * 
	 * @param rId
	 * @return
	 *
	 */
	public String RemoveRule(String ruleId) {
		// 删除规则相关的所有内容数据
		String result = new cInfo().delete(ruleId);
		// 删除规则
		if (JSONObject.toJSON(result).getString("errorcode").equals("0")) {
			db db = bind();
			String[] value = null;
			if (ruleId != null && !ruleId.equals("")) {
				value = ruleId.split(",");
				if (value != null) {
					db.or();
					for (String rId : value) {
						db.eq("_id", rId);
					}
				}
				long code = db.deleteAll();
				result = rMsg.netMSG(0, "删除成功");
			}
		}
		return result;
	}

	/**
	 * 查询所有规则信息
	 * 
	 * @project GrapeInfoCollection
	 * @package interfaceApplication
	 * @file CollectionService.java
	 * 
	 * @param idx
	 * @param pageSize
	 * @return
	 *
	 */
	public String ShowRule(int idx, int pageSize) {
		db db = bind();
		long total = 0, totalSize = 0;
		db.eq("userid", currentUserID);
		JSONArray array = db.dirty().desc("time").desc("_id").page(idx, pageSize);
		total = db.dirty().count();
		totalSize = db.pageMax(pageSize);
		return model.pageShow(array, total, totalSize, idx, pageSize);
	}

	@SuppressWarnings("unchecked")
	protected JSONObject findRule(String ruleId) {
		db db = bind();
		String[] value = null;
		JSONArray array = null;
		JSONObject object = new JSONObject();
		JSONObject temp;
		String id, rule;
		if (ruleId != null && !ruleId.equals("")) {
			value = ruleId.split(",");
			if (value != null) {
				db.or();
				for (String rId : value) {
					db.eq("_id", rId);
				}
				array = db.select();
			}
		}
		if (array != null && array.size() > 0) {
			for (Object obj : array) {
				temp = (JSONObject) obj;
				id = JSONObject.toJSON(temp.getString("_id")).getString("$oid");
				rule = temp.getString("rule");
				object.put(id, rule);
			}
		}
		return object;
	}
}
