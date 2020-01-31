package com.ebabu.event365live.listener;

import com.ebabu.event365live.ticketbuy.modal.ticketmodal.FinalSelectTicketModal;

import java.util.List;

public interface SelectedVipTicketListener {
    //void getSelectedTicketListener(int ticketId, String ticketType,  float ticketPrice,int ticketQty, int personPerTable, int itemPosition);
    void getSelectedTicketListener(List<FinalSelectTicketModal.Ticket> ticketList, int itemPosition, int itemSelectedNumber);
}
