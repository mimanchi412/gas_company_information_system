package com.classwork.gas.controller;

import com.classwork.gas.entity.ApiResponse;
import com.classwork.gas.entity.dto.customer.CustomerDtos;
import com.classwork.gas.entity.dto.meter.MeterDtos;
import com.classwork.gas.entity.Customer;
import com.classwork.gas.entity.Meter;
import com.classwork.gas.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * 新增客户档案。
     */
    @PostMapping
    public ApiResponse<Customer> create(@Valid @RequestBody CustomerDtos.CreateOrUpdateCustomerRequest request) {
        return ApiResponse.ok("新增客户成功", customerService.createCustomer(request));
    }

    /**
     * 更新客户档案。
     */
    @PutMapping("/{id}")
    public ApiResponse<Customer> update(@PathVariable Long id, @Valid @RequestBody CustomerDtos.CreateOrUpdateCustomerRequest request) {
        return ApiResponse.ok("更新客户成功", customerService.updateCustomer(id, request));
    }

    /**
     * 查看单个客户详情。
     */
    @GetMapping("/{id}")
    public ApiResponse<Customer> get(@PathVariable Long id) {
        return ApiResponse.ok(customerService.getCustomer(id));
    }

    /**
     * 客户查询与模糊搜索。
     */
    @GetMapping
    public ApiResponse<List<Customer>> search(@RequestParam(value = "keyword", required = false) String keyword) {
        return ApiResponse.ok(customerService.search(keyword));
    }

    /**
     * 为客户新增表具并绑定。
     */
    @PostMapping("/{id}/meters")
    public ApiResponse<Meter> addMeter(@PathVariable Long id, @Valid @RequestBody MeterDtos.AddMeterRequest request) {
        return ApiResponse.ok("新增表具成功", customerService.addMeter(id, request));
    }

    /**
     * 查询客户名下表具列表。
     */
    @GetMapping("/{id}/meters")
    public ApiResponse<List<Meter>> listMeters(@PathVariable Long id) {
        return ApiResponse.ok(customerService.listMeters(id));
    }
}
