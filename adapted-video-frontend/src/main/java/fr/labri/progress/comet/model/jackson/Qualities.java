
package fr.labri.progress.comet.model.jackson;

import java.util.ArrayList;
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
    "quality"
})
public class Qualities {

    @JsonProperty("quality")
    private List<Quality> quality =new ArrayList<Quality>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The quality
     */
    @JsonProperty("quality")
    public List<Quality> getQuality() {
        return quality;
    }

    /**
     * 
     * @param quality
     *     The quality
     */
    @JsonProperty("quality")
    public void setListQuality(List<Quality> quality) {
        this.quality = quality;
    }
    
    /**
     * 
     * @param quality
     *     add quality
     */
    @JsonProperty("quality")
    public void addQuality(Quality quality) {
        this.quality.add(quality);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
