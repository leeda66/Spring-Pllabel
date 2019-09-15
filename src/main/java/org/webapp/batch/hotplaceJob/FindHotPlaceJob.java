package org.webapp.batch.hotplaceJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.webapp.batch.BatchSettings;
import org.webapp.model.Instaranking;
import javax.sql.DataSource;
import java.util.List;


@Configuration(value = "GetHotPlaceJobBean")
@EnableBatchProcessing
public class FindHotPlaceJob {
    private static final String JOB_NAME = "findHotPlace-Job";
    private static final String SETTING_STEP_NAME = "resetInstaranking-Step";
    private static final String FIRST_STEP_NAME = "setupPlaceRanking-Step";
    private static final String SECOND_STEP_NAME = "setupHotPlaceResult-Step";
    private static final int CHUNK_SIZE = 1;
    private static final Logger logger = LoggerFactory.getLogger(FindHotPlaceJob.class);

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private DataSource dataSource;
    private DataSourceTransactionManager transactionManager;
    private JdbcTemplate jdbcTemplate;

    FindHotPlaceJob() {}

    @Autowired
    public FindHotPlaceJob(JobBuilderFactory jobBuilderFactory,
                           StepBuilderFactory stepBuilderFactory,
                           DataSource dataSource,
                           DataSourceTransactionManager transactionManager) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @Bean
    public CronTriggerFactoryBean findHotPlaceTrigger() {
        return BatchSettings.cronTriggerFactoryBeanBuilder()
                .name("FindHotPlace-Trigger")
                .cronExpression("0 0/2 * * * ?") // 1~2분마다
                //.cronExpression("0 30 6 * * ? *") //매일 오전 6시 30분에
                .jobDetailFactoryBean(findHotPlaceJobSchedule())
                .build();
    }

    @Bean
    public JobDetailFactoryBean findHotPlaceJobSchedule() {
        return BatchSettings.jobDetailFactoryBeanBuilder()
                .job(findHotPlaceJob())
                .build();
    }

    @Bean
    public Job findHotPlaceJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(resetInstarankingStep())
                .next(setupPlaceRankingStep())
                //.next(setupHotPlaceResultStep())
                .build();
    }

    @Bean
    @JobScope
    public Step resetInstarankingStep() {
        return stepBuilderFactory.get(SETTING_STEP_NAME)
                .tasklet(new Tasklet() {

                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        logger.info("[FindHotPlaceJob] : ResetInstaranking-Tasklet started.");
                        String sql = "DELETE FROM Instaranking";
                        jdbcTemplate.update(sql);
                        return RepeatStatus.FINISHED;

                    }
                }).build();
    }

    @Bean
    @JobScope
    public Step setupPlaceRankingStep() {
        return stepBuilderFactory.get(FIRST_STEP_NAME)
                .<Instaranking, List<Instaranking>>chunk(CHUNK_SIZE)
                .reader(setupPlaceRankingReader())
                .processor(setupPlaceRankingProcessor())
                .writer(setupPlaceRankingWriter())
                .transactionManager(this.transactionManager)
                .build();
    }

    @Bean(destroyMethod = "")
    public SetupPlaceRankingReader setupPlaceRankingReader() {
        SetupPlaceRankingReader reader
                = new SetupPlaceRankingReader(CHUNK_SIZE, this.dataSource);
        reader.prepareForRead();

        return reader;
    }

    @Bean
    public ItemProcessor<Instaranking, List<Instaranking>> setupPlaceRankingProcessor() {
        return new SetupPlaceRankingProcessor();
    }

    @Bean
    public ItemWriter<List<Instaranking>> setupPlaceRankingWriter() {
        return new SetupPlaceRankingWriter();
    }

}