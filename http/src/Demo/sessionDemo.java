package Demo;

import org.omg.PortableInterceptor.USER_EXCEPTION;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class sessionDemo {
    private static class User implements Serializable {
        private String name;
        private String password;

        User(String name,String password){
            this.name=name;
            this.password=password;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) throws IOException {
        String uuid= UUID.randomUUID().toString();
        Map<String,Object> map=new HashMap<>();
        map.put("emp1",new User("zpf","123"));
        map.put("emp2",new User("fpz","321"));
        System.out.println(uuid);
        String fileName=String.format("%s\\%s.session","E:\\JavaNotes\\http\\sessions",uuid);
        try (OutputStream os=new FileOutputStream(fileName)){
            ObjectOutputStream oos=new ObjectOutputStream(os);
            oos.writeObject(map);
            oos.flush();
        }
    }
}
