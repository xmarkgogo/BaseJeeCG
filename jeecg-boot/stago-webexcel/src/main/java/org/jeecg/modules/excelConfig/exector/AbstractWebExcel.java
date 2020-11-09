package org.jeecg.modules.excelConfig.exector;

import org.jeecg.modules.excelConfig.entity.ExcelConfigBean;
import org.jeecg.modules.excelConfig.entity.PropertysBean;
import org.jeecg.modules.excelConfig.entity.SqlWrapBean;

import java.util.List;

/**
 * Created by mas on 2020/9/21.
 */
public  abstract class AbstractWebExcel  {

    public PropertysBean onGetCellVue(String ViewBeanId, PropertysBean propertys) throws Exception{
        return propertys;
    }

    /**查询数据，显示为Excel
     * 某些查询需要Java代码进行多次查询，并将结果处理为一个List的时候，可以实现该类
     * 例如：读取某个盘符下的文件
     *      .....
     * @param instance
     * @param CurrentModelID
     * @param propertys
     * @return
     * @throws Exception
     */
    public List onGetDataList(ExcelConfigBean instance, String CurrentModelID, PropertysBean propertys) throws Exception{
        SqlWrapBean PackingBean= instance.getSqlWrapBean(CurrentModelID,propertys);
        return instance.getDataForList(PackingBean.getFinalSQL());
    }

    /**
     *  一般用在通过Excel进行数据保存的情况下
     *  如果你希望在某一个数据行进行保存的时候，进行一些额外逻辑的处理，可以实现该类
     *  方案有2中
     *  1 如果操作数据库的话，请包装SqlWrapBean类到 ExcelConfigBean的实例中，系统会通过事务统一执行
     *  2 如果有其他的java类代码需要执行，可以直接写在此处
     * @param instance
     * @param CurrentModelID
     * @param propertys
     * @throws Exception
     */
    public void onBeforeSave(ExcelConfigBean instance, String CurrentModelID, PropertysBean propertys) throws Exception
    {
        //此处为范例代码
     /*   if("CurrentModelID".equalsIgnoreCase(CurrentModelID)){
            SqlWrapBean sql=new SqlWrapBean();
            sql.setSqlTemplate(new StringBuilder().append("delete xx from XX where xxx"));
            instance.addWrapBeanToList(sql);
        }*/

    }

}
