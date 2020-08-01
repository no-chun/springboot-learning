package com.chun.aopdemo.repository;

import com.chun.aopdemo.model.LogMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogMessage, Long> {
}
