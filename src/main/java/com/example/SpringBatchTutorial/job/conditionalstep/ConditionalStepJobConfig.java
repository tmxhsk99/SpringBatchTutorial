package com.example.SpringBatchTutorial.job.conditionalstep;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
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
 * desc : step 결과에 따른 다음 step 실행
 * run :  --spring.batch.job.names=conditionalStepJob
 */
@Configuration
@RequiredArgsConstructor
public class ConditionalStepJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job conditionalStepJob (
            Step conditionalStartStep,
            Step conditionalAllStep,
            Step conditionalFailStep,
            Step conditionalCompletedStep
    ) {
        return jobBuilderFactory.get("conditionalStepJob")
                .incrementer(new RunIdIncrementer())
                .start(conditionalStartStep)
                    .on("FAILED").to(conditionalFailStep)
                .from(conditionalStartStep)
                    .on("COMPLETED").to(conditionalCompletedStep)
                .from(conditionalStartStep)
                    .on("*").to(conditionalAllStep)
                .end()
                .build();
    }

    @JobScope
    @Bean
    public Step conditionalStartStep() {
        return stepBuilderFactory.get("conditionalStartStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println(">>>>> conditional Start Step");
                        // throw new IllegalArgumentException("step 실패"); // 실제 Exception 발생 시 실패로 간주
                        // contribution.setExitStatus(ExitStatus.FAILED); // FINISHED를 반환하는 경우라도 ExitStatus를 FAILED로 설정하면 실패로 간주
                        // contribution.setExitStatus(ExitStatus.UNKNOWN); // 그 이외의 상태 값인 경우 allStep 실행됨
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @JobScope
    @Bean
    public Step conditionalAllStep() {
        return stepBuilderFactory.get("conditionalAllStep")
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println(">>>>> conditional All Step");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    @JobScope
    @Bean
    public Step conditionalFailStep() {
        return stepBuilderFactory.get("conditionalFailStep")
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println(">>>>> conditional Fail Step");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }


    @JobScope
    @Bean
    public Step conditionalCompletedStep() {
        return stepBuilderFactory.get("conditionalCompletestep")
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println(">>>>> conditional Complete Step");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }
}
