package wtf.demo.core.pagination;

import java.io.Serializable;

/**
 * 排序参数
 * @author gongjf
 * @since 2019年6月13日 17:48:29
 */
public class Sortable implements Serializable {
    private static final long serialVersionUID = 6505288541338596959L;

    private String sortField;

    private String sortDirect;

    public Sortable() {}

    public Sortable(String fieldName) {
        this.sortField = fieldName == null || fieldName.length()==0 ? "create_time" : fieldName;
    }

    public Sortable(String fieldName, String direction) {
        this.sortField = fieldName == null || fieldName.length()==0 ? "create_time" : fieldName;
        this.sortDirect = direction == null || direction.length()==0 ? "desc" : direction;
    }

    public String getSortField() {
        return sortField == null || sortField.length()==0 ? "create_time" : sortField;
    }

    public void setSortField(String sortField) {
        if(sortDirect == null || sortDirect.length() == 0) this.sortField = "create_time";
        this.sortField = sortField;
    }

    public String getSortDirect() {
        return sortDirect == null || sortDirect.length()==0 ? "desc" : sortDirect;
    }

    public void setSortDirect(String sortDirect) {
        if(sortDirect == null || sortDirect.length() == 0) this.sortDirect = "desc";
        else if(!sortDirect.contains("desc,asc")) {
            this.sortDirect = "desc";
        }
        this.sortDirect = sortDirect;
    }

}
