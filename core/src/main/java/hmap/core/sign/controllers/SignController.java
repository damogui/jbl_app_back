package hmap.core.sign.controllers;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import hmap.core.sign.dto.Sign;
import hmap.core.sign.service.ISignService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @User : daming.zhang@hand-china.com
 * @Date : 2017-07-21 12:12 PM
 * @Para : the method params
 * @Desc : 签到接口
 */
@RestController
@RequestMapping("/r/api")
public class SignController extends BaseController {

    @Autowired
    private ISignService iSignService;

    /**
     * 增加签到信息
     * @param request
     * @param sign
     * @return
     */
    @RequestMapping(value = "/addSignInfo", method = RequestMethod.POST)
    public JSONObject addSignInfo(HttpServletRequest request, @RequestBody(required = false) Sign sign) {
        JSONObject result = iSignService.addSignInfo(sign);

        return result;

    }

}
