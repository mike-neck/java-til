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

import java.util.Collections;
import java.util.List;

public class Summary {

    private final Employee employee;
    private final List<Contract> contracts;

    public Summary(final Employee employee, final List<Contract> contracts) {
        this.employee = employee;
        this.contracts = contracts;
    }

    public Summary(final Employee employee) {
        this.employee = employee;
        this.contracts = Collections.emptyList();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Summary{");
        sb.append("employee=").append(employee);
        sb.append(", contracts=").append(contracts);
        sb.append('}');
        return sb.toString();
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Summary)) return false;

        final Summary summary = (Summary) o;

        if (!employee.equals(summary.employee)) return false;
        return contracts.equals(summary.contracts);
    }

    @Override
    public int hashCode() {
        int result = employee.hashCode();
        result = 31 * result + contracts.hashCode();
        return result;
    }
}
