package hmap.core.login.service.impl;


import hmap.core.hms.api.dto.HeaderAndLineDTO;
import hmap.core.hms.api.service.IApiService;
import hmap.core.login.service.ILoginService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved.
 * Project Name:HjingboParent
 * Package Name:hjingbo.core.login.service.impl
 * Date:2017/4/10
 * Create By:jiguang.sun@hand-china.com
 */
@Service
public class LoginServiceImpl implements ILoginService {

    private Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
    @Resource(
            name = "plsqlBean"
    )
    IApiService plsqlService;


    @Override
    public boolean authenticate(String username, String password, Map<String, String> params) {

        //把参数和登录（JBL_APP.LOGIN）封装成合适的格式直接调用plsqlService.invoke()
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("P_USERCODE", username);
        jsonObject.put("P_PASSWORD", password);
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("params", jsonObject);

        HeaderAndLineDTO headerAndLineDTO = new HeaderAndLineDTO();
        headerAndLineDTO.setIftUrl("JBL_APP.LOGIN");
        headerAndLineDTO.setInterfaceCode("jingboPLSQL");
        headerAndLineDTO.setLineCode("login");
        headerAndLineDTO.setEnableFlag("Y");
        headerAndLineDTO.setInterfaceType("PLSQL");
        headerAndLineDTO.setRequestMethod("GET");
        headerAndLineDTO.setRequestFormat("raw");

        String resp = plsqlService.invoke(headerAndLineDTO, jsonParams);
        String respStr = resp.toString().replaceAll("null", "\"\"");
        JSONObject loginResult = JSONObject.fromObject(respStr);

        logger.info("login result======:{}", loginResult);


        if (loginResult.containsKey("result") && "S".equals(loginResult.get("result"))) {
            return true;
        }else {
            return false;
        }



    }

    @Override
    public String getName() {
        return null;
    }
}
