package com.ebabu.event365live.ticketbuy.modal

data class CalculateEventPriceModal(var ticketId: Int, var ticketType: String, var ticketNumber: String?, var ticketPrice: Float, var ticketQty: Int, var personPerTable: Int, var itemSelectedQty: Int, var discountedPrice: Float, var totalPrice: Double) {
}