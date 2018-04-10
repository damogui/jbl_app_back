package hmap.core.sign.mapper;

import com.hand.hap.mybatis.common.Mapper;
import hmap.core.sign.dto.Sign;

/**
 * Created by Xiao on 2017/6/1.
 */
public interface SignMapper extends Mapper<Sign> {

    //获取签到序列
    String getNextSignKey();

}
