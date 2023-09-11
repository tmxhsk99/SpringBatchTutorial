package com.example.SpringBatchTutorial.job.dbdatareadwrite;

import com.example.SpringBatchTutorial.SpringBatchTestConfig;
import com.example.SpringBatchTutorial.core.domain.accounts.AccountsRepository;
import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import com.example.SpringBatchTutorial.core.domain.orders.OrdersRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;


@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@SpringBatchTest
@SpringBootTest(classes = {TrMigrationConfig.class, SpringBatchTestConfig.class})
class TrMigrationConfigTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private AccountsRepository accountsRepository;


    @AfterEach
    public void cleanUpEach() {
        ordersRepository.deleteAll();
        accountsRepository.deleteAll();
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class trMigrationJob_은 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 이관할_데이터가_없는_경우 {
            private JobExecution execution;
            @BeforeEach
            public void setUp() throws Exception {
                // when
                execution = jobLauncherTestUtils.launchJob();
            }

            @DisplayName("COMPLETED 상태를 가지고, 데이터를 이관하지 않는다")
            @Test
            public void it_should_return_completed_status() {
                Assertions.assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
                Assertions.assertThat(accountsRepository.count()).isEqualTo(0);
            }
            
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 이관할_데이터가_있는_경우 {
            private JobExecution execution;

            @BeforeEach
            public void setUp() throws Exception {
                // given
                ordersRepository.save(Orders.builder()
                                .id(null)
                                .orderItem("test gift")
                                .price(10000)
                                .orderDate(new Date())
                        .build());

                ordersRepository.save(Orders.builder()
                        .id(null)
                        .orderItem("test2 gift")
                        .price(10000)
                        .orderDate(new Date())
                        .build());

                // when
                execution = jobLauncherTestUtils.launchJob();
            }

            @DisplayName("COMPLETED 상태를 가지고, 데이터를 이관처리 한다")
            @Test
            public void it_should_return_completed_status() {
                Assertions.assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
                Assertions.assertThat(accountsRepository.count()).isEqualTo(2);
            }
        }
    }

}
