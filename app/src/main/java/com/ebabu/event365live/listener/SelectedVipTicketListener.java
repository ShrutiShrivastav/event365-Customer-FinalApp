package com.ebabu.event365live.listener;

public interface SelectedVipTicketListener {
    void getSelectedTicketListener(int ticketId, String ticketType,  double ticketPrice,int ticketQty);
}
