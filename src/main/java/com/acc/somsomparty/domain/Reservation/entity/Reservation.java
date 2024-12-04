package com.acc.somsomparty.domain.Reservation.entity;

import com.acc.somsomparty.domain.Festival.enums.ReservationStatus;
import com.acc.somsomparty.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "festival_id")
    private Long festivalId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "reservation_date", nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDate reservationDate;
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;
    @Column(name = "reservation_count", nullable = false)
    private Integer reservationCount;
}
