package com.example.SpringBatchTutorial.job.validateparam;

import com.example.SpringBatchTutorial.job.validateparam.validator.FileParamValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * desc : 파일 이름 파라미터 전달 그리고 검증
 * run : --spring.batch.job.names=validatedPramJob -fileName=test.csv
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
                //.validator(new FileParamValidator()) // 단일 Validator 사용시 JobParametersValidator를 구현한 클래스를 등록한다.
                .validator(multipleValidator()) // 복수의 Validator를 사용할 경우 CompositeJobParametersValidator를 사용한다.
                .start(validatedParamStep)
                .build();
    }

    private CompositeJobParametersValidator multipleValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(new FileParamValidator())); // 여기에 복수의 Validator를 등록할 수 있다.
        return validator;
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
    public Tasklet validatedParamStepTasklet(@Value("#{jobParameters['fileName']}") String fileName) {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.printf("fileName = %s\n", fileName);
                System.out.println("validated Param Tasklet");
                return RepeatStatus.FINISHED;
            }
        };
    }
}
