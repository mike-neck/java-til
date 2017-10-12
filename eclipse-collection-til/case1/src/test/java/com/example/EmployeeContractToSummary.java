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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Multimaps;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeContractToSummary {

    @Test
    void convertByJavaCollectionAndStream() {
        final List<EmployeeContract> list = DataSet.employeeContractList();

        final Map<Employee, List<Contract>> map = list.stream().collect(Collectors.groupingBy(
                EmployeeContract::getEmployee, LinkedHashMap::new,
                Collectors.mapping(EmployeeContract::getContract, Collectors.toList())));

        final List<Summary> summaries = map.entrySet()
                .stream()
                .map(e -> new Summary(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        assertEquals(DataSet.answerList(), summaries);
    }

    @Test
    void convertByLegacyJavaCollection() {
        final List<EmployeeContract> list = DataSet.employeeContractList();

        final Map<Employee, List<Contract>> map = new LinkedHashMap<>();
        for (final EmployeeContract ec : list) {
            map.computeIfAbsent(ec.getEmployee(), e -> new ArrayList<>()).add(ec.getContract());
        }
        final List<Summary> summaries = new ArrayList<>();
        for (final Map.Entry<Employee, List<Contract>> entry : map.entrySet()) {
            summaries.add(new Summary(entry.getKey(), entry.getValue()));
        }

        assertEquals(DataSet.answerList(), summaries);
    }

    @Test
    void convertByGuava1() {
        final List<EmployeeContract> list = DataSet.employeeContractList();

        final com.google.common.collect.Multimap<Employee, Contract> multimap = com.google.common.collect.LinkedHashMultimap.create();
        list.forEach(ec -> multimap.put(ec.getEmployee(), ec.getContract()));

        final List<Summary> summaries = multimap.asMap().entrySet().stream()
                .map(e -> new Summary(e.getKey(), com.google.common.collect.ImmutableList.copyOf(e.getValue())))
                .collect(Collectors.toList());

        assertEquals(DataSet.answerList(), summaries);
    }

    @Test
    void convertByGuava2() {
        final List<EmployeeContract> list = DataSet.employeeContractList();

        final com.google.common.collect.Multimap<Employee, Contract> multimap = list.stream().collect(
                Collector.<EmployeeContract, com.google.common.collect.Multimap<Employee, Contract>>of(
                        com.google.common.collect.LinkedHashMultimap::create,
                        (m, ec) -> m.put(ec.getEmployee(), ec.getContract()),
                        (l, r) -> {
                            l.putAll(r);
                            return l;
                        }));

        final List<Summary> summaries = multimap.asMap().entrySet().stream()
                .map(e -> new Summary(e.getKey(), com.google.common.collect.ImmutableList.copyOf(e.getValue())))
                .collect(Collectors.toList());

        assertEquals(DataSet.answerList(), summaries);
    }

    @Test
    void convertByEclipseCollection() {
        final List<EmployeeContract> list = DataSet.employeeContractList();

        final MutableList<Summary> summaries = Lists.immutable.ofAll(list).asLazy()
                .groupBy(EmployeeContract::getEmployee, Multimaps.mutable.list.empty())
                .collectValues(EmployeeContract::getContract)
                .keyMultiValuePairsView()
                .collect(v -> new Summary(v.getOne(), v.getTwo().toList()))
                .toList();

        assertEquals(DataSet.answerList(), summaries);
    }
}
