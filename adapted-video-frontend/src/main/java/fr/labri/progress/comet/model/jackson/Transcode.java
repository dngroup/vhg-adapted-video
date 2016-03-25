
package fr.labri.progress.comet.model.jackson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

import fr.labri.progress.comet.conf.SpringConfiguration;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "task",
    "args",
    "kwargs",
    "retries",
    "eta"
})
public class Transcode {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Transcode.class);
//	@Inject
//	ObjectMapper mapper;
	
	
    @JsonProperty("id")
    private String id;
    @JsonProperty("task")
    private String task;
    @JsonProperty("args")
    private List<String> args = new ArrayList<String>();
    @JsonProperty("kwargs")
    private Kwargs kwargs;
    @JsonProperty("retries")
    private Integer retries;
    @JsonProperty("eta")
    private String eta;

    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The task
     */
    @JsonProperty("task")
    public String getTask() {
        return task;
    }

    /**
     * 
     * @param task
     *     The task
     */
    @JsonProperty("task")
    public void setTask(String task) {
        this.task = task;
    }

    /**
     * 
     * @return
     *     The args
     */
    @JsonProperty("args")
    public List<String> getArgs() {
        return args;
    }

    /**
     * 
     * @param args
     *     The args
     */
    @JsonProperty("args")
    public void setArgs(List<String> args) {
        this.args = args;
    }

    /**
     * 
     * @return
     *     The kwargs
     */
    @JsonProperty("kwargs")
    public Kwargs getKwargs() {
        return kwargs;
    }

    /**
     * 
     * @param kwargs
     *     The kwargs
     */
    @JsonProperty("kwargs")
    public void setKwargs(Kwargs kwargs) {
        this.kwargs = kwargs;
    }

    /**
     * 
     * @return
     *     The retries
     */
    @JsonProperty("retries")
    public Integer getRetries() {
        return retries;
    }

    /**
     * 
     * @param retries
     *     The retries
     */
    @JsonProperty("retries")
    public void setRetries(Integer retries) {
        this.retries = retries;
    }

   
    
    
    /**
     * 
     * @return
     *     The eta
     */
    @JsonProperty("eta")
    public String getEta() {
        return eta;
    }

    /**
     * 
     * @param eta
     *     The eta
     */
    @JsonProperty("eta")
    public void setEta(String eta) {
        this.eta = eta;
    }

    

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    public String toJSON(){
		ObjectMapper mapper = new ObjectMapper();
    	

		//Object to JSON in String
		try {
			return mapper.writeValueAsString(this);
			 
		} catch (JsonProcessingException e) {
			LOGGER.error("Can not convert you Transcode object to json",e);
			throw Throwables.propagate(e);
		}
		
    }

	

}
