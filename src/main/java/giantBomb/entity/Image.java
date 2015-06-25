package giantBomb.entity;

import com.google.gson.annotations.SerializedName;

public class Image {
	@SerializedName("medium_url") private String medium_url;
	@SerializedName("screen_url") private String screen_url;
	@SerializedName("super_url") private String super_url;
	@SerializedName("small_url") private String small_url;
	@SerializedName("thumb_url") private String thumb_url;
	@SerializedName("icon_url") private String icon_url;
	@SerializedName("tiny_url") private String tiny_url;

	public String getMedium_url() {
		return medium_url;
	}

	public String getScreen_url() {
		return screen_url;
	}

	public String getSuper_url() {
		return super_url;
	}

	public String getSmall_url() {
		return small_url;
	}

	public String getThumb_url() {
		return thumb_url;
	}

	public String getIcon_url() {
		return icon_url;
	}

	public String getTiny_url() {
		return tiny_url;
	}
}
