package hmap.core.signReceive.controllers;

import hmap.core.push.controllers.PushMsgController;
import hmap.core.push.service.IPushMsgService;
import hmap.core.push.util.Jdpush;
import hmap.core.signReceive.dto.SignReceiveDTO;
import hmap.core.signReceive.service.ISignReceiveService;
import hmap.core.sms.controllers.SendTextSmsController;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @describe:
 * @author: guanqun.guo 
 * @email: guanqun.guo@hand-china.com
 * @date: Create in 11:59 2017/6/1
 */
@Controller
@RequestMapping(value = "/r/api")
public class SignReceiveController {

    private Logger logger = LoggerFactory.getLogger(SignReceiveController.class);

    @Autowired
    private ISignReceiveService signReceiveService;

    @Autowired
    private IPushMsgService iPushMsgService;
    String str = "";

    /**
     * @return
     * @describe 签到接收通用接口
     * @author: guanqun.guo
     * @param:
     * @Date: 8:57 2017/6/1
     */
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject selectList(HttpServletRequest request, @RequestBody(required = false) JSONObject params) {
        SignReceiveDTO dto = new SignReceiveDTO();
        //从龙口京港获得参数传递到数据库
        JSONObject result = JSONObject.fromObject(params);
        JSONObject data = result.getJSONObject("params");
        //返回给龙口京港
        JSONObject returnJson = new JSONObject();

        dto.setSignKey((String) data.get("signKey"));
        dto.setSignSerial((String) data.get("signSerial"));
        dto.setPhoneNum((String) data.get("phoneNum"));
        dto.setCarNum((String) data.get("carNum"));
        dto.setEnterType((String) data.get("enterType"));
        dto.setSignMessage((String) data.get("signMessage"));
        dto.setManualFlag((String) data.get("manualFlag"));

        JSONObject  pushParams = signReceiveService.receive(dto);

        if ("S".equals(pushParams.get("result"))) {

            pushParams.put("phoneNum", data.get("phoneNum"));

            logger.info("pushParams :{}", pushParams);

            //调用极光推送接口
            JSONObject pushResult = JSONObject.fromObject(pushMsg(pushParams));

            returnJson.put("signSerial", pushResult.get("signSerial"));
            returnJson.put("responseStatus", pushResult.get("result"));
            returnJson.put("responseMsg", pushResult.get("message"));

        } else {

            returnJson.put("signSerial", pushParams.get("signSerial"));
            returnJson.put("responseStatus", pushParams.get("result"));
            returnJson.put("responseMsg", pushParams.get("message"));

        }

        return returnJson;
    }

    public Map pushMsg(JSONObject params) {

        logger.info("Ipush params:{}", params);
        String status = "S";
        String messgae = "消息推送成功！";
        JSONObject data = JSONObject.fromObject(params);
        //JSONObject data = result.getJSONObject("params");

        String phoneNum = data.getString("phoneNum");
        String pushMessage = data.getString("pushMessage");
        str = iPushMsgService.addMessage(phoneNum, pushMessage);
        System.out.println(str);

        //传递信息的map
        Map pushMap = new HashMap();
        pushMap.put("phoneNum", phoneNum);
        pushMap.put("msg", pushMessage);
        pushMap.put("title", "签到消息！");

        try {
            Jdpush jdpush = new Jdpush();
            jdpush.sendPush(pushMap);
        } catch (Exception e) {
            status = "E";
            messgae = "消息推送失败，请稍后再试！";
        }

        Map returnMap = new HashMap();
        returnMap.put("phoneNum", phoneNum);
        returnMap.put("result", status);
        returnMap.put("message", messgae);
        logger.info("Push Return returnMap :{}", returnMap);
        return returnMap;

    }
}
