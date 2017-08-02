package cn.lemon.fileservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * 项目启动后配置
 * Created by lonyee on 2017/4/13.
 */
public class FileServiceApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    static Logger logger = LoggerFactory.getLogger(FileServiceApplicationStartup.class);
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
       
    }
}