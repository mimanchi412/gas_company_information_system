package com.classwork.gas.service;

import com.classwork.gas.entity.dto.billing.BillingDtos;
import com.classwork.gas.entity.Bill;
import com.classwork.gas.entity.MeterReading;
import com.classwork.gas.entity.Payment;
import com.classwork.gas.entity.enums.BillStatus;

import java.util.List;

public interface BillingService {
    MeterReading recordReading(BillingDtos.MeterReadingRequest request);

    Bill createBill(BillingDtos.CreateBillRequest request);

    List<Bill> listBills(Long customerId, BillStatus status);

    Payment payBill(Long billId, BillingDtos.PayBillRequest request);

    List<Bill> listArrears();
}
