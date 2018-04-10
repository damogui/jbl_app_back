package hmap.core.signReceive.service;



import hmap.core.signReceive.dto.SignReceiveDTO;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * @describe:
 * @author: guanqun.guo
 * @email: guanqun.guo@hand-china.com
 * @date: Create in 11:59 2017/6/1
 */
public interface ISignReceiveService {
    /**
     * @return
     * @describe 签到接收通用接口
     * @author: guanqun.guo
     * @param:
     * @Date: 8:57 2017/6/1
     */
    JSONObject receive(SignReceiveDTO dto);

}

