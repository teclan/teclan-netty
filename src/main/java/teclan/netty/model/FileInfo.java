package teclan.netty.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class FileInfo {
    private String id;
    private String srcFileName;
    private String dstFileName;
    private String tmpFileName;
    private int index;
    byte[] data;
    private long start = 0L;
    private long point = 0L;
    private long length = 0L;
    private boolean done;
    private boolean dir;

    public FileInfo(){
    }

    public FileInfo(String srcFileName,String dstFileName,long length){
        this.srcFileName=srcFileName;
        this.dstFileName=dstFileName;
        this.length=length;
        this.tmpFileName = dstFileName+".swap";
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public void setSrcFileName(String srcFileName) {
        this.srcFileName = srcFileName;
    }

    public String getDstFileName() {
        return dstFileName;
    }

    public void setDstFileName(String dstFileName) {
        this.dstFileName = dstFileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTmpFileName() {
        return tmpFileName;
    }

    public void setTmpFileName(String tmpFileName) {
        this.tmpFileName = tmpFileName;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isDir() {
        return dir;
    }

    public void setDir(boolean dir) {
        this.dir = dir;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public  String toString(){
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(this));
//        jsonObject.remove("data");
        jsonObject.put("data",String.format("[data,length = %s]",data==null?0:data.length));
        return jsonObject.toString();
    }

    public byte[] toBytes(){
        return toString().getBytes();
    }
}
