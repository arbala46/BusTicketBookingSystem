package ticket_booking_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ticket_booking_system.ENUM.SeatPosition;
import ticket_booking_system.ENUM.SeatStatus;
import ticket_booking_system.ENUM.SeatType;
import ticket_booking_system.entity.Bus;
import ticket_booking_system.entity.Seat;
import ticket_booking_system.repository.busRepository;
import ticket_booking_system.repository.seatRepository;

@Service
public class BusService {

    @Autowired
    busRepository busRepository;

    @Autowired
    seatRepository seatRepository;

    public Bus createBus(Bus bus)
    {
        Bus savedBus = busRepository.save(bus);
        int totalSeats = savedBus.getTotalSeats();

        for(int i=1;i<=totalSeats;i++)
        {
            Seat seat = new Seat();
            int position = i%6;

            seat.setSeatNumber(String.valueOf(i));
            if(position==1)
            {
                seat.setSeatPosition(SeatPosition.LEFT_WINDOW);
                seat.setSeatType(SeatType.WINDOW);
            } else if (position==2) {
                seat.setSeatPosition(SeatPosition.LEFT_MIDDLE);
                seat.setSeatType(SeatType.MIDDLE);
            }else if (position==3) {
                seat.setSeatPosition(SeatPosition.LEFT_AISLE);
                seat.setSeatType(SeatType.AISLE);
            }else if (position==4) {
                seat.setSeatPosition(SeatPosition.RIGHT_AISLE);
                seat.setSeatType(SeatType.AISLE);
            }else if (position==5) {
                seat.setSeatPosition(SeatPosition.RIGHT_MIDDLE);
                seat.setSeatType(SeatType.MIDDLE);
            }else if (position==0) {
                seat.setSeatPosition(SeatPosition.RIGHT_WINDOW);
                seat.setSeatType(SeatType.WINDOW);
            }

            seat.setSeatRow(((i-1)/6)+1);
            seat.setBus(savedBus);


            seatRepository.save(seat);
        }


        return savedBus;
    }

}
