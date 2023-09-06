package com.example.SpringBatchTutorial;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HelloWorldJobConfig {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    public HelloWorldJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }


    @Bean
    public Job helloWorldJob() {
        return jobBuilderFactory.get("helloWorldJob") // 이름을 정해준다.
                .incrementer(new RunIdIncrementer())
                .start(helloWorldStep())
                .build();
    }

    @Bean
    @JobScope
    public Step helloWorldStep() {
        return stepBuilderFactory.get("helloWorldStep")
                .tasklet(helloWorldTasklet()) // 간단한 작업인 경우 tasklet을 사용한다.
                .build();
    }

    @Bean
    @JobScope
    public Tasklet helloWorldTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Hello, World! Spring Batch!");
                return RepeatStatus.FINISHED;
            }
        };
    }
}
