package com.project.SLX.service;

import com.project.SLX.model.Listing;
import com.project.SLX.model.Log;
import com.project.SLX.model.enums.LogTypeEnum;
import com.project.SLX.repository.ListingRepository;
import com.project.SLX.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {
    private final LogRepository logRepository;
    private final ListingRepository listingRepository;

    @Autowired
    public LogService(LogRepository logRepository, ListingRepository listingRepository) {
        this.logRepository = logRepository;
        this.listingRepository = listingRepository;
    }

    public Log addLog(Long listingId, LogTypeEnum logType) {
        Listing listing = listingRepository.findByListingId(listingId).orElse(null);
        if(listing != null) {
            Log log = new Log();
            log.setTimeStamp(LocalDateTime.now());
            log.setListOwn(listing);
            if(logType.equals(LogTypeEnum.CREATE)) {
                log.setEvent("New listing was created!");
            } else if(logType.equals(LogTypeEnum.UPDATE)) {
                log.setEvent("Listing was updated!");
            }
            return logRepository.saveAndFlush(log);
        }
        return null;
    }
}
