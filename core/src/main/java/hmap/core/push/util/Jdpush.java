package hmap.core.push.util;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Jdpush {
    protected Logger LOG = LoggerFactory.getLogger(Jdpush.class);

    // demo App defined in resources/jpush-api.conf 
	private   String appKey ="8cc0ec299e2eb845c9ca616c";
	private  String masterSecret = "05c12df35de93f7aa03dd4ac";
	
	public  void sendPush(Map map) throws Exception{
		ClientConfig clientConfig = ClientConfig.getInstance();
        JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, clientConfig);
        PushPayload payload =buildPushObject_all_alias_alert(map);
       // try {
            PushResult result = jpushClient.sendPush(payload);
            int code=result.getResponseCode();
            System.out.println(result.getOriginalContent()+"11"+result.getResponseCode()
            +"333"+result.isResultOK()+"444"+result.getRateLimitReset()); 
            LOG.info("Got result - " + result);
            System.out.println(result);
            //把异常抛到外面去捕获
        /*} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
        }*/
	}
	public  PushPayload buildPushObject_all_alias_alert(Map map) {
		 /*JsonObject jsonExtra = new JsonObject();*/
	       /* jsonExtra.addProperty("extra1", 1);
	        jsonExtra.addProperty("extra2", false);*/
	        Map<String, String> extras = new HashMap<String, String>();
	        extras.put("id", "1");
	        return PushPayload.newBuilder()
	                .setPlatform(Platform.all())
	                .setAudience(Audience.alias((String)map.get("phoneNum")))
	                .setNotification(Notification.newBuilder()
	                        .setAlert((String)map.get("msg"))//推送的内容
	                        .addPlatformNotification(AndroidNotification.newBuilder()
	                                .setTitle((String)map.get("title"))//推送的标题
	                                .addExtras(extras)
	                                .build())
	                        .addPlatformNotification(IosNotification.newBuilder()
	                                .incrBadge(1)
	                                .addExtra("id", "1").build())
	                        .build())
	                .build();
	    }



}
