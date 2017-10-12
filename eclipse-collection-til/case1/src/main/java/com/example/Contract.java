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

public class Contract {

    private final long contractId;
    private final String company;
    private final int price;
    private final LocalDate contractDate;

    public Contract(final long contractId, final String company, final int price,
            final LocalDate contractDate) {
        this.contractId = contractId;
        this.company = company;
        this.price = price;
        this.contractDate = contractDate;
    }

    public long getContractId() {
        return contractId;
    }

    public String getCompany() {
        return company;
    }

    public int getPrice() {
        return price;
    }

    public LocalDate getContractDate() {
        return contractDate;
    }

    @Override
    public String toString() {
        //noinspection StringBufferReplaceableByString
        final StringBuilder sb = new StringBuilder("Contract{");
        sb.append("contractId=").append(contractId);
        sb.append(", company='").append(company).append('\'');
        sb.append(", price=").append(price);
        sb.append(", contractDate=").append(contractDate);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Contract)) return false;

        final Contract contract = (Contract) o;

        if (contractId != contract.contractId) return false;
        if (price != contract.price) return false;
        //noinspection SimplifiableIfStatement
        if (!company.equals(contract.company)) return false;
        return contractDate.equals(contract.contractDate);
    }

    @Override
    public int hashCode() {
        int result = (int) (contractId ^ (contractId >>> 32));
        result = 31 * result + company.hashCode();
        result = 31 * result + price;
        result = 31 * result + contractDate.hashCode();
        return result;
    }
}
