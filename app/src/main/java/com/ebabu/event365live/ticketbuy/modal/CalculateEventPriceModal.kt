package com.ebabu.event365live.ticketbuy.modal

data class CalculateEventPriceModal(var ticketId: Int, var ticketType: String, var ticketPrice: Int, var ticketQty: Int) {
}