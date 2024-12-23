package com.acc.somsomparty.reservation.service;

import com.acc.somsomparty.domain.Queue.service.UserQueueService;
import com.acc.somsomparty.domain.Reservation.dto.ReservationRequestDTO;
import com.acc.somsomparty.domain.Reservation.repository.ReservationRepository;
import com.acc.somsomparty.domain.Reservation.service.ReservationQueryService;
import com.acc.somsomparty.domain.Ticket.entity.Ticket;
import com.acc.somsomparty.domain.Ticket.repository.TicketRepository;
import com.acc.somsomparty.global.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ReservationServiceTest {
    @Autowired
    private ReservationQueryService reservationQueryService;     // 예매 서비스
    @Autowired
    private ReservationRepository reservationRepository; // 예약 테이블
    @Autowired
    private UserQueueService userQueueService;
//    @Autowired
//    private ReservationCommandService reservationCommandService;


//    @Test
//    void 티켓_동시_예매_테스트() throws InterruptedException {
//        // given
//        int memberCount = 30;
//        int ticketAmount = 10;
//
//
//        ExecutorService executorService = Executors.newFixedThreadPool(30);
//        CountDownLatch latch = new CountDownLatch(memberCount);
//
//        AtomicInteger successCount = new AtomicInteger();
//        AtomicInteger failCount = new AtomicInteger();
//        ReservationRequestDTO.makeReservationDTO dto = ReservationRequestDTO.makeReservationDTO.builder()
//                .userId(1L)
//                .festivalId(1L)
//                .festivalDate(LocalDate.now())
//                .build();
//        // when
//        for (int i = 0; i < memberCount; i++) {
//            executorService.submit(() -> {
//                try {
//                    reservationCommandService.makeReservation(dto);
//                    System.out.println(dto);
//                    successCount.incrementAndGet();
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                    failCount.incrementAndGet();
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        System.out.println("successCount = " + successCount);
//        System.out.println("failCount = " + failCount);
//
//    }

    @Test
    void 쿠폰차감_분산락_적용_동시성100명_테스트() throws InterruptedException, ExecutionException {

        int numberOfThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            tasks.add(() -> {
                try {
                    // 로그 추

                    // 분산락 적용 메서드 호출
                    userQueueService.allowUser("festival1", 3L);
                    return "Success";
                } catch(CustomException e){
                    return "CustomException: " + e.getMessage();
                }
            });
        }

// 10개의 스레드 실행
        List<Future<String>> results = executorService.invokeAll(tasks);

        // 결과 확인
        int successCount = 0;
        int failureCount = 0;
        int customExceptionCount = 0;


        for (Future<String> result : results) {
            String res = result.get();
            if (res.startsWith("Success")) {
                successCount++;
            } else {
                failureCount++;
                if (res.startsWith("CustomException")) {
                    customExceptionCount++;
                }
            }
            System.out.println(res);
        }
        executorService.shutdown();
    }

}
