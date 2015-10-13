package ac.airconditionsuit.app.entity;

import com.google.gson.Gson;

/**
 * Created by ac on 5/26/15.
 * the entity for device
 */
public class Device extends RootEntity {
    static public class QRCode {
        public QRCode(String chat_id) {
            this.chat_id = chat_id;
        }

        public String getChat_id() {
            return chat_id;
        }

        public String chat_id;

        public String getT() {
            return t;
        }

        public void setT(String t) {
            this.t = t;
        }

        public String t;

    }

    public String toQRCodeString(String t) {
        QRCode qrCode = new QRCode(info.getChat_id().toString());
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
    private String cust_class = "10008";
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

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
        this.info.setChat_id(getIdByAuthCode());
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

    private Long getIdByAuthCode() {
        assert authCode != null;
        String result = "0001";
        for (int i = 12; i >= 2; i -= 2) {
            result += authCode.substring(i, i + 2);
        }
        long chat_id = Long.parseLong(result, 16);
        info.setChat_id(chat_id);
        return chat_id;
    }

    static public Device fromJsonString(String json) {
        return new Gson().fromJson(json, Device.class);

    }

}
