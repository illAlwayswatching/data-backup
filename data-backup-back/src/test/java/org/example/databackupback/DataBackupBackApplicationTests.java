package org.example.databackupback;

import org.example.databackupback.utils.FileUtil;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class DataBackupBackApplicationTests {

    @Autowired
    FileUtil fileUtil;

    @Test
    void contextLoads() {
        File dir = new File("E:\\MyCode\\data-backup\\data-backup-back\\gary\\新建文件夹\\新建文件夹");
        fileUtil.mkdir(dir);
    }

//    @Test
//    public void testMyBenchmark() throws Exception {
//        Options options = new OptionsBuilder()
//                .include(MyBenchmark.class.getSimpleName())
//                .forks(1)
//                .threads(1)
//                .warmupIterations(5)
//                .measurementIterations(5)
//                .mode(Mode.AverageTime)
//                .build();
//
//        new Runner(options).run();
//    }

}
