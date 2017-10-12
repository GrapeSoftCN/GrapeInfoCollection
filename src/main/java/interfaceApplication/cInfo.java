package interfaceApplication;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import JGrapeSystem.rMsg;
import Model.model;
import apps.appsProxy;
import database.DBHelper;
import database.db;
import session.session;
import time.TimeHelper;

public class cInfo {
	private String appid = appsProxy.appidString();
	private DBHelper cInfo;
	private session se;
	private JSONObject userinfo = null;
	private String currentUserID = null;
	private model model = new model();

	public cInfo() {
		cInfo = new DBHelper(appsProxy.configValue().getString("db"), "cInfo");
		se = new session();
		userinfo = se.getSession();
		if (userinfo != null && userinfo.size() > 0) {
			currentUserID = JSONObject.toJSON(userinfo.getString("_id")).getString("$oid");
		}
	}

	private db bind() {
		return cInfo.bind(appid);
	}

	/**
	 * 根据规则查询内容信息
	 * 
	 * @project GrapeInfoCollection
	 * @package interfaceApplication
	 * @file cInfo.java
	 * 
	 * @param rId
	 * @param idx
	 * @param pageSize
	 * @return
	 *
	 */
	public String findByRIDs(String rId, int idx, int pageSize) {
		long total = 0, totalSize = 0;
		JSONArray array = null;
		db db = bind();
		if (rId != null && !rId.equals("")) {
			db.eq("userid", currentUserID).eq("ruleId", rId);
			array = db.dirty().desc("time").desc("_id").page(idx, pageSize);
			total = db.dirty().count();
			totalSize = db.pageMax(pageSize);
		}
		return model.pageShow(fillRule(array), total, totalSize, idx, pageSize);
	}

	/**
	 * 根据规则查询内容信息
	 * 
	 * @project GrapeInfoCollection
	 * @package interfaceApplication
	 * @file cInfo.java
	 * 
	 * @param rId
	 * @param idx
	 * @param pageSize
	 * @return
	 *
	 */
	public String findByRID(String rId) {
		JSONArray array = null;
		db db = bind();
		if (rId != null && !rId.equals("")) {
			db.eq("userid", currentUserID).eq("ruleId", rId);
			array = db.desc("time").desc("_id").select();
		}
		return model.resultMessage(fillRule(array));
	}

	/**
	 * 内容数据中填充规则信息
	 * 
	 * @project GrapeInfoCollection
	 * @package interfaceApplication
	 * @file cInfo.java
	 * 
	 * @param array
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	private JSONArray fillRule(JSONArray array) {
		String rid = "", temp = "", ruleId;
		JSONObject tempobj;
		if (array != null && array.size() > 0) {
			for (Object obj : array) {
				tempobj = (JSONObject) obj;
				temp = tempobj.getString("ruleId");
				if (!temp.equals("")) {
					rid += temp + ",";
				}
			}
			JSONObject ruleObj = new cRule().findRule(rid);
			int l = array.size();
			for (int i = 0; i < l; i++) {
				tempobj = (JSONObject) array.get(i);
				ruleId = tempobj.getString("ruleId");
				if (ruleObj != null && ruleObj.size() > 0) {
					temp = ruleObj.getString(ruleId);
				}
				tempobj.put("ruleInfo", temp);
				array.set(i, tempobj);
			}
		}
		return fillContent(array);
	}

	/**
	 * 内容数据中填充规则信息
	 * 
	 * @project GrapeInfoCollection
	 * @package interfaceApplication
	 * @file cInfo.java
	 * 
	 * @param array
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	private JSONArray fillContent(JSONArray array) {
		String cid = "", temp = "", contentid = "";
		JSONObject tempobj, objs;
		String[] value = null;
		JSONArray arrays = new JSONArray();
		if (array != null && array.size() > 0) {
			for (Object obj : array) {
				tempobj = (JSONObject) obj;
//				temp = JSONObject.toJSON(tempobj.getString("_id")).getString("$oid");
				temp = tempobj.getString("content");
				if (!temp.equals("")) {
					cid += temp + ",";
				}
			}
			JSONObject contentObj = new cObject().findObject(cid);
			int l = array.size();
			if (contentObj != null && contentObj.size() > 0) {
				for (int i = 0; i < l; i++) {
					tempobj = (JSONObject) array.get(i);
					contentid = tempobj.getString("content");
					if (!contentid.equals("")) {
						value = contentid.split(",");
						if (value != null) {
							for (String string : value) {
								objs = JSONObject.toJSON(contentObj.getString(string));
								arrays.add((objs != null && objs.size() > 0) ? objs : new JSONObject());
							}
						}
					}
					tempobj.put("content", arrays.toJSONString());
					array.set(i, tempobj);
				}
			}
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	private JSONObject fillRule(JSONObject object, String rid) {
		String ruleId, ruleInfo = "";
		if (object != null && object.size() > 0) {
			JSONObject ruleObj = new cRule().findRule(rid);
			ruleId = object.getString("ruleId");
			if (ruleObj != null && ruleObj.size() > 0) {
				ruleInfo = ruleObj.getString(ruleId);
			}
			object.put("ruleInfo", ruleInfo);
		}
		return fillContent(object);
	}

	@SuppressWarnings("unchecked")
	private JSONObject fillContent(JSONObject object) {
		String cId = "";
		String[] value = null;
		JSONObject objs = new JSONObject();
		JSONArray arrays = new JSONArray();
		cId = object.getString("content");
		if (!cId.equals("")) {
			JSONObject contentObj = new cObject().findObject(cId);
			if (contentObj != null && contentObj.size() > 0) {
				value = cId.split(",");
				if (value != null) {
					for (String string : value) {
						objs = JSONObject.toJSON(contentObj.getString(string));
						arrays.add((objs != null && objs.size() > 0) ? objs : new JSONObject());
					}
				}
			}
		}
		object.put("content", arrays.toJSONString());
		return object;
	}

	/**
	 * 新增内容
	 * 
	 * @project GrapeInfoCollection
	 * @package interfaceApplication
	 * @file cInfo.java
	 * 
	 * @param Contentarray
	 * @param ruleId
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	protected String AddContent(JSONArray Contentarray, String ruleInfo, String result) {
		// 添加规则到表中
		String ruleId = new cRule().AddRule(ruleInfo, result);
		db db = bind();
		Object info = null;
		JSONObject object = null;
		long time = TimeHelper.nowMillis(); // 添加时间
		String content = new cObject().AddObject(Contentarray);
		JSONObject contentInfo = new JSONObject();
		contentInfo.put("ruleId", ruleId);
		// contentInfo.put("content", (Contentarray != null &&
		// Contentarray.size() > 0) ? Contentarray.toJSONString() : "");
		contentInfo.put("content", content);
		contentInfo.put("userid", currentUserID);
		contentInfo.put("time", time);
		info = db.data(contentInfo).insertOnce();
		if (info != null) {
			object = find(info.toString());
		}
		return model.resultMessage(object);
	}

	/**
	 * 查询内容详细信息，内部使用
	 * 
	 * @project GrapeInfoCollection
	 * @package interfaceApplication
	 * @file cInfo.java
	 * 
	 * @param cId
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	protected JSONObject find(String cId) {
		JSONObject object = null;
		String ruleInfo = "";
		if (cId != null && !cId.equals("")) {
			object = cInfo.eq("_id", cId).find();
		}
		if (object != null && object.size() > 0) {
			String rId = object.getString("ruleId");
			// 获取规则信息
			JSONObject ruleInfos = new cRule().findRule(rId);
			if (ruleInfos != null && ruleInfos.size() > 0) {
				ruleInfo = ruleInfos.getString(rId);
			}
			object.put("ruleInfo", ruleInfo);
		}
		return (object != null && object.size() > 0) ? fillContent(object) : null;
	}

	protected String searchByRID(String RId) {
		JSONObject object = null;
		object = bind().eq("ruleId", RId).eq("userid", currentUserID).find();
		return model.resultMessage(fillRule(object, RId));
	}

	/**
	 * 根据规则删除内容信息
	 * 
	 * @project GrapeInfoCollection
	 * @package interfaceApplication
	 * @file cInfo.java
	 * 
	 * @param rId
	 * @return
	 *
	 */
	protected String delete(String rId) {
		String result = rMsg.netMSG(99, "删除失败");
		db db = bind();
		String[] value = null;
		if (rId != null && !rId.equals("")) {
			value = rId.split(",");
			if (value != null) {
				db.or();
				for (String ruleId : value) {
					db.eq("ruleId", ruleId);
				}
				long code = db.deleteAll();
				result = rMsg.netMSG(0, "删除成功");
			}
		}
		return result;
	}
}
