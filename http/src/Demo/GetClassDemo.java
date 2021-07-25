package Demo;

import java.util.Calendar;

public class GetClassDemo {
    public static void main(String[] args) throws ClassNotFoundException {
        String className="com.fpz.webapps.dictionary.TranslateServlet";
        //方式1
        /*Class<?> clazz1=Class.forName(className);
        System.out.println(clazz1);

        //方式2
        ClassLoader classLoader=GetClassDemo.class.getClassLoader();
        //System.out.println(classLoader);
        Class<?> clazz2=classLoader.loadClass(className);
        System.out.println(clazz2);*/

        //方式3
        Class<GetClassDemo> GetClassDemoclazz=GetClassDemo.class;
        ClassLoader classLoader=GetClassDemoclazz.getClassLoader();
        Class<?> clazz3=classLoader.loadClass(className);
        System.out.println(clazz3);



    }
}
