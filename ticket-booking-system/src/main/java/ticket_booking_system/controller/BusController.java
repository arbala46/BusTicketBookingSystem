package ticket_booking_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ticket_booking_system.entity.Bus;
import ticket_booking_system.service.BusService;

@RestController
@RequestMapping("/buses")
public class BusController {

    @Autowired
    BusService busService;

    @PostMapping
    public ResponseEntity<Bus> createBus(@RequestBody Bus bus)
    {
        return ResponseEntity.ok(busService.createBus(bus));
    }

}
