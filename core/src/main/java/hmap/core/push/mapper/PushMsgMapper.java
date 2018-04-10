package hmap.core.push.mapper;

/**
 * @describe:
 * @author: guanqun.guo
 * @email: guanqun.guo@hand-china.com
 * @date: Create in 14:15 2017/6/5
 */
public interface PushMsgMapper {

    //通过手机号获得一卡通帐号
    String getUserCode(String phone);
}
