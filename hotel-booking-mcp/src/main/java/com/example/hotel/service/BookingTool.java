package com.example.hotel.service;

import com.example.hotel.entity.Booking;
import com.example.hotel.repository.BookingRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;


@Component
public class BookingTool {
    private final BookingRepository repository;

    public BookingTool(BookingRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "bookhotel", description = "Book a hotel for a guest with given details")
    public String bookHotel(
            @JsonProperty("guestName") String guestName,
            @JsonProperty("roomType") String roomType,
            @JsonProperty("checkInDate") String checkIn,
            @JsonProperty("checkOutDate") String checkOut) {

        Booking booking = new Booking();
        booking.setGuestName(guestName);
        booking.setRoomType(roomType);
        booking.setCheckIn(LocalDate.parse(checkIn));
        booking.setCheckOut(LocalDate.parse(checkOut));

        repository.save(booking);

        return "Hotel booked successfully for " + guestName +
                " (" + roomType + ") from " + checkIn + " to " + checkOut;
    }

    @Tool(name = "listHotelBookings", description = "List all hotel bookings")
    public List<Booking> listBookings() {
        return repository.findAll();
    }

}
