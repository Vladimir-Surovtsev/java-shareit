package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, BookingCreateDto bookingCreateDto) {
        return post("", userId, bookingCreateDto);
    }

    public ResponseEntity<Object> updateStatus(long ownerId, long bookingId, boolean approved) {
        return patch(String.format("/%d?approved=%b", bookingId, approved), ownerId);
    }

    public ResponseEntity<Object> getById(long userId, long bookingId) {
        return get(String.format("/%d", bookingId), userId);
    }

    public ResponseEntity<Object> getAllByUserId(long userId, BookingState bookingState) {
        Map<String, Object> parameters = Map.of(
                "state", bookingState.name()
        );
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getAllByOwnerId(long ownerId, BookingState bookingState) {
        Map<String, Object> parameters = Map.of(
                "state", bookingState.name()
        );
        return get("/owner?state={state}", ownerId, parameters);
    }
}
