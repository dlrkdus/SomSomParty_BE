package com.acc.somsomparty.domain.Reservation.service;

import com.acc.somsomparty.domain.Reservation.converter.ReservationConverter;
import com.acc.somsomparty.domain.Reservation.dto.ReservationRequestDTO;
import com.acc.somsomparty.domain.Reservation.dto.ReservationResponseDTO;
import com.acc.somsomparty.domain.Reservation.entity.Reservation;
import com.acc.somsomparty.domain.Reservation.repository.ReservationRepository;
import com.acc.somsomparty.domain.Ticket.entity.Ticket;
import com.acc.somsomparty.domain.Ticket.repository.TicketRepository;
import com.acc.somsomparty.domain.User.entity.User;
import com.acc.somsomparty.domain.User.repository.UserRepository;
import com.acc.somsomparty.global.exception.CustomException;
import com.acc.somsomparty.global.exception.error.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationCommandServiceImpl implements ReservationCommandService{
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public ReservationResponseDTO.makeReservationResultDTO makeReservation(Long userId, ReservationRequestDTO.makeReservationDTO request) {
        Ticket ticket = ticketRepository.findByFestivalIdAndFestivalDateWithLock(request.getFestivalId(), request.getFestivalDate()).orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(ticket.getLeftTickets() >= 1) {
            ticket.setLeftTickets(ticket.getLeftTickets() - 1);
            ticketRepository.save(ticket);
            Reservation reservation = ReservationConverter.toReservation(user, ticket);
            reservationRepository.saveAndFlush(reservation);
            return ReservationConverter.makeReservationResultDTO(reservation);
        }
        // 만약 남은 티켓 수가 0이면 예외를 던짐
        throw new CustomException(ErrorCode.TICKET_SOLD_OUT);
    }
}
