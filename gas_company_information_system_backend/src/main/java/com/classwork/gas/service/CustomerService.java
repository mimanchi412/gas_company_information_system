package com.classwork.gas.service;

import com.classwork.gas.entity.dto.customer.CustomerDtos;
import com.classwork.gas.entity.dto.meter.MeterDtos;
import com.classwork.gas.entity.Customer;
import com.classwork.gas.entity.Meter;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(CustomerDtos.CreateOrUpdateCustomerRequest request);

    Customer updateCustomer(Long id, CustomerDtos.CreateOrUpdateCustomerRequest request);

    Customer getCustomer(Long id);

    List<Customer> search(String keyword);

    Meter addMeter(Long customerId, MeterDtos.AddMeterRequest request);

    List<Meter> listMeters(Long customerId);
}
