package hmap.core.util;

/**
 * Created by Xiao on 2017/5/27.
 */
import hmap.core.beans.TransferDataMapper;
import java.util.Map;

import hmap.core.hms.api.util.JSONAndMap;
import net.sf.json.JSONObject;

public class ExampleMapper extends TransferDataMapper {
    public ExampleMapper() {
    }

    public String requestDataMap(JSONObject params) {
        String xml = null;

        try {
            xml = JSONAndMap.jsonToXml(params.toString(), (String)null);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return xml;
    }

    public String responseDataMap(String params) {
        Map map = null;

        try {
            map = JSONAndMap.xml2map(params,null);
        } catch (Exception var4) {
            throw new RuntimeException();
        }

        JSONObject jsonObject = new JSONObject();
        if(map != null || map.size() > 0) {
            jsonObject = JSONObject.fromObject(map);
        }

        return jsonObject.toString();
    }
}
