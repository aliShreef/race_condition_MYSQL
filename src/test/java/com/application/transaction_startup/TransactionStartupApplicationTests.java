package com.application.transaction_startup;

import com.application.transaction_startup.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;


@SpringBootTest
class TransactionStartupApplicationTests {

	@Autowired
	AccountService accountService;

	@Test
	void singleThreadAndTRxInSequence() {
		assertEquals(10L, accountService.getBalance("Alice-123"));
		assertEquals(0L, accountService.getBalance("Bob-456"));

		accountService.transfer("Alice-123","Bob-456",5L);
		assertEquals(5L, accountService.getBalance("Alice-123"));
		assertEquals(5L, accountService.getBalance("Bob-456"));

		accountService.transfer("Alice-123","Bob-456",5L);
		assertEquals(0L, accountService.getBalance("Alice-123"));
		assertEquals(10L, accountService.getBalance("Bob-456"));
	}


	@Test
	void multipleThreadTransferWithDifferentDBConnection(){
		assertEquals(10L, accountService.getBalance("Alice-123"));
		assertEquals(0L, accountService.getBalance("Bob-456"));
		int threadCount = 8;
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch endLatch = new CountDownLatch(threadCount);

		for (int i = 0 ; i < threadCount; i++){
			new Thread(() ->{
				awaitOnLatch(startLatch);
				accountService.transfer("Alice-123","Bob-456",5L);
				endLatch.countDown();
			}).start();
		}
		System.out.println("Starting threads");
		startLatch.countDown();
		System.out.println("Main thread waits for all transfer threads to finish");
		awaitOnLatch(endLatch);

		System.out.println("Alice's balance: "+ accountService.getBalance("Alice-123"));
		System.out.println("Bob's balance: "+ accountService.getBalance("Bob-456"));

	}

	@Test
	void multipleThreadTransferWithSameDBConnection(){
		long transferAmount = 5L;
		assertEquals(10L, accountService.getBalance("Alice-123"));
		assertEquals(0L, accountService.getBalance("Bob-456"));
		int threadCount = 8;
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch endLatch = new CountDownLatch(threadCount);

		for (int i = 0 ; i < threadCount; i++){
			new Thread(() ->{
				try{
					accountService.doInJdbc(connection -> {
						// This line for solve issue in MYSQL, if you comment it and leave for DB to handle TRx issue exist.
						// try test case with this line and without
						connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
						awaitOnLatch(startLatch);
						long fromBalance = accountService.getBalance(connection,"Alice-123");
						if(fromBalance >= transferAmount){
							accountService.addBalance(connection,"Alice-123",(-1)*5);
							accountService.addBalance(connection,"Bob-456",5);
						}
					});
				}catch (Exception e){
					System.out.println("Exception");
					e.printStackTrace();
				}
				endLatch.countDown();
			}).start();
		}
		System.out.println("Starting threads");
		startLatch.countDown();
		System.out.println("Main thread waits for all transfer threads to finish");
		awaitOnLatch(endLatch);

		System.out.println("Alice's balance: "+ accountService.getBalance("Alice-123"));
		System.out.println("Bob's balance: "+ accountService.getBalance("Bob-456"));

	}

	protected void awaitOnLatch(CountDownLatch latch) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}
