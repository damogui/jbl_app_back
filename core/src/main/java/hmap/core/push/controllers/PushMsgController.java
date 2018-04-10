package hmap.core.push.controllers;

import hmap.core.push.service.IPushMsgService;
import hmap.core.push.util.Jdpush;
import hmap.core.sms.controllers.SendTextSmsController;
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
 * @date: Create in 11:04 2017/6/5
 */

@Controller
@RequestMapping(value = "/r/api")
public class PushMsgController {

    private Logger logger = LoggerFactory.getLogger(SendTextSmsController.class);

    @Autowired
    private IPushMsgService iPushMsgService;
    String str = "";
    @RequestMapping(value = "/push", method = RequestMethod.POST)
    public @ResponseBody Map pushMsg(HttpServletRequest request, @RequestBody(required = false) JSONObject params) {

        String responseStatus = "S";
        String responseMsg = "推送成功！";
        JSONObject result = JSONObject.fromObject(params);
        JSONObject data = result.getJSONObject("params");

        String phoneNum=(String)data.get("phoneNum");
        String carNum = (String)data.get("carNum");
        String pushMessage = (String)data.get("pushMessage");
        str = iPushMsgService.addMessage(phoneNum,pushMessage);
        System.out.println(str);
        /*try {
            userCode = iPushMsgService.getUserCode(phone);
            System.out.print(userCode);
            if (userCode == null) {
                responseStatus = "E";
                responseMsg = "错误，该手机号不存在账户！消息推送失败！";
            }

        } catch (Exception e) {
            responseStatus = "E";
            responseMsg = "错误，手机号查出多个账户！消息推送失败！";
        }*/

        //传递信息的map
        Map pushMap = new HashMap();
        pushMap.put("phoneNum", phoneNum);
        pushMap.put("msg", pushMessage);
        pushMap.put("title", "智能调度提醒");

        try {
            Jdpush jdpush = new Jdpush();
            jdpush.sendPush(pushMap);
        } catch (Exception e) {
            responseStatus = "E";
            responseMsg = "错误，消息推送失败！";
        }

        Map returnMap = new HashMap();
        returnMap.put("phoneNum",phoneNum);
        returnMap.put("carNum", carNum);
        returnMap.put("responseStatus", responseStatus);
        returnMap.put("responseMsg", responseMsg);

        return returnMap;
    }


}

