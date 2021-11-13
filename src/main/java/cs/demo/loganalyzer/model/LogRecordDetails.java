package cs.demo.loganalyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogRecordDetails {
	    private String id;
	    private State state;
	    private EventType type;
	    private String host;
	    private long timestamp;
}
