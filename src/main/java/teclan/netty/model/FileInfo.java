package teclan.netty.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;

public class FileInfo {
    private PackageType packageType;
    private String id=""; // 文件ID
    private String srcFileName="";//源文件路径
    private String dstFileName="";//目标文件路径
    private String tmpFileName="";//临时文件路径
    private String router ="";// 目标客户端
    private String md5=""; // 原文件MD5
    private int index;//包索引，表名是第几个数据包
    private int slice; // 片大小
    private long length = 0L; // 文件长度
    private int packages = 0;// 数据包个数
    byte[] data; // 每个数据包的数据内容
    private long start = 0L;//对应数据包的开始读取位置
    private long point = 0L;//当前数据包的结束读取位置

    private boolean done; // 是否读取完成
    private boolean dir; // 是否是文件夹

    public FileInfo(){
    }

    public FileInfo(String srcFileName,String dstFileName,long length){
        this.srcFileName=srcFileName;
        this.dstFileName=dstFileName;
        this.length=length;

    }

    public void setDefTmpFileName(){
        File file = new File(dstFileName);
        String fileName = file.getName();
        String name = fileName.lastIndexOf(".")>0?fileName.substring(0,fileName.indexOf(".")):fileName;
        String suffix = fileName.lastIndexOf(".")>0?fileName.substring(fileName.lastIndexOf(".")):"";
        this.tmpFileName = file.getParent()+File.separator+name+"_"+id+suffix;
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public  String toString(){
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(this));
        jsonObject.put("data",data==null? "[NULL]" :String.format("[二进制,length = %s]",data.length));
        return jsonObject.toString();
    }

    public int getPackages() {
        return packages;
    }

    public void setPackages(int packages) {
        this.packages = packages;
    }

    public byte[] toBytes(){
        return toString().getBytes();
    }

    public PackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(PackageType packageType) {
        this.packageType = packageType;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }
}
