package hmap.core.sign.controllers;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.system.controllers.BaseController;
import hmap.core.hms.api.dto.HeaderAndLineDTO;
import hmap.core.hms.api.service.IApiService;
import hmap.core.hms.api.service.IHmsHeaderService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Created with IntelliJ IDEA
 * @User : daming.zhang@hand-china.com
 * @Date : 2017-07-17
 * @Time : 12:20 PM
 * @Para : the method params
 * @Desc : To change this template use File | Settings | File Templates.
 */

@Controller
public class SignJudgeController extends BaseController {

    @Resource(
            name = "plsqlBean"
    )
    IApiService plsqlService;
    IHmsHeaderService   headerService;

    private Logger logger = LoggerFactory.getLogger(SignJudgeController.class);

    @ResponseBody
    @RequestMapping(value = "/api/signJudge", method = RequestMethod.POST)
    @Timed
    public JSONObject signJudge(HttpServletRequest request, @RequestBody JSONObject params){

        String sysName = request.getParameter("sysName");
        String apiName = request.getParameter("apiName");
        logger.info("sysName:{}  apiName:{} ", sysName, apiName);
        HeaderAndLineDTO headerAndLineDTO = headerService.getHeaderAndLine(sysName, apiName);

        String resp = plsqlService.invoke(headerAndLineDTO, params);
        String respStr = resp.toString().replaceAll("null", "\"\"");

        JSONObject result = JSONObject.fromObject(respStr);

        return  result;
    }
}
