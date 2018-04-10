package hmap.core.push.service;

/**
 * @describe:
 * @author: guanqun.guo
 * @email: guanqun.guo@hand-china.com
 * @date: Create in 14:08 2017/6/5
 */
public interface IPushMsgService {
    	/**
    		 * @describe: 
    		 * @author:  guanqun.guo
    		 * @param:   phone: 手机号
    		 * @return:  String
    		 * @Date:    14:11 2017/6/5
    		 */
    String addMessage(String phone,String message);
}
