package Model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import JGrapeSystem.rMsg;

public class model {
	@SuppressWarnings("unchecked")
	public String pageShow(JSONArray array, long total, long totalSize, int idx, int pageSize) {
		JSONObject objects = new JSONObject();
		JSONObject object = new JSONObject();
		object.put("data", array);
		object.put("total", total);
		object.put("totalSize", totalSize);
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		objects.put("records", object);
		return resultMessage(object);
	}

	public String resultMessage(JSONObject object) {
		if (object == null || object.size() <= 0) {
			object = new JSONObject();
		}
		return rMsg.netMSG(0, object);
	}

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONArray array) {
		JSONObject object = new JSONObject();
		if (array == null || array.size() <= 0) {
			array = new JSONArray();
		}
		object.put("records", array);
		return resultMessage(object);
	}
}
