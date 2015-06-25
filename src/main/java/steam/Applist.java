package steam;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Applist {
    @SerializedName("apps") private List<AppsEntry> apps;

    public List<AppsEntry> getApps() {
        return apps;
    }
}