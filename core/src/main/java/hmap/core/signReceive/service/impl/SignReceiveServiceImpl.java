package hmap.core.signReceive.service.impl;


import hmap.core.hms.api.dto.HeaderAndLineDTO;
import hmap.core.hms.api.service.IApiService;
import hmap.core.sign.service.impl.SignServiceImpl;
import hmap.core.signReceive.dto.SignReceiveDTO;
import hmap.core.signReceive.service.ISignReceiveService;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @describe:
 * @author: guanqun.guo
 * @email: guanqun.guo@hand-china.com
 * @date: Create in 11:59 2017/6/1
 */
@Service
public class SignReceiveServiceImpl implements ISignReceiveService {
    private Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);
    @Resource(
            name = "plsqlBean"
    )
    IApiService plsqlService;

    @Override
    /**
     * @describe 签到接收通用接口
     * @author: guanqun.guo
     * @param:
     * @return
     * @Date: 8:58 2017/6/1
     */
    public JSONObject receive(SignReceiveDTO dto) {
        HeaderAndLineDTO headerAndLineDTO = new HeaderAndLineDTO();
        headerAndLineDTO.setIftUrl("JBL_APP.SIGN_TO_RECEIVE");
        headerAndLineDTO.setInterfaceCode("jingboPLSQL");
        headerAndLineDTO.setLineCode("login");
        headerAndLineDTO.setEnableFlag("Y");
        headerAndLineDTO.setInterfaceType("PLSQL");
        headerAndLineDTO.setRequestMethod("GET");
        headerAndLineDTO.setRequestFormat("raw");
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("p_signkey", dto.getSignKey());
        jsonObject.put("p_signSerial", dto.getSignSerial());
        jsonObject.put("p_phoneNum", dto.getPhoneNum());
        jsonObject.put("p_carNum", dto.getCarNum());
        jsonObject.put("p_enterType", dto.getEnterType());
        jsonObject.put("p_signMessage", dto.getSignMessage());
        jsonObject.put("p_manualFlag", dto.getManualFlag());

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("params", jsonObject);
        String str = "";
        JSONObject returnJson = new JSONObject();

        try {
            str = plsqlService.invoke(headerAndLineDTO,jsonParams);
            returnJson = JSONObject.fromObject(str);
        }catch (Exception e){
            returnJson.put("signSerial", dto.getSignSerial());
            returnJson.put("result", "E");
            returnJson.put("message", "请重新尝试："+e.getMessage());
        }

        return returnJson;
    }

}
