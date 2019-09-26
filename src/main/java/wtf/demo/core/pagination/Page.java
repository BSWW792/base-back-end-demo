package wtf.demo.core.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 数据页
 * @author gongjf
 * @since 2019年6月13日 17:48:29
 */
public class Page<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int start;
    private int pageSize;
    private List<T> result;
    private int totalCount;

    public Page() {
        this.result = new ArrayList();
    }

    public Page(int start, int totalSize, int pageSize, List<T> result) {
        this.pageSize = pageSize;
        this.start = start;
        this.totalCount = totalSize;
        this.result = result;
    }

    public Page(int start, long totalSize, int pageSize, List<T> result) {
        this.pageSize = pageSize;
        this.start = start;
        this.totalCount = Long.valueOf(totalSize).intValue();
        this.result = result;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public int getTotalPageCount() {
        if (this.totalCount == 0) {
            return 1;
        } else {
            return this.totalCount % this.pageSize == 0 ? this.totalCount / this.pageSize : this.totalCount / this.pageSize + 1;
        }
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getStart() {
        return this.start;
    }

    public List<T> getResult() {
        return this.result;
    }

    public Iterator<T> iteratorResult() {
        return this.result.iterator();
    }

    public T[] arrayResult() {
        return (T[]) this.result.toArray();
    }

    public int getResultSize() {
        return this.result.size();
    }

    public int getCurrentPageNo() {
        return this.start / this.pageSize + 1;
    }

    public boolean hasNextPage() {
        return this.getCurrentPageNo() < this.getTotalPageCount() - 1;
    }

    public boolean hasPreviousPage() {
        return this.getCurrentPageNo() > 1;
    }

    public static int getStartOfPage(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
