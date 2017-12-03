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

import com.example.dao.GameDao;
import com.example.object.UserScore;
import com.example.resource.TimeRepository;
import com.example.resource.UserExternalRepository;
import com.example.util.Pair;
import com.example.value.TeamId;
import com.example.value.UserId;

import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.*;

public class ReportServiceImpl {

    private final TimeRepository timeRepository;

    private final GameDao gameDao;

    private final UserExternalRepository userExternalRepository;

    public ReportServiceImpl(final TimeRepository timeRepository, final GameDao gameDao,
            final UserExternalRepository userExternalRepository) {
        this.timeRepository = timeRepository;
        this.gameDao = gameDao;
        this.userExternalRepository = userExternalRepository;
    }

    public Map<TeamId, LongSummaryStatistics> getDailyReport(final LocalDate date) {
        final LocalDate today = timeRepository.today();
        if (date.isAfter(today)) {
            return Collections.emptyMap();
        }
        final List<UserScore> userScores = gameDao.dailyUserScoreList(date);
        final Map<Pair<TeamId, UserId>, IntSummaryStatistics> scoreByTeamByUser = userScores.stream()
                .collect(groupingBy(us -> new Pair<>(us.getTeamId(), us.getUserId()), summarizingInt(us -> us.getScore().getValue())));
        final Set<UserId> users = scoreByTeamByUser.entrySet().stream()
                .map(Map.Entry::getKey)
                .map(Pair::getRight)
                .collect(toSet());
        final List<UserId> premiumUsers = userExternalRepository.findPremiumUser(users);
        return scoreByTeamByUser.entrySet()
                .stream()
                .filter(s -> premiumUsers.contains(s.getKey().getRight()))
                .filter(s -> s.getValue().getSum() > 0L)
                .collect(groupingBy(s -> s.getKey().getLeft(), summarizingLong(e -> e.getValue().getSum())));
    }
}
