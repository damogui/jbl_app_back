package hmap.core.sign.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import hmap.core.beans.JDBCSqlSessionFactory;
import hmap.core.hms.api.dto.HeaderAndLineDTO;
import hmap.core.hms.api.service.IApiService;
import hmap.core.hms.api.service.IHmsHeaderService;
import hmap.core.sign.dto.Sign;
import hmap.core.sign.mapper.SignMapper;
import hmap.core.sign.service.ISignService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @User : daming.zhang@hand-china.com
 * @Date : 2017-07-21 12:12 PM
 * @Para : the method params
 * @Desc : 龙口签到处理程序
 */
@Service
public class SignServiceImpl extends BaseServiceImpl<Sign> implements ISignService {

    private Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);
    @Resource(
            name = "plsqlBean"
    )
    IApiService plsqlService;

    @Resource(name = "restBean"
    )

    IApiService lkRestService;

    @Autowired
    IHmsHeaderService headerService;

    @Autowired
    private SignMapper signMapper;

    @Autowired
    private JDBCSqlSessionFactory hrmsSqlSessionFactory;

    @Override
    public JSONObject addSignInfo(Sign sign) {

        //返回参数
        JSONObject signResult = new JSONObject();
        //jdbc查询获取签到序列
        String executeSql = "select jb_app_sign_s.nextval from dual";
        String SignKeyNext;

        try {

            SignKeyNext = hrmsSqlSessionFactory.getJdbcTemplateObject().queryForObject(executeSql, new SingleColumnRowMapper<>()).toString();

            //如果签到类型为1，则重新获取签到序列
            String signKey = "1".equals(sign.getP_signtype()) ? SignKeyNext : sign.getP_signkey();

            //龙口请求地址
            HeaderAndLineDTO headerAndLineDTO = headerService.getHeaderAndLine("jingboPLSQL", "addDriverSign");

            //plsql后台请求
            HeaderAndLineDTO lkHeaderAndLineDTO = headerService.getHeaderAndLine("LKService", "AppSign");

            //请求龙口京港参数
            JSONObject lkJsonObject = new JSONObject();
            lkJsonObject.put("phoneNum", sign.getP_phonenum());
            lkJsonObject.put("carNum", sign.getP_carnum());
            lkJsonObject.put("signKey", signKey);
            lkJsonObject.put("signType", sign.getP_signtype());

            //记录签到信息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("p_phonenum", sign.getP_phonenum());
            jsonObject.put("p_carnum", sign.getP_carnum());
            jsonObject.put("p_signstate", sign.getP_signstate());
            jsonObject.put("p_signkey", signKey);
            jsonObject.put("p_signtype", sign.getP_signtype());
            jsonObject.put("p_latitude", sign.getP_latitude());
            jsonObject.put("p_longitude", sign.getP_longitude());
            jsonObject.put("p_insertuser", sign.getP_insertuser());
            jsonObject.put("p_signserial", "");

            JSONObject jsonParams = new JSONObject();
            if ("4".equals(sign.getP_signstate())) {
                //提交龙口签到请求
                jsonObject.put("p_signserial", sign.getP_signserial());
                jsonObject.put("p_signstatus", "S");
                jsonObject.put("p_signmessage", "确认入场");
            } else {
                //提交龙口签到请求
                JSONObject lkJsonParams = new JSONObject();
                lkJsonParams.put("params", lkJsonObject);
                String lkStr;
                logger.info("lkJsonParams======:{}", lkJsonParams);
                lkStr = lkRestService.invoke(lkHeaderAndLineDTO, lkJsonParams);

                System.out.println(lkStr);
                //String lkRespStr = lkStr.toString().replaceAll("", "\"\"");
                JSONObject lkSignResult = JSONObject.fromObject(lkStr);
                jsonObject.put("p_signserial", (lkSignResult.get("signSerial") == null ? signKey : lkSignResult.get("signSerial")));
                jsonObject.put("p_signstatus", lkSignResult.get("responseStatus"));
                jsonObject.put("p_signmessage", lkSignResult.get("responseMsg"));
            }

            jsonParams.put("params", jsonObject);
            String str ;

            //调用PLSQL记录签到信息
            str = plsqlService.invoke(headerAndLineDTO, jsonParams);
            System.out.println(str);
            String respStr = str.toString().replaceAll("null", "\"\"");
            signResult = JSONObject.fromObject(respStr);

        } catch (Exception e) {
            this.logger.error(e.getMessage());
            signResult.put("result","E");
            signResult.put("message","签到异常，请重新尝试");
        }

        return signResult;

    }


}
