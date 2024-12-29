package com.acc.somsomparty.domain.Notification.repository;

import com.acc.somsomparty.domain.Notification.entity.FcmToken;
import com.acc.somsomparty.domain.Notification.projection.FestivalTokenProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    // 축제 시작 날짜를 기준으로 예약자의 fcm token 가져오기
    @Query("SELECT ft.fcmToken AS fcmToken, f AS festival " +
            "FROM FcmToken ft " +
            "JOIN Reservation r ON ft.userId = r.user.id " +
            "JOIN Ticket t ON r.ticket.id = t.id " +
            "JOIN Festival f ON t.festival.id = f.id " +
            "WHERE f.startDate = :tomorrow")
    List<FestivalTokenProjection> findTokensForTomorrowFestival(@Param("tomorrow") LocalDate tomorrow);

    FcmToken findByFcmTokenAndUserId(String fcmToken, Long userId);
}
