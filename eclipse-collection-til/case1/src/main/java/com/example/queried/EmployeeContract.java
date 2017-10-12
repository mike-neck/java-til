/*
 * Copyright 2017 Shinya Mochida
 * 
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.queried;

import com.example.Contract;
import com.example.Employee;

public class EmployeeContract {

    private final Employee employee;
    private final Contract contract;

    public EmployeeContract(final Employee employee, final Contract contract) {
        this.employee = employee;
        this.contract = contract;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Contract getContract() {
        return contract;
    }
}
