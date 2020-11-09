package org.jeecg.modules.excelConfig.util;

import java.util.List;
import java.util.Map;

public class NodeBean {

    private String id;
    private String pid;
    private String text;
    private String value;
    private Map layout;
    public List<NodeBean> getItems() {
        return items;
    }

    public void setItems(List<NodeBean> items) {
        this.items = items;
    }

    private List<NodeBean> items;
    public NodeBean() {}
    public Map getLayout() {
        return layout;
    }

    public void setLayout(Map layout) {
        this.layout = layout;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public NodeBean(String id, String pid, String text,String value) {
        super();
        this.id =id;
        this.pid =pid;
        this.text =text;
        this.value=value;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id =id;
    }

    public String getPid() {
        return pid;

    }

    public void setPid(String pid) {
        this.pid =pid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text =text;
    }
}
