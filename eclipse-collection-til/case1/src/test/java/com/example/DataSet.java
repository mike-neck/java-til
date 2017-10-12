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

import com.example.queried.EmployeeContract;
import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

import java.time.LocalDate;
import java.time.Month;

public final class DataSet {

    public static Iterable<Employee> employees() {
        return Lists.fixedSize.of(
                new Employee(100L, "James", LocalDate.of(2009, Month.APRIL, 1)),
                new Employee(200L, "Iain", LocalDate.of(2010, Month.OCTOBER, 20)),
                new Employee(300L, "Sigurbjörn", LocalDate.of(2010, Month.AUGUST, 13)),
                new Employee(400L, "Rachel", LocalDate.of(2014, Month.JUNE, 21))
        );
    }

    public static Iterable<Contract> contracts() {
        return Lists.fixedSize.of(
                new Contract(1000L, "panible", 500, LocalDate.of(2017, Month.OCTOBER, 2)),
                new Contract(2000L, "mucy", 3900, LocalDate.of(2017, Month.OCTOBER, 3)),
                new Contract(3000L, "zooceee", 2300, LocalDate.of(2017, Month.OCTOBER, 5)),
                new Contract(4000L, "antefix", 3000, LocalDate.of(2017, Month.OCTOBER, 6)),
                new Contract(5000L, "astronder", 2900, LocalDate.of(2017, Month.OCTOBER, 8)),
                new Contract(6000L, "DIEPE", 3900, LocalDate.of(2017, Month.OCTOBER, 9)),
                new Contract(7000L, "yazio", 4200, LocalDate.of(2017, Month.OCTOBER, 9)),
                new Contract(8000L, "dimbee", 920, LocalDate.of(2017, Month.OCTOBER, 10)),
                new Contract(9000L, "paleonoodle", 1240, LocalDate.of(2017, Month.OCTOBER, 10))
        );
    }

    private static IntIntPair pair(final int left, final int right) {
        return PrimitiveTuples.pair(left, right);
    }

    public static Iterable<EmployeeContract> employeeContracts() {
        final FixedSizeList<Employee> employees = Lists.fixedSize.ofAll(employees());
        final FixedSizeList<Contract> contracts = Lists.fixedSize.ofAll(contracts());
        final FixedSizeList<IntIntPair> combination = Lists.fixedSize.of(
                pair(0, 0), pair(0, 1),
                pair(1, 2), pair(1, 3),
                pair(2, 4), pair(2, 5), pair(2, 6), pair(2, 7),
                pair(3, 8)
                
        );
        return combination
                .collect(p -> Tuples.pair(employees.get(p.getOne()), contracts.get(p.getTwo())))
                .collect(p -> new EmployeeContract(p.getOne(), p.getTwo()));
    }
}
