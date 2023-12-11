package com.imaginnovate.test.employee.service;

import java.util.List;

import com.imaginnovate.test.employee.dto.EmployeeDto;

 
public interface EmployeeDetailsService {
	
	public EmployeeDto createEmployee(EmployeeDto dto);

	public 	List<EmployeeDto> getAllEmployee(int pageno, int psgesize, String sortby, String sortdrc);
	

	public Double taxdeductionForCurrentYear(Long id);
}
