package com.drpicox.dddshop.item;

import com.drpicox.dddshop.cashregister.CashRegister;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
