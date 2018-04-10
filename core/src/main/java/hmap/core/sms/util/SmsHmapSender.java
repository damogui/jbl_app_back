package hmap.core.sms.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Created with IntelliJ IDEA
 * @User : daming.zhang@hand-china.com
 * @Date : 2017-07-19
 * @Time : 4:53 PM
 * @Para : the method params
 * @Desc : To change this template use File | Settings | File Templates.
 */
public class SmsHmapSender {

    private Logger logger = LoggerFactory.getLogger(SmsHmapSender.class);

    public Map sendTextSms(Map params) throws Exception {

        ArrayList<String> sendParams = new ArrayList<>();

        JSONObject data = new JSONObject(params);
        //JSONObject data = result.getJSONObject("params");

        int appid = data.getInt("appid");
        System.out.print(appid);
        String appkey = data.getString("appkey");
        String nationCode = data.getString("nationCode");
        String phoneNumber = data.getString("phoneNumber");
        int tplId = data.getInt("tplId");
        String msg = data.getString("msg");
        String expireTime = data.getString("expireTime");
        String sign = data.getString("sign");
        String extend = data.getString("extend");
        String ext = data.getString("ext");

        sendParams.add(msg);
        sendParams.add(expireTime);

        SmsSingleSender singleSender = new SmsSingleSender(appid, appkey);
        JSONObject singleSenderResultPara = new JSONObject();

        try {
            singleSenderResultPara = singleSender.sendWithParam(nationCode, phoneNumber, tplId, sendParams,sign, extend, ext);
        } catch (IOException e) {
            logger.error("发送短信失败:" + e.getMessage());
            e.printStackTrace();
        }

        logger.info("return result :{}", singleSenderResultPara);

        System.out.print(singleSenderResultPara.get("result"));

        Map returnMap = new HashMap();

        try {
            returnMap.put("result",singleSenderResultPara.get("result"));
            returnMap.put("errmsg",singleSenderResultPara.get("errmsg"));
            returnMap.put("ext",singleSenderResultPara.get("ext"));
            returnMap.put("sid",singleSenderResultPara.get("sid"));
            returnMap.put("fee",singleSenderResultPara.get("fee"));
        }
        catch(Exception e){
            logger.error("发送短信失败:" + e.getMessage());
            returnMap.put("ext","");
            returnMap.put("sid","");
        }
        return returnMap;

    }

    public Map sendVoiceSms(Map params) throws Exception {


        JSONObject data = new JSONObject(params);
        //JSONObject data = result.getJSONObject("params");

        int appid = data.getInt("appid");
        System.out.print(appid);
        String appkey = data.getString("appkey");
        String nationCode = data.getString("nationCode");
        String phoneNumber = data.getString("phoneNumber");
        String msg = data.getString("msg");
        Integer playTimes = data.getInt("playTimes");
        String ext = data.getString("ext");

        SmsVoicePromptSender smsVoicePromtSender = new SmsVoicePromptSender(appid, appkey);
        JSONObject singleSenderResultPara = new JSONObject();

        try {
            singleSenderResultPara = smsVoicePromtSender.send(nationCode, phoneNumber, 2, playTimes, msg, ext);
            System.out.println(singleSenderResultPara);
        }catch (IOException e)
        {
            logger.error("发送语音失败:" + e.getMessage());
            e.printStackTrace();
        }

        logger.info("return result :{}", singleSenderResultPara);

        System.out.print(singleSenderResultPara.get("result"));

        Map returnMap = new HashMap();
        try {
            returnMap.put("result",singleSenderResultPara.get("result"));
            returnMap.put("errmsg",singleSenderResultPara.get("errmsg"));
            returnMap.put("ext",singleSenderResultPara.get("ext"));
            returnMap.put("sid",singleSenderResultPara.get("callid"));
        }
        catch(Exception e){
            logger.error("发送语音失败:" + e.getMessage());
            returnMap.put("ext","");
            returnMap.put("sid","");
        }

        return returnMap;

    }


}
