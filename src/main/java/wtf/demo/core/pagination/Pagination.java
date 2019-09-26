package wtf.demo.core.pagination;

import java.io.Serializable;

/**
 * 分页参数
 * @author gongjf
 * @since 2019年6月13日 17:48:29
 */
public class Pagination implements Serializable {
    private static final long serialVersionUID = 1L;

    private int pageNo = 1;
    private int pageSize = 1;

    public Pagination() {}

    public Pagination(int pageNo, int pageSize) throws IllegalArgumentException {
        if (pageNo >= 1 && pageSize >= 1) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
        } else {
            throw new IllegalArgumentException("页码与每页记录数最小分别不能小于1");
        }
    }

    public int getFirstOffset() {
        return (this.pageNo - 1) * this.pageSize;
    }

    public int getLastOffset() {
        return this.pageNo * this.pageSize - 1;
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(int pageNo) throws IllegalArgumentException {
        if (pageNo < 1) {
            throw new IllegalArgumentException("页码最小不能小于1");
        } else {
            this.pageNo = pageNo;
        }
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) throws IllegalArgumentException {
        if (pageSize < 1) {
            throw new IllegalArgumentException("每页记录数最小不能小于1");
        } else {
            this.pageSize = pageSize;
        }
    }
}
