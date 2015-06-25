package myObjects;

public class GameListObject {

    String gameTitle;
    String releaseDate;
    String releaseDateGB;
    Integer appId;
    String url;

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDateGB() {
        return releaseDateGB;
    }

    public void setReleaseDateGB(String releaseDateGB) {
        this.releaseDateGB = releaseDateGB;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
