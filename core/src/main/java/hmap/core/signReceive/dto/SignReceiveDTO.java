package hmap.core.signReceive.dto;

import javax.persistence.Table;

/**
 * @describe:
 * @author: guanqun.guo
 * @email: guanqun.guo@hand-china.com
 * @date: Create in 11:59 2017/6/1
 */
@Table(name = "JB_APP_SIGN")
public class SignReceiveDTO {
    public String signKey;//签到序列
    public String signSerial;//签到流水
    public String phoneNum;//手机号码
    public String carNum;//车牌号
    public String enterType;//是否可以到港/入场
    public String signMessage;//签到信息
    public String manualFlag;//是否人工

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public String getSignSerial() {
        return signSerial;
    }

    public void setSignSerial(String signSerial) {
        this.signSerial = signSerial;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getEnterType() {
        return enterType;
    }

    public void setEnterType(String enterType) {
        this.enterType = enterType;
    }

    public String getSignMessage() {
        return signMessage;
    }

    public void setSignMessage(String signMessage) {
        this.signMessage = signMessage;
    }

    public String getManualFlag() {
        return manualFlag;
    }

    public void setManualFlag(String manualFlag) {
        this.manualFlag = manualFlag;
    }
}
