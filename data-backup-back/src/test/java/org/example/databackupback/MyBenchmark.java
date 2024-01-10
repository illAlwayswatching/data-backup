package org.example.databackupback;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/9 11:32
 **/
@State(Scope.Thread)
public class MyBenchmark {
    private List<String> list;

    @Setup
    public void setup() {
        list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(UUID.randomUUID().toString());
        }
    }

    @TearDown
    public void tearDown() {
        list = null;
    }

    @Benchmark
    public void testSort() {
        Collections.sort(list);
    }
}