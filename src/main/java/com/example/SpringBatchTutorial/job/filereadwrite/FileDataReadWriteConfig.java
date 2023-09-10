package com.example.SpringBatchTutorial.job.filereadwrite;

import com.example.SpringBatchTutorial.job.filereadwrite.dto.Player;
import com.example.SpringBatchTutorial.job.filereadwrite.dto.PlayerYears;
import com.example.SpringBatchTutorial.job.filereadwrite.mapper.PlayerFieldSetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/**
 * desc : 파일 읽고 쓰기
 * run : --spring.batch.job.names=fileReadWriteJob
 */
@Configuration
@RequiredArgsConstructor
public class FileDataReadWriteConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fileReadWriteJob(Step fileReadWriteStep) {
        return jobBuilderFactory.get("fileReadWriteJob")
                .incrementer(new RunIdIncrementer())
                .start(fileReadWriteStep)
                .build();
    }

    @JobScope
    @Bean
    public Step fileReadWriteStep(ItemReader playerItemReader,
                                  ItemProcessor playerItemProcessor,
                                  ItemWriter playerItemWriter) {
        return stepBuilderFactory.get("fileReadWriteStep")
                .<Player, PlayerYears>chunk(5)
                .reader(playerItemReader)
                .processor(playerItemProcessor)
                .writer(playerItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Player> playerItemReader() {
        return new FlatFileItemReaderBuilder<Player>()
                .name("playerItemReader") // reader의 이름을 지정
                .resource(new ClassPathResource("Players.csv"))
                .lineTokenizer(new DelimitedLineTokenizer()) // 구분자를 기준으로 한 라인을 토큰화
                .fieldSetMapper(new PlayerFieldSetMapper())// 읽어온 데이터를 객체로 매핑한다
                .linesToSkip(1) // 첫번째 라인은 헤더이므로 읽지 않는다
                .build();
    }

    /**
     * PlayerYears 객체를 csv 파일로 출력하는 ItemWriter
     * @return
     */
    @StepScope
    @Bean
    public FlatFileItemWriter<PlayerYears> playerItemWriter () {
        BeanWrapperFieldExtractor<PlayerYears> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"ID","lastName","position","yearsExperience"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator(); //어떤 기준으로 만드는지 알려준다.
        lineAggregator.setDelimiter(","); // 콤마로 구분한다
        lineAggregator.setFieldExtractor(fieldExtractor); // 어떤 필드를 추출할 것인가?

        FileSystemResource outputResource = new FileSystemResource("players_output.csv");

        return new FlatFileItemWriterBuilder<PlayerYears>()
                .name("playerYearsItemWriter")
                .resource(outputResource)
                .lineAggregator(lineAggregator)
                .build();
    }

    /**
     * Player 객체를 PlayerYears 객체로 변환하는 ItemProcessor
     * @return
     */
    @StepScope
    @Bean
    public ItemProcessor<Player,PlayerYears> playerItemProcessor() {
        return new ItemProcessor<Player, PlayerYears>() {
            @Override
            public PlayerYears process(Player item) throws Exception {
                return new PlayerYears(item);
            }
        };
    }

    /**
     * 콘솔 줄력을 위한 ItemWriter
     * @return
     */
    @Bean
    @JobScope
    public ItemWriter printItemWriter() {
        return new ItemWriter() {
            @Override
            public void write(List items) throws Exception {
                System.out.println("items.size() = " + items.size());
                items.forEach(System.out::println);
            }
        };
    }


}
