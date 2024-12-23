package com.acc.somsomparty.domain.Reservation.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class ReservationRequestDTO {
    @Getter
    @Builder
    public static class makeReservationDTO {
        Long userId;
        Long festivalId;
        LocalDate festivalDate;
    }
}
