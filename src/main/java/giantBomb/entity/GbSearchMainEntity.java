package giantBomb.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GbSearchMainEntity {
	@SerializedName("status_code") private Integer status_code;
	@SerializedName("limit") private Integer limit;
	@SerializedName("results") private List<ResultsEntry> results;
	@SerializedName("error") private String error;
	@SerializedName("number_of_total_results") private Integer number_of_total_results;
	@SerializedName("offset") private Integer offset;
	@SerializedName("number_of_page_results") private Integer number_of_page_results;
	@SerializedName("version") private Float version;

	public Integer getStatus_code() {
		return status_code;
	}

	public Integer getLimit() {
		return limit;
	}

	public List<ResultsEntry> getResults() {
		return results;
	}

	public String getError() {
		return error;
	}

	public Integer getNumber_of_total_results() {
		return number_of_total_results;
	}

	public Integer getOffset() {
		return offset;
	}

	public Integer getNumber_of_page_results() {
		return number_of_page_results;
	}

	public Float getVersion() {
		return version;
	}
}
