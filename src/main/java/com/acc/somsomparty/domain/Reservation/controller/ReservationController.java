package com.acc.somsomparty.domain.Reservation.controller;

import com.acc.somsomparty.domain.Reservation.dto.ReservationRequestDTO;
import com.acc.somsomparty.domain.Reservation.dto.ReservationResponseDTO;
import com.acc.somsomparty.domain.Reservation.service.ReservationCommandService;
import com.acc.somsomparty.domain.Reservation.service.ReservationQueryService;
import com.acc.somsomparty.domain.User.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;
    private final UserService userService;

    @Operation(summary = "예약 목록 조회", description = "사용자의 예약 목록을 조회합니다.")
    @GetMapping("")
    public ResponseEntity<ReservationResponseDTO.ReservationPreViewListDTO> getReservationList(@RequestParam(defaultValue = "0") Long lastId, @RequestParam(defaultValue = "10") int limit) {
        Long userId = userService.getIdByAuthentication();
        ReservationResponseDTO.ReservationPreViewListDTO reservationPage = reservationQueryService.getReservationList(userId, lastId, limit);
        return new ResponseEntity<>(reservationPage, HttpStatus.OK);
    }

    @Operation(summary = "예약하기", description = "사용자 정보와 예약 날짜로 예약합니다.")
    @PostMapping("")
    public ResponseEntity<ReservationResponseDTO.makeReservationResultDTO> makeReservation(@RequestBody ReservationRequestDTO.makeReservationDTO request) {
        Long userId = userService.getIdByAuthentication();
        ReservationResponseDTO.makeReservationResultDTO reservationResultDTO = reservationCommandService.makeReservation(userId, request);
        return new ResponseEntity<>(reservationResultDTO, HttpStatus.OK);
    }
}
