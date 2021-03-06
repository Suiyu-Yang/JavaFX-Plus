package cn.edu.scau.biubiusuisui.proxy;

import cn.edu.scau.biubiusuisui.entity.FXFieldWrapper;
import cn.edu.scau.biubiusuisui.utils.StringUtils;
import javafx.beans.property.*;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author jack
 * @Date:2019/6/27 18:47
 */
public class FXEntityProxy implements MethodInterceptor {

    Object target;
    private Map<String, FXFieldWrapper> fxFieldWrapperMap;

    public Object getInstance(Object target) {
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    /**
     * intercept get and set method and
     *
     * @param o
     * @param method
     * @param objects
     * @param methodProxy
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object o1 = methodProxy.invokeSuper(o, objects);  //获取该方法运行后的结果
        String methodName = method.getName();
        String fieldName = null;
        if (methodName.length() >= 3) {
            fieldName = StringUtils.toInstanceName(methodName.substring(3));  // 该method有可能是getter和setter方法，进行处理
        } else {
            return o1;
        }
        FXFieldWrapper fxFieldWrapper = fxFieldWrapperMap.get(fieldName);
        Property property = getPropertyByFieldName(fieldName);
        if (fxFieldWrapper == null || property == null) {
            return o1;
        }
        Class type = fxFieldWrapper.getType();
        if (methodName.startsWith("set")) {
            if(Boolean.class.equals(type) || boolean.class.equals(type)){
                ((SimpleBooleanProperty)property).set((Boolean)objects[0]);
            }else if(Double.class.equals(type)||double.class.equals(type)){
                ((SimpleDoubleProperty)property).set((Double)objects[0]);
            }else if (Float.class.equals(type) || float.class.equals(type)){
                ((SimpleFloatProperty)property).set((Float) objects[0]);
            }else if(Integer.class.equals(type) || int.class.equals(type)){
                ((SimpleIntegerProperty)property).set((Integer) objects[0]);
            }else if(Long.class.equals(type) ||long.class.equals(type)){
                ((SimpleLongProperty)property).set((Long)objects[0]);
            }else if(String.class.equals(type)){
                ((SimpleStringProperty)property).set((String)objects[0]);
            }
        }else if (methodName.startsWith("add")){
            ((SimpleListProperty)(property)).add(objects[0]);
        }else if(methodName.startsWith("del")){
            ((SimpleListProperty)(property)).remove(objects[0]);
        }else if(methodName.startsWith("cls")){
            ((SimpleListProperty)(property)).clear();
        }
        return o1;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Property getPropertyByFieldName(String name) {
        if (fxFieldWrapperMap.get(name) == null) {
            return null;
        }
        return fxFieldWrapperMap.get(name).getProperty();
    }

    public Map<String, FXFieldWrapper> getFxFieldWrapperMap() {
        return fxFieldWrapperMap;
    }

    public void setFxFieldWrapperMap(Map<String, FXFieldWrapper> fxFieldWrapperMap) {
        this.fxFieldWrapperMap = fxFieldWrapperMap;
    }
}
