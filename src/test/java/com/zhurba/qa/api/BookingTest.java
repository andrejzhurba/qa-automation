package com.zhurba.qa.api;

import com.zhurba.qa.api.model.Booking;
import com.zhurba.qa.api.model.BookingDates;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;

@Tag("api")
class BookingTest {

    private final BookingApi api = new BookingApi();
    private final Set<Integer> createdBookings = new HashSet<>();
    private String token;

    @AfterEach
    void deleteTestBookings() {
        for (int bookingId : createdBookings) {
            api.deleteBooking(bookingId, token()).then().statusCode(201);
        }
    }

    @Test
    void createAndRetrieveBooking() {
        Booking booking = newBooking();
        int bookingId = createBooking(booking);

        assertThat(getBooking(bookingId)).isEqualTo(booking);
    }

    @Test
    void updateExistingBooking() {
        Booking booking = newBooking();
        int bookingId = createBooking(booking);
        LocalDate checkin = LocalDate.now().plusDays(14);
        Booking updated = new Booking(
                "Updated", booking.lastname(), 375, false,
                new BookingDates(checkin.toString(), checkin.plusDays(5).toString()), "Late checkout");

        Booking response = api.updateBooking(bookingId, token(), updated)
                .then().statusCode(200).contentType(JSON)
                .extract().as(Booking.class);

        assertThat(response).isEqualTo(updated);
        assertThat(getBooking(bookingId)).isEqualTo(updated);
    }

    @Test
    void deleteExistingBooking() {
        int bookingId = createBooking(newBooking());

        api.deleteBooking(bookingId, token()).then().statusCode(201);
        createdBookings.remove(bookingId);

        api.getBooking(bookingId).then().statusCode(404);
    }

    @Test
    void getBookingWithUnknownIdReturns404() {
        api.getBooking(Integer.MAX_VALUE).then().statusCode(404);
    }

    @Test
    void createBookingWithoutRequiredFieldsReturns500() {
        // Restful Booker returns 500 for an incomplete payload, rather than a validation error.
        api.createBooking(Map.of("firstname", "Test")).then().statusCode(500);
    }

    @Test
    void updateBookingWithInvalidTokenIsForbidden() {
        Booking booking = newBooking();
        int bookingId = createBooking(booking);
        Booking updated = new Booking("Changed", booking.lastname(), 1, false,
                booking.bookingdates(), "Changed");

        api.updateBooking(bookingId, "invalid-token", updated).then().statusCode(403);

        assertThat(getBooking(bookingId)).isEqualTo(booking);
    }

    private int createBooking(Booking booking) {
        Response response = api.createBooking(booking);
        response.then().statusCode(200).contentType(JSON).body("bookingid", greaterThan(0));

        int bookingId = response.path("bookingid");
        createdBookings.add(bookingId);
        assertThat(response.jsonPath().getObject("booking", Booking.class)).isEqualTo(booking);
        return bookingId;
    }

    private Booking getBooking(int bookingId) {
        return api.getBooking(bookingId)
                .then().statusCode(200).contentType(JSON)
                .extract().as(Booking.class);
    }

    private String token() {
        if (token == null) {
            token = api.getToken();
        }
        return token;
    }

    private static Booking newBooking() {
        LocalDate checkin = LocalDate.now().plusDays(7);
        return new Booking(
                "Test", "Guest-" + UUID.randomUUID(), 250, true,
                new BookingDates(checkin.toString(), checkin.plusDays(3).toString()), "Breakfast");
    }
}
