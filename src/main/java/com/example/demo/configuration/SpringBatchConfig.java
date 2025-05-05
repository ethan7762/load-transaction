package com.example.demo.configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.model.dto.TransactionRecordDto;
import com.example.demo.service.TransactionRecordService;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
	
	private final PlatformTransactionManager platformTransactionManager;
    private final JobRepository jobRepository;
    
    private final TransactionRecordService transactionRecordService;

    public SpringBatchConfig(PlatformTransactionManager platformTransactionManager, JobRepository jobRepository, TransactionRecordService transactionRecordService) {
        this.platformTransactionManager = platformTransactionManager;
        this.jobRepository = jobRepository;
        this.transactionRecordService = transactionRecordService;
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
        		.<String, TransactionRecordDto>chunk(10, platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job job() {
        return new JobBuilder("loadTransactionJob", jobRepository)
                .start(step1())
                .build();
    }

    @Bean
    public FlatFileItemReader<String> reader() {
        return new FlatFileItemReader<>() {{
            setResource(new ClassPathResource("dataSource.txt"));
            setLinesToSkip(1);
            setLineMapper((line, lineNumber) -> line);
        }};
    }

    @Bean
	public ItemProcessor<String, TransactionRecordDto> processor() {
		return line -> {
			if (line == null || line.trim().isEmpty())
				return null; // skip blank

			String[] tokens = line.split("\\|");
			if (tokens.length != 6) {
				return null;
			}

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

			TransactionRecordDto dto = new TransactionRecordDto();
			dto.setAccountNumber(tokens[0]);
			dto.setTrxAmount(new BigDecimal(tokens[1]));
			dto.setDescription(tokens[2]);
			dto.setTrxDate(LocalDate.parse(tokens[3], dateFormatter));
			dto.setTrxTime(LocalTime.parse(tokens[4], timeFormatter));
			dto.setCustomerId(Long.parseLong(tokens[5]));
			return dto;
		};
	}
	
	@Bean
	public ItemWriter<TransactionRecordDto> writer() {
	    return items -> {
	        for (TransactionRecordDto dto : items) {
	        	transactionRecordService.saveTransactionRecord(dto);
	        }
	    };
	}
	
}
