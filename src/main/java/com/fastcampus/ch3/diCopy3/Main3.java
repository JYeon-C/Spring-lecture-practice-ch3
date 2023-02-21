package com.fastcampus.ch3.diCopy3;

import com.google.common.reflect.ClassPath;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Component class Car {}
@Component class SportsCar extends Car {}
@Component class Truck extends Car {}
@Component class Engine {}

class AppContext {
    Map map; // 객체 저장소

    AppContext() {
        map = new HashMap();
        doComponentScan();
    }

    private void doComponentScan() {
        try {
            // 1. 패키지내의 클래스 목록을 가져온다.
            // 2. 반복문으로 클래스를 하나씩 읽어와서 @Component이 붙어 있는지 확인
            // 3. @Component가 붙어있으면 객체를 생성해서 map에 저장
            ClassLoader classLoader = AppContext.class.getClassLoader();
            ClassPath classPath = ClassPath.from(classLoader);

            // classPath에서 class의 목록을 가져온다.반환하는게 set
            Set<ClassPath.ClassInfo> set = classPath.getTopLevelClasses("com.fastcampus.ch3.diCopy3");

            // for문으로 set에 담긴 클래스를 하나씩 읽어온다.
            for(ClassPath.ClassInfo classInfo : set) {
                Class clazz = classInfo.load();
                Component component = (Component) clazz.getAnnotation(Component.class);
                if(component != null) {
                    String id = StringUtils.uncapitalize(classInfo.getSimpleName());
                    map.put(id, clazz.newInstance()); // 객체를 만들어서 저장
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Object getBean(String key) {
        return map.get(key); // key를 주면 해당하는 객체를 반환
    }
    Object getBean(Class clazz) { // byType
        for(Object obj : map.values()) { // 이 obj가 이 클래스의 객체이거나 자손이면 true
            if(clazz.isInstance(obj))
                return obj;
        }
        return null;
    }
}

public class Main3 {
    public static void main(String[] args) throws Exception {
        AppContext ac = new AppContext();
        Car car = (Car) ac.getBean("car"); // byName으로 객체를 검색
        Car car2 = (Car) ac.getBean(Car.class); // byType으로 객체를 검색
        Engine engine = (Engine) ac.getBean("engine");
        System.out.println("car = " + car);
        System.out.println("car2 = " + car2);
        System.out.println("engine = " + engine);
        System.out.println("engine = " + engine);

    }
}

