package hmap.core.controllers;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.system.controllers.BaseController;
import hmap.core.hms.api.dto.HeaderAndLineDTO;
import hmap.core.hms.api.service.IApiService;
import hmap.core.hms.api.service.IHmsHeaderService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved.
 * Project Name:HjingboParent
 * Package Name:hjingbo.core.controllers
 * Date:2016/12/13
 * Create By:jiguang.sun@hand-china.com
 */

@Controller
public class JbUserController extends BaseController {

    @Resource(
            name = "plsqlBean"
    )
    IApiService plsqlService;
    @Autowired
    IHmsHeaderService headerService;

    private Logger logger = LoggerFactory.getLogger(JbUserController.class);

    @ResponseBody
    @RequestMapping(value = "/hms/api", method = RequestMethod.POST)
    @Timed
    public JSONObject updateUser(HttpServletRequest request, @RequestBody(required = false) JSONObject params) {

        String sysName = request.getParameter("sysName");
        String apiName = request.getParameter("apiName");
        logger.info("sysName:{}  apiName:{} ", sysName, apiName);
        HeaderAndLineDTO headerAndLineDTO = headerService.getHeaderAndLine(sysName, apiName);

        String resp = plsqlService.invoke(headerAndLineDTO, params);
        String respStr = resp.toString().replaceAll("null", "\"\"");
        JSONObject result = JSONObject.fromObject(respStr);
        
        return result;
    }


}
