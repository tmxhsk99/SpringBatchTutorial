package com.example.SpringBatchTutorial.job.validateparam;

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

/**
 * desc : Hello World 를 출력
 * run : --spring.batch.job.names=validatedPramJob
 */
@Configuration
@RequiredArgsConstructor
public class ValidatedParamJobConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job validatedParamJob(Step validatedParamStep) {
        return jobBuilderFactory.get("validatedPramJob") // 이름을 정해준다.
                .incrementer(new RunIdIncrementer())
                .start(validatedParamStep)
                .build();
    }

    @Bean
    @JobScope
    public Step validatedParamStep(Tasklet validatedParamStepTasklet) {
        return stepBuilderFactory.get("validatedParamStep")
                .tasklet(validatedParamStepTasklet) // 간단한 작업인 경우 tasklet을 사용한다.
                .build();
    }

    @Bean
    @JobScope
    public Tasklet validatedParamStepTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("validated Param Tasklet");
                return RepeatStatus.FINISHED;
            }
        };
    }
}
