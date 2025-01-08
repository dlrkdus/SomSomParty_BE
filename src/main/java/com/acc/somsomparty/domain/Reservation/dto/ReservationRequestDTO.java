package com.acc.somsomparty.domain.Reservation.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class ReservationRequestDTO {
    @Getter
    public static class makeReservationDTO {
        Long festivalId;
        LocalDate festivalDate;
    }
}
