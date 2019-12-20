package cn.cuilan.ssmp.utils.enumUtils;

public interface PhoneRecordTypeInterface {

    int bindValue = 1;
    int unbindValue = 2;
    int updateValue = 3;

    String bindStr = "绑定";
    String unbindStr = "解绑";
    String updateStr = "更换";

    default String getStr(Integer value) {
        switch (value) {
            case bindValue:
                return bindStr;
            case unbindValue:
                return unbindStr;
            case updateValue:
                return updateStr;
            default:
                return unbindStr;
        }
    }
}
