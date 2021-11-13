package cs.demo.loganalyzer.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import cs.demo.loganalyzer.entity.EventAlert;

@Repository
public interface EventAlertRepository extends CrudRepository<EventAlert, String> {
}
