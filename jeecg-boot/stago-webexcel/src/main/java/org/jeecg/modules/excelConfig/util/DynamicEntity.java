package org.jeecg.modules.excelConfig.util;
import com.grapecity.documents.excel.Workbook;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by mas on 2020/5/6.
 *
 *
 */
public class DynamicEntity {
    protected static Logger logger = LoggerFactory.getLogger(DynamicEntity.class);
    /**
     * 实体Object
     */
    public Object object = null;

    /**
     * 属性map
     */
    public BeanMap beanMap = null;

    public DynamicEntity() {
        super();
    }

    @SuppressWarnings("unchecked")
    public DynamicEntity(Map propertyMap) {
        this.object = generateBean(propertyMap);
        this.beanMap = BeanMap.create(this.object);
    }

    /**
     * 给bean属性赋值
     *
     * @param property 属性名
     * @param value    值
     */
    public void setValue(String property, Object value) {
        beanMap.put(property, value);
    }

    /**
     * 通过属性名得到属性值
     *
     * @param property 属性名
     * @return 值
     */
    public Object getValue(String property) {
        return beanMap.get(property);
    }

    /**
     * 得到该实体bean对象
     *
     * @return
     */
    public Object getObject() {
        return this.object;
    }

    @SuppressWarnings("unchecked")
    private Object generateBean(Map propertyMap) {
        BeanGenerator generator = new BeanGenerator();
        Set keySet = propertyMap.keySet();
        for (Iterator i = keySet.iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            generator.addProperty(key, (Class) propertyMap.get(key));
        }
        return generator.create();
    }

     public static void tesdt() throws ClassNotFoundException {
         Workbook workbook = new Workbook();
//Load template file Template_StudentInfo.xlsx from resource
        // workbook.open("e:\\code\\Variable[Template].xlsx");


         ArrayList<Object> studentInfos = new ArrayList<Object>();


         ArrayList<Object> studentInfosssss = new ArrayList<Object>();

         HashMap propertyMap = new HashMap();
         propertyMap.put("Month",       Class.forName("java.lang.String"));
         propertyMap.put("Day",    Class.forName("java.lang.String"));
        // propertyMap.put("Name",         Class.forName("java.util.ArrayList"));




         // 生成动态 Bean
         DynamicEntity bean = new DynamicEntity(propertyMap);
         // 给 Bean 设置值
         bean.setValue("Month",     "454");
         bean.setValue("day",  "789V");
       //  bean.setValue("Name",  "NameV");
         Object object = bean.getObject();
         studentInfosssss.add(object);
         bean.setValue("rs",  studentInfosssss);
         bean.getObject();

         logger.debug("  >> Month      = " + bean.getValue("Month"));
         logger.debug("  >> Day    = " + bean.getValue("day"));


         studentInfos.add(object);

            String className = "Class 3";
            ///#endregion

            //Add data source
            workbook.addDataSource("className", className);
            workbook.addDataSource("s", studentInfos);

            //Invoke to process the template
         //   workbook.processTemplate();

            //save to an excel file
         //   workbook.save("E:\\Variable1.xlsx");
    }
        public static void main (String[] args) throws ClassNotFoundException {
            DynamicEntity.tesdt();
        /*// 设置类成员属性
        HashMap propertyMap = new HashMap();
        propertyMap.put("name",      Class.forName("java.lang.String"));
        propertyMap.put("address",    Class.forName("java.lang.String"));
        // 生成动态 Bean
        DynamicEntity bean = new DynamicEntity(propertyMap);
        // 给 Bean 设置值
        bean.setValue("name",     "454");
        bean.setValue("address",  "789");
        // 从 Bean 中获取值，当然了获得值的类型是 Object

        System.out.println("  >> id      = " + bean.getValue("id"));
        System.out.println("  >> name    = " + bean.getValue("name"));
        System.out.println("  >> address = " + bean.getValue("address"));

        // 获得bean的实体
        Object object = bean.getObject();

        // 通过反射查看所有方法名
        Class clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            System.out.println(methods[i].getName());
        }*/
    }

}
