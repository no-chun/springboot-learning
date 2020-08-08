# 定时任务

定时任务只需引入 Spring Boot Starter 包即可。

启动定时任务需要在启动类上加上注解`@EnableScheduling`即可。

然后就是在定时任务的类上加上`@Component`注解，生成Bean。然后就是在需要定时运行的方法上加上`@Scheduled`注解即可。

`@Scheduled`有两种方式定义：

* 类似于Linux的crontab，定义cron="crontab表达式"即可

* 设置fixedRate或fixedDelay

    * @Scheduled(fixedRate = 1000) ：上一次开始执行时间点之后1秒再执行
     
    * @Scheduled(fixedDelay = 1000) ：上一次执行完毕时间点之后1秒再执行
      
    * @Scheduled(initialDelay=1000, fixedRate=6000) ：第一次延迟1秒后执行，之后按 fixedRate 的规则每1秒执行一次
    
