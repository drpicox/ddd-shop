package com.drpicox.dddshop.cashregister;

import com.drpicox.dddshop.events.Event;
import com.drpicox.dddshop.item.ItemId;
import com.drpicox.dddshop.shared.Money;
import com.drpicox.queue.Queue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashRegisterDictionary {

    private long nextCashRegisterId = 0;
    private Map<CashRegisterId, CashRegister> cashRegisters = new HashMap<CashRegisterId, CashRegister>();

    private Queue fakeQueue;

    CashRegisterDictionary() {
        fakeQueue = new Queue();
        fakeQueue.receive(CashDeliveredRecorded.class, this::on);
        fakeQueue.receive(CashRegisterCreated.class, this::on);
        fakeQueue.receive(ItemRecorded.class, this::on);
        fakeQueue.receive(ItemRecordsEnded.class, this::on);
        fakeQueue.receive(ShoppingTransactionEnded.class, this::on);
    }

    public void apply(List<Event> events) {
        events.forEach(event -> this.apply(event));
    }

    public void apply(Event event) {
        fakeQueue.send(event);
        fakeQueue.deliverMessages();
    }

    public CashRegisterCreated createCashRegister() {
        CashRegisterId cashRegisterId = new CashRegisterId(nextCashRegisterId);
        CashRegisterCreated event = new CashRegisterCreated(cashRegisterId, 1L);

        apply(event);
        return event;
    }

    public ItemRecorded recordItem(CashRegisterId cashRegisterId, ItemId itemId, Money price) {
        CashRegister cashRegister = get(cashRegisterId);
        ItemRecorded event = new ItemRecorded(cashRegisterId, cashRegister.getCurrentTicketNumber(), itemId, price);

        apply(event);
        return event;
    }

    public ItemRecordsEnded endItemRecords(CashRegisterId cashRegisterId) {
        CashRegister cashRegister = get(cashRegisterId);
        ItemRecordsEnded event = new ItemRecordsEnded(cashRegisterId, cashRegister.getCurrentTicketNumber());

        apply(event);
        return event;
    }

    public CashDeliveredRecorded recordCashDelivered(CashRegisterId cashRegisterId, Money cashDelivered) {
        CashRegister cashRegister = get(cashRegisterId);
        CashDeliveredRecorded event = new CashDeliveredRecorded(cashRegisterId, cashRegister.getCurrentTicketNumber(), cashDelivered);

        apply(event);
        return event;
    }

    public ShoppingTransactionEnded endShoppingTransaction(CashRegisterId cashRegisterId) {
        CashRegister cashRegister = get(cashRegisterId);
        ShoppingTransactionEnded event = new ShoppingTransactionEnded(cashRegisterId, cashRegister.getCurrentTicketNumber(), cashRegister.getNextTicketNumber());

        apply(event);
        return event;
    }

    public CashRegister get(CashRegisterId cashRegisterId) {
        return cashRegisters.get(cashRegisterId);
    }

    private void on(CashRegisterCreated cashRegisterCreated) {
        nextCashRegisterId = Math.min(cashRegisterCreated.getCashRegisterId().getId() + 1, nextCashRegisterId);

        CashRegister cashRegister = new CashRegister(cashRegisterCreated);
        cashRegisters.put(cashRegister.getId(), cashRegister);
    }

    private void on(CashDeliveredRecorded cashDeliveredRecorded) {
    }

    private void on(ItemRecorded itemRecorded) {
    }

    private void on(ItemRecordsEnded itemRecordsEnded) {
        get(itemRecordsEnded.getCashRegisterId()).endItemRecords();
    }

    private void on(ShoppingTransactionEnded shoppingTransactionEnded) {
        get(shoppingTransactionEnded.getCashRegisterId()).endShoppingTransaction(shoppingTransactionEnded);
    }
}
