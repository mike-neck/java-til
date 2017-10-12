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
package com.example;

import java.time.LocalDate;

public class Employee {

    private final long employeeId;
    private final String name;
    private final LocalDate hireDate;

    public Employee(final long employeeId, final String name, final LocalDate hireDate) {
        this.employeeId = employeeId;
        this.name = name;
        this.hireDate = hireDate;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    @Override
    public String toString() {
        //noinspection StringBufferReplaceableByString
        final StringBuilder sb = new StringBuilder("Employee{");
        sb.append("employeeId=").append(employeeId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", hireDate=").append(hireDate);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;

        final Employee employee = (Employee) o;

        if (employeeId != employee.employeeId) return false;
        //noinspection SimplifiableIfStatement
        if (!name.equals(employee.name)) return false;
        return hireDate.equals(employee.hireDate);
    }

    @Override
    public int hashCode() {
        int result = (int) (employeeId ^ (employeeId >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + hireDate.hashCode();
        return result;
    }
}
