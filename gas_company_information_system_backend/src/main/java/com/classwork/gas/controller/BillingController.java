package com.classwork.gas.controller;

import com.classwork.gas.entity.ApiResponse;
import com.classwork.gas.entity.dto.billing.BillingDtos;
import com.classwork.gas.entity.Bill;
import com.classwork.gas.entity.MeterReading;
import com.classwork.gas.entity.Payment;
import com.classwork.gas.entity.enums.BillStatus;
import com.classwork.gas.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    /**
     * 抄表录入/导入单条读数。
     */
    @PostMapping("/readings")
    public ApiResponse<MeterReading> recordReading(@Valid @RequestBody BillingDtos.MeterReadingRequest request) {
        return ApiResponse.ok("抄表录入成功", billingService.recordReading(request));
    }

    /**
     * 生成账单（可关联抄表记录和表具）。
     */
    @PostMapping("/bills")
    public ApiResponse<Bill> createBill(@Valid @RequestBody BillingDtos.CreateBillRequest request) {
        return ApiResponse.ok("账单生成成功", billingService.createBill(request));
    }

    /**
     * 账单查询，支持按客户或状态过滤。
     */
    @GetMapping("/bills")
    public ApiResponse<List<Bill>> listBills(@RequestParam(value = "customerId", required = false) Long customerId,
                                             @RequestParam(value = "status", required = false) BillStatus status) {
        return ApiResponse.ok(billingService.listBills(customerId, status));
    }

    /**
     * 缴费登记并标记账单已支付。
     */
    @PostMapping("/bills/{id}/pay")
    public ApiResponse<Payment> payBill(@PathVariable Long id, @Valid @RequestBody BillingDtos.PayBillRequest request) {
        return ApiResponse.ok("支付成功", billingService.payBill(id, request));
    }

    /**
     * 欠费/逾期账单列表。
     */
    @GetMapping("/arrears")
    public ApiResponse<List<Bill>> listArrears() {
        return ApiResponse.ok(billingService.listArrears());
    }
}
