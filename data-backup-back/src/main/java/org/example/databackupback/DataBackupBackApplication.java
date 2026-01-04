package org.example.databackupback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataBackupBackApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DataBackupBackApplication.class);
        // 如果设置了 SPRING_PROFILES_ACTIVE，使用它，否则使用默认配置
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");
        if (profile != null && !profile.isEmpty()) {
            app.setAdditionalProfiles(profile);
        }
        app.run(args);
    }

}
