package cn.cuilan.utils;

import com.github.pagehelper.Page;
import lombok.Data;

import java.util.List;

@Data
public class PageInfo {

    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private Integer pages;
    private List<?> data;

    public PageInfo(Page data) {
        this.pageNum = data.getPageNum();
        this.pageSize = data.getPageSize();
        this.total = data.getTotal();
        this.pages = data.getPages();
        this.data = data;
    }
}
