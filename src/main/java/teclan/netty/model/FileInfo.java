package teclan.netty.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class FileInfo {
    // type 为 command 时表示为指令包，由服务端下发，控制的是客户端发送的slice大小以实现速率控制
    // type 为 data 时表示为数据包
   private String type = ""; //消息类型
    private String id=""; // 文件ID
    private String srcFileName="";//源文件路径
    private String dstFileName="";//目标文件路径
    private String tmpFileName="";//临时文件路径
    private int index;//包索引，表名是第几个数据包
    private int slice; // 片大小
    byte[] data; // 每个数据包的数据内容
    private long start = 0L;//对应数据包的开始读取位置
    private long point = 0L;//当前数据包的结束读取位置
    private long length = 0L; // 文件长度
    private boolean done; // 是否读取完成
    private boolean dir; // 是否是文件夹

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

    public int getSlice() {
        return slice;
    }

    public void setSlice(int slice) {
        this.slice = slice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public  String toString(){
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(this));
        jsonObject.put("data",String.format("[二进制,length = %s]",data==null?0:data.length));
        return jsonObject.toString();
    }

    public byte[] toBytes(){
        return toString().getBytes();
    }
}
