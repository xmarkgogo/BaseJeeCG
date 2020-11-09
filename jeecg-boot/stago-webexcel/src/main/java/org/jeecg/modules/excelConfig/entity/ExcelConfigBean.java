package org.jeecg.modules.excelConfig.entity;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.config.DBConfig;
import org.jeecg.modules.excelConfig.exector.WebExcelInterFace;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置文件 最外层的实体
 * Created by qizhang on 2020/4/8.
 */
public class ExcelConfigBean {
    private ViewsBean      viewsForSave;
    private ViewsBean      viewsForQuery;
    private ModelsBean     modelsBean;
    private ParametersBean parametersBean;
    private List<SqlWrapBean> WrapBeanList= new ArrayList<SqlWrapBean>();

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    private JdbcTemplate jdbcTemplate;


    public List<SqlWrapBean> getWrapBeanList() {
        return WrapBeanList;
    }
    public void  addWrapBeanToList(SqlWrapBean  sqlPackingBean)
    {
        WrapBeanList.add(sqlPackingBean);
    }

    public WebExcelViewBean getWebExcelViewBean() {
        return webExcelViewBean;
    }

    public void setWebExcelViewBean(WebExcelViewBean webExcelViewBean) {
        this.webExcelViewBean = webExcelViewBean;
    }

    private WebExcelViewBean       webExcelViewBean;


    public ViewsBean getViewsForSave() {
        return viewsForSave;
    }

    public void setViewsForSave(ViewsBean viewsForSave) {
        this.viewsForSave = viewsForSave;
    }

    public ViewsBean getViewsForQuery() {
        return viewsForQuery;
    }

    public void setViewsForQuery(ViewsBean viewsForQuery) {
        this.viewsForQuery = viewsForQuery;
    }

    public ModelsBean getModelsBean() {
        return modelsBean;
    }

    public void setModelsBean(ModelsBean modelsBean) {
        this.modelsBean = modelsBean;
    }

    public ParametersBean getParametersBean() {
        return parametersBean;
    }

    public void setParametersBean(ParametersBean parametersBean) {
        this.parametersBean = parametersBean;
    }
    private ExcelConfigBean instance;
    public ExcelConfigBean getInstance(){
        if(instance==null){
            instance=new ExcelConfigBean();
            instance.setViewsForQuery(this.getViewsForQuery());
            instance.setViewsForSave(this.getViewsForSave());
            instance.setModelsBean(this.getModelsBean());
            instance.setParametersBean(this.getParametersBean());
            instance.setWebExcelViewBean(this.webExcelViewBean);
        }
        return instance;
    }

    public boolean isHaveSaveMethod(){
        if(this.getInstance().getViewsForSave()==null){
            return false;
        }
        return true;
    }


    /**
     * 合并代码避免业务端重复大量new 工具类
     */

    public ModelBean getModelById(String ID) {

        List<ModelBean> modelBeenls = this.getModelsBean().getModelBeans();
        for (int i = 0; i < modelBeenls.size(); i++) {
            ModelBean modelBean = modelBeenls.get(i);
            String id = modelBean.getId();
            if (ID.equalsIgnoreCase(id)) {
                return modelBean;
            }
        }
        return null;
    }


    public ViewBean getQueryViewBeanById(String ID) {
        ViewsBean viewsForQuery = this.getViewsForQuery();
        List<ViewBean> viewBeans = viewsForQuery.getViewBeans();

        for(int i=0;i<viewBeans.size();i++){
            ViewBean CurrentViewBean=viewBeans.get(i);
            if (CurrentViewBean.getModelId().equalsIgnoreCase(ID)) {
               return  CurrentViewBean;
            }
        }
        return null;
    }
    public ViewBean getSaveViewBeanById(String ID) {
        ViewsBean viewsForSave = this.getViewsForSave();
        List<ViewBean> viewBeans = viewsForSave.getViewBeans();
        for(int i=0;i<viewBeans.size();i++){
            ViewBean CurrentViewBean=viewBeans.get(i);
            if (CurrentViewBean.getModelId().equalsIgnoreCase(ID)) {
                return  CurrentViewBean;
            }
        }
        return null;
    }

    public SqlWrapBean getSqlWrapBean(String ModelID,PropertysBean propertys) throws Exception {
        return  getSqlWrapBean( ModelID, propertys, null);
    }
        public SqlWrapBean getSqlWrapBean(String ModelID,PropertysBean propertys, PropertysBean ParentPropertys) throws Exception {

        ModelBean modelBean = getModelById(ModelID);
        if (modelBean==null){
            throw new Exception("Not Find dataSetId======"+ModelID);
        }

        ParametersBean parametersBean =  getParametersValue(modelBean.getParametersBean(),propertys);


        if (null!=parametersBean){
            List<ParameterBean> parameterBeans = parametersBean.getParameterBeans();
            for (int i = 0; i < parameterBeans.size(); i++) {
                ParameterBean newP = new ParameterBean();
                String value = parameterBeans.get(i).getValue();
                String singleParameterName = parameterBeans.get(i).getName();
                if (value.startsWith("$P!")){

                    //提取参数
                    String GetParameterValue=value.replace("$P!{","").replace("}","");

                    //优先使用上一级中的属性内容
                    if(ParentPropertys!=null) {
                        List<PropertyBean> propertyBeans = ParentPropertys.getPropertyBeans();
                        for (int j = 0; j < propertyBeans.size(); j++) {
                            String P_fileName = propertyBeans.get(j).getFeild();
                            if (GetParameterValue.equals(P_fileName)) {
                                newP = parameterBeans.get(i);
                                newP.setValue(propertyBeans.get(j).getFieldRowValue());
                                parameterBeans.set(i, newP);
                            }
                        }
                    }
                    //使用全局变量
                    List<ParameterBean> overAllParameters = this.getParametersBean().getParameterBeans();

                    for (int j = 0; j < overAllParameters.size(); j++) {
                        String overAllName = overAllParameters.get(j).getName();
                        if (GetParameterValue.equals(overAllName)){
                            value = overAllParameters.get(j).getValue();
                            parameterBeans.get(i).setValue(value);
                        }
                    }
                }
            }
        }

        SqlStrsBean sqlStrsBean = modelBean.getSqlStrsBean();
        StringBuilder sqlBuilder = sqlStrsBean.getSqlStr();

        SqlWrapBean sqlPackingBean=new SqlWrapBean();
        sqlPackingBean.setParameTersBean(parametersBean);
        sqlPackingBean.setSqlTemplate(sqlBuilder);

        return sqlPackingBean;
    }



    /**
     *  将 $F!{}  $P!{中的参数,  StudentID 获取真实的值
     *	<parameter name="Sno" class="java.lang.String" value="$P!{StudentID}"/>
     *	<parameter name="Sno" class="java.lang.String" value="$F!{StudentID}"/>
     * @return
     */
    public ParametersBean getParametersValue(ParametersBean parametersBean, PropertysBean propertys) {
        if (null==parametersBean){
            return null;
        }
        ParametersBean newP = new ParametersBean();
        List<ParameterBean> parameterBeans = parametersBean.getParameterBeans();
        List<ParameterBean> newPl = Lists.newArrayList();
        for (int i = 0; i < parameterBeans.size(); i++) {
            ParameterBean parameterBean = parameterBeans.get(i);
            ParameterBean newPar = new ParameterBean();
            newPar.setName(parameterBean.getName());
            newPar.setCls(parameterBean.getCls());
            newPar.setData_List(parameterBean.getData_List());
            newPar.setDesc(parameterBean.getDesc());
            newPar.setHtmlType(parameterBean.getHtmlType());
            newPar.setModelId(parameterBean.getModelId());
            newPar.setValue(parameterBean.getValue());
            newPar.setShow(parameterBean.isShow());
            newPar.setRequired(parameterBean.isRequired());
            newPl.add(newPar);
        }
        newP.setParameterBeans(newPl);
        List<ParameterBean> newPL = newP.getParameterBeans();
        for (int j = 0; j < newPL.size(); j++) {
            ParameterBean parameterBean = newPL.get(j);
            String _value = parameterBean.getValue();
            //判断值中引用 F代表引用字段 P代表引用参数
            if (_value.toUpperCase().startsWith("$F!{")) {
                if(propertys!=null){
                    //从view中的property中获取属性
                    List<PropertyBean> propertyBeans = propertys.getPropertyBeans();
                    for (int k = 0; k < propertyBeans.size(); k++) {
                        PropertyBean propertyBean = propertyBeans.get(k);
                        String feild = propertyBean.getFeild();

                        String variable=_value.substring(4,_value.length()-1);
                        if (variable.toLowerCase().equalsIgnoreCase(feild.toLowerCase())) {
                            String fieldRowValue = propertyBean.getFieldRowValue();
                            parameterBean.setValue(fieldRowValue);
                            newPL.set(j,parameterBean);
                            break;
                        }
                    }
                }
            }
            if (_value.toUpperCase().startsWith("$P!{")) {
                //从全局的parameter中获取参数值
                List<ParameterBean> parameterBeans_ex = newP.getParameterBeans();
                for (int i = 0; i < parameterBeans_ex.size(); i++) {
                    ParameterBean pb = parameterBeans_ex.get(i);
                    String pbName = pb.getName().toLowerCase();
                    //在全局参数中,找到了对应的参数,则直接获取
                    String variable=_value.substring(4,_value.length()-1);
                    if (pbName.equalsIgnoreCase(variable)){
                        parameterBean.setValue(pb.getValue());
                        newPL.set(j,parameterBean);
                        break;
                    }
                }
            }
        }
        return newP;
    }



    /**
     * 获取数据 list集合
     *
     * @return
     */

    public List getDataForList(String sql) throws Exception {
        if(jdbcTemplate==null){
            throw  new Exception("请初始化JDBC");
        }
       // JdbcTemplate jdbcTemplate = new DBConfig().getJdbcTemplate();
        return jdbcTemplate.queryForList(sql);
    }


    public List getDataForList(String ModelID, PropertysBean propertys) throws Exception {
       String CLS=this.getModelsBean().getCls();
            if(CLS!=null & (!"".equalsIgnoreCase(CLS.toString()))){
                Class c = Class.forName(CLS);
                WebExcelInterFace ImplClass = (WebExcelInterFace) c.newInstance();
                return ImplClass.onGetDataList(this.getInstance(),ModelID,propertys);
            }else{
                SqlWrapBean PackingBean= this.getSqlWrapBean(ModelID,propertys);
                return getDataForList(PackingBean.getFinalSQL());
            }

    }


    /**
     * 根据model的id,判断 models对象中是否存在该model,存在,则替换,不存在则增加
     * @param model
     */
    public void setModel(ModelBean model){
        ModelsBean modelsBean = this.getModelsBean();
        List<ModelBean> modelBeans = modelsBean.getModelBeans();
        boolean isHaveSameModel = false;
        for (int i = 0; i < modelBeans.size(); i++) {
            ModelBean modelBean = modelBeans.get(i);
            String id = modelBean.getId();
            String compareId = model.getId();
            if(id.equalsIgnoreCase(compareId)){
                modelBeans.set(i,modelBean);
                isHaveSameModel = true;
            }
        }
        if (!isHaveSameModel){
            modelBeans.add(model);
        }
        modelsBean.setModelBeans(modelBeans);
        this.setModelsBean(modelsBean);
    }

    /**
     * 判断是否是根节点
     * entity 的层级
     * @return
     */
    public boolean isRootView(ViewBean viewBeanBean){
        String ref = viewBeanBean.getRef();
        if (StringUtils.isEmpty(ref)){
            return true;
        }else {
            return false;
        }
    }


    public List<ViewBean> getChildViewForQueryBeanList(String modelId){
        List<ViewBean> entityBeanList = Lists.newArrayList();
        ViewsBean  vQuery = this.getViewsForQuery();
        List<ViewBean> vq = vQuery.getViewBeans();
        for(int i=0;i<vq.size();i++){
            ViewBean vb=vq.get(i);
            if(modelId.equalsIgnoreCase(vb.getRef())){
                entityBeanList.add(vb);
            }
        }
        return entityBeanList;
    }
    public List<ViewBean> getChildViewForSaveBeanList(String modelId){
        List<ViewBean> entityBeanList = Lists.newArrayList();
        ViewsBean  vsave = this.getViewsForSave();
        List<ViewBean> viewBeans = vsave.getViewBeans();
        for(int i=0;i<viewBeans.size();i++){
            ViewBean vb=viewBeans.get(i);
            if(modelId.equalsIgnoreCase(vb.getRef())){
                entityBeanList.add(vb);
            }
        }
        return entityBeanList;
    }



    public ParameterBean getParameterByName(String name){

        ParametersBean parametersBean=    this.getParametersBean();
        if (parametersBean==null)return  null;
        List<ParameterBean> parameterBeans =parametersBean.getParameterBeans();
        for (int i = 0; i < parameterBeans.size(); i++) {
            ParameterBean parameterBean = parameterBeans.get(i);
            if (name.equalsIgnoreCase(parameterBean.getName())){
                return parameterBean;
            }
        }
        return null;
    }



    public ParametersBean setParameterByName(String name,String value){
        ParametersBean parametersBean = this.getParametersBean();
        if(parametersBean==null){
            parametersBean= new ParametersBean();
        }

        List<ParameterBean> parameterBeans = parametersBean.getParameterBeans();

        if(parameterBeans==null){
            parameterBeans= new ArrayList<>();
        }


        int count = 0;
        for (int i = 0; i < parameterBeans.size(); i++) {
            ParameterBean parameterBean = parameterBeans.get(i);
            String pName = parameterBean.getName();
            if (pName.equalsIgnoreCase(name)){
                count = 1;
            }
        }
        if (0 == count){
            ParameterBean parameterBean = new ParameterBean();
            parameterBean.setName(name);
            parameterBean.setValue(value);
            parameterBeans.add(parameterBean);
        }else {
            for (int i = 0; i < parameterBeans.size(); i++) {
                ParameterBean parameterBean = parameterBeans.get(i);
                String pName = parameterBean.getName();
                if (pName.equalsIgnoreCase(name)){
                    parameterBean.setValue(value);
                    parameterBeans.set(i,parameterBean);
                }
            }
        }
        parametersBean.setParameterBeans(parameterBeans);
        return parametersBean;
    }

}
