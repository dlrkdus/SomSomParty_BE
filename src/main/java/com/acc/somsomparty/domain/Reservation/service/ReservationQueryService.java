package com.acc.somsomparty.domain.Reservation.service;

import com.acc.somsomparty.domain.Reservation.dto.ReservationResponseDTO;
import com.acc.somsomparty.domain.Reservation.entity.Reservation;
import com.acc.somsomparty.domain.User.entity.User;

import java.util.List;

public interface ReservationQueryService {
    ReservationResponseDTO.ReservationPreViewListDTO getReservationList(Long userId, Long lastId, int offset);
    List<User> getReservationListByFestivalId(Long festivalId);
}
