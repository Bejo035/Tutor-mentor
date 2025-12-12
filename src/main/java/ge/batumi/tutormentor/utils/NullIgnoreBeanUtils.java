package ge.batumi.tutormentor.utils;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class NullIgnoreBeanUtils extends BeanUtilsBean {
    @Override
    public void copyProperty(Object dest, String name, Object value) {

        if (value == null) return;
        try {
            super.copyProperty(dest, name, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Could not copy the properties.");
        }
    }
}