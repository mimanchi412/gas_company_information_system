package com.classwork.gas.service.impl;

import com.classwork.gas.entity.dto.billing.BillingDtos;
import com.classwork.gas.mapper.*;
import com.classwork.gas.entity.*;
import com.classwork.gas.entity.enums.BillStatus;
import com.classwork.gas.service.BillingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillingServiceImpl implements BillingService {

    private final MeterMapper meterMapper;
    private final MeterReadingMapper meterReadingMapper;
    private final BillMapper billMapper;
    private final CustomerMapper customerMapper;
    private final PaymentMapper paymentMapper;

    public BillingServiceImpl(MeterMapper meterMapper,
                              MeterReadingMapper meterReadingMapper,
                              BillMapper billMapper,
                              CustomerMapper customerMapper,
                              PaymentMapper paymentMapper) {
        this.meterMapper = meterMapper;
        this.meterReadingMapper = meterReadingMapper;
        this.billMapper = billMapper;
        this.customerMapper = customerMapper;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public MeterReading recordReading(BillingDtos.MeterReadingRequest request) {
        Meter meter = meterMapper.findByMeterNo(request.getMeterNo());
        if (meter == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "表具不存在");
        }
        MeterReading reading = MeterReading.builder()
                .meterId(meter.getId())
                .readingValue(request.getReadingValue())
                .readingTime(request.getReadingTime() == null ? LocalDateTime.now() : request.getReadingTime())
                .periodLabel(request.getPeriodLabel())
                .createdAt(LocalDateTime.now())
                .build();
        meterReadingMapper.insert(reading);
        return reading;
    }

    @Override
    public Bill createBill(BillingDtos.CreateBillRequest request) {
        Customer customer = customerMapper.selectById(request.getCustomerId());
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "客户不存在");
        }
        if (request.getMeterId() != null && meterMapper.selectById(request.getMeterId()) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "表具不存在");
        }
        if (request.getReadingId() != null && meterReadingMapper.selectById(request.getReadingId()) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "抄表记录不存在");
        }
        Bill bill = Bill.builder()
                .customerId(request.getCustomerId())
                .meterId(request.getMeterId())
                .readingId(request.getReadingId())
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .status(BillStatus.UNPAID)
                .createdAt(LocalDateTime.now())
                .build();
        billMapper.insert(bill);
        return bill;
    }

    @Override
    public List<Bill> listBills(Long customerId, BillStatus status) {
        if (customerId != null) {
            return billMapper.findByCustomerId(customerId);
        }
        if (status != null) {
            return billMapper.findByStatus(status);
        }
        return billMapper.selectList(null);
    }

    @Override
    public Payment payBill(Long billId, BillingDtos.PayBillRequest request) {
        Bill bill = billMapper.selectById(billId);
        if (bill == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "账单不存在");
        }
        if (bill.getStatus() == BillStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "账单已支付");
        }
        Payment payment = Payment.builder()
                .billId(bill.getId())
                .amount(request.getAmount())
                .method(request.getMethod())
                .paidAt(LocalDateTime.now())
                .remark(request.getRemark())
                .createdAt(LocalDateTime.now())
                .build();
        paymentMapper.insert(payment);
        bill.setStatus(BillStatus.PAID);
        billMapper.updateById(bill);
        return payment;
    }

    @Override
    public List<Bill> listArrears() {
        LocalDate today = LocalDate.now();
        List<Bill> overdue = billMapper.findByStatusAndDueDateBefore(BillStatus.UNPAID, today);
        overdue.forEach(b -> b.setStatus(BillStatus.OVERDUE));
        overdue.forEach(billMapper::updateById);
        return billMapper.findByStatus(BillStatus.OVERDUE);
    }
}
