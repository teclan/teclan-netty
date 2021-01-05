package teclan.netty.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IdUtils {
     public static  SnowFlake snowFlake = new SnowFlake(2, 3);

    public static Map<String,Object> TOKENS = new ConcurrentHashMap<String,Object>();

    public static void put(String user,Object token){
        TOKENS.put(user,token);
    }

    public static void remove(String user){
        TOKENS.remove(user);
    }

    public static Object get(String user){
       return  TOKENS.get(user);
    }

    public static synchronized  String get(){
       return  String.valueOf(snowFlake.nextId());
    }
}
