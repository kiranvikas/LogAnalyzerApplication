package cs.demo.loganalyzer.entity;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cs.demo.loganalyzer.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "EventAlerts")

public class EventAlert {
	  	@Id
	    private String id;
	    private int duration;
	    private EventType type;
	    private String host;
	    private Boolean alert;
}

