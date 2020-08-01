package com.chun.aopdemo.aspect;

import com.chun.aopdemo.annotation.Log;
import com.chun.aopdemo.model.LogMessage;
import com.chun.aopdemo.service.LogService;
import com.chun.aopdemo.util.HttpContextUtils;
import com.chun.aopdemo.util.IPUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class LogAspect {
    private final LogService logService;

    public LogAspect(LogService logService) {
        this.logService = logService;
    }

    @Pointcut("@annotation(com.chun.aopdemo.annotation.Log)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public void around(ProceedingJoinPoint point) {
        long beginTime = System.currentTimeMillis();
        try {
            point.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis() - beginTime;
        saveLog(point, time);
    }

    private void saveLog(ProceedingJoinPoint point, Long time) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        LogMessage logMessage = new LogMessage();
        Log log = method.getAnnotation(Log.class);
        if (log != null) {
            logMessage.setOperation(log.value());
        }
        String className = point.getTarget().getClass().getName();
        String methodName = signature.getName();
        logMessage.setMethod(className + "." + methodName + "()");
        Object[] args = point.getArgs();
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if (args != null && paramNames != null) {
            StringBuilder params = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                params.append("  ").append(paramNames[i]).append(": ").append(args[i]);
            }
            logMessage.setParams(params.toString());
        }
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        logMessage.setIp(IPUtils.getIpAddr(request));
        logMessage.setUsername("admin");
        logMessage.setTime(time);
        logMessage.setCreateTime(new Date());
        logService.saveLog(logMessage);
    }

}
