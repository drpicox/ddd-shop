package com.drpicox.dddshop.cashregister;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketLineRepository extends JpaRepository<TicketLine, TicketId> {
}