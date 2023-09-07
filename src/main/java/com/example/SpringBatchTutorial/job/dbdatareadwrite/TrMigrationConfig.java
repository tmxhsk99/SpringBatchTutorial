package com.example.SpringBatchTutorial.job.dbdatareadwrite;


import com.example.SpringBatchTutorial.core.domain.accounts.AccountsRepository;
import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import com.example.SpringBatchTutorial.core.domain.orders.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * desc : 주문 테이블 -> 정산 테이블 데이터 이관
 * run : --spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final OrdersRepository ordersRepository;
    private final AccountsRepository accountsRepository;

    @Bean
    public Job trMigrationJob(Step trMigrationStep) {
        return jobBuilderFactory.get("trMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    @JobScope
    @Bean
    public Step trMigrationStep(ItemReader trOrdersReader) {
        return stepBuilderFactory.get("trMigrationStep")
                .<Orders, Orders>chunk(5)// ItemReader의 경우 어떤데이터로 읽어서 어떤데이터로 쓸건지 명시해야한다 / 5개 단위로 데이터를 처리하겠다 처리할데이터의 트랜잭션 사이즈 5개의 데이터를 커밋
                .reader(trOrdersReader)
                .writer(new ItemWriter() {
                    @Override
                    public void write(List items) throws Exception {// 단순히 출력만 수행한다.
                        items.forEach(System.out::println); // 출력
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Orders> trOrdersReader() {
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll") // 사용할 명령어
                .pageSize(5) // 읽어올 데이터 사이즈 [청크사이즈와 동일하게 지정한다.]
                .arguments(Arrays.asList()) // 메소드에 전달할 파라미터 : 전달할 매개변수가 없으므로 빈리스트를 전달한다.
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC)) // 오름차순 정렬
                .build();
    }

}
