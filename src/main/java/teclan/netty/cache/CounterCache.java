package teclan.netty.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.model.FileInfo;

import java.util.concurrent.ConcurrentHashMap;

public class CounterCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(CounterCache.class);
    private static final ConcurrentHashMap<String, Package> COUNTER = new ConcurrentHashMap<String, Package>();

    public static void increase(String key,int total){
        if(!COUNTER.containsKey(key)){
            COUNTER.put(key,new Package(1,total));
        }else {
            Package pkg = COUNTER.get(key);
            pkg.setCount(pkg.getCount()+1);
            COUNTER.put(key,pkg);
        }
    }


    public static void increase(FileInfo fileInfo){
        String key = fileInfo.getId();
        increase(key,fileInfo.getPackages());
    }

    public static  void remove(FileInfo fileInfo){
        COUNTER.remove(fileInfo.getId());
    }

    public static  boolean isDone(FileInfo fileInfo){
        String key = fileInfo.getId();
        Package pkg = COUNTER.get(key);
        return pkg.getCount()==pkg.getTotal();


    }
}
