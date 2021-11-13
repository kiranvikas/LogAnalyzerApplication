package cs.demo.loganalyzer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cs.demo.loganalyzer.repository.EventAlertRepository;
import cs.demo.loganalyzer.service.LogAnalyzerService;

@SpringBootTest
class LogAnalyzerServiceTest {
	private static Logger logger = LoggerFactory.getLogger(LogAnalyzerServiceTest.class);

	@Autowired
	private EventAlertRepository eventAlertRepository;

	@Autowired
	private LogAnalyzerService logAnalyzerService;

	@Test
	void testProcess() {
		logAnalyzerService.process(String.join("/", "", "logfile.txt"));
		long countOfRecordsInserted= eventAlertRepository.count();
		logger.info("Total count of record in event_alert table:" + countOfRecordsInserted);
		 assertThat(eventAlertRepository.count() > 0);
	}

}
