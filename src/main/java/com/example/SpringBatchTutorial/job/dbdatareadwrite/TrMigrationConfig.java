package com.example.SpringBatchTutorial.job.dbdatareadwrite;


import com.example.SpringBatchTutorial.core.domain.accounts.Accounts;
import com.example.SpringBatchTutorial.core.domain.accounts.AccountsRepository;
import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import com.example.SpringBatchTutorial.core.domain.orders.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * desc : 주문 테이블 -> 정산 테이블 데이터 이관
 * run : --spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
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
    public Step trMigrationStep(ItemReader trOrdersReader,ItemProcessor trOrdersProcessor, ItemWriter trOrdersRepositoryItemWriter) {
        return stepBuilderFactory.get("trMigrationStep")
                .<Orders, Accounts>chunk(5)// ItemReader의 경우 어떤데이터로 읽어서 어떤데이터로 쓸건지 명시해야한다 / 5개 단위로 데이터를 처리하겠다 처리할데이터의 트랜잭션 사이즈 5개의 데이터를 커밋
                .reader(trOrdersReader)
                .processor(trOrdersProcessor)
                .writer(trOrdersRepositoryItemWriter)
                .build();
    }

    /**
     * RepositoryItemWriter
     * 읽어들인 값을 Repostiory를 사용해 DB에 저장하는 역할
     * @return
     */
    @Bean
    @StepScope
    public RepositoryItemWriter<Accounts> trOrdersRepositoryItemWriter() {
        log.info("trOrdersRepositoryItemWriter 호출 !!!");
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")
                .build();
    }


    /**
     * 그냥 ItemWriter로 도 구현이 가능하다 이런 경우 직접 Repository를 호출하건 DB를 호출하건 해야한다.
     * @return
     */
    public ItemWriter<Accounts> trOrdersItemWriter() {
        log.info("trOrdersItemWriter 호출 !!!");
        return new ItemWriter<Accounts>() {
            @Override
            public void write(List<? extends Accounts> items) throws Exception {
                items.forEach(item -> accountsRepository.save(item));
            }
        };
    }

    /**
     * ItemReader로 읽어들인값을 결과 값으로 파싱하는 역할을 하는 함수이다.
     * @return
     */
    @StepScope
    @Bean
    public ItemProcessor<Orders,Accounts> trOrdersProcessor() {
        return orders -> {
            return Accounts.builder()
                    .id(orders.getId())
                    .orderItem(orders.getOrderItem())
                    .orderDate(orders.getOrderDate())
                    .accountDate(new Date())
                    .price(orders.getPrice())
                    .build();
        };
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
