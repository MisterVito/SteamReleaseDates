package giantBomb.entity;

import com.google.gson.annotations.SerializedName;

public class PlatformsEntry {
	@SerializedName("id") private Integer id;
	@SerializedName("api_detail_url") private String api_detail_url;
	@SerializedName("site_detail_url") private String site_detail_url;
	@SerializedName("name") private String name;
	@SerializedName("abbreviation") private String abbreviation;

	public Integer getId() {
		return id;
	}

	public String getApi_detail_url() {
		return api_detail_url;
	}

	public String getSite_detail_url() {
		return site_detail_url;
	}

	public String getName() {
		return name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}
}
