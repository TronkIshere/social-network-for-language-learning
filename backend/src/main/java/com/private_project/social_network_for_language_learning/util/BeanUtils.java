package com.private_project.social_network_for_language_learning.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;

public class BeanUtils {
    private BeanUtils() {}

    public static void copyNonNullFields(Object source, Object target) {
        BeanWrapper src = new BeanWrapperImpl(source);
        BeanWrapper trg = new BeanWrapperImpl(target);

        for (PropertyDescriptor property : src.getPropertyDescriptors()) {
            String name = property.getName();
            Object value = src.getPropertyValue(name);

            if ("class".equals(name)) continue;
            if (value != null && trg.isWritableProperty(name)) {
                trg.setPropertyValue(name, value);
            }
        }
    }
}
