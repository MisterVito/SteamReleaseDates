package steam;

import com.google.gson.annotations.SerializedName;

public class AppsEntry {
    @SerializedName("name") private String name;
    @SerializedName("appid") private Integer appid;

    public String getName() {
        return name;
    }

    public Integer getAppid() {
        return appid;
    }

}
