package com.ebabu.event365live.listener;

public interface SelectedVipTicketListener {
    void getSelectedTicketListener(int ticketId, String ticketType,  float ticketPrice,int ticketQty, int personPerTable);
}
