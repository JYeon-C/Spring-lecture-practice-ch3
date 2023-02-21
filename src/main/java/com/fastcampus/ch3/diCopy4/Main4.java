package com.fastcampus.ch3.diCopy4;

import com.google.common.reflect.ClassPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component class Car {
    @Resource
    Engine engine;
    // @Resource
    Door door;

    @Override
    public String toString() {
        return "Car{" +
                "engine=" + engine +
                ", door=" + door +
                '}';
    }
}
@Component class SportsCar extends Car {}
@Component class Truck extends Car {}
@Component class Engine {}
@Component class Door {}

class AppContext {
    Map map; // 객체 저장소

    AppContext() {
        map = new HashMap();
        doComponentScan();
        doAutowired();
        doResource();
    }

    private void doResource() {
        // map에 저장된 객체의 iv중에 @Resource가 붙어 있으면
        // map에서 iv의 타입에 맞는 객체를 찾아서 연결(객체의 주소를 iv저장)
        try {
            for(Object bean : map.values()) { // map에 저장된 것들을 가져다가
                // 갖고 있는 iv 중 @Autowired가 붙은게 있는지 확인한다.
                for(Field fld : bean.getClass().getDeclaredFields()) {
                    if(fld.getAnnotation(Resource.class)!=null) // byName
                        // 붙었으면 해당하는 타입을 뒤져서 저장해준다.
                        fld.set(bean, getBean(fld.getName())); // car.engine = obj;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void doAutowired() {
        // map에 저장된 객체의 iv중에 @Autowired가 붙어 있으면
        // map에서 iv의 타입에 맞는 객체를 찾아서 연결(객체의 주소를 iv저장)
        try {
            for(Object bean : map.values()) { // map에 저장된 것들을 가져다가
                // 갖고 있는 iv 중 @Autowired가 붙은게 있는지 확인한다.
                for(Field fld : bean.getClass().getDeclaredFields()) {
                    if(fld.getAnnotation(Autowired.class)!=null) // byType
                        // 붙었으면 해당하는 타입을 뒤져서 저장해준다.
                        fld.set(bean, getBean(fld.getType())); // car.engine = obj;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void doComponentScan() {
        try {
            // 1. 패키지내의 클래스 목록을 가져온다.
            // 2. 반복문으로 클래스를 하나씩 읽어와서 @Component이 붙어 있는지 확인
            // 3. @Component가 붙어있으면 객체를 생성해서 map에 저장
            ClassLoader classLoader = AppContext.class.getClassLoader();
            ClassPath classPath = ClassPath.from(classLoader);

            // classPath에서 class의 목록을 가져온다.반환하는게 set
            Set<ClassPath.ClassInfo> set = classPath.getTopLevelClasses("com.fastcampus.ch3.diCopy4");

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

public class Main4 {
    public static void main(String[] args) throws Exception {
        AppContext ac = new AppContext();
        Car car = (Car) ac.getBean("car"); // byName으로 객체를 검색
        Door door = (Door) ac.getBean(Door.class); // byType으로 객체를 검색
        Engine engine = (Engine) ac.getBean("engine");

        // 수동으로 객체를 연결
//        car.engine = engine;
//        car.door = door;

        System.out.println("car = " + car);
        System.out.println("engine = " + engine);
        System.out.println("door = " + door);

        }
    }


