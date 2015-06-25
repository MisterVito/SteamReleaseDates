package steam;

import com.google.gson.annotations.SerializedName;

public class SteamAppListEntity {
    @SerializedName("applist") private Applist applist;

    public Applist getApplist() {
        return applist;
    }

    public void setApplist(Applist applist) {
        this.applist = applist;
    }
}
