package org.jeecg.modules.excelConfig.exector;
import org.jeecg.modules.excelConfig.entity.ExcelConfigBean;
import org.jeecg.modules.excelConfig.entity.PropertysBean;

import java.util.List;

/**
   Web Excel   接口类
   如有特殊需求，请实现该类
 */
public interface WebExcelInterFace {

    /**
     * 查询数据，显示为Excel
     * 处理某一个行中的某一个单元格中的数据时候，请实现此方法
     * 例如：某个单元格的数字希望转换为大写字母 10000 -> 壹万元整
     * 例如：某个单元格中的数字，需要执行一个非常复杂的方法进行查询后处理为一个汇总数字 A部门-（人数合计XX个）
     * 等等情况
     * @return
     * @throws Exception
     */
    public PropertysBean onGetCellVue(String ViewBeanId, PropertysBean propertys) throws Exception;

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
    public List onGetDataList(ExcelConfigBean instance, String CurrentModelID, PropertysBean propertys) throws Exception;

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
    public void onBeforeSave(ExcelConfigBean instance, String CurrentModelID, PropertysBean propertys) throws Exception;


}
