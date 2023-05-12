package com.application.transaction_startup;

import com.application.transaction_startup.repo.AccountRepository;
import com.application.transaction_startup.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;


@SpringBootTest(classes = {TransactionStartupApplication.class})
@RunWith(SpringRunner.class)
class TransactionStartupApplicationTests {

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRepository accountRepository;

	@Test
	void singleThreadAndTRxInSequence() {
		assertEquals(10L, accountRepository.getBalance("Alice-123"));
		assertEquals(0L, accountRepository.getBalance("Bob-456"));

		accountService.transfer("Alice-123","Bob-456",5L);
		assertEquals(5L, accountRepository.getBalance("Alice-123"));
		assertEquals(5L, accountRepository.getBalance("Bob-456"));

		accountService.transfer("Alice-123","Bob-456",5L);
		assertEquals(0L, accountRepository.getBalance("Alice-123"));
		assertEquals(10L, accountRepository.getBalance("Bob-456"));
	}

	@Test
	void testParallelExecution() throws InterruptedException {
		long transferAmount = 5L;
//		assertEquals(10L, accountRepository.getBalance("Alice-123"));
//		assertEquals(0L, accountRepository.getBalance("Bob-456"));
		int threadCount = 10;
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch endLatch = new CountDownLatch(threadCount);

		for (int i = 0 ; i < threadCount; i++){
			new Thread(() ->{
				try{
					startLatch.await();
					accountService.transfer("Alice-123","Bob-456",5L);

				}catch (Exception e){
					System.out.println("Exception");
					e.printStackTrace();
				}finally {
					endLatch.countDown();
				}
			}).start();
		}
		System.out.println("Starting threads");
		startLatch.countDown();
		System.out.println("Main thread waits for all transfer threads to finish");
		endLatch.await();

		System.out.println("Alice's balance: "+ accountRepository.getBalance("Alice-123"));
		System.out.println("Bob's balance: "+ accountRepository.getBalance("Bob-456"));

	}

	protected void awaitOnLatch(CountDownLatch latch) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}
