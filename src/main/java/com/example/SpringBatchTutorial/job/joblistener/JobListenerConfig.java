package com.example.SpringBatchTutorial.job.joblistener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * desc : JobListener를 사용하여 Job 실행 전후에 로그를 남긴다.
 * run : --spring.batch.job.names=jobListenerJob
 */
@Configuration
@RequiredArgsConstructor
public class JobListenerConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobListenerJob(Step jobListenerStep) {
        return jobBuilderFactory.get("jobListenerJob") // 이름을 정해준다.
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener()) // JobListener를 등록한다.
                .start(jobListenerStep)
                .build();
    }

    @JobScope
    @Bean
    public Step jobListenerStep(Tasklet jobListenerStepTasklet) {
        return stepBuilderFactory.get("jobListenerStep")
                .tasklet(jobListenerStepTasklet)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet jobListenerStepTasklet() {
        return (contribution, chunkContext) -> { // new Tasklet() { ... } 대신 람다식을 사용할 수 있다.
            System.out.println("Job Listener Tasklet");
            return RepeatStatus.FINISHED;
        };
    }
}
