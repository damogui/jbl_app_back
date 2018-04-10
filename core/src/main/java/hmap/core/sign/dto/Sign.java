package hmap.core.sign.dto;

import com.hand.hap.system.dto.BaseDTO;

/**
 * Created by Xiao on 2017/6/1.
 */
public class Sign extends BaseDTO {
    private String p_phonenum;
    private String p_carnum;
    private String p_signstate;
    private String p_signkey;
    private String p_signtype;
    private String p_latitude;
    private String p_longitude;
    private String p_insertuser;
    private String p_signserial;

    public String getP_phonenum() {
        return p_phonenum;
    }

    public void setP_phonenum(String p_phonenum) {
        this.p_phonenum = p_phonenum;
    }

    public String getP_carnum() {
        return p_carnum;
    }

    public void setP_carnum(String p_carnum) {
        this.p_carnum = p_carnum;
    }

    public String getP_signstate() {
        return p_signstate;
    }

    public void setP_signstate(String p_signstate) {
        this.p_signstate = p_signstate;
    }

    public String getP_signkey() {
        return p_signkey;
    }

    public void setP_signkey(String p_signkey) {
        this.p_signkey = p_signkey;
    }

    public String getP_signtype() {
        return p_signtype;
    }

    public void setP_signtype(String p_signtype) {
        this.p_signtype = p_signtype;
    }

    public String getP_latitude() {
        return p_latitude;
    }

    public void setP_latitude(String p_latitude) {
        this.p_latitude = p_latitude;
    }

    public String getP_longitude() {
        return p_longitude;
    }

    public void setP_longitude(String p_longitude) {
        this.p_longitude = p_longitude;
    }

    public String getP_insertuser() {
        return p_insertuser;
    }

    public void setP_insertuser(String p_insertuser) {
        this.p_insertuser = p_insertuser;
    }

    public String getP_signserial() {
        return p_signserial;
    }

    public void setP_signserial(String p_signserial) {
        this.p_signserial = p_signserial;
    }
}
