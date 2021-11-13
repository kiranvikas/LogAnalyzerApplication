/**
 * 
 */
package cs.demo.loganalyzer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cs.demo.loganalyzer.service.LogAnalyzerService;

/**
 * @author vikas.kirange
 *
 *Controller class to monitor the requests / data flow in to the application and dispaches the
 * request to specific service class.
 * 
 *Application can be accessed by using url:	
 *http://localhost:8085//loganalyzer/api/v1/logfile?filename=logfile.txt
 */

@RestController
@RequestMapping("/loganalyzer/api/v1")
public class LogAnalyserRestController {
	@Autowired
	private LogAnalyzerService logAnalyzerService;
	private static Logger logger = LoggerFactory.getLogger(LogAnalyserRestController.class);
	
	@RequestMapping(method = RequestMethod.GET, value = "/logfile", produces = "text/plain")
	public ResponseEntity<String> processLogFile(@RequestParam("filename") String filename) {
	
		try {
			 this.logAnalyzerService.process(filename);			 
		} 
		catch (Exception e) {
			logger.info("Exception while reading file.", e);
		}
		return ResponseEntity
		        .ok()
		        .body(String.format("Processing file '%s'. Check logs for further details.", filename));
	}
}
