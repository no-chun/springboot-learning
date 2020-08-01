package com.chun.aopdemo.service.Impl;

import com.chun.aopdemo.model.LogMessage;
import com.chun.aopdemo.repository.LogRepository;
import com.chun.aopdemo.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }


    @Override
    public void saveLog(LogMessage logMessage) {
        logRepository.save(logMessage);
    }
}
