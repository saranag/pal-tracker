package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository timeEntryRepository;
    private final DistributionSummary timeEntrySummary;
    private final Counter actionCounter;

    public TimeEntryController(TimeEntryRepository timeEntryRepository,
                               MeterRegistry meterRegistry) {
        this.timeEntryRepository = timeEntryRepository;

        timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        actionCounter = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping
    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {
        try{
            TimeEntry timeEntry = timeEntryRepository.create(timeEntryToCreate);
            actionCounter.increment();
            timeEntrySummary.record(timeEntryRepository.list().size());
            return new ResponseEntity(timeEntry,HttpStatus.CREATED);
        }catch(Exception ex){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{timeEntryId}")
    public ResponseEntity delete(@PathVariable long timeEntryId) {
        timeEntryRepository.delete(timeEntryId);
        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());
        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }

    @PutMapping("{timeEntryId}")
    public ResponseEntity update(@PathVariable long timeEntryId, @RequestBody TimeEntry timeEntry) {

        TimeEntry newTimeEntry = timeEntryRepository.update(timeEntryId, timeEntry);
        if (newTimeEntry == null){
            actionCounter.increment();
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity(newTimeEntry,HttpStatus.OK);
        }

      /*  if(timeEntryRepository.find(timeEntryId)!= null)
        {
            TimeEntry newTimeEntry = timeEntryRepository.update(timeEntryId, timeEntry);
            return new ResponseEntity(newTimeEntry,HttpStatus.OK);
        }
        else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }*/
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        actionCounter.increment();
        List<TimeEntry> timeEntries = timeEntryRepository.list();
        return new ResponseEntity(timeEntries,HttpStatus.OK);
    }

    @GetMapping("{timeEntryId}")
    public ResponseEntity<TimeEntry> read(@PathVariable long timeEntryId) {
        TimeEntry timeEntry = timeEntryRepository.find(timeEntryId);
        if(timeEntry != null)
        {
            actionCounter.increment();
            return new ResponseEntity(timeEntry, HttpStatus.OK);
        }
        else
            return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
