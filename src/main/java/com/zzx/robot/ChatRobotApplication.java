package com.zzx.robot;

import com.zzx.robot.framing.AutoFarmingMonitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zzx
 */
@SpringBootApplication
public class ChatRobotApplication {

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "farming".equals(args[0])) {
            AutoFarmingMonitor monitor = new AutoFarmingMonitor();
            monitor.monitor();
        } else {
            SpringApplication.run(ChatRobotApplication.class, args);
        }

    }

}
