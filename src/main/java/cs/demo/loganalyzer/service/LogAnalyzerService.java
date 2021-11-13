package cs.demo.loganalyzer.service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs.demo.loganalyzer.entity.EventAlert;
import cs.demo.loganalyzer.model.LogRecordDetails;
import cs.demo.loganalyzer.model.State;
import cs.demo.loganalyzer.repository.EventAlertRepository;
import lombok.NonNull;

@Component
@ConfigurationProperties(prefix = "loganalyserapp")
public class LogAnalyzerService {
	 @Autowired
	private EventAlertRepository eventAlertRepository;
	private static Logger logger = LoggerFactory.getLogger(LogAnalyzerService.class);
	private ConcurrentHashMap<String, LogRecordDetails> logRecordLocalStore;
	private ConcurrentHashMap<String, EventAlert> logEventAlertLocalStore;
	private long eventTimeoutInMilliseconds;
	private int batchSizeForDbInsertion;
	List<LogRecordDetails> logEventList;

	public void process(String filename) {
		logRecordLocalStore = new ConcurrentHashMap<>();
		logEventAlertLocalStore = new ConcurrentHashMap<>();
		readLogFile(filename);
		logger.info("Complete processing in Service.");
	}

	private void readLogFile(String logfile) {
		logger.info("Start reading the logfile at :" + logfile);
		try (LineIterator li = FileUtils.lineIterator(new ClassPathResource("static/" + logfile).getFile())) {
			while (li.hasNext()) {
				EventAlert eAlert;
				LogRecordDetails logRecord;
				try {
					logRecord = new ObjectMapper().readValue(li.nextLine(), LogRecordDetails.class);
					logger.info("{}", logRecord);

					if (logRecordLocalStore.containsKey(logRecord.getId())) {
						LogRecordDetails logRecord1 = logRecordLocalStore.get(logRecord.getId());
						long executionTime = calculateExecutionTime(logRecord, logRecord1);
						eAlert = EventAlert
								.builder()
								.duration(Math.toIntExact(executionTime))
								.host(logRecord.getHost())
								.id(logRecord.getId())
								.type(logRecord.getType())
								.build();
						
						// if the execution time is more than the specified threshold, flag the alert as TRUE						
						if (executionTime > this.eventTimeoutInMilliseconds) {
							eAlert.setAlert(Boolean.TRUE);
							logger.info("!!! Execution time for the event {} is {}ms", logRecord.getId(),
									executionTime);
						}
						// add it to the pool of alerts that are yet to be persisted
						logEventAlertLocalStore.put(logRecord.getId(), eAlert);
						// we are done with logrecord as we found the matching event. Keep memory in control.
						logRecordLocalStore.remove(logRecord.getId());
					} 
					else {
						logRecordLocalStore.put(logRecord.getId(), logRecord);
					}
				} catch (JsonProcessingException execption) {
					logger.info("Unable to parse the log record!.. {}", execption.getMessage());
				}
				// to reduce i/o, write the event alerts in batches
                if (logEventAlertLocalStore.size() > this.getBatchSizeForDbInsertion()) {
                    persistAlerts(logEventAlertLocalStore.values());
                    logEventAlertLocalStore = new ConcurrentHashMap<>();
                }
			}
			if (logEventAlertLocalStore.size() != 0) {
                persistAlerts(logEventAlertLocalStore.values());
            }

		} catch (Exception e) {
			logger.info("Unable to read the file. {}", e.getMessage());
			e.printStackTrace();
		}
		logger.info("logEventAlertsMap is :" + logEventAlertLocalStore.toString());
	}
	
	private long calculateExecutionTime(@NonNull LogRecordDetails logRecord, @NonNull LogRecordDetails logRecord1) {
		if(logRecord.getState().equals(State.FINISHED)) 
			return logRecord.getTimestamp() - logRecord1.getTimestamp();
		else
			return logRecord1.getTimestamp() - logRecord.getTimestamp();
	}

	public void setEventTimeoutInMilliseconds(long eventTimeoutInMilliseconds) {
		this.eventTimeoutInMilliseconds = eventTimeoutInMilliseconds;
	}

	public int getBatchSizeForDbInsertion() {
		return batchSizeForDbInsertion;
	}

	public void setBatchSizeForDbInsertion(int batchSizeForDbInsertion) {
		this.batchSizeForDbInsertion = batchSizeForDbInsertion;
	}
    private void persistAlerts(Collection<EventAlert> alerts) {
        logger.info("Persisting {} eventAlerts...", alerts.size());
        eventAlertRepository.saveAll(alerts);
    }
}
