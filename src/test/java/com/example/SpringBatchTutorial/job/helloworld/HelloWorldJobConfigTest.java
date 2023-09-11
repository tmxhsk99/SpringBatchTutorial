package com.example.SpringBatchTutorial.job.helloworld;

import com.example.SpringBatchTutorial.SpringBatchTestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@SpringBatchTest
@SpringBootTest(classes = {HelloWorldJobConfig.class, SpringBatchTestConfig.class})
class HelloWorldJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    @DisplayName("HelloWorldJobConfig 실행 시 ExitStatus COMPLETED 을 반환하는지 확인")
    public void success() throws Exception {
        // when
        JobExecution excution = jobLauncherTestUtils.launchJob();

        // then
        Assertions.assertThat(excution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }
}
