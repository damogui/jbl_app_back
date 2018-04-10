package hmap.core.sign.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import hmap.core.sign.dto.Sign;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by Xiao on 2017/6/1.
 */
public interface ISignService extends IBaseService<Sign>,ProxySelf<ISignService> {


    /**
     * 增加司机签到信息
     * @param signList
     * @return
     */
    JSONObject addSignInfo(Sign signList);


}
