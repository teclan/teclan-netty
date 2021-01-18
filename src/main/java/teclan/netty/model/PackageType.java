package teclan.netty.model;

public enum PackageType {

    DATA(0, "数据包"), HEARBEAT(1,"心跳包"), CMD_NEED_REPEAT(2,"需重发命令");

    private int value;
    private String des;

    PackageType(int value, String des) {
        this.value = value;
        this.des = des;
    }

    public int getValue() {
        return value;
    }

    public String getDes() {
        return des;
    }

    public static PackageType parse(int value) throws Exception {
        for (PackageType packageType:PackageType.values()){
            if(packageType.getValue()==value){
                return packageType;
            }
        }
        throw new Exception("数据包类型解析错误，无效的枚举值...");
    }
}
