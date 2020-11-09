package org.jeecg.modules.excelConfig.util;

import com.alibaba.fastjson.JSON;

import java.util.*;


public class TreeBuilder {


    List<NodeBean> nodes = new ArrayList<NodeBean>();
    public TreeBuilder(List<NodeBean> nodes) {
        super();
        this.nodes= nodes;

    }

    /**
     * 构建树形结构
     * @return
     */

    public List<NodeBean> buildTree() {

        List<NodeBean>treeNodes = new ArrayList<NodeBean>();
        List<NodeBean>rootNodes = getRootNodes();
        for (NodeBean rootNode : rootNodes) {
            buildChildNodes(rootNode);
            treeNodes.add(rootNode);
        }
        return treeNodes;
    }



    /**
     * 递归子节点
     * @param node
     */

    public void buildChildNodes(NodeBean node) {
        List<NodeBean> children = getChildNodes(node);
        if (!children.isEmpty()) {
            for(NodeBean child : children) {
                buildChildNodes(child);
            }
            node.setItems(children);
        }
    }



    /**
     * 获取父节点下所有的子节点
     * @param nodes
     * @param pnode
     * @return
     */

    public List<NodeBean> getChildNodes(NodeBean pnode) {
        List<NodeBean>childNodes = new ArrayList<NodeBean>();
        for (NodeBean n : nodes){
            if (pnode.getId().equals(n.getPid())) {
                childNodes.add(n);
            }
        }
        return childNodes;
    }



    /**
     * 判断是否为根节点
     * @param nodes
     * @param inNode
     * @return
     */

    public boolean rootNode(NodeBean node) {
        boolean isRootNode = true;
        for (NodeBean n : nodes){
            if (node.getPid().equals(n.getId())) {
                isRootNode= false;
                break;
            }
        }
        return isRootNode;
    }



    /**
     * 获取集合中所有的根节点
     * @param nodes
     * @return
     */

    public List<NodeBean> getRootNodes() {
        List<NodeBean>rootNodes = new ArrayList<NodeBean>();
        for (NodeBean n : nodes){
            if (rootNode(n)) {
                rootNodes.add(n);
            }
        }
        return rootNodes;
    }



    public static void main(String[] args) {


        List<NodeBean>nodes = new ArrayList();
        NodeBean p1 = new NodeBean("01", "444","01","A");
        NodeBean p6 = new NodeBean("02", "e","02","B");
        NodeBean p7 = new NodeBean("0201", "02","0201","C");
        NodeBean p2 = new NodeBean("0101", "01","0101","D");
        NodeBean p3 = new NodeBean("0102", "01","0102","E");
        NodeBean p4 = new NodeBean("010101", "0101","010101","F");
        NodeBean p5 = new NodeBean("010102", "0101","010102","G");
        NodeBean p8 = new NodeBean("03", "","03","H");

        nodes.add(p1);
        nodes.add(p2);
        nodes.add(p3);
        nodes.add(p4);
        nodes.add(p5);
        nodes.add(p6);
        nodes.add(p8);
        nodes.add(p7);

        TreeBuilder treeBuilder = new TreeBuilder(nodes);
        List<NodeBean> nodeTree= treeBuilder.buildTree();
        System.out.println(JSON.toJSON(nodeTree));
    }

}
