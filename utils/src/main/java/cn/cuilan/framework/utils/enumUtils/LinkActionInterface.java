package cn.cuilan.framework.utils.enumUtils;

public interface LinkActionInterface {

    int normalVal = 0;
    String normalStr = "正常";

    int updateVal = 1;
    String updateStr = "已更改";

    int deleteVal = 2;
    String deleteStr = "被删除(根据ID)";

    int poolVal = 3;
    String poolStr = "新热榜互换状态";

    int deleteByDomainVal = 4;
    String deleteByDomainStr = "被删除(根据DOMAIN)";

    int sensitiveWordVal = 5;
    String sensitiveWordStr = "包含敏感词";


}
