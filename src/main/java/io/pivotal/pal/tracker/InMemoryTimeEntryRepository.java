package io.pivotal.pal.tracker;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryTimeEntryRepository implements TimeEntryRepository{

    private Map<Long, TimeEntry> timeEntries = null;
    private Long index = 0L;

    public TimeEntry create(TimeEntry timeEntry) {
        if(timeEntries==null)
           this.timeEntries = new ConcurrentHashMap<>() ;
        timeEntry.setId(++index);
        timeEntries.put(index, timeEntry);
        return timeEntry;
    }

    public TimeEntry find(long id) {
        if(timeEntries==null)
            return null;
        return timeEntries.get(id);
    }

    public List<TimeEntry> list() {
        if(timeEntries==null)
            return null;
        return timeEntries.values().stream().collect(Collectors.toList());
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        if(timeEntries==null)
            return null;

        if (timeEntries.containsKey(id)){
            timeEntry.setId(id);
            timeEntries.put(id,timeEntry);
        }
        return find(id);

    }

    public void delete(long id) {
        timeEntries.remove(id);
    }

    public static void main(String args[])
    {
        System.out.println("Hello");
    }
}
