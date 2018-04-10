package hmap.core.push.service.impl;

import hmap.core.hms.api.dto.HeaderAndLineDTO;
import hmap.core.hms.api.service.IApiService;
import hmap.core.push.service.IPushMsgService;
import hmap.core.sign.service.impl.SignServiceImpl;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @describe:
 * @author: guanqun.guo
 * @email: guanqun.guo@hand-china.com
 * @date: Create in 14:12 2017/6/5
 */
@Service
public class PushMsgServiceImpl implements IPushMsgService {
    private Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);
    @Resource(
            name = "plsqlBean"
    )
    IApiService plsqlService;
    	/**
    		 * @describe: 通过手机号获得一卡通帐号
    		 * @author:  guanqun.guo
    		 * @Date:    10:41 2017/6/7
    		 */
    @Override
    public String addMessage(String phone,String message) {
        HeaderAndLineDTO headerAndLineDTO = new HeaderAndLineDTO();
        headerAndLineDTO.setIftUrl("JBL_APP.ADD_MESSAGE");
        headerAndLineDTO.setInterfaceCode("jingboPLSQL");
        headerAndLineDTO.setLineCode("login");
        headerAndLineDTO.setEnableFlag("Y");
        headerAndLineDTO.setInterfaceType("PLSQL");
        headerAndLineDTO.setRequestMethod("GET");
        headerAndLineDTO.setRequestFormat("raw");
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("p_phone",phone);
        jsonObject.put("p_message",message);
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("params", jsonObject);
        String str = "";
        str = plsqlService.invoke(headerAndLineDTO,jsonParams);
        return str;
    }
}
