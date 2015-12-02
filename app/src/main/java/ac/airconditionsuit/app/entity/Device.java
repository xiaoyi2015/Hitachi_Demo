package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.util.ByteUtil;
import com.google.gson.Gson;

import java.util.Arrays;

/**
 * Created by ac on 5/26/15.
 * the entity for device
 */
public class Device extends RootEntity {
    public Device() {
    }

    public Device(QRCode qrCode) {
        this.getInfo().setIp(qrCode.getAddress());
        this.getInfo().setMac(qrCode.getMac());
        this.getInfo().setName(qrCode.getName());
        this.getInfo().setCreator_cust_id(qrCode.getCreator_cust_id());
        this.getInfo().setChat_id(qrCode.getChat_id());
    }

    static public class QRCode extends RootEntity {
        private long chat_id = -1;
        private String t;
        private long creator_cust_id;
        private String address;
        private String mac;
        private int cust_id = 10001;
        private int status = 1;

        public String getHome() {
            return home;
        }

        private String name;

        public void setHome(String home) {
            this.home = home;
        }

        private String home;

        public QRCode(long chat_id) {
            this.chat_id = chat_id;
        }

        public long getChat_id() {
            return chat_id;
        }

        public String getT() {
            return t;
        }

        public void setT(String t) {
            this.t = t;
        }

        public static QRCode decodeFromJson(String jsonString) {
            return new Gson().fromJson(jsonString, QRCode.class);
        }

        public void setChat_id(long chat_id) {
            this.chat_id = chat_id;
        }

        public long getCreator_cust_id() {
            return creator_cust_id;
        }

        public void setCreator_cust_id(long creator_cust_id) {
            this.creator_cust_id = creator_cust_id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public int getCust_id() {
            return cust_id;
        }

        public void setCust_id(int cust_id) {
            this.cust_id = cust_id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public String toQRCodeString(String t) {
        QRCode qrCode = new QRCode(info.getChat_id());
        qrCode.setT(t);
        return new Gson().toJson(qrCode);
    }

    public static class Info extends RootEntity {
        private Long chat_id;
        private String comment;
        private String creator_cust_name;
        private Long creator_cust_id;
        private Integer cust_total_count;
        //home name
        private String introduce;
        private String ip;
        private String mac = "";
        private Integer max_cust_count;
        private String name;
        private String longitude;
        private String latitude;

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        private String port;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }


        public Long getChat_id() {
            return chat_id;
        }

        public void setChat_id(Long chat_id) {
            this.chat_id = chat_id;
        }

        public String getCreator_cust_name() {
            return creator_cust_name;
        }

        public void setCreator_cust_name(String creator_cust_name) {
            this.creator_cust_name = creator_cust_name;
        }

        public Long getCreator_cust_id() {
            return creator_cust_id;
        }

        public void setCreator_cust_id(Long creator_cust_id) {
            this.creator_cust_id = creator_cust_id;
        }

        public Integer getCust_total_count() {
            return cust_total_count;
        }

        public void setCust_total_count(Integer cust_total_count) {
            this.cust_total_count = cust_total_count;
        }

        public String getIntroduce() {
            if (introduce == null) {
                return "我的家";
            }
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public Integer getMax_cust_count() {
            return max_cust_count;
        }

        public void setMax_cust_count(Integer max_cust_count) {
            this.max_cust_count = max_cust_count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private Info info = new Info();

    private String authCodeEncode;
    private String authCode;
    private String cust_class = "10001";
    private String port;
    private String position;
    private Integer state;
    private String filePath;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getAuthCodeEncode() {
        return authCodeEncode;
    }

    public void setAuthCodeEncode(String authCodeEncode) {
        this.authCodeEncode = authCodeEncode;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(byte[] authCodeBytes) {
        this.authCode = ByteUtil.byteArrayToHexString(authCodeBytes);
        byte[] bytes = Arrays.copyOf(authCodeBytes, 6);
        String mac = ByteUtil.byteArrayToHexString(bytes);
        this.info.setMac(mac);
        this.info.setChat_id(getIdByAuthCode(authCodeBytes));
        this.info.setName("10001-" + info.getChat_id());
    }

    public String getCust_class() {
        return cust_class;
    }

    public void setCust_class(String cust_class) {
        this.cust_class = cust_class;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private Long getIdByAuthCode(byte[] authCodeBytes) {
        return ByteUtil.byteArrayToLong(authCodeBytes);
    }

    static public Device fromJsonString(String json) {
        return new Gson().fromJson(json, Device.class);

    }

}
