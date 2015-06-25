package giantBomb.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultsEntry {
	@SerializedName("expected_release_quarter") private Object expected_release_quarter;
	@SerializedName("image") private Image image;
	@SerializedName("date_last_updated") private String date_last_updated;
	@SerializedName("platforms") private List<PlatformsEntry> platforms;
	@SerializedName("expected_release_year") private Object expected_release_year;
	@SerializedName("aliases") private Object aliases;
	@SerializedName("original_game_rating") private Object original_game_rating;
	@SerializedName("id") private Integer id;
	@SerializedName("api_detail_url") private String api_detail_url;
	@SerializedName("site_detail_url") private String site_detail_url;
	@SerializedName("date_added") private String date_added;
	@SerializedName("description") private String description;
	@SerializedName("name") private String name;
	@SerializedName("resource_type") private String resource_type;
	@SerializedName("number_of_user_reviews") private Integer number_of_user_reviews;
	@SerializedName("original_release_date") private String original_release_date;
	@SerializedName("expected_release_day") private Object expected_release_day;
	@SerializedName("deck") private String deck;
	@SerializedName("expected_release_month") private Object expected_release_month;

	public Object getExpected_release_quarter() {
		return expected_release_quarter;
	}

	public Image getImage() {
		return image;
	}

	public String getDate_last_updated() {
		return date_last_updated;
	}

	public List<PlatformsEntry> getPlatforms() {
		return platforms;
	}

	public Object getExpected_release_year() {
		return expected_release_year;
	}

	public Object getAliases() {
		return aliases;
	}

	public Object getOriginal_game_rating() {
		return original_game_rating;
	}

	public Integer getId() {
		return id;
	}

	public String getApi_detail_url() {
		return api_detail_url;
	}

	public String getSite_detail_url() {
		return site_detail_url;
	}

	public String getDate_added() {
		return date_added;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getResource_type() {
		return resource_type;
	}

	public Integer getNumber_of_user_reviews() {
		return number_of_user_reviews;
	}

	public String getOriginal_release_date() {
		return original_release_date;
	}

	public Object getExpected_release_day() {
		return expected_release_day;
	}

	public String getDeck() {
		return deck;
	}

	public Object getExpected_release_month() {
		return expected_release_month;
	}
}
