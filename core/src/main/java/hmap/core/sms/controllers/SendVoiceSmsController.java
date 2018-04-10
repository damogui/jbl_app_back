package hmap.core.sms.controllers;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.system.controllers.BaseController;
import hmap.core.hms.api.dto.HeaderAndLineDTO;
import hmap.core.hms.api.service.IApiService;
import hmap.core.hms.api.service.IHmsHeaderService;
import hmap.core.sms.util.SmsHmapSender;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @User : daming.zhang@hand-china.com
 * @Date : 2017-07-21 12:12 PM
 * @Para : the method params
 * @Desc : 发送语音通知
 */

@Controller
public class SendVoiceSmsController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(SendVoiceSmsController.class);

    @Resource(
            name = "plsqlBean"
    )
    IApiService plsqlService;

    @Autowired
    IHmsHeaderService headerService;

    @ResponseBody
    @RequestMapping(value = "/api/sendVoiceSms", method = RequestMethod.POST)
    @Timed
    public JSONObject sendVoiceSms(HttpServletRequest request, @RequestBody Map params) throws Exception {

        //return
        JSONObject returnJson = new JSONObject();

        //定义变量
        String mobile = "";
        String operateType = "";
        String msg = "";
        String smsType = "VOICE";
        String ext = "";
        String sid = "";
        String result = "";
        String errmsg = "";
        String playtimes ="";

        //获取参数
        JSONObject para = JSONObject.fromObject(params);
        JSONObject data = para.getJSONObject("params");

        mobile = data.getString("mobile");
        operateType = data.getString("operateType");
        msg = data.getString("msg");
        ext = data.getString("ext");
        playtimes=data.getString("playtimes");

        //调用腾讯云发送短信
        returnJson = sendVoiceSms(data);

        sid = returnJson.getString("sid");
        result = returnJson.getString("result");
        errmsg = returnJson.getString("errmsg");

        //设置plsql函数请求参数
        HeaderAndLineDTO headerAndLineDTO = headerService.getHeaderAndLine("jingboPLSQL", "sendSms");

        JSONObject plsqlObject = new JSONObject();
        JSONObject plsqlParams = new JSONObject();
        JSONObject plsqlResult = new JSONObject();

        plsqlObject.put("p_mobile", mobile);
        plsqlObject.put("p_sms_type", smsType);
        plsqlObject.put("p_operate_type", operateType);
        plsqlObject.put("p_msg", msg);
        plsqlObject.put("p_ext", ext);
        plsqlObject.put("p_expire_time", "");
        plsqlObject.put("p_play_times",playtimes);
        plsqlObject.put("p_sid", sid);
        plsqlObject.put("p_result", result);
        plsqlObject.put("p_errmsg", errmsg);
        plsqlParams.put("params", plsqlObject);

        //调用PLSQL记录发送记录
        try {
            String str = "";
            str = plsqlService.invoke(headerAndLineDTO, plsqlParams);
            System.out.println(str);
            String respStr = str.toString().replaceAll("null", "\"\"");
            plsqlResult = JSONObject.fromObject(respStr);
        } catch (Exception e) {
            logger.error("发送消息失败:" + e.getMessage());
            plsqlResult.put("result", "E");
            plsqlResult.put("message", "发送消息异常" + e.getMessage());
        }

        return plsqlResult;

    }

    public JSONObject sendVoiceSms(JSONObject params) {

        JSONObject returnJson = new JSONObject();

        //设置发送参数
        JSONObject data = new JSONObject();
        data.put("appid", "1400034022");
        data.put("appkey", "a1aeb97b70424800eab74224b1990c47");
        data.put("nationCode", "86");
        data.put("phoneNumber", params.get("mobile"));
        data.put("msg", params.get("msg"));
        data.put("playTimes", params.get("playtimes"));
        data.put("ext", params.get("ext"));

        SmsHmapSender smsHmapSender = new SmsHmapSender();

        try {
            returnJson = JSONObject.fromObject(smsHmapSender.sendVoiceSms(data));
        } catch (Exception e) {
            logger.error("消息发送失败:" + e.getMessage());
            e.printStackTrace();
        }

        return returnJson;
    }
}
