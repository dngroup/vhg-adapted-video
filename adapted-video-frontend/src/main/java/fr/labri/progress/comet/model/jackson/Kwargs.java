
package fr.labri.progress.comet.model.jackson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "url",
    "qualities"
})
public class Kwargs {

    @JsonProperty("url")
    private String url;
    @JsonProperty("qualities")
    private List<Quality> qualities;


	@JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    @JsonProperty("returnURL")
    private String returnURL;
    @JsonProperty("cacheURL")
	private String cacheURL;
	/**
     * 
     * @return
     *     The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return
     *     The qualities
     */
    @JsonProperty("qualities")
    public List<Quality> getQualities() {
        return qualities;
    }

    /**
     * 
     * @param qualities
     *     The qualities
     */
    @JsonProperty("qualities")
    public void setQualities(List<Quality> qualities) {
		this.qualities = qualities;
	}

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

	/**
	 * 
	 * @return
	 *     The returnAddr
	 */
	@JsonProperty("returnURL")
	public String getReturnUrl() {
	    return returnURL;
	}

	/**
	 * 
	 * @param eta
	 *     The returnAddr
	 */
	@JsonProperty("returnURL")
	public void setReturnAddr(String returnUrl) {
	    this.returnURL = returnUrl;
	}

	/**
	 * 
	 * @return
	 *     The cacheAddr
	 */
	@JsonProperty("cacheURL")
	public String getCacheUrl() {
	    return cacheURL;
	}

	/**
	 * 
	 * @param eta
	 *     The cacheAddr
	 */
	@JsonProperty("cacheURL")
	public void setCacheAddr(String cacheUrl) {
	    this.cacheURL = cacheUrl;
	}



}
